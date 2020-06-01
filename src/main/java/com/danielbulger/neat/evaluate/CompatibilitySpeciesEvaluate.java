package com.danielbulger.neat.evaluate;

import com.danielbulger.neat.*;
import com.danielbulger.neat.util.Random;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CompatibilitySpeciesEvaluate implements SpeciesEvaluate {

	private final float disjointWeighting;

	private final float excessWeighting;

	private final float weightWeighting;

	private final float threshold;

	public CompatibilitySpeciesEvaluate(
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

		final Optional<Phenotype> phenotypeOptional = Random.fromList(species.getPhenotypes());

		if (!phenotypeOptional.isPresent()) {
			return false;
		}

		final Genome rep = phenotypeOptional.get().getGenome();

		final float excess = (excessWeighting * getExcess(rep, genome)) / factor;

		final float disjoint = (disjointWeighting * getDisjoint(rep, genome)) / factor;

		final float weight = weightWeighting * getAverageWeightDifference(rep, genome);

		final float distance = excess + disjoint + weight;

		return distance < threshold;
	}

	@Contract(pure = true)
	private int getExcess(@NotNull Genome representative, @NotNull Genome genome) {

		Innovation last = representative.getInnovations().last();

		int size = 0;

		final NavigableSet<Innovation> innovations = genome.getInnovations();

		// Keep moving up the set until we run out of innovations
		// that are higher than the representative innovations.
		while ((last = innovations.higher(last)) != null) {
			++size;
		}

		return size;
	}

	@Contract(pure = true)
	private int getDisjoint(@NotNull Genome representative, @NotNull Genome genome) {

		final NavigableSet<Innovation> repSet = representative.getInnovations();

		final NavigableSet<Innovation> genomeSet = genome.getInnovations();

		int size = 0;

		for (final Innovation innovation : repSet) {

			if (!genomeSet.contains(innovation)) {
				++size;
			}
		}

		final Innovation last = repSet.last();

		for (final Innovation innovation : genomeSet) {

			if (!repSet.contains(innovation)) {

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

		for (final Connection c1 : representative.getConnections()) {

			for (final Connection c2 : genome.getConnections()) {

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
