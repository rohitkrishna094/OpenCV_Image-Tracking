package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Util {
	
	public static BufferedReader reader;
	public static BufferedWriter writer;
	
	public static int getScore(String url) {
		try {
			reader = new BufferedReader(new FileReader(new File(url)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int highScore = 0;
		try {
			highScore = Integer.parseInt(reader.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return highScore;
	}
	
	public static int compareScore(String url, int score) {
		try {
			reader = new BufferedReader(new FileReader(new File(url)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int highScore = 0;
		try {
			highScore = Integer.parseInt(reader.readLine());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (score > highScore) {
			highScore = score;
		}
		
		try {
			writer = new BufferedWriter(new FileWriter(new File(url)));
			writer.write(""+highScore);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return highScore;
		
	}
}
