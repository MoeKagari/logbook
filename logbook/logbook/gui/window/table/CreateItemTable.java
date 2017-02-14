package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.data.DataType;
import logbook.context.dto.data.record.CreateItemDto;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.logic.TimeString;
import logbook.gui.logic.data.ItemDataMap;
import logbook.gui.logic.data.ItemDataMap.ItemData;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

/**
 * 开发记录
 * @author MoeKagari
 */
public class CreateItemTable extends RecordTable<CreateItemDto> {

	public CreateItemTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<CreateItemDto>> tcms) {
		tcms.add(new TableColumnManager<>("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		tcms.add(new TableColumnManager<>("状态", rd -> rd.isSuccess() ? "成功" : "失败"));
		tcms.add(new TableColumnManager<>("装备", rd -> {
			if (rd.isSuccess() == false) return "";
			ItemData itemData = ItemDataMap.get(rd.getSlotitemId());
			if (itemData == null) return "";
			return itemData.getName();
		}));
		tcms.add(new TableColumnManager<>("油", rd -> Integer.toString(-rd.getMaterial()[0])));
		tcms.add(new TableColumnManager<>("弹", rd -> Integer.toString(-rd.getMaterial()[1])));
		tcms.add(new TableColumnManager<>("钢", rd -> Integer.toString(-rd.getMaterial()[2])));
		tcms.add(new TableColumnManager<>("铝", rd -> Integer.toString(-rd.getMaterial()[3])));
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
