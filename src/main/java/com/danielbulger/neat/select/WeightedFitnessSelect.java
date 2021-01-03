package com.danielbulger.neat.select;

import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Species;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class WeightedFitnessSelect implements Select {

	@Override
	@Contract(pure = true)
	public @NotNull Genome select(@NotNull Species species) {

		final float totalFitness = species.getTotalFitness();

		if(!(totalFitness > 0f)) {
			return species.getGenomes().get(0);
		}

		final float fitness = (float) ThreadLocalRandom.current().nextDouble(species.getTotalFitness());

		float sum = 0;

		for (final Genome genome : species.getGenomes()) {

			sum += genome.getFitness();

			if (sum >= fitness) {

				return genome;

			}
		}

		throw new IllegalStateException("Unable to select Genome from " + species);
	}
}
