package com.danielbulger.neat;

import org.jetbrains.annotations.NotNull;

public class Phenotype implements Comparable<Phenotype> {

	private Genome genome;

	private float fitness;

	public Genome getGenome() {
		return genome;
	}

	public void setGenome(Genome genome) {
		this.genome = genome;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	@Override
	public int compareTo(@NotNull Phenotype o) {
		return Float.compare(fitness, o.fitness);
	}
}
