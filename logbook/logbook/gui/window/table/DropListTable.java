package logbook.gui.window.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.AbstractInfoBattleResult.BattleResult_GetShip;
import logbook.context.dto.battle.AbstractInfoBattleStartNext;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.translator.BattleDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;

/**
 * 掉落记录
 * @author MoeKagari
 */
public class DropListTable extends AbstractTable<DropListTable.SortDrop> {

	public DropListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> AppConstants.TABLE_TIME_FORMAT.format(new Date(rd.getTime()))));
		tcms.add(new TableColumnManager("地图", SortDrop::getMap));
		tcms.add(new TableColumnManager("Cell", SortDrop::getCell));
		tcms.add(new TableColumnManager("Boss", rd -> rd.isBoss() ? "是" : ""));
		tcms.add(new TableColumnManager("评价", SortDrop::getRank));
		tcms.add(new TableColumnManager("舰种", SortDrop::getShipType));
		tcms.add(new TableColumnManager("舰名", SortDrop::getShipName));

	}

	@Override
	protected void updateData(List<SortDrop> datas) {
		Iterator<BattleDto> it = GlobalContext.getBattlelist().getBattleList().iterator();
		Supplier<BattleDto> next = () -> it.hasNext() ? it.next() : null;

		BattleDto battle = null;
		while (it.hasNext()) {
			if (battle instanceof AbstractInfoBattleStartNext == false) {
				battle = next.get();
				continue;
			}
			AbstractInfoBattleStartNext battleStartNext = (AbstractInfoBattleStartNext) battle;

			battle = next.get();
			if (battle instanceof AbstractBattle == false) continue;
			boolean haveDamage = BattleDtoTranslator.haveDamage((AbstractBattle) battle);

			battle = next.get();
			if (battle instanceof AbstractBattle) {
				haveDamage |= BattleDtoTranslator.haveDamage((AbstractBattle) battle);
				battle = next.get();
			}
			if (battle instanceof AbstractInfoBattleResult == false) continue;
			AbstractInfoBattleResult battleResult = (AbstractInfoBattleResult) battle;

			datas.add(new SortDrop(battleStartNext, haveDamage, battleResult));
		}
	}

	public class SortDrop {
		private final long time;
		private final String map, shipType, shipName, rank;
		private final int cell;
		private final boolean isBoss;
		private final int shipId;

		public SortDrop(AbstractInfoBattleStartNext battleStartNext, boolean haveDamage, AbstractInfoBattleResult battleResult) {
			this.time = battleResult.getTime();
			this.map = battleStartNext.getMapString();
			this.cell = battleStartNext.getNext();
			this.isBoss = battleStartNext.isBoss();
			this.rank = (!haveDamage && battleResult.getRank().startsWith("S")) ? "S完全胜利" : battleResult.getRank();

			BattleResult_GetShip newShip = battleResult.getNewShip();
			if (newShip != null) {
				this.shipId = newShip.getId();
				this.shipType = newShip.getType();
				this.shipName = newShip.getName();
			} else {
				this.shipId = -1;
				this.shipType = "";
				this.shipName = "";
			}
		}

		public boolean isBoss() {
			return this.isBoss;
		}

		public long getTime() {
			return this.time;
		}

		public String getMap() {
			return this.map;
		}

		public int getCell() {
			return this.cell;
		}

		public String getRank() {
			return this.rank;
		}

		public int getShipId() {
			return this.shipId;
		}

		public String getShipType() {
			return this.shipType;
		}

		public String getShipName() {
			return this.shipName;
		}
	}
}
