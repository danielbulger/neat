package com.danielbulger.neat;

import com.danielbulger.neat.evaluate.GenomeFitnessEvaluator;
import com.danielbulger.neat.evaluate.SpeciesClassifier;
import com.danielbulger.neat.mate.CloneMate;
import com.danielbulger.neat.mate.CrossoverMate;
import com.danielbulger.neat.mate.Mate;
import com.danielbulger.neat.mutation.AddConnectionMutation;
import com.danielbulger.neat.mutation.AddNodeMutation;
import com.danielbulger.neat.mutation.ConnectionWeightMutation;
import com.danielbulger.neat.mutation.Mutation;
import com.danielbulger.neat.select.Select;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Evolution {

	private final Config config;

	private final GenomeFitnessEvaluator genomeFitnessEvaluator;

	private final SpeciesClassifier speciesClassifier;

	private final Select genomeSelect;

	private final Map<Mate, Float> mateStrategy;

	private final Map<Mutation, Float> mutationStrategy = new HashMap<>();

	private final Population population;

	public Evolution(
		final @NotNull Properties properties,
		final @NotNull SpeciesClassifier speciesClassifier,
		final @NotNull GenomeFitnessEvaluator genomeFitnessEvaluator,
		final @NotNull Select genomeSelect
	) {
		this.config = new Config(properties);

		this.genomeSelect = Objects.requireNonNull(genomeSelect);

		this.genomeFitnessEvaluator = Objects.requireNonNull(genomeFitnessEvaluator);

		this.speciesClassifier = Objects.requireNonNull(speciesClassifier);

		this.initialiseMutations(config);

		this.mateStrategy = this.initialiseMates(config);

		this.population = new Population(
			config,
			this
		);

		this.population.populate(config.getPopulationSize());
	}

	public Genome evolve() {
		population.makeNextGeneration();

		return population.getBest();
	}

	private void initialiseMutations(final Config config) {
		mutationStrategy.put(new AddConnectionMutation(), config.getMutateAddConnectionChance());

		mutationStrategy.put(new AddNodeMutation(), config.getMutateAddNodeChance());

		mutationStrategy.put(new ConnectionWeightMutation(), config.getMutateWeightChance());
	}

	private Map<Mate, Float> initialiseMates(final Config config) {

		final Map<Mate, Float> unsorted = new HashMap<>();

		unsorted.put(new CloneMate(), config.getCloneMateChance());

		unsorted.put(
			new CrossoverMate(config.getCrossoverDisableConnectionChance()),

			config.getCrossoverMateChance()
		);

		// Since the ordering of the probability may be important,
		// we sorted them in ascending chance.

		return unsorted.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue())
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(e1, e2) -> e2, HashMap::new
			));
	}

	@Contract(mutates = "param1")
	protected void mutate(final Genome genome) {

		for (final Map.Entry<Mutation, Float> entry : mutationStrategy.entrySet()) {

			final float chance = ThreadLocalRandom.current().nextFloat();

			if (chance < entry.getValue()) {

				entry.getKey().mutate(genome);

			}
		}
	}

	protected Mate getMateStrategy() {
		final float chance = ThreadLocalRandom.current().nextFloat();

		float sum = 0;

		for (final Map.Entry<Mate, Float> entry : mateStrategy.entrySet()) {

			sum += entry.getValue();

			if(sum >= chance) {
				return entry.getKey();
			}

		}

		throw new IllegalStateException("Unable to choose mate strategy");
	}

	public Select getGenomeSelect() {
		return genomeSelect;
	}

	public GenomeFitnessEvaluator getGenomeFitnessEvaluator() {
		return genomeFitnessEvaluator;
	}

	public SpeciesClassifier getSpeciesClassifier() {
		return speciesClassifier;
	}
}
