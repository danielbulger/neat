package com.danielbulger.neat.mate;

import com.danielbulger.neat.Connection;
import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Innovation;
import com.danielbulger.neat.Phenotype;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public class CrossoverMate implements Mate {

	private final float connectionDisableChance;

	public CrossoverMate(float connectionDisableChance) {
		this.connectionDisableChance = connectionDisableChance;
	}

	@Contract(pure = true)
	private Genome crossover(@NotNull Phenotype best, @NotNull Phenotype other) {

		final ThreadLocalRandom random = ThreadLocalRandom.current();

		final Genome genome = new Genome();

		final Map<Innovation, Connection> bestConnections = best.getGenome().getConnections();

		final Map<Innovation, Connection> otherConnections = other.getGenome().getConnections();

		final Set<Innovation> innovations = new TreeSet<>(bestConnections.keySet());
		innovations.addAll(otherConnections.keySet());

		for (final Innovation innovation : innovations) {

			Connection connection = null;

			// If they occur in both genomes just randomly choose a parent to take from.
			if (bestConnections.containsKey(innovation) && otherConnections.containsKey(innovation)) {

				connection = new Connection(
					(random.nextBoolean() ? bestConnections : otherConnections).get(innovation)
				);

				// If one has disabled the connection we need to randomly choose
				// if the connection should be enabled or disabled.
				if (bestConnections.get(innovation).isEnabled() != otherConnections.get(innovation).isEnabled()) {
					connection.setEnabled(random.nextFloat() >= connectionDisableChance);
				}

			} else if (best.compareTo(other) == 0) {
				// If they are both equally fit then we include both excess/disjoint genes
				connection = bestConnections.containsKey(innovation) ?

					bestConnections.get(innovation) : otherConnections.get(innovation);

			} else {

				// Otherwise we only include the ones from the fittest parent.
				if (bestConnections.containsKey(innovation)) {
					connection = bestConnections.get(innovation);
				}

			}

			if (connection != null) {

				genome.addConnection(connection);
			}
		}

		return genome;
	}

	@Override
	@Contract(pure = true)
	public @NotNull Genome mate(@NotNull Phenotype mother, @NotNull Phenotype father) {

		if (mother.compareTo(father) > 0) {

			return crossover(mother, father);
		} else {

			return crossover(father, mother);
		}
	}
}
