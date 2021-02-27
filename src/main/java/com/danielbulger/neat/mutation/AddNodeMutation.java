package com.danielbulger.neat.mutation;

import com.danielbulger.neat.Connection;
import com.danielbulger.neat.Genome;
import com.danielbulger.neat.Node;
import com.danielbulger.neat.NodeType;
import com.danielbulger.neat.util.Random;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AddNodeMutation implements Mutation {

	@Override
	public void mutate(@NotNull Genome genome) {

		final List<Connection> connections = genome.getActiveConnections();
		final Optional<Connection> optionalConnection = Random.fromList(connections);

		if(optionalConnection.isEmpty()) {
			return;
		}

		final Connection oldConnection = optionalConnection.get();
		oldConnection.setEnabled(false);

		final Node node = Node.create(NodeType.HIDDEN);
		genome.addNode(node);

		genome.addConnections(
			// The connection from the existing from node to the new node.
			Connection.create(oldConnection.getFrom(), node),
			// The connection from the new node to the existing to node.
			Connection.create(node, oldConnection.getTo())
		);
	}
}
