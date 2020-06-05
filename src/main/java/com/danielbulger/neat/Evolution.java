package com.danielbulger.neat;

import com.danielbulger.neat.evaluate.GenomeFitnessEvaluator;
import com.danielbulger.neat.evaluate.SpeciesClassifier;
import com.danielbulger.neat.mate.Mate;
import com.danielbulger.neat.mutation.Mutation;
import com.danielbulger.neat.select.Select;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Evolution {

	private final Config config;

	private final GenomeFitnessEvaluator genomeFitnessEvaluator;

	private final SpeciesClassifier speciesClassifier;

	private final Select phenotypeSelect;

	private final Map<Mate, Float> mateStrategy = new HashMap<>();

	private final Map<Mutation, Float> mutationStrategy = new HashMap<>();

	private final Population population;

	public Evolution(
		final @NotNull Properties properties,
		final @NotNull SpeciesClassifier speciesClassifier,
		final @NotNull GenomeFitnessEvaluator genomeFitnessEvaluator,
		final @NotNull Select phenotypeSelect
	) {
		this.config = new Config(properties);

		this.phenotypeSelect = Objects.requireNonNull(phenotypeSelect);

		this.genomeFitnessEvaluator = Objects.requireNonNull(genomeFitnessEvaluator);

		this.speciesClassifier = Objects.requireNonNull(speciesClassifier);

		this.population = new Population(
			config,
			this
		);
	}

	@Contract(mutates = "param1")
	public void mutate(final Genome genome) {

		for (final Map.Entry<Mutation, Float> entry : mutationStrategy.entrySet()) {

			final float chance = ThreadLocalRandom.current().nextFloat();

			if(chance < entry.getValue()) {

				entry.getKey().mutate(genome);

			}
		}
	}

	public Mate getMateStrategy() {
		final float chance = ThreadLocalRandom.current().nextFloat();

		for (final Map.Entry<Mate, Float> entry : mateStrategy.entrySet()) {

			if (chance < entry.getValue()) {

				return entry.getKey();
			}

		}

		throw new IllegalStateException("Unable to choose mate strategy");
	}

	public Select getPhenotypeSelect() {
		return phenotypeSelect;
	}

	public GenomeFitnessEvaluator getGenomeFitnessEvaluator() {
		return genomeFitnessEvaluator;
	}

	public SpeciesClassifier getSpeciesClassifier() {
		return speciesClassifier;
	}
}
