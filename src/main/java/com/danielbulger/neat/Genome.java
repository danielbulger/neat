package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Genome implements Comparable<Genome> {

	private final NavigableMap<Integer, Node> nodes = new TreeMap<>();

	private final EnumMap<NodeType, List<Node>> nodeTypes = new EnumMap<>(NodeType.class);

	private final NavigableMap<Innovation, Connection> connections = new TreeMap<>();

	private float fitness;

	public Genome() {
	}

	public Genome(Genome parent) {

		for (final Node node : parent.nodes.values()) {
			addNode(new Node(node));
		}

		// Make sure we copy the node/connections so updating the value doesn't affect the
		// referenced parent node.
		for (final Map.Entry<Innovation, Connection> entry : parent.connections.entrySet()) {

			final Connection connection = entry.getValue();

			final Node from = nodes.get(connection.getFrom().getId());

			final Node to = nodes.get(connection.getTo().getId());

			addConnection(new Connection(from, to, connection.getWeight(), entry.getKey(), connection.isEnabled()));
		}

		fitness = parent.fitness;
	}

	public Genome(int numInputs, int numOutputs) {
		if (numInputs <= 0) {
			throw new IllegalArgumentException("must have at least 1 input");
		}

		if (numOutputs <= 0) {
			throw new IllegalArgumentException("must have at least 1 output");
		}

		this.initialiseNodes(numInputs, numOutputs);
	}

	public float[] feedForward(float[] values) {
		if (values == null) {
			throw new NullPointerException();
		}

		final List<Node> inputNodes = nodeTypes.get(NodeType.INPUT);

		if (values.length != inputNodes.size()) {
			throw new IllegalArgumentException();
		}

		// Clear the previous network state.
		for (final Node node : nodes.values()) {
			node.reset();
		}

		for (int i = 0; i < inputNodes.size(); ++i) {
			inputNodes.get(i).setValue(values[i]);
		}

		for (final Node node : inputNodes) {
			node.process();
		}

		final List<Node> outputNodes = nodeTypes.get(NodeType.OUTPUT);

		final float[] output = new float[outputNodes.size()];

		for (int i = 0; i < output.length; ++i) {
			output[i] = outputNodes.get(i).getValue();
		}

		return output;
	}

	private void initialiseNodes(int numInputs, int numOutputs) {

		for (int i = 0; i < numInputs; ++i) {
			addNode(Node.create(NodeType.INPUT));
		}

		for (int i = 0; i < numOutputs; ++i) {
			addNode(Node.create(NodeType.OUTPUT));
		}

		final Collection<Node> inputNodes = nodeTypes.get(NodeType.INPUT);

		final Collection<Node> outputNodes = nodeTypes.get(NodeType.OUTPUT);

		for (final Node input : inputNodes) {

			for (final Node output : outputNodes) {

				this.addConnection(Connection.create(
					input, output
				));
			}
		}
	}

	@Contract(pure = true)
	public boolean isConnected(Node from, Node to) {

		for (final Connection connection : connections.values()) {

			if (connection.getTo().equals(to) && connection.getFrom().equals(from)) {
				return true;
			}
		}

		return false;
	}

	public void addNode(final @NotNull Node node) {
		if (nodes.containsKey(node.getId())) {
			throw new IllegalArgumentException();
		}

		nodes.put(node.getId(), node);

		List<Node> nodes = nodeTypes.get(node.getType());

		if (nodes == null) {
			nodes = new ArrayList<>();
		}

		nodes.add(node);
		nodeTypes.put(node.getType(), nodes);
	}

	public void addConnection(final @NotNull Connection connection) {

		connections.put(connection.getInnovation(), connection);

		if (!nodes.containsKey(connection.getFrom().getId())) {
			addNode(connection.getFrom());
		}

		if (!nodes.containsKey(connection.getTo().getId())) {
			addNode(connection.getTo());
		}

		connection.getFrom().addOutgoingConnection(connection);
		connection.getTo().addIncomingConnection(connection);
	}

	public void addConnections(final @NotNull Connection... elements) {

		for (final Connection connection : elements) {

			this.addConnection(connection);
		}
	}

	@NotNull
	@Contract(pure = true)
	public List<Connection> getActiveConnections() {
		return connections.values()
			.stream()
			.filter(Connection::isEnabled)
			.collect(Collectors.toList());
	}

	@NotNull
	@Contract(pure = true)
	public NavigableMap<Innovation, Connection> getConnections() {
		return connections;
	}

	@NotNull
	@Contract(pure = true)
	public NavigableMap<Integer, Node> getNodes() {
		return nodes;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	@Override
	public int compareTo(@NotNull Genome o) {
		return Float.compare(fitness, o.fitness);
	}

	public int getNumInputs() {
		return nodeTypes.get(NodeType.INPUT).size();
	}

	public int getNumOutputs() {
		return nodeTypes.get(NodeType.OUTPUT).size();
	}

	@Override
	public String toString() {
		return "Genome{" +
			"nodes=" + nodes +
			", connections=" + connections +
			", fitness=" + fitness +
			'}';
	}
}
