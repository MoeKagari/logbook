package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.record.CreateItemDto;
import logbook.context.dto.translator.ItemDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;

/**
 * 开发记录
 * @author MoeKagari
 */
public class CreateItemTable extends AbstractTable<CreateItemDto> {

	public CreateItemTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager("状态", rd -> rd.isSuccess() ? "成功" : "失败"));
		tcms.add(new TableColumnManager("装备", rd -> rd.isSuccess() ? ItemDtoTranslator.getName(rd.getSlotitemId()) : ""));
		tcms.add(new TableColumnManager("油", rd -> Integer.toString(rd.getMaterial()[0])));
		tcms.add(new TableColumnManager("弹", rd -> Integer.toString(rd.getMaterial()[1])));
		tcms.add(new TableColumnManager("钢", rd -> Integer.toString(rd.getMaterial()[2])));
		tcms.add(new TableColumnManager("铝", rd -> Integer.toString(rd.getMaterial()[3])));
	}

	@Override
	protected List<CreateItemDto> getList() {
		return GlobalContext.getCreateitemlist();
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return this.isVisible() && type == DataType.CREATEITEM;
	}

}
