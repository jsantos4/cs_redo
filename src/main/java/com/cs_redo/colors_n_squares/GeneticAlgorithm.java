package com.cs_redo.colors_n_squares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

//import java.util.concurrent.Exchanger;

class GeneticAlgorithm implements Runnable {

	//private Exchanger<Grid> exchanger = new Exchanger<>();
	private int scale, generationCount;
	private double mutationRate, optimumAffinity, summedAffinities;
	private Grid best;
	private ArrayList<Grid> generation;
	private ThreadLocalRandom rng = ThreadLocalRandom.current();

	public GeneticAlgorithm(int scale, Double mutationRate) {
		this.mutationRate = mutationRate;
		this.scale = scale;
		generation = new ArrayList<>();
		generationCount = 0;
		init();
	}

	private void init() {
		for (int i = 0; i < scale; i++) {
			generation.add(new Grid(scale, scale, 256));
			summedAffinities += generation.get(i).getTotalAffinity();
		}
		Collections.sort(generation);
		best = generation.get(0);
		optimumAffinity = best.getOptimumAffinity();
	}

	private void breed() {
		Grid offspring;

		for (int i = 0; i < scale; i++) {
			try {
				offspring = crossover(selectParent(), selectParent());
				generation.add(offspring);
			} catch (NullPointerException e) {
				System.out.println("PARENT SELECTION FAILED");
			}
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

		while (best.getTotalAffinity() < optimumAffinity * 0.85 && generationCount < 100000) {
			
			breed();
			generationCount++;
			if (generationCount % 1000 == 0)
				System.out.println("Generation: " + generationCount + "\n Best grid: " + best.getTotalAffinity());
		}
		
		System.out.println("Generation: " + generationCount + "\n Best grid: " + best.getTotalAffinity());
		best.printGrid();
	}
}