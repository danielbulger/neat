package com.danielbulger.neat.mate;

import com.danielbulger.neat.Genome;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Mate {

	@NotNull
	@Contract(pure = true)
	Genome mate(@NotNull Genome mother, @NotNull Genome father);
}
