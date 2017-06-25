package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;

import logbook.utils.SwtUtils;

public class FloatingWindow extends WindowBase {
	private Label[] deckTimeLabels = new Label[4];
	private Label[] ndockTimeLabels = new Label[4];

	public FloatingWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		for (int i = 0; i < this.deckTimeLabels.length; i++) {
			Label deckTimeLabel = new Label(this.getComposite(), SWT.RIGHT);
			SwtUtils.initLabel(deckTimeLabel, "00时00分00秒", new GridData(GridData.FILL_HORIZONTAL));
			this.allowMouseDrag(deckTimeLabel);
			main.getDeckTimeLabel()[i].setData(deckTimeLabel);

			this.deckTimeLabels[i] = deckTimeLabel;
		}
		SwtUtils.insertHSeparator(this.getComposite());
		for (int i = 0; i < this.ndockTimeLabels.length; i++) {
			Label ndockTimeLabel = new Label(this.getComposite(), SWT.RIGHT);
			SwtUtils.initLabel(ndockTimeLabel, "00时00分00秒", new GridData(GridData.FILL_HORIZONTAL));
			this.allowMouseDrag(ndockTimeLabel);
			main.getNdockTimeLabel()[i].setData(ndockTimeLabel);

			this.ndockTimeLabels[i] = ndockTimeLabel;
		}
	}

	@Override
	public int getShellStyle() {
		return super.getShellStyle() | SWT.NO_TRIM | SWT.ON_TOP;
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(80, 140));
	}
}
