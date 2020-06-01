package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Species {

	private Phenotype best;

	private float highestFitness = Float.MIN_VALUE;

	private final List<Phenotype> phenotypes = new ArrayList<>();

	private int staleness = 0;

	@Contract(mutates = "this")
	public void add(@NotNull Phenotype phenotype) {
		this.phenotypes.add(phenotype);
	}

	@Contract(mutates = "this")
	public void sort() {
		phenotypes.sort(Comparator.naturalOrder());
	}

	@Contract(mutates = "this")
	public void updateBest() {
		final Phenotype currentBest = getCurrentBest();

		if (currentBest.compareTo(this.best) > 0) {

			staleness = 0;

			best = currentBest;

			highestFitness = currentBest.getFitness();

		} else {
			++staleness;
		}
	}

	@NotNull
	@Contract(pure = true)
	public Phenotype getCurrentBest() {
		return phenotypes.get(0);
	}

	@Contract(mutates = "this")
	public void clear() {
		phenotypes.clear();
	}

	@NotNull
	@Contract(pure = true)
	public List<Phenotype> getPhenotypes() {
		return phenotypes;
	}
}
