package com.danielbulger.neat.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Random {

	@NotNull
	@Contract(pure = true)
	public static <V> Optional<V> fromMap(@NotNull NavigableMap<Integer, V> map) {
		if (map.isEmpty()) {
			return Optional.empty();
		}

		final ThreadLocalRandom random = ThreadLocalRandom.current();

		final int element = random.nextInt(map.lastKey());

		final Integer key = map.ceilingKey(element);

		return Optional.of(map.get(key));

	}

	@NotNull
	@Contract(pure = true)
	public static <T> Optional<T> fromList(@NotNull List<T> list) {

		if (list.isEmpty()) {
			return Optional.empty();
		}

		final ThreadLocalRandom random = ThreadLocalRandom.current();

		final int element = random.nextInt(list.size());

		return Optional.of(list.get(element));
	}

	private Random() {
	}
}
