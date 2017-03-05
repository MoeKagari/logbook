package logbook.context.dto.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import logbook.config.AppConstants;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.AbstractBattleDay.BattleDayStage;
import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.AbstractBattleMidnight.BattleMidnightStage;
import logbook.context.dto.battle.AbstractInfoBattle;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.AbstractInfoBattleStartNext;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleDto.BattleDeck;
import logbook.context.dto.battle.BattleDto.BattleDeckAttackDamage;
import logbook.context.dto.battle.info.InfoBattleGobackPortDto;
import logbook.context.dto.battle.info.InfoBattleStartAirBaseDto;
import logbook.context.dto.battle.info.InfoBattleStartDto;
import logbook.gui.logic.HPMessage;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class BattleDtoTranslator {

	public static BTResult getBattle(BattleDto battleDto) {
		if (battleDto instanceof AbstractBattle) {
			return newBattleDayMidnight(battleDto);
		}
		if (battleDto instanceof AbstractInfoBattle) {
			if (battleDto instanceof AbstractInfoBattleStartNext) {
				return newBattleStartNext(battleDto);
			}
			if (battleDto instanceof InfoBattleStartAirBaseDto) {
				return newBattleStartAirBase(battleDto);
			}
			if (battleDto instanceof AbstractInfoBattleResult) {
				return newBattleResult(battleDto);
			}
			if (battleDto instanceof InfoBattleGobackPortDto) {
				return newBattleGobackPort(battleDto);
			}
		}
		System.out.println(battleDto == null ? "battleDto == null" : battleDto.getBattleType());
		return null;
	}

	private static BTResult newBattleStartNext(BattleDto battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();

		if (battleDto instanceof InfoBattleStartDto) {
			InfoBattleStartDto battleStartDto = (InfoBattleStartDto) battleDto;
			String text = AppConstants.DEFAULT_FLEET_NAME[battleStartDto.getDeckId() - 1] + " → " + battleStartDto.getMap();
			deckInformations.add(text);

			text = "起点:" + battleStartDto.getStart();
			deckInformations.add(text);
		}
		if (battleDto instanceof AbstractInfoBattleStartNext) {
			AbstractInfoBattleStartNext battleNextDto = (AbstractInfoBattleStartNext) battleDto;
			String text = "地图:" + battleNextDto.getMap() + ",Cell:" + battleNextDto.getNext() + "(" + battleNextDto.getNextType() + (battleNextDto.isGoal() ? ",终点" : "") + ")";
			deckInformations.add(text);
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleResult(BattleDto battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();

		AbstractInfoBattleResult battleResult = (AbstractInfoBattleResult) battleDto;
		String text = "战斗结果:" + battleResult.getRank();
		deckInformations.add(text);

		if (battleResult.haveNewShip()) {
			text = battleResult.getNewShipTypeName() + " 加入镇守府";
			deckInformations.add(text);
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleDayMidnight(BattleDto battleDto) {
		ArrayList<String[]> before = new ArrayList<>();
		ArrayList<String[]> after = new ArrayList<>();

		BiConsumer<BattleDeck, BattleDeckAttackDamage> addOneState = (bd, bdad) -> {
			if (bd != null && bd.exist()) {
				int[] nowhps = bd.getNowhp();
				int[] maxhps = bd.getMaxhp();
				int[] dmgs = bdad.getDamage();
				int count = bd.getShipCount();

				String[] oneStateBefore = new String[count];
				String[] oneStateAfter = new String[count];
				for (int i = 0; i < count; i++) {
					oneStateBefore[i] = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(nowhps[i] * 1.0 / maxhps[i]);
					oneStateAfter[i] = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString((nowhps[i] - dmgs[i]) * 1.0 / maxhps[i]);
				}
				before.add(oneStateBefore);
				after.add(oneStateAfter);
			}
		};

		if (battleDto instanceof AbstractBattleDay) {
			AbstractBattleDay battleDay = (AbstractBattleDay) battleDto;
			addOneState.accept(battleDay.getfDeck(), battleDay.getfDeckAttackDamage());
			addOneState.accept(battleDay.getfDeckCombine(), battleDay.getfDeckCombineAttackDamage());
			addOneState.accept(battleDay.geteDeck(), battleDay.geteDeckAttackDamage());
			addOneState.accept(battleDay.geteDeckCombine(), battleDay.geteDeckCombineAttackDamage());
		}
		if (battleDto instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight battleMidnight = (AbstractBattleMidnight) battleDto;
			BattleDeck[] activeDecks = battleMidnight.getActiveDeck();
			BattleMidnightStage battleMidnightStage = battleMidnight.getBattleMidnightStage();
			addOneState.accept(activeDecks[0], battleMidnightStage.getfAttackDamage());
			addOneState.accept(activeDecks[1], battleMidnightStage.geteAttackDamage());
		}

		return new BTResult(null, before, after);
	}

	private static BTResult newBattleGobackPort(BattleDto battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();
		deckInformations.add("大破的舰娘已退避");
		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleStartAirBase(BattleDto battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String[] NUMBERS = new String[] { "一", "二", "三", "四", "五", "六" };
		InfoBattleStartAirBaseDto battleStartAirBaseDto = (InfoBattleStartAirBaseDto) battleDto;
		int[][] strikePoints = battleStartAirBaseDto.getStrikePoint();
		for (int i = 0; i < strikePoints.length; i++) {
			String number = NUMBERS[i];
			int[] strikePoint = strikePoints[i];
			ToolUtils.notNullThenHandle(strikePoint, sp -> deckInformations.add("第" + number + "基地航空队 -> " + Arrays.toString(sp)));
		}

		return new BTResult(deckInformations, null, null);
	}

	public static class BTResult {
		private final ArrayList<String> deckInformations;
		private final ArrayList<String[]> before;
		private final ArrayList<String[]> after;

		public BTResult(ArrayList<String> deckInformations, ArrayList<String[]> before, ArrayList<String[]> after) {
			this.deckInformations = deckInformations;
			this.before = before;
			this.after = after;
		}

		public ArrayList<String> getDeckInformations() {
			return this.deckInformations;
		}

		public ArrayList<String[]> getBefore() {
			return this.before;
		}

		public ArrayList<String[]> getAfter() {
			return this.after;
		}
	}

	/*------------------------------------------------------------------------------*/

	public static void createBattleFlow(Composite parent, BattleDto battleDto) {
		if (battleDto instanceof AbstractBattle) {
			AbstractBattle battle = (AbstractBattle) battleDto;

			SwtUtils.initLabel(new Label(parent, SWT.LEFT), battle.getBattleType().toString(), new GridData(GridData.FILL_HORIZONTAL));
			SwtUtils.insertBlank(parent);
			SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗结束后-各船状态", new GridData(GridData.FILL_HORIZONTAL));
			addShipState(parent, battle);
			SwtUtils.insertBlank(parent);
			SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗信息", new GridData(GridData.FILL_HORIZONTAL));
			addBattleInformation(parent, battle);
			SwtUtils.insertBlank(parent);
			SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗Stage", new GridData(GridData.FILL_HORIZONTAL));
			if (battle instanceof AbstractBattleDay) {
				AbstractBattleDay battleDay = ((AbstractBattleDay) battle);
				battleDay.getBattleDayStage().forEach(stage -> addBattleDayStage(parent, battleDay, stage));
			}
			if (battle instanceof AbstractBattleMidnight) {
				AbstractBattleMidnight battleMidnight = ((AbstractBattleMidnight) battle);
				addBattleMidnightStage(parent, battleMidnight, battleMidnight.getBattleMidnightStage());
			}
		}
	}

	private static void addShipState(Composite parent, AbstractBattle battle) {
		BiConsumer<BattleDeck, BattleDeckAttackDamage> addOneHPState = (bd, bdad) -> {
			if (bd == null) return;

			Table table = new Table(parent, SWT.HIDE_SELECTION | SWT.NO_SCROLL | SWT.VERTICAL);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			for (String title : new String[] { "", "状态", "先前", "伤害", "当前", "状态" }) {
				TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
				tableColumn.setText(title);
				tableColumn.setResizable(false);
			}

			String[] names = bd.getName();
			int[] nowhps = bd.getNowhp();
			int[] maxhps = bd.getMaxhp();
			int[] dmgs = bdad.getDamage();
			for (int i = 0; i < 6; i++) {
				String name = names[i];
				int before = nowhps[i];
				int max = maxhps[i];
				int dmg = dmgs[i];
				if (before != -1) {
					int after = before - dmg;
					after = after < 0 ? 0 : after;

					String state;
					TableItem tableItem = new TableItem(table, SWT.NONE);
					{
						tableItem.setText(0, name);

						state = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(before * 1.0 / max);
						tableItem.setText(1, state);
						ToolUtils.notNullThenHandle(HPMessage.getColor(state), color -> tableItem.setBackground(1, color));

						tableItem.setText(2, before + "/" + max);
						tableItem.setText(3, "" + dmg);
						tableItem.setText(4, after + "/" + max);

						state = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(after * 1.0 / max);
						tableItem.setText(5, state);
						ToolUtils.notNullThenHandle(HPMessage.getColor(state), color -> tableItem.setBackground(5, color));
					}
				}
			}

			ToolUtils.forEach(table.getColumns(), TableColumn::pack);
		};

		if (battle instanceof AbstractBattleDay) {
			AbstractBattleDay battleDay = (AbstractBattleDay) battle;
			addOneHPState.accept(battleDay.getfDeck(), battleDay.getfDeckAttackDamage());
			addOneHPState.accept(battleDay.getfDeckCombine(), battleDay.getfDeckCombineAttackDamage());
			addOneHPState.accept(battleDay.geteDeck(), battleDay.geteDeckAttackDamage());
			addOneHPState.accept(battleDay.geteDeckCombine(), battleDay.geteDeckCombineAttackDamage());
		}
		if (battle instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight battleMidnight = (AbstractBattleMidnight) battle;
			BattleDeck[] activeDecks = battleMidnight.getActiveDeck();
			BattleMidnightStage battleMidnightStage = battleMidnight.getBattleMidnightStage();
			addOneHPState.accept(activeDecks[0], battleMidnightStage.getfAttackDamage());
			addOneHPState.accept(activeDecks[1], battleMidnightStage.geteAttackDamage());
		}
	}

	private static void addBattleInformation(Composite parent, AbstractBattle battle) {
		Composite infoComposite = new Composite(parent, SWT.BORDER);
		infoComposite.setLayout(new RowLayout());

		Consumer<String[]> addOneInfo = infos -> {
			Composite oneInfo = new Composite(infoComposite, SWT.NONE);
			oneInfo.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			ToolUtils.forEach(infos, info -> SwtUtils.initLabel(new Label(oneInfo, SWT.CENTER), info, new GridData(SWT.CENTER, SWT.CENTER, true, true)));
		};

		ToolUtils.notNullThenHandle(battle.getHangxiang(), hangxiang -> addOneInfo.accept(new String[] { "航向", hangxiang }));
		ToolUtils.notNullThenHandle(battle.getZhenxin(), zhenxin -> addOneInfo.accept(new String[] { "阵型", zhenxin[0], zhenxin[1] }));
		ToolUtils.notNullThenHandle(battle.getSearch(), search -> addOneInfo.accept(new String[] { "索敌", search[0], search[1] }));

		if (infoComposite.getChildren().length == 0) {
			infoComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			SwtUtils.initLabel(new Label(infoComposite, SWT.CENTER), "无", new GridData(SWT.CENTER, SWT.CENTER, true, true));
		}
	}

	private static void addBattleDayStage(Composite parent, AbstractBattleDay battleDay, BattleDayStage stage) {

	}

	private static void addBattleMidnightStage(Composite parent, AbstractBattleMidnight battleMidnight, BattleMidnightStage stage) {

	}

}
