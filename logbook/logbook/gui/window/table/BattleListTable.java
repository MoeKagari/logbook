package logbook.gui.window.table;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.dto.memory.battle.info.InfoBattleStartDto;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.update.GlobalContext;

/**
 * 战斗记录
 * @author MoeKagari
 */
public class BattleListTable extends AbstractTable<BattleListTable.SortBattle> {

	public BattleListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("时间", rd -> AppConstants.TABLE_TIME_FORMAT.format(new Date(rd.getTime()))));
		tcms.add(new TableColumnManager("舰队", SortBattle::getFleet));
		tcms.add(new TableColumnManager("地图", SortBattle::getMap));
		tcms.add(new TableColumnManager("起点", true, SortBattle::getStart));
	}

	@Override
	protected void updateData(List<SortBattle> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof InfoBattleStartDto) {
				datas.add(new SortBattle((InfoBattleStartDto) memory));
			}
		});
	}

	public class SortBattle {
		private final InfoBattleStartDto battle;

		public SortBattle(InfoBattleStartDto battle) {
			this.battle = battle;
		}

		public String getFleet() {
			if (this.battle.isCombined() && this.battle.getDeckId() == 1) {
				return "联合舰队";
			} else {
				return AppConstants.DEFAULT_FLEET_NAME[this.battle.getDeckId() - 1];
			}
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
