package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Species implements Comparable<Species> {

	private Phenotype best;

	private float highestFitness = Float.MIN_VALUE;

	private final List<Phenotype> phenotypes = new ArrayList<>();

	private int staleness = 0;

	public void add(@NotNull Phenotype phenotype) {
		this.phenotypes.add(phenotype);
	}

	private void sort() {
		phenotypes.sort(Comparator.reverseOrder());
	}

	public void updateBest() {

		this.sort();

		final Phenotype currentBest = getCurrentBest();

		if (currentBest.compareTo(this.best) > 0) {

			staleness = 0;

			best = currentBest;

			highestFitness = currentBest.getFitness();

		} else {
			++staleness;
		}
	}

	public void cull() {

		if (phenotypes.size() < 2) {
			return;
		}

		for (int start = phenotypes.size() / 2, end = phenotypes.size() - 1; end > start; --end) {
			phenotypes.remove(end);
		}
	}

	@Contract(pure = true)
	public float getTotalFitness() {
		return (float) phenotypes.stream().mapToDouble(Phenotype::getFitness).sum();
	}

	@Contract(pure = true)
	public float getAverageFitness() {

		return getTotalFitness() / phenotypes.size();
	}

	public int getStaleness() {
		return staleness;
	}

	@NotNull
	@Contract(pure = true)
	public Phenotype getCurrentBest() {
		return phenotypes.get(0);
	}

	public void clear() {
		phenotypes.clear();
	}

	@NotNull
	@Contract(pure = true)
	public List<Phenotype> getPhenotypes() {
		return phenotypes;
	}

	@Contract(pure = true)
	public boolean isEmpty() {
		return phenotypes.isEmpty();
	}

	@Override
	public int compareTo(@NotNull Species o) {
		return Float.compare(highestFitness, o.highestFitness);
	}
}
