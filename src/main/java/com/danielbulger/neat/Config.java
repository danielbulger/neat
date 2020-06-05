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

	public Config(final @NotNull Properties properties) {
		initialise(Objects.requireNonNull(properties));
	}

	private void initialise(final Properties properties) {

		numInputs = getInt(properties, "genome.input-nodes");

		numOutputs = getInt(properties, "genome.output-nodes");

		populationSize = getInt(properties, "population.initial-size");

		staleThreshold = getInt(properties, "species.stale-threshold");
	}

	@Contract(pure = true)
	private int getInt(final Properties properties, final @NotNull String key) {

		final String value = properties.getProperty(key);

		if (value == null) {
			throw new IllegalArgumentException(String.format("No property %s found", key));
		}

		return Integer.parseInt(properties.getProperty(key));
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
}
