package com.danielbulger.neat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Connection {

	@NotNull
	public static Connection create(int from, int to) {
		return new Connection(from, to, ThreadLocalRandom.current().nextFloat(), Innovation.next());
	}

	private final int from;

	private final int to;

	private float weight;

	private final Innovation innovation;

	private boolean enabled;

	public Connection(int from, int to, float weight, Innovation innovation) {
		this(from, to, weight, innovation, true);
	}

	public Connection(int from, int to, float weight, Innovation innovation, boolean enabled) {
		this.from = from;
		this.to = to;
		this.weight = weight;
		this.innovation = innovation;
		this.enabled = enabled;
	}

	public Connection(@NotNull Connection other) {
		this(other.from, other.to, other.weight, other.innovation, other.enabled);
	}

	@Contract(pure = true)
	public int getFrom() {
		return from;
	}

	@Contract(pure = true)
	public int getTo() {
		return to;
	}

	@Contract(pure = true)
	public float getWeight() {
		return weight;
	}

	@Contract(mutates = "this")
	public void setWeight(float weight) {
		this.weight = weight;
	}

	@Contract(pure = true)
	public Innovation getInnovation() {
		return innovation;
	}

	@Contract(pure = true)
	public boolean isEnabled() {
		return enabled;
	}

	@Contract(mutates = "this")
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Connection that = (Connection) o;
		return from == that.from &&
			to == that.to;
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public String toString() {
		return "Connection{" +
			"from=" + from +
			", to=" + to +
			", weight=" + weight +
			", innovation=" + innovation +
			", enabled=" + enabled +
			'}';
	}
}