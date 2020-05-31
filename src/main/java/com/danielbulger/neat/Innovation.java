package com.danielbulger.neat;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Innovation {

	private static final AtomicInteger counter = new AtomicInteger(0);

	@NotNull
	public static Innovation next() {
		return new Innovation(counter.incrementAndGet());
	}

	private final int id;

	protected Innovation(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		return id == ((Innovation) o).id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Innovation{" +
			"id=" + id +
			'}';
	}
}
