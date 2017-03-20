package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import logbook.context.update.GlobalContextUpdater;
import logbook.context.update.data.DataType;
import logbook.context.update.data.EventListener;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.NotCloseButHiddenShellListener;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

/**
 * 呼出式窗口的super class
 * @author MoeKagari
 */
public abstract class WindowBase implements EventListener {
	private final ApplicationMain main;
	private final Shell shell;
	private final MenuItem menuItem;
	private final Composite composite;
	private final Menu menuBar;

	public WindowBase(ApplicationMain main, MenuItem menuItem, String title, boolean top) {
		this.main = main;

		this.shell = new Shell(main.getSubShell(), this.getShellStyle() | (top ? SWT.ON_TOP : SWT.NONE));
		this.shell.setText(title);
		this.shell.setImage(main.getLogo());
		this.shell.setSize(this.getDefaultSize());
		this.shell.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.shell.addShellListener(new NotCloseButHiddenShellListener(ev -> this.setVisible(false)));
		this.shell.addShellListener(new NotCloseButHiddenShellListener(this::handlerAfterHidden));

		this.composite = new Composite(this.shell, SWT.NONE);
		this.composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.menuItem = menuItem;
		ToolUtils.notNullThenHandle(menuItem, mi -> mi.addSelectionListener(new ControlSelectionListener(ev -> this.setVisible(mi.getSelection()))));

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);
		GlobalContextUpdater.addEventListener(this);
	}

	public WindowBase(ApplicationMain main, MenuItem menuItem, String title) {
		this(main, menuItem, title, false);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	public ApplicationMain getMain() {
		return this.main;
	}

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
		ToolUtils.ifHandle(visible, this::handlerBeforeDisplay);
		ToolUtils.notNullThenHandle(this.menuItem, mi -> mi.setSelection(visible));
		ToolUtils.ifHandle(visible, () -> this.shell.setMinimized(false));
		this.shell.setVisible(visible);
		ToolUtils.ifHandle(visible, () -> this.shell.forceActive());
	}

	protected boolean isVisible() {
		return this.shell.isVisible() && (this.shell.getMinimized() == false);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/** 显示窗口前的操作 */
	protected void handlerBeforeDisplay() {}

	/** 关闭窗口时的操作 */
	protected void handlerAfterHidden(ShellEvent ev) {}

	/** 更新窗口(延迟redraw) */
	protected void updateWindowRedraw(Runnable run) {
		this.shell.setRedraw(false);
		run.run();
		this.shell.setRedraw(true);
	}

	@Override
	public void update(DataType type) {}

	/** 默认size */
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	/** 默认shellstyle */
	public int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MIN;
	}

}
