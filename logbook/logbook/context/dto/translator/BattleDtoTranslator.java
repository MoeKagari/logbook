package logbook.context.dto.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;

import logbook.config.AppConstants;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.AbstractBattleDay;
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
			System.out.println(battleDto == null ? "battleDto == null" : battleDto.getBattleType());
		}
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
			String text = "下一点:" + battleNextDto.getMap() + "(" + battleNextDto.getNext() + "," + battleNextDto.getNextType() + (battleNextDto.isGoal() ? ",终点" : "") + ")";
			text = "地图:" + battleNextDto.getMap() + ",Cell:" + battleNextDto.getNext() + "(" + battleNextDto.getNextType() + (battleNextDto.isGoal() ? ",终点" : "") + ")";
			deckInformations.add(text);
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleResult(BattleDto battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();

		if (battleDto instanceof AbstractInfoBattleResult) {
			AbstractInfoBattleResult battleResult = (AbstractInfoBattleResult) battleDto;
			String text = "战斗结果:" + battleResult.getRank();
			deckInformations.add(text);

			if (battleResult.haveNewShip()) {
				text = battleResult.getNewShipTypeName() + " 加入镇守府";
				deckInformations.add(text);
			}
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
					oneStateBefore[i] = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.get(nowhps[i] * 1.0 / maxhps[i]);
					oneStateAfter[i] = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.get((nowhps[i] - dmgs[i]) * 1.0 / maxhps[i]);
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
		{
			InfoBattleStartAirBaseDto battleStartAirBaseDto = (InfoBattleStartAirBaseDto) battleDto;
			int[][] strikePoint = battleStartAirBaseDto.getStrikePoint();
			for (int i = 0; i < strikePoint.length; i++) {
				if (strikePoint[i] != null) {
					deckInformations.add("第" + new String[] { "一", "二", "三", "四", "五" }[i] + "基地航空队 -> " + Arrays.toString(strikePoint[i]));
				}
			}
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
}
