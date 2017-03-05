package logbook.gui.listener;

import java.util.function.Consumer;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ControlSelectionListener extends SelectionAdapter {
	private final Consumer<SelectionEvent> handler;

	public ControlSelectionListener(Consumer<SelectionEvent> handler) {
		this.handler = handler;
	}

	@Override
	public void widgetSelected(SelectionEvent ev) {
		this.handler.accept(ev);
	}
}
