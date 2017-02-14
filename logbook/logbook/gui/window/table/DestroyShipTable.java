package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.data.DataType;
import logbook.context.dto.data.record.DestroyShipDto;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

public class DestroyShipTable extends RecordTable<DestroyShipDto> {

	public DestroyShipTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<DestroyShipDto>> tcms) {
		tcms.add(new TableColumnManager<>("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager<>("事件", DestroyShipDto::getEvent));
		tcms.add(new TableColumnManager<>("ID", DestroyShipDto::getId));
		tcms.add(new TableColumnManager<>("舰娘", DestroyShipDto::getName));
		tcms.add(new TableColumnManager<>("等级", DestroyShipDto::getLevel));
	}

	@Override
	protected List<DestroyShipDto> getList() {
		return GlobalContext.getDestroyshiplist();
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return this.isVisible() && type == DataType.DESTROYSHIP;
	}

}