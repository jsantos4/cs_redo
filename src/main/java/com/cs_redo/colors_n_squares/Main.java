package com.cs_redo.colors_n_squares;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Main {
	public static void main(String args[]) {
		try {
			
			BufferedImage imageFile = ImageIO.read(new File("C://someimage"));
			GeneticAlgorithm ga = new GeneticAlgorithm(10, 0.001, imageFile);
			ga.run();

		} catch (IOException exception) {
			System.out.println("FAILED TO INGEST IMAGE");
		}
	}
}
