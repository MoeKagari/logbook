package logbook.gui.listener;

import java.util.function.Consumer;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Spinner;

public class SpinnerMouseWheelListener implements MouseWheelListener {
	private final Consumer<MouseEvent> handler;
	private final Spinner spinner;

	public SpinnerMouseWheelListener(Spinner spinner, Consumer<MouseEvent> handler) {
		this.spinner = spinner;
		this.handler = handler;
	}

	@Override
	public void mouseScrolled(MouseEvent ev) {
		int count = ev.count;
		if (count != 0) {
			int cur = this.spinner.getSelection();
			int next = cur + (count > 0 ? 1 : -1);
			if (next >= this.spinner.getMinimum() && next <= this.spinner.getMaximum()) {
				this.spinner.setSelection(next);
				this.handler.accept(ev);
			}
		}
	}

}
