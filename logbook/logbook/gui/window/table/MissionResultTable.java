package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.data.DataType;
import logbook.context.dto.data.record.MissionResultDto;
import logbook.context.dto.data.record.MissionResultDto.MissionResultItem;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

/**
 * 远征记录
 * @author MoeKagari
 */
public class MissionResultTable extends RecordTable<MissionResultDto> {

	public MissionResultTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<MissionResultDto>> tcms) {
		tcms.add(new TableColumnManager<>("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager<>("远征", rd -> rd.getName()));
		tcms.add(new TableColumnManager<>("状态", rd -> rd.getStateString()));
		tcms.add(new TableColumnManager<>("油", rd -> rd.getMaterial()[0]));
		tcms.add(new TableColumnManager<>("弹", rd -> rd.getMaterial()[1]));
		tcms.add(new TableColumnManager<>("钢", rd -> rd.getMaterial()[2]));
		tcms.add(new TableColumnManager<>("铝", rd -> rd.getMaterial()[3]));
		tcms.add(new TableColumnManager<>("道具1", rd -> {
			MissionResultItem item = rd.getItems()[0];
			return item != null ? item.getName() : "";
		}));
		tcms.add(new TableColumnManager<>("数量", rd -> {
			MissionResultItem item = rd.getItems()[0];
			return item != null ? item.getCount() : "";
		}));
		tcms.add(new TableColumnManager<>("道具2", rd -> {
			MissionResultItem item = rd.getItems()[1];
			return item != null ? item.getName() : "";
		}));
		tcms.add(new TableColumnManager<>("数量", rd -> {
			MissionResultItem item = rd.getItems()[1];
			return item != null ? item.getCount() : "";
		}));
	}

	@Override
	protected List<MissionResultDto> getList() {
		return GlobalContext.getMissionresulutlist();
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return this.isVisible() && type == DataType.MISSIONRESULT;
	}

}
