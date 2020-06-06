package com.danielbulger.neat.select;

import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Species;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Select {

	@NotNull
	@Contract(pure = true)
	Genome select(final @NotNull Species species);
}
