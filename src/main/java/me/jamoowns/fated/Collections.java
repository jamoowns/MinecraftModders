package me.jamoowns.fated;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Collections {

	public static <T> T findFirst(Collection<T> items) {
		return items.stream().findFirst().get();
	}

	public static <T, V> Optional<T> find(Collection<T> items, Function<T, V> valueMapper, V value) {
		Stream<T> result = items.stream().filter(v -> valueMapper.apply(v).equals(value));
		return result.count() == 1 ? result.findFirst() : Optional.empty();
	}
}
