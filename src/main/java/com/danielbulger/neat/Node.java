package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Node implements Comparable<Node> {

	private static final AtomicInteger counter = new AtomicInteger(0);

	public static Node create(NodeType type) {
		return new Node(counter.incrementAndGet(), type);
	}

	private final int id;

	private final NodeType type;

	private float value = 0;

	public Node(int id, @NotNull NodeType type) {
		this.id = id;
		this.type = Objects.requireNonNull(type);
	}

	public Node(@NotNull Node other) {
		this(other.id, other.type);
		this.value = 0;
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
			'}';
	}

	@Override
	public int compareTo(@NotNull Node o) {
		return Integer.compare(id, o.id);
	}
}
