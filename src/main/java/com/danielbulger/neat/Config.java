package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Properties;

public class Config {

	private int numInputs;
	private int numOutputs;
	private int populationSize;
	private int staleThreshold;
	private float mutateWeightChance;
	private float mutateAddConnectionChance;
	private float mutateAddNodeChance;
	private float cloneMateChance;
	private float crossoverMateChance;
	private float crossoverDisableConnectionChance;

	public Config(final @NotNull Properties properties) {
		initialise(Objects.requireNonNull(properties));
	}

	private void initialise(final Properties properties) {
		numInputs = getInt(properties, "genome.input-nodes");
		numOutputs = getInt(properties, "genome.output-nodes");
		populationSize = getInt(properties, "population.initial-size");
		staleThreshold = getInt(properties, "species.stale-threshold");
		mutateWeightChance = getFloat(properties, "mutation.change-weights-chance");
		mutateAddConnectionChance = getFloat(properties, "mutation.add-connection-chance");
		mutateAddNodeChance = getFloat(properties, "mutation.add-node-chance");
		cloneMateChance = getFloat(properties, "mate.clone-chance");
		crossoverMateChance = getFloat(properties, "mate.crossover-chance");
		crossoverDisableConnectionChance = getFloat(properties, "mate.crossover.disable-connection-chance");
	}

	@Contract(pure = true)
	private String getString(final Properties properties, final @NotNull String key) {
		final String value = properties.getProperty(key);

		if (value == null) {
			throw new IllegalArgumentException(String.format("No property %s found", key));
		}

		return value;
	}

	@Contract(pure = true)
	private float getFloat(final Properties properties, final @NotNull String key) {
		return Float.parseFloat(getString(properties, key));
	}

	@Contract(pure = true)
	private int getInt(final Properties properties, final @NotNull String key) {
		return Integer.parseInt(getString(properties, key));
	}

	public int getNumInputs() {
		return numInputs;
	}

	public int getNumOutputs() {
		return numOutputs;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getStaleThreshold() {
		return staleThreshold;
	}

	public float getMutateWeightChance() {
		return mutateWeightChance;
	}

	public float getMutateAddConnectionChance() {
		return mutateAddConnectionChance;
	}

	public float getMutateAddNodeChance() {
		return mutateAddNodeChance;
	}

	public float getCloneMateChance() {
		return cloneMateChance;
	}

	public float getCrossoverMateChance() {
		return crossoverMateChance;
	}

	public float getCrossoverDisableConnectionChance() {
		return crossoverDisableConnectionChance;
	}
}
