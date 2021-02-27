package com.danielbulger.neat.mate;

import com.danielbulger.neat.Genome;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class CloneMate implements Mate {

	@Override
	@Contract(pure = true)
	public @NotNull Genome mate(@NotNull Genome mother, @NotNull Genome father) {
		final Genome parent = ThreadLocalRandom.current().nextBoolean() ? mother : father;
		return new Genome(parent);
	}
}
