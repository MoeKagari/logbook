package logbook.gui.listener;

import java.util.function.Consumer;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class NotCloseButHiddenShellListener extends ShellAdapter {
	private final Consumer<ShellEvent> handler;

	public NotCloseButHiddenShellListener(Consumer<ShellEvent> handler) {
		this.handler = handler;
	}

	@Override
	public void shellClosed(ShellEvent ev) {
		ev.doit = false;
		this.handler.accept(ev);
	}

}
