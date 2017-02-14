package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import logbook.context.data.DataType;
import logbook.util.SwtUtils;

public class FleetWindowAll extends WindowBase {
	private FleetWindow[] fleetWindows;

	public FleetWindowAll(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
		this.initFleetWindows();
	}

	private void initFleetWindows() {
		Composite fleetComposite = new Composite(this.getComposite(), SWT.NONE);
		fleetComposite.setLayout(SwtUtils.makeGridLayout(2, 2, 2, 0, 0));
		fleetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		int fleetLength = 4;
		this.fleetWindows = new FleetWindow[fleetLength];
		for (int i = 0; i < fleetLength; i++) {
			this.fleetWindows[i] = new FleetWindow(new Composite(fleetComposite, SWT.BORDER), i + 1, false);
		}
	}

	public FleetWindow[] getFleetWindows() {
		return this.fleetWindows;
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(410, 502));
	}

	@Override
	public void update(DataType type) {}

}
