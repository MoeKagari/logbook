package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.utils.SwtUtils;

/**
 * 舰队面板-单
 * @author MoeKagari
 */
public abstract class FleetWindowOut extends WindowBase {
	private FleetWindow fleetWindow;

	public FleetWindowOut(ApplicationMain main, MenuItem menuItem, int id) {
		super(main, menuItem, AppConstants.DEFAULT_FLEET_NAME[id - 1]);
		this.fleetWindow = new FleetWindow(new Composite(this.getComposite(), SWT.BORDER), id);
	}

	public FleetWindow getFleetWindow() {
		return this.fleetWindow;
	}

	public abstract int getId();

	@Override
	protected String getWindowConfigKey() {
		return super.getWindowConfigKey() + this.getId();
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(250, 269));
	}
}
