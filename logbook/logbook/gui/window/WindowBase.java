package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import logbook.context.update.GlobalContextUpdater;
import logbook.context.update.data.EventListener;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.NotCloseButHiddenShellListener;
import logbook.util.SwtUtils;

/**
 * 呼出式窗口的super class
 * @author MoeKagari
 */
public abstract class WindowBase implements EventListener {
	private final Shell shell;
	private final MenuItem menuItem;
	private final Composite composite;
	private final Menu menuBar;

	public WindowBase(ApplicationMain main, MenuItem menuItem, String title) {
		this.shell = new Shell(main.getSubShell(), this.getShellStyle());
		this.shell.setText(title);
		this.shell.setImage(main.getLogo());
		this.shell.setSize(this.getDefaultSize());
		this.shell.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.shell.addShellListener(new NotCloseButHiddenShellListener(ev -> this.setVisible(false)));

		this.composite = new Composite(this.shell, SWT.NONE);
		this.composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.menuItem = menuItem;
		this.menuItem.addSelectionListener(new ControlSelectionListener(ev -> this.setVisible(this.menuItem.getSelection())));

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);
		GlobalContextUpdater.addEventListener(this);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	public Menu getMenuBar() {
		return this.menuBar;
	}

	public Shell getShell() {
		return this.shell;
	}

	public Composite getComposite() {
		return this.composite;
	}

	public void setVisible(boolean visible) {
		this.menuItem.setSelection(visible);
		if (visible) {
			this.shell.setMinimized(false);
			this.shell.forceActive();
		}
		this.shell.setVisible(visible);
	}

	public boolean isVisible() {
		return this.shell.isVisible() || this.shell.getMinimized();
	}

	/*------------------------------------------------------------------------------------------------------------*/

	public abstract Point getDefaultSize();

	public int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MIN;
	}

}
