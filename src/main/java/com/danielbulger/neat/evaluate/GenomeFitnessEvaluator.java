package com.danielbulger.neat.evaluate;

import com.danielbulger.neat.Genome;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface GenomeFitnessEvaluator {

	@Contract(pure = true)
	float evaluate(@NotNull Genome genome);
}