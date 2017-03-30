package logbook.gui.listener;

import java.util.function.Consumer;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

import logbook.util.ToolUtils;

public class ControlSelectionListener extends SelectionAdapter {
	private final Consumer<SelectionEvent> handler;

	public ControlSelectionListener(Consumer<SelectionEvent> handler) {
		this.handler = handler;
	}

	public ControlSelectionListener(Runnable run) {
		this.handler = ev -> run.run();
	}

	@Override
	public void widgetSelected(SelectionEvent ev) {
		ToolUtils.notNullThenHandle(this.handler, h -> h.accept(ev));
	}

	public static void add(MenuItem menuItem, Consumer<SelectionEvent> handler) {
		menuItem.addSelectionListener(new ControlSelectionListener(handler));
	}

	public static void add(MenuItem menuItem, Runnable run) {
		menuItem.addSelectionListener(new ControlSelectionListener(run));
	}
}
