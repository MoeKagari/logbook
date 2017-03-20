package logbook.gui.window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import logbook.context.update.data.DataType;
import logbook.gui.listener.ControlSelectionListener;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public abstract class AbstractTable<T> extends WindowBase {
	private Table table;
	private final List<T> datas = new ArrayList<>();
	private final ArrayList<TableColumnManager> tcms = new ArrayList<>();

	public AbstractTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
		this.tcms.add(new TableColumnManager("", true, null));//行头
		this.initTCMS(this.tcms);
		this.initTable();
		this.initMenuBar();
	}

	private void initTable() {
		this.table = new Table(this.getComposite(), SWT.MULTI | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		for (int index = 0; index < this.tcms.size(); index++) {
			TableColumnManager tcm = this.tcms.get(index);
			tcm.stc = new SortedTableColumn(index, tcm);
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
			update.addSelectionListener(new ControlSelectionListener(ev -> this.updateWindowRedraw(this::updateTable)));
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		ToolUtils.ifHandle(this.getShell().isVisible() && this.needUpdate(type), () -> this.updateWindowRedraw(this::updateTable));
	}

	private void updateTable() {
		ToolUtils.forEach(this.table.getItems(), TableItem::dispose);

		//更新数据
		this.updateData(this.datas);
		for (int row = 0; row < this.datas.size(); row++) {
			T data = this.datas.get(row);
			TableItem tableItem = new TableItem(this.table, SWT.NONE);
			for (int col = 0; col < this.tcms.size(); col++) {
				tableItem.setText(col, this.tcms.get(col).getValue(row + 1, data));
			}
		}
		this.datas.clear();

		//排序
		TableColumn sortColumn = this.table.getSortColumn();
		this.tcms.stream().filter(tcm -> tcm.stc.tableColumn == sortColumn).forEach(tcm -> this.sortTable(tcm.stc));

		ToolUtils.forEach(this.table.getColumns(), TableColumn::pack);
	}

	/**
	 * 排序table
	 * @param stc 需要排序的TableColumn
	 */
	private void sortTable(SortedTableColumn stc) {
		TableItem[] tableItems = this.table.getItems();
		for (int i = 1; i < tableItems.length; i++) {
			TableItem tableItem = tableItems[i];
			String value = tableItem.getText(stc.index);
			for (int j = 0; j < i; j++) {
				if (stc.compare(value, tableItems[j].getText(stc.index)) > 0) {
					String[] values = ToolUtils.toStringArray(this.tcms.size(), n -> tableItem.getText(n));
					tableItem.dispose();
					new TableItem(this.table, SWT.NONE, j).setText(values);
					tableItems = this.table.getItems();
					break;
				}
			}
		}//刷新行号
		ToolUtils.forEach(this.table.getItems(), (tableItem, index) -> tableItem.setText(0, String.valueOf(index + 1)));
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	protected abstract void initTCMS(ArrayList<TableColumnManager> tcms);

	protected abstract void updateData(List<T> datas);

	protected boolean needUpdate(DataType type) {
		return false;
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(800, 600));
	}

	@Override
	public int getShellStyle() {
		return super.getShellStyle() | SWT.MAX;
	}

	@Override
	protected void handlerAfterHidden(ShellEvent ev) {
		ToolUtils.forEach(this.table.getItems(), TableItem::dispose);
	}

	/** 显示之前更新 */
	@Override
	protected void handlerBeforeDisplay() {
		this.updateWindowRedraw(this::updateTable);
	}

	/**
	 * table列的属性
	 * @author MoeKagari
	 */
	public class TableColumnManager {
		private final boolean isInteger;
		private final String name;
		private final Function<T, Object> value;
		private SortedTableColumn stc = null;

		public TableColumnManager(String name, boolean isInteger, Function<T, Object> value) {
			this.name = name;
			this.isInteger = isInteger;
			this.value = value;
		}

		public TableColumnManager(String name, Function<T, Object> value) {
			this(name, false, value);
		}

		public String getValue(int index, T t) {
			return this.value == null ? Integer.toString(index) : this.value.apply(t).toString();
		}
	}

	/**
	 * 排序table列
	 * @author MoeKagari
	 */
	private class SortedTableColumn implements Listener {
		private boolean direction = false;//是否从小到大排序,否则从大到小排序
		private final boolean isInteger;
		private final int index;
		private final TableColumn tableColumn;

		public SortedTableColumn(int index, TableColumnManager tcm) {
			this.index = index;
			this.isInteger = tcm.isInteger;
			this.tableColumn = new TableColumn(AbstractTable.this.table, SWT.LEFT);
			this.tableColumn.setText(tcm.name);
			this.tableColumn.setWidth(40);
			this.tableColumn.addListener(SWT.Selection, this);
		}

		@Override
		public void handleEvent(Event ev) {
			//不对行头排序
			if (this.index == 0) return;
			//如果不是改变排序列,则改变排序方向
			ToolUtils.ifHandle(AbstractTable.this.table.getSortColumn() == this.tableColumn, () -> this.direction = !this.direction);
			AbstractTable.this.updateWindowRedraw(() -> AbstractTable.this.sortTable(this));
			AbstractTable.this.table.setSortColumn(this.tableColumn);
			AbstractTable.this.table.setSortDirection(this.direction ? SWT.UP : SWT.DOWN);
		}

		public int compare(String value, String othervalue) {
			int result = this.direction ? -1 : 1;

			if (this.isInteger) {
				if (StringUtils.isBlank(value) && StringUtils.isBlank(othervalue)) {
					return 0;
				}
				if (StringUtils.isBlank(value)) {
					return -1;
				}
				if (StringUtils.isBlank(othervalue)) {
					return 1;
				}
				return result * Integer.compare(Integer.parseInt(value), Integer.parseInt(othervalue));
			} else {
				return result * value.compareTo(othervalue);
			}
		}
	}

}
