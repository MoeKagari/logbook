package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.dto.data.ShipDto;
import logbook.gui.logic.ShipTranslator;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

/**
 * 所有舰娘
 * @author MoeKagari
 */
public class ShipListTable extends RecordTable<ShipDto> {

	public ShipListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<ShipDto>> tcms) {
		tcms.add(new TableColumnManager<>("ID", ShipDto::getId));
		tcms.add(new TableColumnManager<>("舰娘", ShipDto::getName));
		tcms.add(new TableColumnManager<>("等级", ShipDto::getLv));
		tcms.add(new TableColumnManager<>("现有经验", ShipDto::getCurrentExp));
		tcms.add(new TableColumnManager<>("升级所需", ShipDto::getNextExp));
		tcms.add(new TableColumnManager<>("现在耐久", ShipDto::getNowHP));
		tcms.add(new TableColumnManager<>("最大耐久", ShipDto::getMaxHp));
		tcms.add(new TableColumnManager<>("Cond", ShipDto::getCond));
		tcms.add(new TableColumnManager<>("速力", rd -> ShipTranslator.getSokuString(rd)));
		tcms.add(new TableColumnManager<>("Lock", rd -> rd.isLocked() ? "有" : ""));
	}

	@Override
	protected List<ShipDto> getList() {
		return new ArrayList<>(GlobalContext.getShipmap().values());
	}

}
