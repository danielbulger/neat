package com.danielbulger.neat.mutation;

import com.danielbulger.neat.Genome;
import org.jetbrains.annotations.NotNull;

public interface Mutation {

	void mutate(@NotNull Genome genome);
}
