package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Species implements Comparable<Species> {

	private Genome best;

	private float highestFitness = Float.MIN_VALUE;
	private final List<Genome> genomes = new ArrayList<>();
	private int staleness = 0;

	public void add(@NotNull Genome genome) {
		this.genomes.add(genome);
	}

	private void sort() {
		genomes.sort(Comparator.reverseOrder());
	}

	public void updateBest() {

		this.sort();

		final Genome currentBest = getCurrentBest();

		if (best == null || currentBest.compareTo(this.best) > 0) {
			staleness = 0;
			best = currentBest;
			highestFitness = currentBest.getFitness();
		} else {
			++staleness;
		}
	}

	public void cull() {

		if (genomes.size() < 2) {
			return;
		}

		for (int start = genomes.size() / 2, end = genomes.size() - 1; end > start; --end) {
			genomes.remove(end);
		}
	}

	@Contract(pure = true)
	public float getTotalFitness() {
		return (float) genomes.stream().mapToDouble(Genome::getFitness).sum();
	}

	@Contract(pure = true)
	public float getAverageFitness() {
		return getTotalFitness() / genomes.size();
	}

	public int getStaleness() {
		return staleness;
	}

	@NotNull
	@Contract(pure = true)
	public Genome getCurrentBest() {
		return genomes.get(0);
	}

	public void clear() {
		genomes.clear();
	}

	@NotNull
	@Contract(pure = true)
	public List<Genome> getGenomes() {
		return genomes;
	}

	@Contract(pure = true)
	public boolean isEmpty() {
		return genomes.isEmpty();
	}

	@Override
	public int compareTo(@NotNull Species o) {
		return Float.compare(highestFitness, o.highestFitness);
	}
}
