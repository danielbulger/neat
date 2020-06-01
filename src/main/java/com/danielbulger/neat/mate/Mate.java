package com.danielbulger.neat.mate;

import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Phenotype;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Mate {

	@NotNull
	@Contract(pure = true)
	Genome mate(@NotNull Phenotype mother, @NotNull Phenotype father);
}
