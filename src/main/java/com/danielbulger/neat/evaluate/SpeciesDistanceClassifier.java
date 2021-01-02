package com.danielbulger.neat.evaluate;

import com.danielbulger.neat.Connection;
import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Innovation;
import com.danielbulger.neat.Species;
import com.danielbulger.neat.util.Random;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NavigableMap;
import java.util.Optional;

public class SpeciesDistanceClassifier implements SpeciesClassifier {

	private final float disjointWeighting;

	private final float excessWeighting;

	private final float weightWeighting;

	private final float threshold;

	public SpeciesDistanceClassifier() {
		this(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public SpeciesDistanceClassifier(
		float disjointWeighting,
		float excessWeighting,
		float weightWeighting,
		float threshold
	) {
		this.disjointWeighting = disjointWeighting;
		this.excessWeighting = excessWeighting;
		this.weightWeighting = weightWeighting;
		this.threshold = threshold;
	}

	@Override
	public boolean isWithinSpecies(@NotNull Species species, @NotNull Genome genome) {

		final int factor = 1;

		final Optional<Genome> optionalGenome = Random.fromList(species.getGenomes());

		if (optionalGenome.isEmpty()) {
			return false;
		}

		final Genome rep = optionalGenome.get();

		final float excess = (excessWeighting * getExcess(rep, genome)) / factor;

		final float disjoint = (disjointWeighting * getDisjoint(rep, genome)) / factor;

		final float weight = weightWeighting * getAverageWeightDifference(rep, genome);

		final float distance = excess + disjoint + weight;

		return distance < threshold;
	}

	@Contract(pure = true)
	private int getExcess(@NotNull Genome representative, @NotNull Genome genome) {

		Innovation last = representative.getConnections().lastKey();

		int size = 0;

		final NavigableMap<Innovation, Connection> innovations = genome.getConnections();

		// Keep moving up the set until we run out of innovations
		// that are higher than the representative innovations.
		while ((last = innovations.higherKey(last)) != null) {
			++size;
		}

		return size;
	}

	@Contract(pure = true)
	private int getDisjoint(@NotNull Genome representative, @NotNull Genome genome) {

		final NavigableMap<Innovation, Connection> repSet = representative.getConnections();

		final NavigableMap<Innovation, Connection> genomeSet = genome.getConnections();

		int size = 0;

		for (final Innovation innovation : repSet.keySet()) {

			if (!genomeSet.containsKey(innovation)) {
				++size;
			}
		}

		final Innovation last = repSet.lastKey();

		for (final Innovation innovation : genomeSet.keySet()) {

			if (!repSet.containsKey(innovation)) {

				// If the innovation is higher than the largest in the other set
				// the it is classified as an excess not an disjoint.
				if (innovation.compareTo(last) <= 0) {
					++size;
				}
			}
		}

		return size;
	}

	@Contract(pure = true)
	private float getAverageWeightDifference(@NotNull Genome representative, @NotNull Genome genome) {

		int count = 0;

		float sum = 0;

		for (final Connection c1 : representative.getConnections().values()) {

			for (final Connection c2 : genome.getConnections().values()) {

				if (c1.equals(c2)) {

					sum += Math.abs(c1.getWeight() - c2.getWeight());

					count += 1;
				}

			}

		}

		if (count == 0) {
			return 0;
		}

		return sum / count;
	}
}
