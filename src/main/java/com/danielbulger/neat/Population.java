package com.danielbulger.neat;

import com.danielbulger.neat.evaluate.SpeciesClassifier;
import com.danielbulger.neat.mate.Mate;
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

	private final List<Phenotype> phenotypes = new ArrayList<>();

	private int generationNum = 0;

	public Population(
		final @NotNull Config config,
		final @NotNull Evolution evolution
	) {
		this.config = Objects.requireNonNull(config);

		this.evolution = Objects.requireNonNull(evolution);
	}

	public void populate(int size) {

		for (int i = phenotypes.size(); i < size; ++i) {

			final Phenotype phenotype = new Phenotype(
				new Genome(config.getNumInputs(), config.getNumOutputs())
			);

			phenotypes.add(phenotype);
		}
	}

	private void updatePhenotypeFitness() {

		for (final Phenotype phenotype : phenotypes) {

			final float fitness = evolution.getGenomeFitnessEvaluator().evaluate(phenotype.getGenome());

			phenotype.setFitness(fitness);
		}
	}

	public void makeNextGeneration() {

		update();

		if (species.isEmpty()) {
			throw new IllegalStateException();
		}

		final double sum = getTotalFitness();

		final List<Phenotype> children = new ArrayList<>();

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
		while (children.size() < phenotypes.size()) {

			final Optional<Species> speciesOptional = Random.fromList(species);

			if (!speciesOptional.isPresent()) {
				continue;
			}

			children.add(makeChild(speciesOptional.get()));
		}

		// Replace all the parents with the newest generation.
		phenotypes.clear();

		phenotypes.addAll(children);
	}

	@Contract(pure = true)
	private Phenotype makeChild(final Species species) {

		final Phenotype mother = evolution.getPhenotypeSelect().select(species);

		final Phenotype father = evolution.getPhenotypeSelect().select(species);

		final Mate mate = evolution.getMateStrategy();

		final Genome child = mate.mate(mother, father);

		evolution.mutate(child);

		return new Phenotype(child);
	}

	private void update() {
		++generationNum;

		updatePhenotypeFitness();

		updateSpecies();
	}

	@NotNull
	private Species classify(final @NotNull Phenotype phenotype) {

		final SpeciesClassifier classifier = evolution.getSpeciesClassifier();

		for (final Species s : species) {
			if (classifier.isWithinSpecies(s, phenotype.getGenome())) {
				return s;
			}
		}

		final Species child = new Species();
		species.add(child);
		return child;
	}

	@NotNull
	public Phenotype getBest() {

		Phenotype best = null;

		for (final Species s : species) {

			for (final Phenotype phenotype : s.getPhenotypes()) {
				if (best == null || phenotype.compareTo(best) > 0) {
					best = phenotype;
				}
			}
		}

		if (best == null) {
			throw new IllegalStateException("Population has no best phenotype");
		}

		return best;
	}

	private void speciate() {

		species.forEach(Species::clear);

		for (final Phenotype phenotype : phenotypes) {
			final Species species = classify(phenotype);

			species.add(phenotype);
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

		while (it.hasNext()) {
			final Species sp = it.next();

			if (sp.getStaleness() >= config.getStaleThreshold()) {
				LOG.info("Species was removed due to staleness({})", sp.getStaleness());
				it.remove();
				continue;
			}

			sp.cull();

			final int numBreeds = getNumSpeciesBreeds(sum, sp);

			if (numBreeds < 1) {
				LOG.info("Species was removed due to not enough breeds({})", numBreeds);
				it.remove();
			}
		}
	}

	@Contract(pure = true)
	private int getNumSpeciesBreeds(double totalFitness, Species species) {
		return (int) Math.floor(species.getAverageFitness() / totalFitness * phenotypes.size());
	}

	private double getTotalFitness() {
		return species.stream()
			.mapToDouble(Species::getAverageFitness)
			.sum();
	}
}
