package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.info.InfoBattleStartDto;
import logbook.context.update.GlobalContext;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;

/**
 * 战斗记录
 * @author MoeKagari
 */
public class BattleListTable extends AbstractTable<BattleListTable.SortBattle> {

	public BattleListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("时间", rd -> AppConstants.TABLE_TIME_FORMAT.format(new Date(rd.getTime()))));
		tcms.add(new TableColumnManager("舰队", SortBattle::getFleet));
		tcms.add(new TableColumnManager("地图", SortBattle::getMap));
		tcms.add(new TableColumnManager("起点", true, SortBattle::getStart));
	}

	@Override
	protected void updateData(List<SortBattle> datas) {
		datas.addAll(GlobalContext.getBattlelist().getBattleList().stream().filter(BattleDto::isStart).map(SortBattle::new).collect(Collectors.toList()));
	}

	public class SortBattle {
		private final InfoBattleStartDto battle;

		public SortBattle(BattleDto battle) {
			this.battle = (InfoBattleStartDto) battle;
		}

		public String getFleet() {
			return this.battle.isCombined() ? "联合舰队" : AppConstants.DEFAULT_FLEET_NAME[this.battle.getDeckId() - 1];
		}

		public int getStart() {
			return this.battle.getStart();
		}

		public long getTime() {
			return this.battle.getTime();
		}

		public String getMap() {
			return this.battle.getMapString();
		}
	}
}
