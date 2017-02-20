package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.record.DestroyItemDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.AbstractTable;

public class DestroyItemTable extends AbstractTable<DestroyItemDto> {

	public DestroyItemTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager("事件", DestroyItemDto::getEvent));
		tcms.add(new TableColumnManager("ID", DestroyItemDto::getId));
		tcms.add(new TableColumnManager("装备", DestroyItemDto::getName));
		Function<Integer, String> levelString = level -> level > 0 ? String.valueOf(level) : "";
		tcms.add(new TableColumnManager("改修等级", rd -> levelString.apply(rd.getLv())));
		tcms.add(new TableColumnManager("熟练度", rd -> levelString.apply(rd.getAlv())));
		tcms.add(new TableColumnManager("组", DestroyItemDto::getGroup));
	}

	@Override
	protected List<DestroyItemDto> getList() {
		return GlobalContext.getDestroyitemlist();
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return this.isVisible() && (type == DataType.DESTROYITEM || type == DataType.DESTROYSHIP);
	}

}
