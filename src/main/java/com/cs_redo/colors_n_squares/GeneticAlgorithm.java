package com.cs_redo.colors_n_squares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;

//import java.util.concurrent.Exchanger;

class GeneticAlgorithm implements Runnable {

	//private Exchanger<Grid> exchanger = new Exchanger<>();
	private int scale, generationCount;
	private double mutationRate, optimumAffinity, summedAffinities;
	private Grid best;
	private ArrayList<Grid> generation;
	private ThreadLocalRandom rng = ThreadLocalRandom.current();
	private BufferedImage image;

	public GeneticAlgorithm(int scale, Double mutationRate, BufferedImage image) {
		
		this.image = image;
		this.scale = scale;
		this.mutationRate = mutationRate;
		generation = new ArrayList<>();
		generationCount = 0;
		init();
	}

	private void init() {
		int[] originalImage = image.getRGB(0, 0, image.getWidth() - 1, image.getHeight() - 1, null, 0, image.getWidth());
		
		for (int i = 0; i < scale; i++) {
			generation.add(new Grid(originalImage, image.getWidth(), image.getHeight()));
			summedAffinities += generation.get(i).getTotalAffinity();
		}
		Collections.sort(generation);
		best = generation.get(0);
		optimumAffinity = best.getOptimumAffinity();
	}

	private void breed() {
		Grid offspring;

		for (int i = 0; i < scale; i++) {
			offspring = crossover(selectParent(), selectParent());
			generation.add(offspring);
		}

		Collections.sort(generation);
		best = generation.get(0);
		generation.subList(scale, generation.size()).clear();
		summedAffinities = 0;
		for (Grid grid : generation)
			summedAffinities += grid.getTotalAffinity();

	}

	private Grid crossover(Grid parent1, Grid parent2) {
		Grid offspring = new Grid(parent1, parent2, mutationRate);
		return offspring;
	}

	private Grid selectParent() {

		double selected = rng.nextDouble(summedAffinities);
		double partialSum = 0;
		for (Grid grid : generation) {
			if ((partialSum += grid.getTotalAffinity()) > selected)
				return grid;
		}

		return null;

	}

	public Grid getBest() {
		return best;
	}
	

	public void run() {

		System.out.println("Generation: " + generationCount + "\n Best grid: " + best.getTotalAffinity());
		BufferedImage imageReconstruction = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		File imageFile = new File("I://Pictures/misc/BaseReconstruction.jpg");
		
		while (best.getTotalAffinity() < optimumAffinity * 0.85 && generationCount < 100000) {
			
			breed();

			//"Render" reconstructed image so far
			for (int x = 0; x < imageReconstruction.getWidth(); x++) {
				for (int y = 0; y < imageReconstruction.getHeight(); y++) {
					imageReconstruction.setRGB(x, y, best.getCells()[x][y]);
				}
			}

			try {
				ImageIO.write(imageReconstruction, "jpg", imageFile);					
			} catch (IOException exception) {
				exception.printStackTrace();
			}

			generationCount++;
			if (generationCount % 1000 == 0) {
				System.out.println("Generation: " + generationCount + "\n Best grid: " + best.getTotalAffinity());
				
				//"Render" reconstructed image so far
				for (int x = 0; x < imageReconstruction.getWidth(); x++) {
					for (int y = 0; y < imageReconstruction.getHeight(); y++) {
						imageReconstruction.setRGB(x, y, best.getCells()[x][y]);
					}
				}

				try {
					ImageIO.write(imageReconstruction, "jpg", imageFile);					
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
		
		System.out.println("Generation: " + generationCount + "\n Best grid: " + best.getTotalAffinity());
		best.printGrid();
	}
}