package logbook.gui.window.table;

import java.util.List;
import java.util.function.IntFunction;

import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.dto.memory.MissionResultDto;
import logbook.dto.memory.MissionResultDto.MissionResultItem;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.update.GlobalContext;
import logbook.util.ToolUtils;

/**
 * 远征记录
 * @author MoeKagari
 */
public class MissionResultTable extends AbstractTable<MissionResultDto> {

	public MissionResultTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager("舰队", rd -> AppConstants.DEFAULT_FLEET_NAME[rd.getDeckId() - 1]));
		tcms.add(new TableColumnManager("远征", rd -> rd.getName()));
		tcms.add(new TableColumnManager("状态", rd -> rd.getStateString()));
		IntFunction<String> materialString = count -> count == 0 ? "" : String.valueOf(count);
		tcms.add(new TableColumnManager("油", true, rd -> materialString.apply(rd.getMaterial()[0])));
		tcms.add(new TableColumnManager("弹", true, rd -> materialString.apply(rd.getMaterial()[1])));
		tcms.add(new TableColumnManager("钢", true, rd -> materialString.apply(rd.getMaterial()[2])));
		tcms.add(new TableColumnManager("铝", true, rd -> materialString.apply(rd.getMaterial()[3])));
		tcms.add(new TableColumnManager("道具1", rd -> ToolUtils.notNullThenHandle(rd.getItems()[0], MissionResultItem::getId, "")));
		tcms.add(new TableColumnManager("数量", true, rd -> ToolUtils.notNullThenHandle(rd.getItems()[0], MissionResultItem::getCount, "")));
		tcms.add(new TableColumnManager("道具2", rd -> ToolUtils.notNullThenHandle(rd.getItems()[1], MissionResultItem::getId, "")));
		tcms.add(new TableColumnManager("数量", true, rd -> ToolUtils.notNullThenHandle(rd.getItems()[1], MissionResultItem::getCount, "")));
	}

	@Override
	protected void updateData(List<MissionResultDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof MissionResultDto) {
				datas.add((MissionResultDto) memory);
			}
		});
	}
}
