package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.data.DataType;
import logbook.context.dto.data.record.CreateshipDto;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.logic.TimeString;
import logbook.gui.logic.data.ShipDataMap;
import logbook.gui.logic.data.ShipDataMap.ShipData;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

/**
 * 建造记录
 * @author MoeKagari
 */
public class CreateShipTable extends RecordTable<CreateshipDto> {

	public CreateShipTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<CreateshipDto>> tcms) {
		tcms.add(new TableColumnManager<>("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager<>("舰娘", rd -> {
			ShipData shipData = ShipDataMap.get(rd.getShipId());
			if (shipData == null) return "";
			return shipData.getName();
		}));
		tcms.add(new TableColumnManager<>("油", rd -> Integer.toString(rd.cost()[0])));
		tcms.add(new TableColumnManager<>("弹", rd -> Integer.toString(rd.cost()[1])));
		tcms.add(new TableColumnManager<>("钢", rd -> Integer.toString(rd.cost()[2])));
		tcms.add(new TableColumnManager<>("铝", rd -> Integer.toString(rd.cost()[3])));
		tcms.add(new TableColumnManager<>("开发资材", rd -> Integer.toString(rd.zhicai())));
		tcms.add(new TableColumnManager<>("大型建造", rd -> rd.largeflag() ? "是" : ""));
		tcms.add(new TableColumnManager<>("高速建造", rd -> rd.highspeed() ? "是" : ""));
	}

	@Override
	protected List<CreateshipDto> getList() {
		return GlobalContext.getCreateshiplist();
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return this.isVisible() && type == DataType.CREATESHIP;
	}

}
