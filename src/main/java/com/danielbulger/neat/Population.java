package com.danielbulger.neat;

import com.danielbulger.neat.evaluate.SpeciesClassifier;
import com.danielbulger.neat.util.Random;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Population {

	private static final Logger LOG = LoggerFactory.getLogger(Population.class);

	private final Config config;
	private final Evolution evolution;
	private final List<Species> species = new ArrayList<>();
	private final List<Genome> genomes = new ArrayList<>();

	public Population(
		final @NotNull Config config,
		final @NotNull Evolution evolution
	) {
		this.config = Objects.requireNonNull(config);
		this.evolution = Objects.requireNonNull(evolution);
	}

	public void populate(int size) {

		final int initSize = genomes.size();

		for (int i = initSize; i < size; ++i) {
			genomes.add(new Genome(config.getNumInputs(), config.getNumOutputs()));
		}
	}

	public void makeNextGeneration() {

		update();

		if (species.isEmpty()) {
			throw new IllegalStateException();
		}

		final double sum = getTotalFitness();

		final List<Genome> children = new ArrayList<>();
		for (final Species sp : species) {

			// Retain the best of the Species without change.
			children.add(sp.getCurrentBest());

			final int numChildren = getNumSpeciesBreeds(sum, sp) - 1;

			for (int i = 0; i < numChildren; ++i) {
				children.add(makeChild(sp));
			}
		}

		// If there is any more room for children
		// populate the remaining space from a random
		// selection
		while (children.size() < genomes.size()) {
			final Optional<Species> speciesOptional = Random.fromList(species);

			if (speciesOptional.isEmpty()) {
				continue;
			}

			children.add(makeChild(speciesOptional.get()));
		}

		// Replace all the parents with the newest generation.
		genomes.clear();
		genomes.addAll(children);
	}

	@Contract(pure = true)
	private Genome makeChild(final Species species) {

		final Genome mother = evolution.getGenomeSelect().select(species);
		final Genome father = evolution.getGenomeSelect().select(species);
		final Genome child = evolution.getMateStrategy().mate(mother, father);
		evolution.mutate(child);

		return child;
	}

	private void update() {
		updateSpecies();
	}

	@NotNull
	private Species classify(final @NotNull Genome genome) {

		final SpeciesClassifier classifier = evolution.getSpeciesClassifier();

		for (final Species s : species) {
			if (classifier.isWithinSpecies(s, genome)) {
				return s;
			}
		}

		final Species child = new Species();
		species.add(child);
		return child;
	}

	@NotNull
	public Genome getBest() {

		Genome best = null;

		for (final Species s : species) {
			for (final Genome genome : s.getGenomes()) {
				if (best == null || genome.compareTo(best) > 0) {
					best = genome;
				}
			}
		}

		if (best == null) {
			throw new IllegalStateException("Population has no best Genome");
		}

		return best;
	}

	private void speciate() {

		species.forEach(Species::clear);

		for (final Genome genome : genomes) {
			final Species sp = classify(genome);

			sp.add(genome);
		}

		final Iterator<Species> it = species.iterator();

		while (it.hasNext()) {
			final Species sp = it.next();

			if (sp.isEmpty()) {
				it.remove();
				continue;
			}

			sp.updateBest();
		}

		// Rank the species by fitness in descending order.
		species.sort(Comparator.reverseOrder());
	}

	private void updateSpecies() {

		speciate();

		final Iterator<Species> it = species.iterator();

		final double sum = getTotalFitness();

		// Skip the best species.
		it.next();

		while (it.hasNext()) {
			final Species sp = it.next();

			if (sp.getStaleness() >= config.getStaleThreshold()) {
				LOG.debug("Species was removed due to staleness({})", sp.getStaleness());
				it.remove();
				continue;
			}

			sp.cull();

			final int numBreeds = getNumSpeciesBreeds(sum, sp);

			if (numBreeds < 1) {
				LOG.debug("Species was removed due to not enough breeds({})", numBreeds);
				it.remove();
			}
		}
	}

	@Contract(pure = true)
	private int getNumSpeciesBreeds(double totalFitness, Species species) {
		return (int) Math.floor(species.getAverageFitness() / totalFitness * genomes.size());
	}

	private double getTotalFitness() {
		return species.stream()
			.mapToDouble(Species::getAverageFitness)
			.sum();
	}

	public List<Genome> getGenomes() {
		// The caller should not mutated the populate state directly.
		return genomes;
	}
}
