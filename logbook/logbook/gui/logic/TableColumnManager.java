package logbook.gui.logic;

import java.util.function.Function;

public class TableColumnManager<T> {
	private final String name;
	private final Function<T, Object> value;

	public TableColumnManager(String name, Function<T, Object> value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue(int count, T t) {
		return this.value == null ? Integer.toString(count) : this.value.apply(t);
	}

}
