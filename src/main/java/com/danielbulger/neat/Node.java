package com.danielbulger.neat;

import com.danielbulger.neat.util.MathUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Node implements Comparable<Node> {

	private static final AtomicInteger counter = new AtomicInteger(0);

	public static Node create(NodeType type) {
		return new Node(counter.incrementAndGet(), type);
	}

	private final List<Connection> incomingConnections = new ArrayList<>();

	private final List<Connection> outgoingConnections = new ArrayList<>();

	private final int id;

	private final NodeType type;

	private float value = 0;

	private boolean processed = false;

	public Node(int id, @NotNull NodeType type) {
		this.id = id;
		this.type = Objects.requireNonNull(type);
	}

	public Node(@NotNull Node other) {
		this(other.id, other.type);
		this.value = 0;
		this.processed = false;
	}

	private boolean isReady() {
		for (final Connection connection : incomingConnections) {
			if (!connection.getFrom().processed) {
				return false;
			}
		}

		return true;
	}

	public void process() {

		if (type.shouldActivate()) {
			this.value = MathUtil.sigmoid(this.value);
		}

		for (final Connection connection : outgoingConnections) {

			if (!connection.isEnabled()) {
				continue;
			}

			connection.getTo().value += (connection.getWeight() * this.value);
		}

		this.processed = true;

		for (final Connection connection : outgoingConnections) {
			if (connection.getTo().isReady()) {
				connection.getTo().process();
			}
		}
	}

	public void reset() {
		this.value = 0f;
		this.processed = false;
	}

	public void addIncomingConnection(final Connection connection) {

		if(!connection.getTo().equals(this)) {
			throw new IllegalArgumentException();
		}

		this.incomingConnections.add(connection);
	}

	public void addOutgoingConnection(final Connection connection) {
		if(!connection.getFrom().equals(this)) {
			throw new IllegalArgumentException();
		}

		this.outgoingConnections.add(connection);
	}

	@Contract(pure = true)
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Contract(pure = true)
	public int getId() {
		return id;
	}

	@Contract(pure = true)
	public NodeType getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Node node = (Node) o;
		return id == node.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "Node{" +
			"id=" + id +
			", type=" + type +
			", value=" + value +
			", processed=" + processed +
			'}';
	}

	@Override
	public int compareTo(@NotNull Node o) {
		return Integer.compare(id, o.id);
	}
}
