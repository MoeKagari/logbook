package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import logbook.context.dto.data.MasterDataDto.MasterShipDataDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.translator.MasterDataDtoTranslator;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.util.ToolUtils;

/**
 * 所有舰娘
 * @author MoeKagari
 */
public class ShipListTable extends AbstractTable<ShipDto> {

	public ShipListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected boolean haveToolBar() {
		return true;
	}

	@Override
	protected void initFilters(ArrayList<Predicate<ShipDto>> filters) {
		ToolItem all = this.newToolItem(SWT.CHECK, "全");
		all.setSelection(true);
		filters.add(ship -> all.getSelection());

		this.addFilter(filters, "駆逐艦", 2);
		this.addFilter(filters, "軽巡洋艦", 3);
		this.addFilter(filters, "重雷装巡洋艦", 4);
		this.addFilter(filters, "重巡洋艦", 5);
		this.addFilter(filters, "航空巡洋艦", 6);
		this.addFilter(filters, "軽空母", 7);
		this.addFilter(filters, "正規空母", 11);
		this.addFilter(filters, "装甲空母", 18);
		this.addFilter(filters, "戦艦", 9);
		this.addFilter(filters, "巡洋戦艦", 8);
		this.addFilter(filters, "航空戦艦", 10);
		this.addFilter(filters, "超弩級戦艦", 12);
		this.addFilter(filters, "潜水艦", 13);
		this.addFilter(filters, "潜水空母", 14);
		this.addFilter(filters, "水上機母艦", 16);
		this.addFilter(filters, "其它", 1, 17, 19, 20, 21, 22);
	}

	private void addFilter(ArrayList<Predicate<ShipDto>> filters, String text, int... types) {
		ToolUtils.notNullThenHandle(this.newToolItem(SWT.CHECK, text), toolItem -> {
			filters.add(ship -> {
				if (toolItem.getSelection() == false) return false;
				MasterShipDataDto msdd = MasterDataDtoTranslator.getMasterShipDataDto(ship.getShipId());
				return Arrays.stream(types).anyMatch(i -> msdd == null ? false : i == msdd.getType());
			});
		});
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("ID", true, ShipDto::getId));
		tcms.add(new TableColumnManager("舰娘", rd -> ShipDtoTranslator.getName(rd)));
		tcms.add(new TableColumnManager("舰种", rd -> ShipDtoTranslator.getType(rd)));
		tcms.add(new TableColumnManager("等级", true, ShipDto::getLv));
		tcms.add(new TableColumnManager("现有经验", true, ShipDto::getCurrentExp));
		tcms.add(new TableColumnManager("升级所需", true, ShipDto::getNextExp));
		tcms.add(new TableColumnManager("现在耐久", true, ShipDto::getNowHp));
		tcms.add(new TableColumnManager("最大耐久", true, ShipDto::getMaxHp));
		tcms.add(new TableColumnManager("Cond", true, ShipDto::getCond));
		tcms.add(new TableColumnManager("速力", rd -> ShipDtoTranslator.getSokuString(rd)));
		tcms.add(new TableColumnManager("增设", rd -> rd.getSlotex() != 0 ? "有" : ""));
		tcms.add(new TableColumnManager("Lock", rd -> rd.isLocked() ? "有" : ""));
	}

	@Override
	protected void updateData(List<ShipDto> datas) {
		datas.addAll(GlobalContext.getShipMap().values());
		Collections.sort(datas, (a, b) -> Integer.compare(a.getId(), b.getId()));
	}
}
