package logbook.gui.window;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import logbook.context.data.DataType;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.logic.TableColumnManager;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public abstract class RecordTable<T> extends WindowBase {
	private Table table;
	private final ArrayList<TableColumnManager<T>> tcms = new ArrayList<>();

	public RecordTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
		this.tcms.add(new TableColumnManager<>("No", null));//行头
		this.initTCMS(this.tcms);
		this.initTable();
		this.initMenuBar();
	}

	private void initTable() {
		this.table = new Table(this.getComposite(), SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		for (int i = 0; i < this.tcms.size(); i++) {
			TableColumn tableColumn = new TableColumn(this.table, SWT.LEFT);
			tableColumn.setText(this.tcms.get(i).getName());
			tableColumn.setWidth(40);
		}
	}

	private void initMenuBar() {
		MenuItem cmdMenuItem = new MenuItem(this.getMenuBar(), SWT.CASCADE);
		cmdMenuItem.setText("操作");
		Menu cmdMenu = new Menu(cmdMenuItem);
		cmdMenuItem.setMenu(cmdMenu);
		{
			MenuItem update = new MenuItem(cmdMenu, SWT.PUSH);
			update.setText("刷新");
			update.addSelectionListener(new ControlSelectionListener(ev -> this.updateTable()));
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		if (this.isVisible() && this.needUpdate(type)) {
			this.updateTable();
		}
	}

	private void updateTable() {
		this.getShell().setRedraw(false);
		this.table.removeAll();

		List<T> datas = this.getList();
		for (int row = 0; row < datas.size(); row++) {
			T data = datas.get(row);
			TableItem tableItem = new TableItem(this.table, SWT.NONE);
			for (int col = 0; col < this.tcms.size(); col++) {
				String text = this.tcms.get(col).getValue(row + 1, data).toString();
				tableItem.setText(col, text);
			}
		}

		ToolUtils.forEach(this.table.getColumns(), tableColumn -> tableColumn.pack());
		this.getShell().setRedraw(true);
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	protected abstract void initTCMS(ArrayList<TableColumnManager<T>> tcms);

	protected abstract List<T> getList();

	protected boolean needUpdate(DataType type) {
		return false;
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(800, 600));
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) this.updateTable();
		super.setVisible(visible);
	}

}
