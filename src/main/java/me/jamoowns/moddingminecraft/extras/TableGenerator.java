package me.jamoowns.moddingminecraft.extras;

import java.util.ArrayList;
import java.util.List;

public class TableGenerator {

	public enum Alignment {

		CENTER, LEFT, RIGHT
	}

	private class Row {

		public List<String> texts = new ArrayList<>();

		public Row(String... texts) {
			for (String text : texts) {
				this.texts.add(text == null ? "" : text);
			}
		}
	}

	private static String delimiter = " | ";
	private Alignment[] alignments;

	private List<Row> table = new ArrayList<>();

	private int columns;

	public TableGenerator(Alignment... alignments) {
		this.columns = alignments.length;
		this.alignments = alignments;
	}

	public void addRow(String... texts) {
		if (texts.length > columns) {
			throw new IllegalArgumentException("Too big for the table.");
		}

		Row r = new Row(texts);

		for (int i = 0; i < columns; i++) {
			if (i >= texts.length)
				r.texts.add("");
		}

		table.add(r);
	}

	public List<String> generate() {
		Integer[] columWidths = new Integer[columns];

		for (Row r : table) {
			for (int i = 0; i < columns; i++) {
				if (columWidths[i] == null)
					columWidths[i] = r.texts.get(i).length();

				else if (r.texts.get(i).length() > columWidths[i])
					columWidths[i] = r.texts.get(i).length();
			}
		}

		List<String> lines = new ArrayList<String>();

		for (Row r : table) {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < columns; i++) {
				Alignment agn = alignments[i];
				String text = r.texts.get(i);
				String spaces = spaces(columWidths[i] - text.length());

				if (agn == Alignment.LEFT) {
					sb.append(text).append(spaces);
				}
				if (agn == Alignment.RIGHT) {
					sb.append(spaces).append(text);
				}
				if (agn == Alignment.CENTER) {
					int left = spaces.length() / 2;
					int right = spaces.length() - left;

					sb.append(spaces(left)).append(text).append(spaces(right));
				}

				if (i < columns - 1)
					sb.append(delimiter);
			}

			lines.add(sb.toString());
		}
		return lines;
	}

	private String spaces(int length) {
		String s = "";
		for (int i = 0; i < length; i++)
			s += " ";
		return s;
	}
}
