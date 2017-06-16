package logbook.gui.window.table;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import logbook.config.AppConstants;
import logbook.dto.translator.ItemDtoTranslator;
import logbook.dto.translator.MasterDataDtoTranslator;
import logbook.dto.translator.ShipDtoTranslator;
import logbook.dto.word.ItemDto;
import logbook.dto.word.MasterDataDto.MasterShipDataDto;
import logbook.dto.word.ShipDto;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.update.GlobalContext;
import logbook.util.ToolUtils;

/**
 * 所有舰娘
 * @author MoeKagari
 */
public abstract class ShipListTable extends AbstractTable<ShipDto> {
	public ShipListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected boolean haveToolBar() {
		return true;
	}

	@Override
	protected Predicate<ShipDto> initFilter() {
		ToolItem all = this.newToolItem(SWT.CHECK, "全");
		all.setSelection(true);
		Predicate<ShipDto> filter = ship -> all.getSelection();

		return filter//
				.or(this.addFilter("駆逐艦", 2))//
				.or(this.addFilter("軽巡洋艦", 3))//
				.or(this.addFilter("重雷装巡洋艦", 4))//
				.or(this.addFilter("重巡洋艦", 5))//
				.or(this.addFilter("航空巡洋艦", 6))//
				.or(this.addFilter("軽空母", 7))//
				.or(this.addFilter("正規空母", 11))//
				.or(this.addFilter("装甲空母", 18))//
				.or(this.addFilter("戦艦", 9))//
				.or(this.addFilter("巡洋戦艦", 8))//
				.or(this.addFilter("航空戦艦", 10))//
				.or(this.addFilter("超弩級戦艦", 12))//
				.or(this.addFilter("潜水艦", 13))//
				.or(this.addFilter("潜水空母", 14))//
				.or(this.addFilter("水上機母艦", 16))//
				.or(this.addFilter("其它", 1, 17, 19, 20, 21, 22))//
		;
	}

	private Predicate<ShipDto> addFilter(String text, int... types) {
		ToolItem toolItem = this.newToolItem(SWT.CHECK, text);
		return ship -> {
			if (toolItem.getSelection() == false) return false;
			MasterShipDataDto msdd = MasterDataDtoTranslator.getMasterShipDataDto(ship.getShipId());
			return msdd == null ? false : Arrays.stream(types).anyMatch(type -> type == msdd.getType());
		};
	}

	protected abstract int getMode();

	@Override
	protected String getWindowConfigKey() {
		return ShipListTable.class.getName() + this.getMode();
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("ID", true, ShipDto::getId));
		tcms.add(new TableColumnManager("舰娘", rd -> ShipDtoTranslator.getName(rd)));
		tcms.add(new TableColumnManager("舰种", rd -> ShipDtoTranslator.getTypeString(rd)));
		tcms.add(new TableColumnManager("等级", true, ShipDto::getLevel));
		tcms.add(new TableColumnManager("所处", rd -> ToolUtils.ifHandle(ShipDtoTranslator.whichDeck(rd), wd -> wd != -1, wd -> AppConstants.DEFAULT_FLEET_NAME[wd], "")));

		switch (this.getMode()) {
			case 1:
				this.initTCMS1(tcms);
				break;
			case 2:
				this.initTCMS2(tcms);
				break;
			case 3:
				this.initTCMS1(tcms);
				this.initTCMS2(tcms);
				break;
		}

		for (int i = 0; i < 5; i++) {
			final int index = i;
			tcms.add(new TableColumnManager("装备" + (index + 1), rd -> {
				ItemDto item = GlobalContext.getItem(index == 4 ? rd.getSlotex() : rd.getSlots()[index]);
				return item == null ? "" : ItemDtoTranslator.getNameWithLevel(item);
			}));
		}
	}

	private void initTCMS1(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("补给", rd -> ShipDtoTranslator.needHokyo(rd) ? "需要" : ""));
		tcms.add(new TableColumnManager("状态", rd -> ShipDtoTranslator.getStateString(rd, false)));
		tcms.add(new TableColumnManager("Cond", true, ShipDto::getCond));
		tcms.add(new TableColumnManager("现有经验", true, ShipDto::getCurrentExp));
		tcms.add(new TableColumnManager("升级所需", true, ShipDto::getNextExp));
		tcms.add(new TableColumnManager("现在耐久", true, ShipDto::getNowHp));
		tcms.add(new TableColumnManager("最大耐久", true, ShipDto::getMaxHp));
		tcms.add(new TableColumnManager("速力", rd -> ShipDtoTranslator.getSokuString(rd, false)));
		tcms.add(new TableColumnManager("增设", rd -> rd.getSlotex() != 0 ? "有" : ""));
		tcms.add(new TableColumnManager("Lock", rd -> rd.isLocked() ? "" : "无"));
		tcms.add(new TableColumnManager("远征中", rd -> ShipDtoTranslator.isInMission(rd) ? "是" : ""));
		tcms.add(new TableColumnManager("入渠中", rd -> ShipDtoTranslator.isInNyukyo(rd) ? "是" : ""));
		tcms.add(new TableColumnManager("油耗", true, rd -> ToolUtils.notNullThenHandle(MasterDataDtoTranslator.getMasterShipDataDto(rd.getShipId()), MasterShipDataDto::getFuelMax, "")));
		tcms.add(new TableColumnManager("弹耗", true, rd -> ToolUtils.notNullThenHandle(MasterDataDtoTranslator.getMasterShipDataDto(rd.getShipId()), MasterShipDataDto::getBullMax, "")));
		{
			TableColumnManager tcm = new TableColumnManager("修理时间", rd -> TimeString.toDateRestString(rd.getNdockTime() / 1000, ""));
			tcm.setComparator((a, b) -> Long.compare(a.getNdockTime(), b.getNdockTime()));
			tcms.add(tcm);
		}
		{
			TableColumnManager tcm = new TableColumnManager("修理花费", rd -> ShipDtoTranslator.getHPPercent(rd) != 1 ? Arrays.toString(rd.getNdockCost()) : "");
			tcm.setComparator((a, b) -> Integer.compare(a.getNdockCost()[0], b.getNdockCost()[0]));
			tcms.add(tcm);
		}
	}

	private void initTCMS2(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("火力", true, rd -> rd.getKaryoku()[0]));
		tcms.add(new TableColumnManager("雷装", true, rd -> rd.getRaisou()[0]));
		tcms.add(new TableColumnManager("对空", true, rd -> rd.getTaiku()[0]));
		tcms.add(new TableColumnManager("装甲", true, rd -> rd.getSoukou()[0]));
		tcms.add(new TableColumnManager("回避", true, rd -> rd.getKaihi()[0]));
		tcms.add(new TableColumnManager("对潜", true, rd -> rd.getTaisen()[0]));
		tcms.add(new TableColumnManager("索敌", true, rd -> rd.getSakuteki()[0]));
		tcms.add(new TableColumnManager("运", true, rd -> rd.getLuck()[0]));
	}

	@Override
	protected void updateData(List<ShipDto> datas) {
		datas.addAll(GlobalContext.getShipMap().values());
		Collections.sort(datas, (a, b) -> Integer.compare(a.getId(), b.getId()));
	}
}
