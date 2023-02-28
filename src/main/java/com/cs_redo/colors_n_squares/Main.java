package com.cs_redo.colors_n_squares;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Main {
	public static void main(String args[]) {
		try {
			
			BufferedImage imageFile = ImageIO.read(new File("I://Pictures/misc/Base.jpg"));
			GeneticAlgorithm ga = new GeneticAlgorithm(10, 0.05, imageFile);
			ga.run();

		} catch (IOException exception) {
			System.out.println("FAILED TO INGEST IMAGE");
		}
	}
}
