package com.danielbulger.neat.evaluate;

import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Species;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface SpeciesEvaluate {

	@Contract(pure = true)
	boolean isWithinSpecies(@NotNull Species species, @NotNull Genome genome);
}
