package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

// todo: 
// * make all button states visible by button color
// * make gui look much better
// * snake game
// ActionListener for screenshot button
// First draw rectangle, then click auto, then output
// Also add GUI for morphing options
public class CameraPanel extends JPanel implements Runnable, ActionListener, ChangeListener {

	boolean DEBUG = true;

	BufferedImage image;
	BufferedImage tempImage;
	VideoCapture capture;
	JButton flipButton, hsvToggleButton, thresholdButton, morphButton, trackButton;
	JButton outputButton, autoButton, gridButton, startButton;
	JSlider hMinSlider, hMaxSlider, sMinSlider, sMaxSlider, vMinSlider, vMaxSlider;
	JLabel hMinLabel, hMaxLabel, sMinLabel, sMaxLabel, vMinLabel, vMaxLabel, redLabel, greenLabel, blueLabel, erodeLabel, dilateLabel;
	JLabel minAreaLabel;
	SpinnerModel spinnerModel, erodeSpinnerModel, dilateSpinnerModel, erodeSizeSpinnerModel, dilateSizeSpinnerModel, minAreaSpinnerModel;
	JSpinner spinner, erodeSpinner, dilateSpinner, erodeSizeSpinner, dilateSizeSpinner, minAreaSpinner;
	JTextField redField, greenField, blueField;
	String[] comboTitles = new String[] { "BLACKHAT", "CLOSE", "CROSS", "DILATE", "ELLIPSE", "ERODE", "GRADIENT", "HITMISS", "OPEN", "RECT", "TOPHAT" };
	JComboBox<String> comboList;

	// values for slider
	int H_MIN = 0, H_MAX = 256, S_MIN = 0, S_MAX = 256, V_MIN = 0, V_MAX = 256;
	int MAX_NUM_OBJECTS = 50;
	int MIN_OBJECT_AREA = 20 * 20;
	int MAX_OBJECT_AREA;

	// Mouse dragged
	int x, y, x2, y2;
	int auto_xmin, auto_xmax, auto_ymin, auto_ymax;

	// states
	boolean flipState = false; // can be triggered from any state
	boolean hsvState = false; // the first to be triggered so no problem
	boolean thresholdState = false; // if this is triggered, then immediately set hsvState to 1 also
	boolean morphState = false;
	boolean trackState = false;
	boolean outputState = false;
	boolean autoState = false;
	boolean drawState = true;
	boolean drawGridState = false;
	boolean play = false;

	// Constructor
	public CameraPanel(int w, int h) {
		x = y = x2 = y2 = 0;
		MyMouseListener listener = new MyMouseListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);

		int MAX_OBJECT_AREA = (int) (w * h / 1.5);

		setLayout(null);
		flipButton = new JButton("Flip Camera");
		flipButton.addActionListener(this);
		flipButton.setBounds(10, 4, 110, 30);
		add(flipButton);

		hsvToggleButton = new JButton("Toggle HSV/Original");
		hsvToggleButton.addActionListener(this);
		hsvToggleButton.setBounds(10 + 10 + 110, 4, 150, 30);
		add(hsvToggleButton);

		thresholdButton = new JButton("Threshold");
		thresholdButton.addActionListener(this);
		thresholdButton.setBounds(290, 4, 100, 30);
		add(thresholdButton);

		morphButton = new JButton("Morph");
		morphButton.addActionListener(this);
		morphButton.setBounds(400, 4, 80, 30);
		add(morphButton);

		trackButton = new JButton("Track");
		trackButton.addActionListener(this);
		trackButton.setBounds(490, 4, 70, 30);
		add(trackButton);

		outputButton = new JButton("Output");
		outputButton.addActionListener(this);
		outputButton.setBounds(570, 4, 80, 30);
		add(outputButton);

		autoButton = new JButton("Auto");
		autoButton.addActionListener(this);
		autoButton.setBounds(710, 230, 290, 30);
		add(autoButton);

		gridButton = new JButton("Show Grid");
		gridButton.addActionListener(this);
		gridButton.setBounds(710, 270, 290, 30);
		add(gridButton);

		startButton = new JButton("Start");
		startButton.addActionListener(this);
		startButton.setBounds(710, 310, 290, 30);
		add(startButton);

		spinnerModel = new SpinnerNumberModel(20, // initial value
				10, // min
				100, // max
				1);// step
		spinner = new JSpinner(spinnerModel);
		spinner.addChangeListener(this);
		spinner.setBounds(960, 350, 40, 30);
		add(spinner);

		erodeSpinnerModel = new SpinnerNumberModel(2, // initial value
				1, // min
				10, // max
				1);// step
		erodeSpinner = new JSpinner(erodeSpinnerModel);
		erodeSpinner.addChangeListener(this);
		erodeSpinner.setBounds(750, 390, 40, 30);
		add(erodeSpinner);

		dilateSpinnerModel = new SpinnerNumberModel(2, // initial value
				1, // min
				10, // max
				1);// step
		dilateSpinner = new JSpinner(dilateSpinnerModel);
		dilateSpinner.addChangeListener(this);
		dilateSpinner.setBounds(890, 390, 40, 30);
		add(dilateSpinner);

		erodeSizeSpinnerModel = new SpinnerNumberModel(3, // initial value
				1, // min
				100, // max
				1);// step
		erodeSizeSpinner = new JSpinner(erodeSizeSpinnerModel);
		erodeSizeSpinner.addChangeListener(this);
		erodeSizeSpinner.setBounds(800, 390, 40, 30);
		add(erodeSizeSpinner);

		dilateSizeSpinnerModel = new SpinnerNumberModel(8, // initial value
				1, // min
				100, // max
				1);// step
		dilateSizeSpinner = new JSpinner(dilateSizeSpinnerModel);
		dilateSizeSpinner.addChangeListener(this);
		dilateSizeSpinner.setBounds(940, 390, 40, 30);
		add(dilateSizeSpinner);

		minAreaLabel = new JLabel("Min Area: ");
		add(minAreaLabel);
		minAreaLabel.setBounds(710, 430, 60, 30);

		minAreaSpinnerModel = new SpinnerNumberModel(400, // initial value
				20, // min
				307200, // max
				1);// step
		minAreaSpinner = new JSpinner(minAreaSpinnerModel);
		minAreaSpinner.addChangeListener(this);
		minAreaSpinner.setBounds(770, 430, 120, 30);
		add(minAreaSpinner);

		comboList = new JComboBox<String>(comboTitles);
		comboList.setBounds(900, 430, 100, 30);
		comboList.setSelectedItem("RECT");
//		add(comboList);

		redField = new JTextField("0");
		greenField = new JTextField("255");
		blueField = new JTextField("0");
		redLabel = new JLabel("Red: ");
		greenLabel = new JLabel("Green: ");
		blueLabel = new JLabel("Blue: ");
		redLabel.setBounds(710, 350, 30, 30);
		redField.setBounds(740, 350, 30, 30);
		greenLabel.setBounds(780, 350, 50, 30);
		greenField.setBounds(820, 350, 30, 30);
		blueLabel.setBounds(860, 350, 50, 30);
		blueField.setBounds(890, 350, 30, 30);
		add(redField);
		add(greenField);
		add(blueField);
		add(redLabel);
		add(blueLabel);
		add(greenLabel);

		erodeLabel = new JLabel("Erode: ");
		dilateLabel = new JLabel("Dilate: ");
		erodeLabel.setBounds(710, 390, 40, 30);
		dilateLabel.setBounds(850, 390, 40, 30);
		add(erodeLabel);
		add(dilateLabel);

		hMinSlider = new JSlider(JSlider.HORIZONTAL, 0, H_MAX, 0);
		hMinSlider.setBounds(800, 30, 200, 30);
		add(hMinSlider);
		hMinLabel = new JLabel("H_MIN: ");
		hMinLabel.setBounds(710, 30, 200, 30);
		add(hMinLabel);
		hMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, H_MAX, H_MAX);
		hMaxSlider.setBounds(800, 60, 200, 30);
		add(hMaxSlider);
		hMaxLabel = new JLabel("H_MAX: ");
		hMaxLabel.setBounds(710, 60, 200, 30);
		add(hMaxLabel);

		sMinSlider = new JSlider(JSlider.HORIZONTAL, 0, S_MAX, 0);
		sMinSlider.setBounds(800, 90, 200, 30);
		add(sMinSlider);
		sMinLabel = new JLabel("S_MIN: ");
		sMinLabel.setBounds(710, 90, 200, 30);
		add(sMinLabel);
		sMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, S_MAX, S_MAX);
		sMaxSlider.setBounds(800, 120, 200, 30);
		add(sMaxSlider);
		sMaxLabel = new JLabel("S_MAX: ");
		sMaxLabel.setBounds(710, 120, 200, 30);
		add(sMaxLabel);

		vMinSlider = new JSlider(JSlider.HORIZONTAL, 0, V_MAX, 0);
		vMinSlider.setBounds(800, 150, 200, 30);
		add(vMinSlider);
		vMinLabel = new JLabel("V_MIN: ");
		vMinLabel.setBounds(710, 150, 200, 30);
		add(vMinLabel);
		vMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, V_MAX, V_MAX);
		vMaxSlider.setBounds(800, 180, 200, 30);
		add(vMaxSlider);
		vMaxLabel = new JLabel("V_MAX: ");
		vMaxLabel.setBounds(710, 180, 200, 30);
		add(vMaxLabel);
	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == flipButton) {
			flipState = (flipState == true) ? false : true;
		}
		if(e.getSource() == hsvToggleButton) {
			hsvState = (hsvState == true) ? false : true;
		}
		if(e.getSource() == thresholdButton) {
			thresholdState = (thresholdState == true) ? false : true;
		}
		if(e.getSource() == morphButton) {
			morphState = (morphState == true) ? false : true;
		}
		if(e.getSource() == trackButton) {
			trackState = (trackState == true) ? false : true;
		}
		if(e.getSource() == outputButton) {
			outputState = (outputState == true) ? false : true;
			hsvState = false;
			thresholdState = false;
			morphState = true;
			trackState = true;
			drawState = false;
		}
		if(e.getSource() == autoButton) {
			autoState = (autoState == true) ? false : true;
		}
		if(e.getSource() == gridButton) {
			drawGridState = (drawGridState == true) ? false : true;
		}
		if(e.getSource() == startButton) {
			play = true;
		}
	}

	@Override
	public void run() {
		System.loadLibrary("opencv_java320");
		capture = new VideoCapture(0); // webcam
		// capture = new VideoCapture("C:\\Users\\admin\\Desktop\\video.mp4");
		Mat webcam_image = new Mat();
		Mat hsv_image = new Mat();
		Mat threshold_image = new Mat();
		if(capture.isOpened()) {
			while (true) {
				capture.read(webcam_image);
				// cam applications
				if(flipState) {
					Core.flip(webcam_image, webcam_image, 1);
				}
				if(hsvState) {
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
				}
				if(thresholdState) {
					Scalar lowerBound = new Scalar(hMinSlider.getValue(), sMinSlider.getValue(), vMinSlider.getValue());
					Scalar upperBound = new Scalar(hMaxSlider.getValue(), sMaxSlider.getValue(), vMaxSlider.getValue());
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
					Core.inRange(hsv_image, lowerBound, upperBound, threshold_image);
				}
				if(autoState) {
					auto_xmin = Math.min(x, x2);
					auto_xmax = Math.max(x, x2);
					auto_ymin = Math.min(y, y2);
					auto_ymax = Math.max(y, y2);
				}
				// if(DEBUG) System.out.println(autoState + ": " + auto_xmin + ":" + auto_xmax + ":" + auto_ymin + ":" + auto_ymax);
				// GUI stuff
				hMinLabel.setText("H_MIN: " + hMinSlider.getValue());
				hMaxLabel.setText("H_MAX: " + hMaxSlider.getValue());
				sMinLabel.setText("S_MIN: " + sMinSlider.getValue());
				sMaxLabel.setText("S_MAX: " + sMaxSlider.getValue());
				vMinLabel.setText("V_MIN: " + vMinSlider.getValue());
				vMaxLabel.setText("V_MAX: " + vMaxSlider.getValue());

				if(outputState) {
					if(thresholdState) outputState = false;
					Scalar lowerBound = new Scalar(hMinSlider.getValue(), sMinSlider.getValue(), vMinSlider.getValue());
					Scalar upperBound = new Scalar(hMaxSlider.getValue(), sMaxSlider.getValue(), vMaxSlider.getValue());
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);
					if(autoState) {
						// This function should change slider values
						getHSVValues(hsv_image, auto_xmin - 10, auto_xmax - 10, auto_ymin - 40, auto_ymax - 40);
						lowerBound = new Scalar(hMinSlider.getValue(), sMinSlider.getValue(), vMinSlider.getValue());
						upperBound = new Scalar(hMaxSlider.getValue(), sMaxSlider.getValue(), vMaxSlider.getValue());
						autoState = false;
					}
					Core.inRange(hsv_image, lowerBound, upperBound, threshold_image);
					if(!threshold_image.empty()) {
						Imgproc.cvtColor(threshold_image, threshold_image, Imgproc.COLOR_GRAY2BGR); // to get it back to 3 channels
						if(morphState) {
							performMorphologicalOperations(threshold_image);
							if(trackState) {
								trackImage(threshold_image, webcam_image);
							}
						}
						MatToBufferedImage(webcam_image);
						repaint();
					}
				} else if(thresholdState) {
					if(!threshold_image.empty()) {
						Imgproc.cvtColor(threshold_image, threshold_image, Imgproc.COLOR_GRAY2BGR);
						// Imgproc.cvtColor(threshold_image, threshold_image, Imgproc.COLOR_BGR2HSV);
						if(morphState) {
							performMorphologicalOperations(threshold_image);
							if(trackState) {
								trackImage(threshold_image, threshold_image);
							}
						}
						MatToBufferedImage(threshold_image);
						repaint();
					}
				} else {
					// if Mat is not empty, then we convert to BufferedImage
					if(hsvState) {
						MatToBufferedImage(hsv_image);
						repaint();
					} else {
						if(!webcam_image.empty()) {
							// sizing the image in the panel based on the
							// webcam_image
							// that we just captured from the real webcam source
							// JFrame topFrame = (JFrame)
							// SwingUtilities.getWindowAncestor(this);
							// topFrame.setSize(webcam_image.width() + 40,
							// webcam_image.height() + 150);
							MatToBufferedImage(webcam_image);
							repaint();
						}
					}
				}
			} // end while
		} // end if
	} // end run

	public void getHSVValues(Mat mat, int x1, int x2, int y1, int y2) {
		int width = mat.width();
		int height = mat.height();
		int channels = mat.channels();
		byte[] source = new byte[width * height * channels];
		mat.get(0, 0, source);

		byte[] h = new byte[width * height];
		byte[] s = new byte[width * height];
		byte[] v = new byte[width * height];
		int index = 0;
		// System.out.println(width+":"+height+":"+channels+":"+x1+ "=>"+x2+"=>"+y1+"=>"+y2);
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				h[index] = source[i * 3 * height + j * 3 + 0];
				s[index] = source[i * 3 * height + j * 3 + 1];
				v[index] = source[i * 3 * height + j * 3 + 2];
				index++;
			}
		} // end for
		System.out.println(minOfByteArray(h) + " : " + maxOfByteArray(h));
		// printByteArray(h);

		// Mat to bufferedImage
		tempImage = new BufferedImage(x2 - x1, y2 - y1, BufferedImage.TYPE_3BYTE_BGR);

		final byte[] target = ((DataBufferByte) tempImage.getRaster().getDataBuffer()).getData();

		// copy stuff from source to target
		System.arraycopy(source, x1 * 3 * height + y1 * 3, target, 0, (x2 - x1) * (y2 - y1));

		hMinSlider.setValue(minOfByteArray(h) + 127);
		hMaxSlider.setValue(maxOfByteArray(h) + 127);
		sMinSlider.setValue(minOfByteArray(s) + 127);
		sMaxSlider.setValue(maxOfByteArray(s) + 127);
		vMinSlider.setValue(minOfByteArray(v) + 127);
		vMaxSlider.setValue(maxOfByteArray(v) + 127);
	}

	public void printByteArray(byte[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + " ");
		}
	}

	public byte maxOfByteArray(byte[] array) {
		byte max = array[0];
		for (int i = 1; i < array.length; i++) {
			if(array[i] > max) max = array[i];
		}
		return max;
	}

	public byte minOfByteArray(byte[] array) {
		byte min = array[0];
		for (int i = 1; i < array.length; i++) {
			if(array[i] < min) min = array[i];
		}
		return min;
	}

	public void printMat(Mat mat) {
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				System.out.print(mat.get(i, j)[0]);
			}
			System.out.println();
		}
	}

	public void trackImage(Mat img, Mat originalFrame) {
		MIN_OBJECT_AREA = Integer.parseInt(minAreaSpinner.getValue().toString());
		int x = 0, y = 0;
		Mat temp = new Mat();
		img.copyTo(temp);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();

		// Convert it to monochrome to avoid findcontours error
		Imgproc.cvtColor(temp, temp, Imgproc.COLOR_BGR2GRAY);
		Imgproc.findContours(temp, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		boolean objectFound = false;

		if(contours.size() > 0) {
			int numberOfObjects = contours.size();
			if(numberOfObjects < MAX_NUM_OBJECTS) {
				for (int i = 0; i < contours.size(); i++) {
					Moments moments = Imgproc.moments(contours.get(i));
					double area = moments.get_m00();
					if(area > MIN_OBJECT_AREA) {
						x = (int) (moments.m10 / area);
						y = (int) (moments.m01 / area);
						objectFound = true;
					} else {
						objectFound = false;
					}
				}
				if(objectFound) {
					// if(DEBUG) System.out.println("Object found at location: " + x + "," + y);
					drawObject(x, y, originalFrame);
				}
			} else {
				System.out.println("Adjust sliders to reduce noise");
			}
		}
	}

	public void drawObject(int x, int y, Mat img) {
		// System.out.println(x + ": " + y);
		// System.out.println(getQuadrant(x + 10, y + 40));
		Global.quadValue = getQuadrant(x + 10, y + 40);

		int r, g, b;
		if(redField.getText() == "" || blueField.getText() == "" || greenField.getText() == "") {
			r = b = 0;
			g = 255;
		} else {
			r = Integer.parseInt(redField.getText());
			g = Integer.parseInt(greenField.getText());
			b = Integer.parseInt(blueField.getText());
		}

		if(r > 255 || r < 0) {
			r = 0;
			redField.setText("0");
		}
		if(g > 255 || g < 0) {
			g = 0;
			greenField.setText("0");
		}
		if(b > 255 || b < 0) {
			b = 0;
			blueField.setText("0");
		}

		Scalar s = new Scalar(b, g, r);
		Imgproc.circle(img, new Point(x, y), Integer.parseInt(spinner.getValue().toString()), s, 2);
	}

	public void performMorphologicalOperations(Mat img) {
		int erodeSize = Integer.parseInt(erodeSizeSpinner.getValue().toString());
		int dilateSize = Integer.parseInt(dilateSizeSpinner.getValue().toString());
		Mat erodeSTREL, dilateSTREL;
		// get values form combobox
//		if(comboList.getSelectedItem().toString().equals("BLACKHAT")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_BLACKHAT, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_BLACKHAT, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("CLOSE")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_CLOSE, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_CLOSE, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("DILATE")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("ELLIPSE")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("ERODE")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("GRADIENT")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_GRADIENT, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_GRADIENT, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("HITMISS")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_HITMISS, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_HITMISS, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("OPEN")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_OPEN, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_OPEN, new Size(dilateSize, dilateSize));
//		} else if(comboList.getSelectedItem().toString().equals("TOPHAT")) {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_TOPHAT, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_TOPHAT, new Size(dilateSize, dilateSize));
//		} else {
//			erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erodeSize, erodeSize));
//			dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilateSize, dilateSize));
//		}

		erodeSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erodeSize, erodeSize));
		dilateSTREL = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(dilateSize, dilateSize));
		
		int n = Integer.parseInt(erodeSpinner.getValue().toString());
		if(n >= 1 || n <= 10) {
		} else {
			n = 2;
		}

		for (int i = 0; i < n; i++) {
			Imgproc.erode(img, img, erodeSTREL);
		}

		int k = Integer.parseInt(dilateSpinner.getValue().toString());
		if(k >= 1 || k <= 10) {
		} else {
			k = 2;
		}
		for (int i = 0; i < k; i++) {
			Imgproc.dilate(img, img, dilateSTREL);
		}
	}

	public void MatToBufferedImage(Mat img) {
		// Get width, height and channels
		int width = img.width();
		int height = img.height();
		int channels = img.channels();
		byte[] source = new byte[width * height * channels];
		img.get(0, 0, source);

		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		final byte[] target = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

		// copy stuff from source to target
		System.arraycopy(source, 0, target, 0, source.length);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(this.image == null) return;

		g.drawImage(image, 10, 40, image.getWidth(), image.getHeight(), null);

		if(drawState && !autoState) {
			g.setColor(Color.RED);
			drawPerfectRect(g, x, y, x2, y2);
		}
		if(drawGridState) {
			g.setColor(Color.BLUE);
			g.drawLine(10, 40, 10 + 640, 40);
			// horizontal lines
			g.drawLine(10, 40 + 160, 10 + 640, 40 + 160);
			g.drawLine(10, 40 + 320, 10 + 640, 40 + 320);
			// vertical lines
			g.drawLine(10 + 213, 40, 10 + 213, 40 + 480);
			g.drawLine(10 + 427, 40, 10 + 427, 40 + 480);
			g.drawLine(10, 40 + 480, 10 + 640, 40 + 480);
		}
	}

	// If no change, returns 4 i.e., middle quadrant
	public int getQuadrant(int x, int y) {
		if(x >= 0 && x < 223) {
			// Col1
			if(y >= 40 && y < 200) {
				// row1
				return 0;
			} else if(y >= 200 && y < 360) {
				// row 2
				return 3;
			} else if(y >= 360 && y <= 520) {
				// row 3
				return 6;
			}
		} else if(x >= 233 & x < 437) {
			// Col2
			if(y >= 40 && y < 200) {
				// row1
				return 1;
			} else if(y >= 200 && y < 360) {
				// row 2
				return 4;
			} else if(y >= 360 && y <= 520) {
				// row 3
				return 7;
			}
		} else if(x >= 437 && x <= 650) {
			// Col3
			if(y >= 40 && y < 200) {
				// row1
				return 2;
			} else if(y >= 200 && y < 360) {
				// row 2
				return 5;
			} else if(y >= 360 && y <= 520) {
				// row 3
				return 8;
			}
		}
		return 4;
	}

	public void setStartPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setEndPoint(int x, int y) {
		x2 = (x);
		y2 = (y);
	}

	public void drawPerfectRect(Graphics g, int x, int y, int x2, int y2) {
		int px = Math.min(x, x2);
		int py = Math.min(y, y2);
		int pw = Math.abs(x - x2);
		int ph = Math.abs(y - y2);
		g.drawRect(px, py, pw, ph);
	}

	class MyMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			setStartPoint(e.getX(), e.getY());
		}

		public void mouseDragged(MouseEvent e) {
			setEndPoint(e.getX(), e.getY());
			repaint();
		}

		public void mouseReleased(MouseEvent e) {
			setEndPoint(e.getX(), e.getY());
			repaint();
		}
	} // end class
}
