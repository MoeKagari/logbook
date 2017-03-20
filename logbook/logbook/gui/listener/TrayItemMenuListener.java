package logbook.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import logbook.gui.window.ApplicationMain;

public class TrayItemMenuListener implements MenuDetectListener {
	private Menu menu;

	public TrayItemMenuListener(ApplicationMain main) {
		this.menu = new Menu(main.getShell());

		MenuItem config = new MenuItem(this.menu, SWT.NONE);
		config.setText("设置");
		config.addSelectionListener(new ControlSelectionListener(ev -> {}));

		final MenuItem dispose = new MenuItem(this.menu, SWT.NONE);
		dispose.setText("退出");
		dispose.addSelectionListener(new ControlSelectionListener(ev -> main.getShell().close()));
	}

	@Override
	public void menuDetected(MenuDetectEvent e) {
		this.menu.setVisible(true);
	}
}
