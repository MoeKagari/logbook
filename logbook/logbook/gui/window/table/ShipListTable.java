package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.dto.translator.ItemDtoTranslator;
import logbook.dto.translator.ShipDtoTranslator;
import logbook.dto.word.ItemDto;
import logbook.dto.word.MasterDataDto.MasterShipDto;
import logbook.dto.word.ShipDto;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.update.GlobalContext;
import logbook.utils.SwtUtils;
import logbook.utils.ToolUtils;

/**
 * 所有舰娘
 * @author MoeKagari
 */
public abstract class ShipListTable extends AbstractTable<ShipDto> {
	private Composite typeFilterComposite;
	private Composite infoFilterComposite;
	private Button allShipButton;
	private List<Button> typeButtons;
	private List<Button> infoButtons;

	public ShipListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected Predicate<ShipDto> initFilter() {
		this.typeButtons = new ArrayList<>();
		this.infoButtons = new ArrayList<>();
		this.initFilterComposite();
		return this.buildFilter().negate();
	}

	private void initFilterComposite() {
		Composite filterComposite = new Composite(this.getCoolBar(), SWT.NONE);
		filterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filterComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		{
			this.typeFilterComposite = new Composite(filterComposite, SWT.NONE);
			this.typeFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.typeFilterComposite.setLayout(SwtUtils.makeGridLayout(20, 0, 0, 0, 0));
		}
		{
			this.infoFilterComposite = new Composite(filterComposite, SWT.NONE);
			this.infoFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.infoFilterComposite.setLayout(SwtUtils.makeGridLayout(20, 0, 0, 0, 0));
		}
		CoolItem filterCoolItem = new CoolItem(this.getCoolBar(), SWT.NONE);
		filterCoolItem.setControl(filterComposite);
	}

	private Predicate<ShipDto> buildFilter() {
		this.allShipButton = new Button(this.typeFilterComposite, SWT.CHECK);
		this.allShipButton.setText("全舰");
		this.allShipButton.setSelection(true);
		this.allShipButton.addSelectionListener(new ControlSelectionListener(ev -> {
			if (this.allShipButton.getSelection()) {
				this.typeButtons.forEach(button -> button.setSelection(false));
			}
		}));
		this.allShipButton.addSelectionListener(this.getUpdateTableListener());
		Predicate<ShipDto> allShipFilter = ship -> this.allShipButton.getSelection();

		Predicate<ShipDto> typeFilter = allShipFilter//
				.or(this.newTypeFilter("駆逐艦", 2))//
				.or(this.newTypeFilter("軽巡洋艦", 3))//
				.or(this.newTypeFilter("重雷装巡洋艦", 4))//
				.or(this.newTypeFilter("重巡洋艦", 5))//
				.or(this.newTypeFilter("航空巡洋艦", 6))//
				.or(this.newTypeFilter("軽空母", 7))//
				.or(this.newTypeFilter("正規空母", 11))//
				.or(this.newTypeFilter("装甲空母", 18))//
				.or(this.newTypeFilter("戦艦", 8, 9, 10, 12))//
				.or(this.newTypeFilter("潜水艦", 13, 14))//
				.or(this.newTypeFilter("水上機母艦", 16))//
				.or(this.newTypeFilter("其它", 1, 17, 19, 20, 21, 22))//
		;

		Predicate<ShipDto> infoFilter =//
				this.newInfoFilter("没远征", ship -> ToolUtils.isFalse(ShipDtoTranslator.isInMission(ship)))//
						.and(this.newInfoFilter("非LV1", ship -> ship.getLevel() != 1))//
						.and(this.newInfoFilter("婚舰", ship -> ship.getLevel() > 99))//
						.and(this.newInfoFilter("Lock", ShipDto::isLocked))//
						.and(this.newInfoFilter("有增设", ship -> ship.getSlotex() != 0))//
						.and(this.newInfoFilter("有闪", ship -> ship.getCond() > 49))//
						.and(this.newInfoFilter("需入渠", ship -> ToolUtils.isFalse(ShipDtoTranslator.perfectState(ship))))//
						.and(this.newInfoFilter("需补给", ShipDtoTranslator::needHokyo))//
						.and(this.newInfoFilter("可装大发系", ship -> true))//TODO
						.and(this.newInfoFilter("可先制反潜", ship -> true))//TODO
		;

		return infoFilter.and(typeFilter::test);
	}

	private Predicate<ShipDto> newTypeFilter(String text, int... types) {
		Button button = new Button(this.typeFilterComposite, SWT.CHECK);
		button.setText(text);
		this.typeButtons.add(button);
		button.addSelectionListener(new ControlSelectionListener(ev -> {
			if (this.typeButtons.stream().anyMatch(Button::getSelection)) {
				this.allShipButton.setSelection(false);
			}
		}));
		button.addSelectionListener(this.getUpdateTableListener());

		return ship -> {
			if (ToolUtils.isFalse(button.getSelection())) return false;
			MasterShipDto msdd = ship.getMasterData();
			if (msdd == null) return false;
			return Arrays.stream(types).anyMatch(type -> type == msdd.getType());
		};
	}

	private Predicate<ShipDto> newInfoFilter(String text, Predicate<ShipDto> pre) {
		return this.newInfoFilter(text, pre, "");
	}

	private Predicate<ShipDto> newInfoFilter(String text, Predicate<ShipDto> pre, String tooltip) {
		Button button = new Button(this.infoFilterComposite, SWT.CHECK);
		button.setText(text);
		button.setToolTipText(tooltip);
		this.infoButtons.add(button);
		button.addSelectionListener(this.getUpdateTableListener());
		return ship -> ToolUtils.isFalse(button.getSelection()) || pre.test(ship);
	}

	public enum ShipListTableMode {
		INFORMATION,
		PARAMENTER,
		ALL
	}

	protected abstract ShipListTableMode getMode();

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
			case INFORMATION:
				this.initTCMS1(tcms);
				break;
			case PARAMENTER:
				this.initTCMS2(tcms);
				break;
			case ALL:
				this.initTCMS1(tcms);
				this.initTCMS2(tcms);
				break;
		}

		for (int i = 0; i < 5; i++) {
			final int index = i;
			tcms.add(new TableColumnManager("装备" + (index + 1), rd -> {
				ItemDto item = GlobalContext.getItem(index == 4 ? rd.getSlotex() : rd.getSlots()[index]);
				return ToolUtils.notNull(item, ItemDtoTranslator::getNameWithLevel, "");
			}));
		}
	}

	private void initTCMS1(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("补给", rd -> ShipDtoTranslator.needHokyo(rd) ? "需要" : ""));
		{
			TableColumnManager tcm = new TableColumnManager("状态", rd -> ShipDtoTranslator.getStateString(rd, false));
			tcm.setComparator((a, b) -> Double.compare(ShipDtoTranslator.getHPPercent(a), ShipDtoTranslator.getHPPercent(b)));
			tcms.add(tcm);
		}
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
		tcms.add(new TableColumnManager("油耗", true, rd -> ToolUtils.notNull(rd.getMasterData(), MasterShipDto::getFuelMax, "")));
		tcms.add(new TableColumnManager("弹耗", true, rd -> ToolUtils.notNull(rd.getMasterData(), MasterShipDto::getBullMax, "")));
		{
			TableColumnManager tcm = new TableColumnManager("修理时间", rd -> TimeString.toDateRestString(rd.getNdockTime() / 1000, ""));
			tcm.setComparator((a, b) -> Long.compare(a.getNdockTime(), b.getNdockTime()));
			tcms.add(tcm);
		}
		{
			TableColumnManager tcm = new TableColumnManager("修理花费", rd -> ShipDtoTranslator.perfectState(rd) ? "" : Arrays.toString(rd.getNdockCost()));
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
