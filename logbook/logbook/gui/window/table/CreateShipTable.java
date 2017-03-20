package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.record.CreateshipDto;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.util.ToolUtils;

/**
 * 建造记录
 * @author MoeKagari
 */
public class CreateShipTable extends AbstractTable<CreateshipDto> {

	public CreateShipTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager("舰娘", rd -> ShipDtoTranslator.getName(rd.getShipId())));
		tcms.add(new TableColumnManager("油", true, rd -> Integer.toString(rd.cost()[0])));
		tcms.add(new TableColumnManager("弹", true, rd -> Integer.toString(rd.cost()[1])));
		tcms.add(new TableColumnManager("钢", true, rd -> Integer.toString(rd.cost()[2])));
		tcms.add(new TableColumnManager("铝", true, rd -> Integer.toString(rd.cost()[3])));
		tcms.add(new TableColumnManager("开发资材", true, rd -> Integer.toString(rd.zhicai())));
		tcms.add(new TableColumnManager("大型建造", rd -> rd.largeflag() ? "是" : ""));
		tcms.add(new TableColumnManager("高速建造", rd -> rd.highspeed() ? "是" : ""));
		tcms.add(new TableColumnManager("秘书舰", rd -> ToolUtils.notNullThenHandle(rd.getFlagship(), ToolUtils::returnOneself, "")));
		tcms.add(new TableColumnManager("秘书舰LV", rd -> ToolUtils.ifHandle(rd.getFlagship() != null, () -> String.valueOf(rd.getFlagshipLevel()), "")));
		tcms.add(new TableColumnManager("空渠", true, rd -> String.valueOf(rd.getEmptyCount())));
	}

	@Override
	protected void updateData(List<CreateshipDto> datas) {
		datas.addAll(GlobalContext.getCreateshiplist());
	}
}
