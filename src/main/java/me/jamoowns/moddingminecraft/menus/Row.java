package me.jamoowns.moddingminecraft.menus;

import java.util.ArrayList;
import java.util.List;

final class Row<T> {

	/** Max items in a row. */
	private static final Integer MAX_ITEMS_IN_ROW = 9;

	private List<T> items;

	public Row() {
		items = new ArrayList<>();
	}

	public final void addItem(T item) {
		if (!hasSpace()) {
			throw new IllegalStateException("Row is already full");
		}
		items.add(item);
	}

	public List<T> getItems() {
		return items;
	}

	public boolean hasSpace() {
		return items.size() <= MAX_ITEMS_IN_ROW;
	}

	public boolean isEmpty() {
		return items.size() == 0;
	}
}
