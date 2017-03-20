package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.record.DestroyShipDto;
import logbook.context.update.GlobalContext;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;

public class DestroyShipTable extends AbstractTable<DestroyShipDto> {

	public DestroyShipTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager("事件", DestroyShipDto::getEvent));
		tcms.add(new TableColumnManager("舰娘", DestroyShipDto::getName));
		tcms.add(new TableColumnManager("ID", true, DestroyShipDto::getId));
		tcms.add(new TableColumnManager("等级", true, DestroyShipDto::getLevel));
	}

	@Override
	protected void updateData(List<DestroyShipDto> datas) {
		datas.addAll(GlobalContext.getDestroyshiplist());
	}
}
