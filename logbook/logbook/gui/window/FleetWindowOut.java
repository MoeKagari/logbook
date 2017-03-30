package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.util.SwtUtils;

/**
 * 舰队面板-单
 * @author MoeKagari
 */
public class FleetWindowOut extends WindowBase {
	private FleetWindow fleetWindow;

	public FleetWindowOut(ApplicationMain main, MenuItem menuItem, int id) {
		super(main, menuItem, AppConstants.DEFAULT_FLEET_NAME[id - 1]);
		this.fleetWindow = new FleetWindow(new Composite(this.getComposite(), SWT.BORDER), id, false);
	}

	public FleetWindow getFleetWindow() {
		return this.fleetWindow;
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(250, 269));
	}
}
