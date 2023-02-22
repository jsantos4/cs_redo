package com.cs_redo.colors_n_squares;

import java.util.concurrent.ThreadLocalRandom;

class Grid implements Comparable<Grid> {

	private int[][] cells;
	private int rows, columns, population, affinityBound;
	private double totalAffinity, optimumAffinity;
	private ThreadLocalRandom rng = ThreadLocalRandom.current();

	public Grid(int rows, int columns, int cellMax){
		this.rows = rows;
		this.columns = columns;
		affinityBound = cellMax;
		population = rows * columns;
		optimumAffinity = population * 2;

		init();
		calculateTotalAffinity();
	}

	public Grid(Grid g){
		this.rows = g.getRows();
		this.columns = g.getColumns();
		this.population = g.getPopulation();
		this.cells = g.getCells();
		this.optimumAffinity = g.getOptimumAffinity();
		this.totalAffinity = g.getTotalAffinity();
		this.affinityBound = g.getAffinityBound();
		calculateTotalAffinity();

	}


	public Grid(Grid parent1, Grid parent2, double mutationRate) {

		this.cells = new int[parent1.getRows()][parent1.getColumns()];
		this.rows = parent1.getRows();
		this.columns = parent1.getColumns();
		this.affinityBound = parent1.getAffinityBound();
		this.optimumAffinity = parent1.getOptimumAffinity();

		this.population = rows * columns;

		for (int row = 0; row < rows; row++){
			for (int column = 0; column < columns; column++) {	
				if (rng.nextDouble() < mutationRate) {
					this.cells[row][column] = rng.nextInt(affinityBound);
				} else {
					if (parent1.calculateLocalAffinity(row, column) > parent2.calculateLocalAffinity(row, column))
						this.cells[row][column] = parent1.getCells()[row][column];
					else
						this.cells[row][column] = parent2.getCells()[row][column];
				}
			}
		}

		calculateTotalAffinity();
	}

	//Initialize grid
	private void init() {
		cells = new int[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				cells[row][column] = rng.nextInt(affinityBound);
			}
		}
	}

	//Calculate affinity of whole grid by multiplying local affinities 
	private void calculateTotalAffinity(){
		totalAffinity = 0.0;
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				totalAffinity += calculateLocalAffinity(row, column);
			}
		}
	}

	private double calculateLocalAffinity(int row, int column) {
		double affinity = 0.0;
	
		//Check above and below for bounds
		if (row < rows - 1)
			affinity += Math.abs(cells[row][column] - cells[row + 1][column]);
		if (row > 0)
			affinity += Math.abs(cells[row][column] - cells[row - 1][column]);

		//Check right column for bound
		if (column < columns - 1){
			affinity += Math.abs(cells[row][column] - cells[row][column + 1]);
			if (row < rows - 1)
				affinity += Math.abs(cells[row][column] - cells[row + 1][column + 1]);
			if (row > 0)
				affinity += Math.abs(cells[row][column] - cells[row - 1][column + 1]);
		}

		//Check left column for bound
		if (column > 0) {
			affinity += Math.abs(cells[row][column] - cells[row][column - 1]);
		
			if (row < rows - 1)
				affinity += Math.abs(cells[row][column] - cells[row + 1][column - 1]);
			if (row > 0)
				affinity += Math.abs(cells[row][column]- cells[row - 1][column - 1]);
		}

		//Since we want cells to be similar to those around it, Affinity of a cell will be 1 over it's cumulative difference to it's neighbors 
		//(Adding one to divisor to avoid dividing by zero)
		return 1 / (affinity + 1);
	}

	public void printGrid() {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				System.out.print(cells[row][column] + " ");
			}
			System.out.println();
		}
	}

	public int getRows() { 
		return rows; 
	}
	public int getColumns() {
		return columns;
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

	public int getAffinityBound() {
		return affinityBound;
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
