package com.danielbulger.neat.mutation;

import com.danielbulger.neat.Connection;
import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Node;
import com.danielbulger.neat.util.Random;
import org.jetbrains.annotations.NotNull;

import java.util.NavigableMap;
import java.util.Optional;

public class AddConnectionMutation implements Mutation {

	@Override
	public void mutate(@NotNull Genome genome) {

		final NavigableMap<Integer, Node> nodes = genome.getNodes();

		final Optional<Node> optionalFirstNode = Random.fromMap(nodes);

		final Optional<Node> optionalSecondNode = Random.fromMap(nodes);

		if (!optionalFirstNode.isPresent() || !optionalSecondNode.isPresent()) {
			return;
		}

		Node fromNode = optionalFirstNode.get();

		Node toNode = optionalSecondNode.get();

		if(fromNode.equals(toNode)) {
			return;
		}

		// Check if the nodes are the same type and if they are allowed to connect by type.
		if (fromNode.getType() == toNode.getType() && !fromNode.getType().isSameTypeConnectionAllowed()) {
			return;
		}

		// Ensure that they are in the correct ordering so input nodes connect to output nodes etc.
		if (fromNode.getType().getOrder() > toNode.getType().getOrder()) {
			Node temp = fromNode;
			fromNode = toNode;
			toNode = temp;
		}

		if (genome.isConnected(fromNode.getId(), toNode.getId())) {
			return;
		}

		genome.addConnection(Connection.create(fromNode.getId(), toNode.getId()));
	}
}
