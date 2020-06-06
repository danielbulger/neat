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

		// Make sure we copy the node/connections so updating the value doesn't affect the
		// referenced parent node.
		for (final Map.Entry<Innovation, Connection> entry : parent.connections.entrySet()) {

			addConnection(new Connection(entry.getValue()));
		}

		fitness = parent.fitness;
	}

	public Genome(int numInputs, int numOutputs) {
		if (numInputs <= 0) {
			throw new IllegalArgumentException();
		}

		if (numOutputs <= 0) {
			throw new IllegalArgumentException();
		}

		this.initialiseNodes(numInputs, numOutputs);
	}

	@NotNull
	public float[] feedForward(@NotNull float[] values) {
		if (values == null) {
			throw new NullPointerException();
		}

		final List<Node> inputNodes = nodeTypes.get(NodeType.INPUT);

		if (values.length != inputNodes.size()) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < inputNodes.size(); ++i) {
			inputNodes.get(i).setValue(values[i]);
		}

		for (final Connection connection : connections.values()) {

			if (!connection.isEnabled()) {
				continue;
			}

			final Node from = nodes.get(connection.getFrom().getId());

			final Node to = nodes.get(connection.getTo().getId());

			if (from == null || to == null) {
				throw new IllegalStateException();
			}

			// TODO: Need to fix so 'from' is completely processed before 'to' is so the value is correct in a multilayer network.
			to.setValue(to.getValue() + connection.getWeight() * from.getValue());
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

		for (final Node input : nodeTypes.get(NodeType.INPUT)) {

			for (final Node output : nodeTypes.get(NodeType.OUTPUT)) {

				this.addConnection(Connection.create(

					input,

					output
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

		final Collection<Node> nodes = nodeTypes.computeIfAbsent(
			node.getType(),
			k -> new ArrayList<>()
		);

		nodes.add(node);
	}

	public void addConnection(final @NotNull Connection connection) {

		connections.put(connection.getInnovation(), connection);

		if (!nodes.containsKey(connection.getFrom().getId())) {
			addNode(connection.getFrom());
		}

		if (!nodes.containsKey(connection.getTo().getId())) {
			addNode(connection.getTo());
		}
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
}
