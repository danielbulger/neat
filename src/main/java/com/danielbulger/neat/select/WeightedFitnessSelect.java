package com.danielbulger.neat.select;

import com.danielbulger.neat.Phenotype;
import com.danielbulger.neat.Species;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class WeightedFitnessSelect implements Select {

	@Override
	@Contract(pure = true)
	public @NotNull Phenotype select(@NotNull Species species) {

		final float fitness = (float) ThreadLocalRandom.current().nextDouble(0, species.getTotalFitness());

		float sum = 0;

		for (final Phenotype phenotype : species.getPhenotypes()) {

			sum += phenotype.getFitness();

			if (sum > fitness) {

				return phenotype;

			}

		}

		throw new IllegalStateException("Unable to select Phenotype from " + species);
	}
}
