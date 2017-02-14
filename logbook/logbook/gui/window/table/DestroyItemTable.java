package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.data.DataType;
import logbook.context.dto.data.record.DestroyItemDto;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

public class DestroyItemTable extends RecordTable<DestroyItemDto> {

	public DestroyItemTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<DestroyItemDto>> tcms) {
		tcms.add(new TableColumnManager<>("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager<>("事件", DestroyItemDto::getEvent));
		tcms.add(new TableColumnManager<>("ID", DestroyItemDto::getId));
		tcms.add(new TableColumnManager<>("装备", DestroyItemDto::getName));
		tcms.add(new TableColumnManager<>("改修等级", rd -> {
			int level = rd.getLv();
			return level > 0 ? level : "";
		}));
		tcms.add(new TableColumnManager<>("熟练度", rd -> {
			int alv = rd.getAlv();
			return alv > 0 ? alv : "";
		}));
		tcms.add(new TableColumnManager<>("组", DestroyItemDto::getGroup));
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
