package com.danielbulger.neat.mate;

import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Phenotype;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class CloneMate implements Mate {

	@Override
	@Contract(pure = true)
	public @NotNull Genome mate(@NotNull Phenotype mother, @NotNull Phenotype father) {

		final Phenotype parent = ThreadLocalRandom.current().nextBoolean() ? mother : father;

		return new Genome(parent.getGenome());
	}
}
