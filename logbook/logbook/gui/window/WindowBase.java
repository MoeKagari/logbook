package logbook.gui.window;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import logbook.config.WindowConfig;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.NotCloseButHiddenShellListener;
import logbook.update.GlobalContextUpdater;
import logbook.update.data.DataType;
import logbook.update.data.EventListener;
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
	private ToolBar toolBar = null;
	private WindowConfig windowConfig = null;

	public WindowBase(ApplicationMain main, MenuItem menuItem, String title) {
		this(main, menuItem, title, false);
	}

	public WindowBase(ApplicationMain main, MenuItem menuItem, String title, boolean top) {
		this.main = main;

		this.shell = new Shell(main.getSubShell(), this.getShellStyle() | (top ? SWT.ON_TOP : SWT.NONE));
		this.shell.setText(title);
		this.shell.setImage(main.getLogo());
		this.shell.setSize(this.getDefaultSize());
		this.shell.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.shell.addShellListener(new NotCloseButHiddenShellListener(this::hiddenWindow));
		this.shell.addShellListener(new NotCloseButHiddenShellListener(this::handlerAfterHidden));

		this.composite = new Composite(this.shell, SWT.NONE);
		this.composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.menuItem = menuItem;
		ToolUtils.notNullThenHandle(this.menuItem, mi -> ControlSelectionListener.add(mi, () -> this.setVisible(mi.getSelection())));

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);

		if (this.haveToolBar()) {
			this.toolBar = new ToolBar(this.composite, SWT.WRAP);
			this.toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		GlobalContextUpdater.addEventListener(this);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/** 存储当前窗口的配置 */
	public void storeWindowConfig() {
		if (this.shell.isDisposed()) return;
		if (this.shell.getMaximized() == false) {//最大化不记录
			this.windowConfig.setSize(this.shell.getSize());
			this.windowConfig.setLocation(this.shell.getLocation());
		}
		this.windowConfig.setMinimized(this.shell.getMinimized());
		this.windowConfig.setVisible(this.shell.isVisible());
	}

	/** 恢复当前窗口的配置 */
	public void restoreWindowConfig() {
		if (this.windowConfig == null) {
			this.windowConfig = WindowConfig.get().get(this.getWindowConfigKey());
			if (this.windowConfig == null) {
				this.windowConfig = new WindowConfig();
				WindowConfig.get().put(this.getWindowConfigKey(), this.windowConfig);
				this.storeWindowConfig();
				return;
			}
		}

		this.shell.setSize(this.windowConfig.getSize());
		this.shell.setLocation(this.windowConfig.getLocation());
		this.shell.setMinimized(this.windowConfig.getMinimized());
		this.setVisible(this.windowConfig.isVisible());
	}

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

	public void hiddenWindow() {
		this.setVisible(false);
	}

	public void displayWindow() {
		this.setVisible(true);
	}

	private void setVisible(boolean visible) {
		ToolUtils.ifHandle(visible, this::handlerBeforeDisplay);
		ToolUtils.notNullThenHandle(this.menuItem, mi -> mi.setSelection(visible));
		this.shell.setVisible(visible);
		ToolUtils.ifHandle(visible, this.shell::forceActive);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	protected String getWindowConfigKey() {
		return this.getClass().getName();
	}

	/** 是否有toolbar,默认false */
	protected boolean haveToolBar() {
		return false;
	}

	/**
	 * 需要将haveToolBar()返回true
	 * @return 可能返回null(haveToolBar()为false时)
	 */
	protected ToolItem newToolItem(int style, String text, Consumer<SelectionEvent> handler) {
		if (this.haveToolBar()) {
			ToolItem toolItem = new ToolItem(this.toolBar, style);
			toolItem.setText(text);
			toolItem.addSelectionListener(new ControlSelectionListener(handler));
			return toolItem;
		}
		return null;
	}

	@Override
	public void update(DataType type) {}

	/** 显示窗口前的操作 */
	protected void handlerBeforeDisplay() {}

	/** 关闭窗口时的操作 */
	protected void handlerAfterHidden() {}

	/** 更新窗口(延迟redraw) */
	protected void updateWindowRedraw(Runnable run) {
		this.composite.setRedraw(false);
		run.run();
		this.composite.setRedraw(true);
	}

	/** 更新窗口(延迟redraw) */
	protected void updateWindowRedraw(boolean flag, Runnable run) {
		ToolUtils.ifHandle(run, ToolUtils.getPredicater(flag, run), this::updateWindowRedraw);
	}

	/** 默认size */
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	/** 默认shellstyle */
	public int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MIN;
	}
}
