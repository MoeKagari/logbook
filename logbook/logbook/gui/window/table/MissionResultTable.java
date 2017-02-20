package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.record.MissionResultDto;
import logbook.context.dto.data.record.MissionResultDto.MissionResultItem;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.AbstractTable;

/**
 * 远征记录
 * @author MoeKagari
 */
public class MissionResultTable extends AbstractTable<MissionResultDto> {

	public MissionResultTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager("远征", rd -> rd.getName()));
		tcms.add(new TableColumnManager("状态", rd -> rd.getStateString()));
		Function<Integer, String> materialString = material -> material == 0 ? "" : String.valueOf(material);
		tcms.add(new TableColumnManager("油", rd -> materialString.apply(rd.getMaterial()[0])));
		tcms.add(new TableColumnManager("弹", rd -> materialString.apply(rd.getMaterial()[1])));
		tcms.add(new TableColumnManager("钢", rd -> materialString.apply(rd.getMaterial()[2])));
		tcms.add(new TableColumnManager("铝", rd -> materialString.apply(rd.getMaterial()[3])));
		BiFunction<MissionResultItem, Function<MissionResultItem, String>, String> materialItemString = (item, fun) -> item == null ? "" : fun.apply(item);
		tcms.add(new TableColumnManager("道具1", rd -> materialItemString.apply(rd.getItems()[0], item -> String.valueOf(item.getName()))));
		tcms.add(new TableColumnManager("数量", rd -> materialItemString.apply(rd.getItems()[0], item -> String.valueOf(item.getCount()))));
		tcms.add(new TableColumnManager("道具2", rd -> materialItemString.apply(rd.getItems()[1], item -> String.valueOf(item.getName()))));
		tcms.add(new TableColumnManager("数量", rd -> materialItemString.apply(rd.getItems()[1], item -> String.valueOf(item.getCount()))));
	}

	@Override
	protected List<MissionResultDto> getList() {
		return GlobalContext.getMissionlist();
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return this.isVisible() && type == DataType.MISSIONRESULT;
	}

}
