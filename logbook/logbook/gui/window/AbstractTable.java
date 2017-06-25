package logbook.gui.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import logbook.gui.listener.ControlSelectionListener;
import logbook.update.GlobalContext;
import logbook.update.data.DataType;
import logbook.utils.ToolUtils;

public abstract class AbstractTable<T> extends WindowBase {
	private Table table;
	private final List<T> datas = new ArrayList<>();
	private final List<TableColumnManager> tcms = new ArrayList<>();
	private final List<TableColumn> sortColumns = new ArrayList<>();//多级排序顺序
	private final Predicate<T> filter;
	private final ControlSelectionListener updateTableListener = new ControlSelectionListener(ev -> this.updateWindowRedraw(this::updateTable));

	public AbstractTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		this.filter = this.initFilter();
		this.initTCMS(this.tcms);
		this.initTable();
		this.initMenuBar();
	}

	private void initTable() {
		this.table = new Table(this.getComposite(), SWT.MULTI | SWT.HIDE_SELECTION);
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		this.tcms.add(0, new TableColumnManager("", true, null));//行头
		for (int index = 0; index < this.tcms.size(); index++) {
			new SortedTableColumn(index, this.tcms.get(index));
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
			update.addSelectionListener(this.updateTableListener);

			MenuItem autoWidth = new MenuItem(cmdMenu, SWT.PUSH);
			autoWidth.setText("自适应列宽");
			autoWidth.addSelectionListener(new ControlSelectionListener(ev -> this.updateWindowRedraw(this::autoWidth)));

			MenuItem clearSort = new MenuItem(cmdMenu, SWT.PUSH);
			clearSort.setText("默认顺序");
			clearSort.addSelectionListener(new ControlSelectionListener(ev -> this.updateWindowRedraw(this::clearSort)));
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		if (this.needUpdate(type) && this.getShell().isVisible()) {
			this.updateWindowRedraw(this::updateTable);
		}
	}

	/** 清除排序 */
	private void clearSort() {
		this.sortColumns.clear();
		this.table.setSortColumn(null);
		this.updateTable();
	}

	/** 自适应列宽 */
	private void autoWidth() {
		ToolUtils.forEach(this.table.getColumns(), TableColumn::pack);
	}

	private void updateTable() {
		int top = this.table.getTopIndex();
		ToolUtils.forEach(this.table.getItems(), TableItem::dispose);

		//更新数据
		this.updateData(this.datas);
		this.datas.removeIf(this.filter);
		this.sortColumns.forEach(sortColumn -> {//多级排序
			for (TableColumnManager tcm : this.tcms) {
				if (tcm.stc.tableColumn == sortColumn) {
					this.datas.sort(tcm.stc::compare);
					break;
				}
			}
		});
		for (int row = 0; row < this.datas.size(); row++) {
			T data = this.datas.get(row);
			DataTableItem tableItem = new DataTableItem(data);
			for (int col = 0; col < this.tcms.size(); col++) {
				TableColumnManager tcm = this.tcms.get(col);
				tableItem.setText(col, tcm.getValue(row + 1, data));
			}
		}
		this.datas.clear();

		if (this.table.getData("packed") == null) {//只自动pack一次
			this.autoWidth();
			this.table.setData("packed", "");
		}
		this.table.setTopIndex(top);
	}

	/*------------------------------------------------------------------------------------------------------------------------*/

	public ControlSelectionListener getUpdateTableListener() {
		return this.updateTableListener;
	}

	/** 时候remove某条data */
	protected Predicate<T> initFilter() {
		return data -> false;
	}

	protected abstract void initTCMS(List<AbstractTable<T>.TableColumnManager> tcms);

	protected abstract void updateData(List<T> datas);

	/** {@link GlobalContext}接收到类型为type的数据,更新了全局数据后,是否自动更新此table */
	protected boolean needUpdate(DataType type) {
		return false;
	}

	@Override
	public int getShellStyle() {
		return super.getShellStyle() | SWT.MAX;
	}

	/** 隐藏之前清空 */
	@Override
	protected void handlerAfterHidden() {
		ToolUtils.forEach(this.table.getItems(), TableItem::dispose);
	}

	/** 显示之前更新 */
	@Override
	protected void handlerBeforeDisplay() {
		this.updateTable();
	}

	public class DataTableItem extends TableItem {
		public final T data;

		public DataTableItem(T data) {
			super(AbstractTable.this.table, SWT.NONE);
			this.data = data;
		}

		public DataTableItem(T data, int index) {
			super(AbstractTable.this.table, SWT.NONE, index);
			this.data = data;
		}

		@Override
		protected void checkSubclass() {}
	}

	/**
	 * table列的属性
	 * @author MoeKagari
	 */
	public class TableColumnManager {
		private Comparator<T> compa = null;
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

		public void setComparator(Comparator<T> compa) {
			this.compa = compa;
		}
	}

	/**
	 * table列的排列功能
	 * @author MoeKagari
	 */
	private class SortedTableColumn implements Listener {
		private boolean direction = true;//是否从小到大排序,否则从大到小排序
		private final int index;
		private final TableColumnManager tcm;
		private final TableColumn tableColumn;

		public SortedTableColumn(int index, TableColumnManager tcm) {
			this.index = index;
			this.tcm = tcm;
			tcm.stc = this;

			this.tableColumn = new TableColumn(AbstractTable.this.table, SWT.LEFT);
			this.tableColumn.setText(tcm.name);
			this.tableColumn.setWidth(40);
			this.tableColumn.addListener(SWT.Selection, this);
		}

		@Override
		public void handleEvent(Event ev) {
			if (this.index == 0) return;//不对行头排序
			AbstractTable.this.sortColumns.remove(this.tableColumn);
			AbstractTable.this.sortColumns.add(this.tableColumn);

			if (AbstractTable.this.table.getSortColumn() == this.tableColumn) {
				this.direction = !this.direction;//如果不是改变排序列,则改变排序方向
			}

			AbstractTable.this.updateWindowRedraw(this::sortTable);
			AbstractTable.this.table.setSortColumn(this.tableColumn);
			AbstractTable.this.table.setSortDirection(this.direction ? SWT.DOWN : SWT.UP);
		}

		/* 排序table */
		@SuppressWarnings("unchecked")
		private void sortTable() {
			TableItem[] tableItems = AbstractTable.this.table.getItems();
			Arrays.sort(tableItems, (a, b) -> this.compare((DataTableItem) a, (DataTableItem) b));
			for (TableItem tableItem : tableItems) {
				T data = ((DataTableItem) tableItem).data;
				String[] values = ToolUtils.toStringArray(AbstractTable.this.tcms.size(), tableItem::getText);

				tableItem.dispose();
				new DataTableItem(data).setText(values);
			}
			//刷新行号
			ToolUtils.forEach(AbstractTable.this.table.getItems(), (tableItem, index) -> tableItem.setText(0, String.valueOf(index + 1)));
		}

		public int compare(DataTableItem item1, DataTableItem item2) {
			if (this.tcm.compa != null) {
				return this.compare(item1.data, item2.data);
			} else {
				return this.compare(item1.getText(this.index), item2.getText(this.index));
			}
		}

		public int compare(T data1, T data2) {
			if (this.tcm.compa != null) {
				return (this.direction ? -1 : 1) * this.tcm.compa.compare(data1, data2);
			} else {
				return this.compare(this.tcm.value.apply(data1).toString(), this.tcm.value.apply(data2).toString());
			}
		}

		public int compare(String value1, String value2) {
			int direction = this.direction ? -1 : 1;
			if (this.tcm.isInteger) {
				int a = StringUtils.isBlank(value1) ? 0 : Integer.parseInt(value1);
				int b = StringUtils.isBlank(value2) ? 0 : Integer.parseInt(value2);
				return direction * Integer.compare(a, b);
			} else {
				return direction * value1.compareTo(value2);
			}
		}
	}
}
