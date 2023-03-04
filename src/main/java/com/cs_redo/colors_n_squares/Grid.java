package com.cs_redo.colors_n_squares;

import java.util.concurrent.ThreadLocalRandom;
import java.awt.image.BufferedImage;


class Grid implements Comparable<Grid> {

	private int[][] cells;
	private int height, width, population;
	private double totalAffinity, optimumAffinity;
	private ThreadLocalRandom rng = ThreadLocalRandom.current();
	private BufferedImage originalImage;

	public Grid(BufferedImage originalImage, int width, int height){
		this.height = height;
		this.width = width;
		this.originalImage = originalImage;

		population = height * width;
		optimumAffinity = population * 2;

		init();
		calculateTotalAffinity();
	}



	public Grid(Grid g){
		this.height = g.getHeight();
		this.width = g.getWidth();
		this.population = g.getPopulation();
		this.cells = g.getCells();
		this.optimumAffinity = g.getOptimumAffinity();
		this.totalAffinity = g.getTotalAffinity();
		this.originalImage = g.getOriginalImage();
		calculateTotalAffinity();

	}

	public Grid(Grid parent1, Grid parent2, double mutationRate) {

		this.cells = new int[parent1.getWidth()][parent1.getHeight()];
		this.height = parent1.getHeight();
		this.width = parent1.getWidth();
		this.optimumAffinity = parent1.getOptimumAffinity();
		this.originalImage = parent1.getOriginalImage();

		this.population = height * width;

		for (int x = 0; x < width; x++) {	
			for (int y = 0; y < height; y++){
				if (rng.nextDouble() < mutationRate) {
					this.cells[x][y] = (rng.nextInt(256) << 24) | (rng.nextInt(256) << 16) | (rng.nextInt(256) << 8) | rng.nextInt(256);
				} else {
					if (parent1.calculateLocalAffinity(x, y) > parent2.calculateLocalAffinity(x, y))
						this.cells[x][y] = parent1.getCells()[x][y];
					else
						this.cells[x][y] = parent2.getCells()[x][y];
				}
			}
		}

		calculateTotalAffinity();
	}

	private void init() {
		cells = new int[width][height];
		
		for (int x = width - 1; x > 0; x--) {
			for (int y = height - 1; y > 0; y--) {
				cells[x][y] = originalImage.getRGB(rng.nextInt(x + 1), rng.nextInt(y + 1));
			}
		}
	
	}

	//Calculate affinity of whole grid by multiplying local affinities 
	private void calculateTotalAffinity(){
		totalAffinity = 0.0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				totalAffinity += calculateLocalAffinity(x, y);
			}
		}
	}

	private double calculateLocalAffinity(int x, int y) {
		
		double affinity =  Math.abs(cells[x][y] - originalImage.getRGB(x, y));

		//Since we want cells to be similar to those around it, Affinity of a cell will be 1 over it's cumulative difference to it's neighbors 
		//(Adding one to divisor to avoid dividing by zero)
		return 1 / (affinity + 1);
	
		/*
		//Check above and below for bounds
		if (y < height - 1)
			affinity += Math.abs(cells[x][y] - cells[x][y + 1]);
		if (y > 0)
			affinity += Math.abs(cells[x][y] - cells[x][y - 1]);

		//Check right x for bound
		if (x < width - 1){
			affinity += Math.abs(cells[x][y] - cells[x + 1][y]);
			if (y < height - 1)
				affinity += Math.abs(cells[x][y] - cells[x + 1][y + 1]);
			if (y > 0)
				affinity += Math.abs(cells[x][y] - cells[x + 1][y - 1]);
		}

		//Check left x for bound
		if (x > 0) {
			affinity += Math.abs(cells[x][y] - cells[x - 1][y]);
		
			if (y < height - 1)
				affinity += Math.abs(cells[x][y] - cells[x - 1][y + 1]);
			if (y > 0)
				affinity += Math.abs(cells[x][y]- cells[x - 1][y - 1]);
		}
		 */

	}

	public void printGrid() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				System.out.print(cells[x][y] + " ");
			}
			System.out.println();
		}
	}

	public int getHeight() { 
		return height; 
	}
	public int getWidth() {
		return width;
	}
	
	public int getPopulation() { 
		return population; 
	}

	public int[][] getCells() { 
		return cells; 
	}

	public double getTotalAffinity() { 
		return totalAffinity; 
	}

	public double getOptimumAffinity() {
		return optimumAffinity;
	}
	
	private BufferedImage getOriginalImage() {
		return originalImage;
	}

	@Override
	public int compareTo(Grid grid) {
		if (this.getTotalAffinity() > grid.getTotalAffinity())
			return -1;
		else if (this.getTotalAffinity() < grid.getTotalAffinity())
			return 1;
		else
			return 0;
	}

}
