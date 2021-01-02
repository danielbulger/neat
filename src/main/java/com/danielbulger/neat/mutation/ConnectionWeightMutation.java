package com.danielbulger.neat.mutation;

import com.danielbulger.neat.Connection;
import com.danielbulger.neat.Genome;
import com.danielbulger.neat.util.Random;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class ConnectionWeightMutation implements Mutation {

	@Override
	public void mutate(@NotNull Genome genome) {

		final Optional<Connection> optionalConnection = Random.fromList(genome.getActiveConnections());

		if(optionalConnection.isEmpty()) {
			return;
		}

		final Connection connection = optionalConnection.get();

		connection.setWeight(ThreadLocalRandom.current().nextFloat());
	}
}
