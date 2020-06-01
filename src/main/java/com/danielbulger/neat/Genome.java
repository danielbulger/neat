package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Genome {

	private final NavigableMap<Integer, Node> nodes = new TreeMap<>();

	private final EnumMap<NodeType, List<Node>> nodeTypes = new EnumMap<>(NodeType.class);

	private final List<Connection> connections = new ArrayList<>();

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
	@Contract(mutates = "this")
	public float[] feedForward(@NotNull float[] values) {
		if (values == null) {
			throw new NullPointerException();
		}

		final List<Node> inputNodes = nodeTypes.get(NodeType.INPUT);

		if (values.length != inputNodes.size()) {
			throw new IllegalArgumentException();
		}

		for(int i = 0; i < inputNodes.size(); ++i) {
			inputNodes.get(i).setValue(values[i]);
		}

		for(final Connection connection : connections) {

			if(!connection.isEnabled()) {
				continue;
			}

			final Node from = nodes.get(connection.getFrom());

			final Node to = nodes.get(connection.getTo());

			if(from == null || to == null) {
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

	@Contract(mutates = "this")
	private void initialiseNodes(int numInputs, int numOutputs) {


		for (int i = 0; i < numInputs; ++i) {
			Node node = Node.create(NodeType.INPUT);
			addNode(node);
		}

		for (int i = 0; i < numOutputs; ++i) {
			Node node = Node.create(NodeType.OUTPUT);
			addNode(node);
		}

		for (final Node input : nodeTypes.get(NodeType.INPUT)) {

			for (final Node output : nodeTypes.get(NodeType.OUTPUT)) {

				this.addConnection(Connection.create(

					input.getId(),

					output.getId()
				));
			}
		}
	}

	@Contract(pure = true)
	public boolean isConnected(int from, int to) {

		for (final Connection connection : connections) {

			if (connection.getTo() == to && connection.getFrom() == from) {
				return true;
			}
		}

		return false;
	}

	@Contract(mutates = "this")
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

	@Contract(mutates = "this")
	public void addConnection(final @NotNull Connection connection) {
		connections.add(connection);
	}

	@Contract(mutates = "this")
	public void addConnections(final @NotNull Connection... elements) {
		connections.addAll(Arrays.asList(elements));
	}

	@NotNull
	@Contract(pure = true)
	public List<Connection> getActiveConnections() {
		return connections.stream()
			.filter(Connection::isEnabled)
			.collect(Collectors.toList());
	}

	@NotNull
	@Contract(pure = true)
	public NavigableMap<Integer, Node> getNodes() {
		return nodes;
	}
}
