package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

import logbook.config.WindowConfig;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.NotCloseButHiddenShellListener;
import logbook.update.GlobalContextUpdater;
import logbook.update.data.DataType;
import logbook.update.data.EventListener;
import logbook.utils.SwtUtils;
import logbook.utils.ToolUtils;

/**
 * 呼出式窗口的super class
 * @author MoeKagari
 */
public class WindowBase implements EventListener {
	private final ApplicationMain main;
	private final Shell shell;
	private final MenuItem menuItem;
	private final Composite composite;
	private final Menu menuBar;

	private boolean topMost = false;
	private CoolBar coolBar = null;
	private WindowConfig windowConfig = null;
	private final MouseDragListener mouseDragListener = new MouseDragListener();

	public WindowBase(ApplicationMain main, MenuItem menuItem, String title) {
		this.main = main;

		this.shell = new Shell(new Shell(main.getDisplay(), SWT.TOOL), this.getShellStyle());
		this.shell.setText(title);
		this.shell.setImage(main.getLogo());
		this.shell.setSize(this.getDefaultSize());
		this.shell.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.shell.addShellListener(new NotCloseButHiddenShellListener(this::hiddenWindow));
		this.shell.addShellListener(new NotCloseButHiddenShellListener(this::handlerAfterHidden));

		this.coolBar = new CoolBar(this.shell, SWT.HORIZONTAL);
		this.coolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.coolBar.addListener(SWT.Resize, event -> this.shell.layout());

		this.composite = new Composite(this.shell, SWT.NONE);
		this.composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.menuItem = menuItem;
		ToolUtils.notNull(this.menuItem, mi -> ControlSelectionListener.add(mi, this::setVisible));

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);

		GlobalContextUpdater.addEventListener(this);
	}

	public void resizeCoolBar() {
		CoolItem[] items = this.coolBar.getItems();
		for (int i = 0; i < items.length; i++) {
			CoolItem item = items[i];
			Control control = item.getControl();

			Point controlSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point coolItemSize = item.computeSize(controlSize.x, controlSize.y);

			if (control instanceof ToolBar) {
				ToolBar toolBar = (ToolBar) control;
				if (toolBar.getItemCount() > 0) {
					controlSize.x = toolBar.getItem(0).getWidth();
				}
			}
			item.setMinimumSize(controlSize);
			item.setPreferredSize(coolItemSize);
			item.setSize(coolItemSize);
		}

		int[] ind = new int[items.length];
		for (int index = 0; index < ind.length; index++) {
			ind[index] = index;
		}
		this.coolBar.setWrapIndices(ind);
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
		//	this.windowConfig.setTopMost(this.isTopMost());
	}

	/** 恢复当前窗口的配置 */
	protected void restoreWindowConfig() {
		if (this.windowConfig == null) {
			this.windowConfig = WindowConfig.get().get(this.getWindowConfigKey());
			if (this.windowConfig == null) {
				this.windowConfig = new WindowConfig();
				this.storeWindowConfig();
				WindowConfig.get().put(this.getWindowConfigKey(), this.windowConfig);
				return;
			}
		}

		this.shell.setSize(this.windowConfig.getSize());
		this.shell.setLocation(this.windowConfig.getLocation());
		this.shell.setMinimized(this.windowConfig.getMinimized());
		this.setVisible(this.windowConfig.isVisible());
		//this.setTopMost(this.windowConfig.isTopMost());
	}

	public ApplicationMain getMain() {
		return this.main;
	}

	public Menu getMenuBar() {
		return this.menuBar;
	}

	public CoolBar getCoolBar() {
		return this.coolBar;
	}

	public Shell getShell() {
		return this.shell;
	}

	public Composite getComposite() {
		return this.composite;
	}

	protected void hiddenWindow() {
		this.setVisible(false);
	}

	protected void displayWindow() {
		this.setVisible(true);
	}

	private void setVisible() {
		if (this.menuItem != null) {
			this.setVisible(this.menuItem.getSelection());
		}
	}

	private void setVisible(boolean visible) {
		ToolUtils.ifHandle(visible, this::handlerBeforeDisplay);
		ToolUtils.notNull(this.menuItem, mi -> mi.setSelection(visible));
		this.shell.setVisible(visible);
		ToolUtils.ifHandle(visible, this.shell::forceActive);
	}

	protected void allowMouseDrag(Control con) {
		con.addMouseListener(this.mouseDragListener);
		con.addMouseMoveListener(this.mouseDragListener);
	}

	protected boolean isTopMost() {
		return this.topMost;
	}

	protected void setTopMost(boolean topMost) {
		this.topMost = topMost;

		Point location = this.shell.getLocation();
		Point size = this.shell.getSize();
		int hWndInsertAfter = topMost ? OS.HWND_TOPMOST : OS.HWND_NOTOPMOST;
		OS.SetWindowPos(this.shell.handle, hWndInsertAfter, location.x, location.y, size.x, size.y, SWT.NULL);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {}

	protected String getWindowConfigKey() {
		return this.getClass().getName();
	}

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

	/** 默认size */
	protected Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	/** 默认shellstyle */
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MIN;
	}

	/** shell跟随鼠标的拖动而移动 */
	private class MouseDragListener implements MouseListener, MouseMoveListener {
		private boolean allowDrag = false;
		private Point oldLocation = null;

		@Override
		public void mouseMove(MouseEvent ev) {
			if (this.allowDrag) {
				Point shellLocation = WindowBase.this.shell.getLocation();
				int x = shellLocation.x - this.oldLocation.x + ev.x;
				int y = shellLocation.y - this.oldLocation.y + ev.y;
				WindowBase.this.shell.setLocation(x, y);
			}
		}

		@Override
		public void mouseDoubleClick(MouseEvent ev) {}

		@Override
		public void mouseDown(MouseEvent ev) {
			if (ev.button == 1) {
				this.allowDrag = true;
				this.oldLocation = new Point(ev.x, ev.y);
			}
		}

		@Override
		public void mouseUp(MouseEvent ev) {
			this.allowDrag = false;
		}
	}
}
