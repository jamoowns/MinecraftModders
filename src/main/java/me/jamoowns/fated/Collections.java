package me.jamoowns.fated;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Collections {

	public static <T> T findFirst(Collection<T> items) {
		return items.stream().findFirst().get();
	}

	public static <T, V> Optional<T> find(Collection<T> items, Function<T, V> valueMapper, V value) {
		List<T> result = items.stream().filter(v -> valueMapper.apply(v).equals(value)).collect(Collectors.toList());
		return result.size() == 1 ? Optional.of(result.get(0)) : Optional.empty();
	}
}
