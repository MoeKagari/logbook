package logbook.gui.listener;

import java.util.function.Consumer;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;

public class ControlResizeAdapter extends ControlAdapter {
	private final Consumer<ControlEvent> handler;

	public ControlResizeAdapter(Consumer<ControlEvent> handler) {
		this.handler = handler;
	}

	@Override
	public void controlResized(ControlEvent e) {
		this.handler.accept(e);
	}

}
