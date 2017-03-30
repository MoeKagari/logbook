package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.record.DestroyItemDto;
import logbook.context.update.GlobalContext;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;

/**
 * 废弃记录
 * @author MoeKagari
 */
public class DestroyItemTable extends AbstractTable<DestroyItemDto> {

	public DestroyItemTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager("事件", DestroyItemDto::getEvent));
		tcms.add(new TableColumnManager("ID", true, DestroyItemDto::getId));
		tcms.add(new TableColumnManager("装备", DestroyItemDto::getName));
		Function<Integer, String> levelString = level -> level > 0 ? String.valueOf(level) : "";
		tcms.add(new TableColumnManager("改修等级", true, rd -> levelString.apply(rd.getLv())));
		tcms.add(new TableColumnManager("熟练度", true, rd -> levelString.apply(rd.getAlv())));
		tcms.add(new TableColumnManager("组", true, DestroyItemDto::getGroup));
	}

	@Override
	protected void updateData(List<DestroyItemDto> datas) {
		datas.addAll(GlobalContext.getDestroyitemlist());
	}
}
