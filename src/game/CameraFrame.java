package game;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.opencv.videoio.VideoCapture;

public class CameraFrame extends JFrame implements ActionListener{

	public CameraPanel cp;
	
	public CameraFrame() {
		System.loadLibrary("opencv_java320");
		int width = 1100, height = 720;
		
		// 0 is the first camera connected to this computer: in my case webcam.
		// If you want to get another camera, just change this number
		VideoCapture list = new VideoCapture(0);
		
		cp = new CameraPanel(width, height);
		
		Thread thread = new Thread(cp);
		
		// Create a menu for changing the cameras attached to this computer
		JMenu camera = new JMenu("Camera");
		JMenuBar bar = new JMenuBar();
		bar.add(camera);
		
		// Loop through all the cameras that are available
		int i = 1;
		
		// while list is opened means we are able to use the camera
		while(list.isOpened()){
			JMenuItem cam = new JMenuItem("Camera " + i);
			cam.addActionListener(this);
			
			// add cam MenuItem to camera Menu
			camera.add(cam);
			
			list.release();
			list = new VideoCapture(i);
			i++;
		}
		
		// start the thread
		thread.start();
		
		// add camera panel to JFrame
		add(cp);
		
		// add bar to JFrame
		setJMenuBar(bar);
		
		// Set usual JFrame operations
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, height);
		setVisible(true);
		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
	
}
