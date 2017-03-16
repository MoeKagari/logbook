package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.AbstractInfoBattleResult.BattleResult_GetShip;
import logbook.context.dto.battle.AbstractInfoBattleStartNext;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.translator.BattleDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;

public class DropListTable extends AbstractTable<DropListTable.SortDrop> {

	public DropListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> AppConstants.TABLE_TIME_FORMAT.format(new Date(rd.getTime()))));
		tcms.add(new TableColumnManager("地图", SortDrop::getMap));
		tcms.add(new TableColumnManager("Cell", SortDrop::getCell));
		tcms.add(new TableColumnManager("评价", SortDrop::getRank));
		tcms.add(new TableColumnManager("舰种", SortDrop::getShipType));
		tcms.add(new TableColumnManager("舰名", SortDrop::getShipName));

	}

	@Override
	protected boolean needUpdate(DataType type) {
		return type == DataType.BATTLE_RESULT || type == DataType.COMBINEBATTLE_RESULT;
	}

	@Override
	protected void updateData(List<SortDrop> datas) {
		Iterator<BattleDto> it = GlobalContext.getBattlelist().getBattleList().iterator();

		BattleDto battle = null;
		while (it.hasNext()) {
			if (battle instanceof AbstractInfoBattleStartNext == false) {
				if (it.hasNext() == false) break;
				battle = it.next();
				continue;
			}
			String map = ((AbstractInfoBattleStartNext) battle).getMap();
			int cell = ((AbstractInfoBattleStartNext) battle).getNext();

			if (it.hasNext() == false) break;
			battle = it.next();
			if (battle instanceof AbstractBattle == false) continue;

			boolean haveDamage = BattleDtoTranslator.haveDamage((AbstractBattle) battle);
			if (it.hasNext() == false) break;
			battle = it.next();

			if (battle instanceof AbstractBattle) {
				if (it.hasNext() == false) break;
				battle = it.next();
				haveDamage &= BattleDtoTranslator.haveDamage((AbstractBattle) battle);
			}
			if (battle instanceof AbstractInfoBattleResult == false) continue;

			AbstractInfoBattleResult battleResult = (AbstractInfoBattleResult) battle;
			long time = battleResult.getTime();
			String rank = battleResult.getRank();
			rank = (!haveDamage && rank.startsWith("S")) ? "S完全胜利" : rank;

			BattleResult_GetShip newShip = battleResult.getNewShip();
			if (newShip != null) {
				datas.add(new SortDrop(time, map, cell, rank, newShip.getId(), newShip.getType(), newShip.getName()));
			} else {
				datas.add(new SortDrop(time, map, cell, rank, -1, "", ""));
			}
		}
	}

	public class SortDrop {
		private final long time;
		private final String map, shipType, shipName, rank;
		private final int cell;
		private final int shipId;

		public SortDrop(long time, String map, int cell, String rank, int shipId, String shipType, String shipName) {
			this.time = time;
			this.map = map;
			this.cell = cell;
			this.rank = rank;
			this.shipId = shipId;
			this.shipType = shipType;
			this.shipName = shipName;
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
