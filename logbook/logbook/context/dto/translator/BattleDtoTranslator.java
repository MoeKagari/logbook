package logbook.context.dto.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import logbook.config.AppConstants;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.AbstractBattleDay.BattleDayStageType;
import logbook.context.dto.battle.AbstractBattleDay.InjectionKouko;
import logbook.context.dto.battle.AbstractBattleDay.Kouko;
import logbook.context.dto.battle.AbstractBattleDay.OpeningAttack;
import logbook.context.dto.battle.AbstractBattleDay.OpeningTaisen;
import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.AbstractBattleMidnight.BattleMidnightStage;
import logbook.context.dto.battle.AbstractInfoBattle;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.AbstractInfoBattleResult.BattleResult_GetShip;
import logbook.context.dto.battle.AbstractInfoBattleStartNext;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleDto.BattleDeck;
import logbook.context.dto.battle.BattleDto.BattleDeckAttackDamage;
import logbook.context.dto.battle.BattleDto.BattleOneAttack;
import logbook.context.dto.battle.BattleType;
import logbook.context.dto.battle.info.InfoBattleGobackPortDto;
import logbook.context.dto.battle.info.InfoBattleShipdeckDto;
import logbook.context.dto.battle.info.InfoBattleStartAirBaseDto;
import logbook.context.dto.battle.info.InfoBattleStartDto;
import logbook.context.dto.data.DeckDto;
import logbook.context.update.GlobalContext;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.logic.HPMessage;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class BattleDtoTranslator {

	public static void newBattleComposite(Composite parent, BiConsumer<AbstractBattle, SelectionEvent> handler, boolean hasDownArrow, BattleDto lastOne, BattleDto lastTwo) {
		if (lastOne instanceof InfoBattleStartDto) {
			BTResult btr = newBattleStart((InfoBattleStartDto) lastOne);
			if (btr != null) {
				newOneBattleComposite(parent, btr, null, null);
				SwtUtils.initLabel(new Label(parent, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
			}
		}

		BTResult btr = BattleDtoTranslator.getBattle(lastOne);
		if (btr != null) {
			if (lastOne.getBattleType() == BattleType.PRACTICE_DAY) {
				SwtUtils.initLabel(new Label(parent, SWT.CENTER), "演习", new GridData(GridData.FILL_HORIZONTAL));
				SwtUtils.initLabel(new Label(parent, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
			}

			if (hasDownArrow && lastTwo instanceof InfoBattleShipdeckDto) {
				SwtUtils.initLabel(new Label(parent, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
			}

			newOneBattleComposite(parent, btr, handler, lastOne);
		}
	}

	private static void newOneBattleComposite(Composite parent, BTResult btr, BiConsumer<AbstractBattle, SelectionEvent> handler, BattleDto lastOne) {
		Composite base = new Composite(parent, SWT.CENTER);
		base.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		base.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ToolUtils.notNullThenHandle(btr.deckInformations, di -> newLabels(base, di));
		if (btr.before != null && btr.after != null) {
			Composite stateComposite = new Composite(base, SWT.NONE);
			stateComposite.setLayout(SwtUtils.makeGridLayout(3, 4, 0, 0, 0));
			stateComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

			newStateComposite(stateComposite, btr.before);
			SwtUtils.initLabel(new Label(stateComposite, SWT.CENTER), "→", new GridData(SWT.CENTER, SWT.FILL, true, true));
			newStateComposite(stateComposite, btr.after);

			if (lastOne instanceof AbstractBattle) {//右键菜单
				MenuItem show = new MenuItem(new Menu(stateComposite), SWT.NONE);
				show.setText("战斗流程");
				if (handler != null) ControlSelectionListener.add(show, ev -> handler.accept((AbstractBattle) lastOne, ev));
				SwtUtils.setMenu(stateComposite, show.getParent());
			}
		}
	}

	private static void newLabels(Composite composite, ArrayList<String> deckInformation) {
		deckInformation.forEach(text -> SwtUtils.initLabel(new Label(composite, SWT.CENTER), text, new GridData(GridData.FILL_HORIZONTAL)));
	}

	private static void newStateComposite(Composite composite, ArrayList<String[]> shipInformations) {
		int length = shipInformations.stream().mapToInt(strs -> strs.length).max().orElse(0);
		if (length == 0) return;

		BiFunction<Integer, Integer, String> getText = (i, j) -> {
			if (j >= shipInformations.size() || j < 0) return "";
			String[] strs = shipInformations.get(j);
			if (i >= strs.length || i < 0) return "";
			return strs[i];
		};

		Composite oneSide = new Composite(composite, SWT.BORDER);
		oneSide.setLayout(SwtUtils.makeGridLayout(length, 4, 0, 0, 0));
		oneSide.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		for (int i = 0; i < length; i++) {
			Composite oneState = new Composite(oneSide, SWT.NONE);
			oneState.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			oneState.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			for (int j = 0; j < shipInformations.size(); j++) {
				String text = getText.apply(i, j);
				Color background = HPMessage.getColor(text);
				SwtUtils.initLabel(new Label(oneState, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, true, true), background);
			}
		}
	}

	private static BTResult getBattle(BattleDto battleDto) {
		if (battleDto instanceof AbstractInfoBattle) {
			if (battleDto instanceof AbstractInfoBattleStartNext) {
				return newBattleStartNext((AbstractInfoBattleStartNext) battleDto);
			}
			if (battleDto instanceof InfoBattleStartAirBaseDto) {
				return newBattleStartAirBase((InfoBattleStartAirBaseDto) battleDto);
			}
			if (battleDto instanceof AbstractInfoBattleResult) {
				return newBattleResult((AbstractInfoBattleResult) battleDto);
			}
			if (battleDto instanceof InfoBattleGobackPortDto) {
				return newBattleGobackPort((InfoBattleGobackPortDto) battleDto);
			}
			if (battleDto instanceof InfoBattleShipdeckDto) {
				return newBattleShipdeck((InfoBattleShipdeckDto) battleDto);
			}
		}
		if (battleDto instanceof AbstractBattle) {
			return newBattleDayMidnight((AbstractBattle) battleDto);
		}
		return null;
	}

	private static BTResult newBattleStart(InfoBattleStartDto battleStart) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String deckString;
		if (battleStart.isCombined() && battleStart.getDeckId() == 1) {
			deckString = "联合舰队";
		} else {
			deckString = AppConstants.DEFAULT_FLEET_NAME[battleStart.getDeckId() - 1];
		}
		deckInformations.add(deckString + " → " + battleStart.getMapString() + "-" + battleStart.getStart());

		//大破检查
		DeckDto[] decks;//出击的deck
		int deckId = battleStart.getDeckId();
		if (battleStart.isCombined() && deckId == 1) {
			decks = new DeckDto[] { GlobalContext.getDeckRoom()[0].getDeck(), GlobalContext.getDeckRoom()[1].getDeck() };
		} else {
			decks = new DeckDto[] { GlobalContext.getDeckRoom()[deckId - 1].getDeck() };
		}
		if (Arrays.stream(decks).anyMatch(ToolUtils::isNull)) {
			deckInformations.add("出击舰队状态未知,请注意");
		} else {
			if (Arrays.stream(decks).anyMatch(DeckDtoTranslator::hasDapo)) {
				deckInformations.add("出击舰队中有大破舰娘,请注意");
			}
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleStartNext(AbstractInfoBattleStartNext battleNext) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String text = "地图:" + battleNext.getMapString() + ",Cell:" + battleNext.getNext() +//
				"(" + battleNext.getNextType() +//
				battleNext.getItems().stream().map(item -> new StringBuilder(",").append(item.toString())).reduce(new StringBuilder(""), StringBuilder::append) +//
				(battleNext.isGoal() ? ",终点" : "") + ")";
		deckInformations.add(text);

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleResult(AbstractInfoBattleResult battleResult) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String text = "战斗结果:" + battleResult.getRank() + " " + "MVP:" + battleResult.getMvp() + (battleResult.getMvpCombined() > 0 ? ("," + battleResult.getMvpCombined()) : "");
		deckInformations.add(text);

		BattleResult_GetShip newShip = battleResult.getNewShip();
		if (newShip != null) {
			deckInformations.add(newShip.getType() + "-" + newShip.getName() + " 加入镇守府");
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleShipdeck(InfoBattleShipdeckDto battleShipdeck) {
		if (battleShipdeck.hasDapo()) {
			ArrayList<String> deckInformations = new ArrayList<>();
			deckInformations.add("出击舰队中有大破舰娘,请注意");
			return new BTResult(deckInformations, null, null);
		}
		return null;
	}

	private static BTResult newBattleDayMidnight(AbstractBattle battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();
		ArrayList<String[]> before = new ArrayList<>();
		ArrayList<String[]> after = new ArrayList<>();

		BiConsumer<BattleDeck, BattleDeckAttackDamage> addOneState = (bd, bdad) -> {
			if (bd != null && bd.exist()) {
				int length = bd.getDeckLength();
				int[] nowhps = bd.nowhps;
				int[] maxhps = bd.maxhps;
				int[] dmgs = bdad.dmgs;

				String[] oneStateBefore = new String[length];
				String[] oneStateAfter = new String[length];
				for (int index = 0; index < length; index++) {
					oneStateBefore[index] = bd.escapes.contains(index) ? HPMessage.ESCAPE_STRING : HPMessage.getString(nowhps[index] * 1.0 / maxhps[index]);
					oneStateAfter[index] = bd.escapes.contains(index) ? HPMessage.ESCAPE_STRING : HPMessage.getString((nowhps[index] - dmgs[index]) * 1.0 / maxhps[index]);
				}

				before.add(oneStateBefore);
				after.add(oneStateAfter);
			}
		};

		if (battleDto instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight battleMidnight = (AbstractBattleMidnight) battleDto;
			BattleDeck[] activeDecks = battleMidnight.getActiveDeck();
			BattleMidnightStage battleMidnightStage = battleMidnight.getBattleMidnightStage();
			addOneState.accept(activeDecks[0], battleMidnightStage.getfAttackDamage());
			addOneState.accept(activeDecks[1], battleMidnightStage.geteAttackDamage());

			if (battleMidnight.isMidnightOnly() == false) deckInformations.add("夜战");
		}
		if (battleDto instanceof AbstractBattleDay) {
			AbstractBattleDay battleDay = (AbstractBattleDay) battleDto;
			addOneState.accept(battleDay.getfDeck(), battleDay.getfDeckAttackDamage());
			addOneState.accept(battleDay.getfDeckCombine(), battleDay.getfDeckCombineAttackDamage());
			addOneState.accept(battleDay.geteDeck(), battleDay.geteDeckAttackDamage());
			addOneState.accept(battleDay.geteDeckCombine(), battleDay.geteDeckCombineAttackDamage());
		}

		return new BTResult(deckInformations, before, after);
	}

	private static BTResult newBattleGobackPort(InfoBattleGobackPortDto battleGobackPort) {
		ArrayList<String> deckInformations = new ArrayList<>();
		deckInformations.add("大破的舰娘已退避");
		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleStartAirBase(InfoBattleStartAirBaseDto battleStartAirBase) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String[] NUMBERS = { "一", "二", "三", "四", "五", "六" };
		int[][] strikePoints = battleStartAirBase.getStrikePoint();
		for (int i = 0; i < strikePoints.length; i++) {
			String number = NUMBERS[i];
			int[] strikePoint = strikePoints[i];
			ToolUtils.notNullThenHandle(strikePoint, sp -> deckInformations.add("第" + number + "基地航空队 -> " + Arrays.toString(sp)));
		}

		return new BTResult(deckInformations, null, null);
	}

	private static class BTResult {
		final ArrayList<String> deckInformations;
		final ArrayList<String[]> before;
		final ArrayList<String[]> after;

		public BTResult(ArrayList<String> deckInformations, ArrayList<String[]> before, ArrayList<String[]> after) {
			this.deckInformations = deckInformations;
			this.before = before;
			this.after = after;
		}
	}

	/*------------------------------------------------------------------------------*/

	public static void createBattleFlow(Composite parent, AbstractBattle battle) {
		//本次战斗结束后,所有船的状态
		SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗结束-各船状态", new GridData(GridData.FILL_HORIZONTAL));
		addShipState(parent, battle);

		//与战斗相关的一些信息
		SwtUtils.insertBlank(parent);
		SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗信息", new GridData(GridData.FILL_HORIZONTAL));
		addBattleInformation(parent, battle);

		SwtUtils.insertBlank(parent);
		if (battle instanceof AbstractBattleMidnight) {
			SwtUtils.initLabel(new Label(parent, SWT.CENTER), "夜战", new GridData(GridData.FILL_HORIZONTAL));
			//夜战只有一个stage,所以只加入详细的攻击流程
			AbstractBattleMidnight battleMidnight = ((AbstractBattleMidnight) battle);
			addBattleAttack(parent, battle, battleMidnight.getBattleMidnightStage().getBattleAttacks(), enemyAttack -> Boolean.FALSE);
		}
		if (battle instanceof AbstractBattleDay) {
			SwtUtils.initLabel(new Label(parent, SWT.CENTER), "昼战流程", new GridData(GridData.FILL_HORIZONTAL));

			BattleDeckAttackDamage fbdad = new BattleDeckAttackDamage();
			BattleDeckAttackDamage fbdadco = new BattleDeckAttackDamage();
			BattleDeckAttackDamage ebdad = new BattleDeckAttackDamage();
			BattleDeckAttackDamage ebdadco = new BattleDeckAttackDamage();
			AbstractBattleDay battleDay = ((AbstractBattleDay) battle);
			battleDay.getBattleDayStage().forEach(stage -> {
				//昼战stage的name
				SwtUtils.initLabel(new Label(parent, SWT.LEFT), stage.getStageName(), new GridData(GridData.FILL_HORIZONTAL));

				//有无详细的攻击信息
				boolean haveAttack = false;
				//详细的攻击流程,仅有,开幕对潜,开幕雷击,炮击战,雷击战
				if (stage.getType() == BattleDayStageType.OPENINGTAISEN || stage.getType() == BattleDayStageType.HOUGEKI) {
					haveAttack = true;
					addBattleAttack(parent, battle, stage.battleAttacks, ((OpeningTaisen) stage)::getSimulatorObject);
				} else if (stage.getType() == BattleDayStageType.OPENINGATTACK || stage.getType() == BattleDayStageType.RAIGEKI) {
					haveAttack = true;
					addRaigekiAttack(parent, battleDay, (OpeningAttack) stage);
				}

				//喷气机受损情况
				if (stage.getType() == BattleDayStageType.INJECTIONKOUKO) {
					String text;
					InjectionKouko ik = (InjectionKouko) stage;
					Function<int[], String> getPLSString = pls -> pls == null ? "" : (pls[0] + "→" + (pls[0] - pls[1]));

					int[][] planeLostStage1 = ik.getPlaneLostStage1();
					int[][] planeLostStage2 = ik.getPlaneLostStage2();

					text = "自:" + getPLSString.apply(planeLostStage1[0]) + "," + getPLSString.apply(planeLostStage2[0]);
					SwtUtils.initLabel(new Label(parent, SWT.LEFT), text, new GridData(GridData.FILL_HORIZONTAL));

					text = "敌:" + getPLSString.apply(planeLostStage1[1]) + "," + getPLSString.apply(planeLostStage2[1]);
					SwtUtils.initLabel(new Label(parent, SWT.LEFT), text, new GridData(GridData.FILL_HORIZONTAL));
				}

				//stage结束后,各船状态
				Composite deckNowState = new Composite(parent, SWT.NONE);
				deckNowState.setLayout(new RowLayout());
				{
					addOnedeckNowState(deckNowState, battleDay.getfDeck(), fbdad, stage.fAttackDamage, haveAttack);
					fbdad.add(stage.fAttackDamage);

					addOnedeckNowState(deckNowState, battleDay.getfDeckCombine(), fbdadco, stage.fAttackDamageco, haveAttack);
					fbdadco.add(stage.fAttackDamageco);

					addOnedeckNowState(deckNowState, battleDay.geteDeck(), ebdad, stage.eAttackDamage, haveAttack);
					ebdad.add(stage.eAttackDamage);

					addOnedeckNowState(deckNowState, battleDay.geteDeckCombine(), ebdadco, stage.eAttackDamageco, haveAttack);
					ebdadco.add(stage.eAttackDamageco);
				}
			});
		}
	}

	private static void addShipState(Composite parent, AbstractBattle battle) {
		if (battle instanceof AbstractBattleDay) {
			AbstractBattleDay battleDay = (AbstractBattleDay) battle;
			addOneShipState(parent, "自-主力舰队", battleDay.getfDeck(), battleDay.getfDeckAttackDamage());
			addOneShipState(parent, "自-随从舰队", battleDay.getfDeckCombine(), battleDay.getfDeckCombineAttackDamage());
			addOneShipState(parent, "敌-主力舰队", battleDay.geteDeck(), battleDay.geteDeckAttackDamage());
			addOneShipState(parent, "敌-随从舰队", battleDay.geteDeckCombine(), battleDay.geteDeckCombineAttackDamage());
		}
		if (battle instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight battleMidnight = (AbstractBattleMidnight) battle;
			BattleDeck[] activeDecks = battleMidnight.getActiveDeck();
			BattleMidnightStage battleMidnightStage = battleMidnight.getBattleMidnightStage();
			addOneShipState(parent, "自", activeDecks[0], battleMidnightStage.getfAttackDamage());
			addOneShipState(parent, "敌", activeDecks[1], battleMidnightStage.geteAttackDamage());
		}
	}

	private static void addOneShipState(Composite parent, String deckname, BattleDeck bd, BattleDeckAttackDamage bdad) {
		if (bd == null || bd.exist() == false) return;

		String[] headers = { deckname, "状态", "先前", "伤害", "当前", "状态", "攻击" };
		int len = headers.length;

		Composite stateComposite = new Composite(parent, SWT.BORDER);
		stateComposite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = new Composite(stateComposite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
			parts[i] = part;
		}

		String[] names = bd.names;
		int[] nowhps = bd.nowhps;
		int[] maxhps = bd.maxhps;
		int[] dmgs = bdad.dmgs;
		int[] attacks = bdad.attack;
		for (int i = 0; i < 6; i++) {
			if (nowhps[i] == -1) continue;

			String name = names[i];
			int before = nowhps[i];
			int max = maxhps[i];
			int dmg = dmgs[i];
			int attack = attacks[i];

			String state;
			Color color;
			int after = before - dmg;
			if (after < 0) after = 0;
			{
				addNewLabel(parts[0], name);

				state = bd.escapes.contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(before * 1.0 / max);
				color = HPMessage.getColor(state);
				addNewLabel(parts[1], state, color);

				addNewLabel(parts[2], before + "/" + max);
				addNewLabel(parts[3], "" + dmg);
				addNewLabel(parts[4], after + "/" + max);

				state = bd.escapes.contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(after * 1.0 / max);
				color = HPMessage.getColor(state);
				addNewLabel(parts[5], state, color);

				addNewLabel(parts[6], "" + attack);
			}
		}
	}

	private static void addBattleInformation(Composite parent, AbstractBattle battle) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new RowLayout());

		ToolUtils.notNullThenHandle(battle.getHangxiang(), hangxiang -> addOneBattleInformation(composite, "航向", hangxiang));
		ToolUtils.notNullThenHandle(battle.getZhenxin(), zhenxin -> addOneBattleInformation(composite, "阵型", zhenxin[0], zhenxin[1]));
		ToolUtils.notNullThenHandle(battle.getSearch(), search -> addOneBattleInformation(composite, "索敌", search[0], search[1]));

		if (battle instanceof AbstractBattleDay) {
			AbstractBattleDay day = (AbstractBattleDay) battle;
			day.getBattleDayStage().forEach(stage -> {
				if (stage instanceof Kouko) {
					Predicate<int[]> hasPlane = pls -> pls != null && pls[0] != 0;
					Function<int[], String> getPLSString = pls -> pls == null ? "" : (pls[0] + "→" + (pls[0] - pls[1]));
					Kouko kouko = (Kouko) stage;

					ToolUtils.notNullThenHandle(kouko.getSeiku(), seiku -> addOneBattleInformation(composite, "制空", seiku));
					ToolUtils.notNullThenHandle(kouko.getTouchPlane(), tp -> {
						if (tp[0] == true || tp[1] == true) {
							addOneBattleInformation(composite, "触接", tp[0] ? "有" : "", tp[1] ? "有" : "");
						}
					});
					if (kouko.getStages()[0]) {
						int[][] pls1 = kouko.getPlaneLostStage1();
						if (hasPlane.test(pls1[0]) || hasPlane.test(pls1[1])) {
							addOneBattleInformation(composite, "stage1", getPLSString.apply(pls1[0]), getPLSString.apply(pls1[1]));
						}
					}
					if (kouko.getStages()[1]) {
						int[][] pls2 = kouko.getPlaneLostStage2();
						if (hasPlane.test(pls2[0]) || hasPlane.test(pls2[1])) {
							addOneBattleInformation(composite, "stage2", getPLSString.apply(pls2[0]), getPLSString.apply(pls2[1]));
						}

						//对空ci
						int[] duikongci = kouko.getDuikongci();
						if (duikongci != null) {
							BattleDeck fdc = day.getfDeckCombine();
							String name = ArrayUtils.addAll(day.getfDeck().names, (fdc != null && fdc.exist()) ? fdc.names : AppConstants.EMPTY_NAMES)[duikongci[0]];
							addOneBattleInformation(composite, "对空CI", name, String.valueOf(duikongci[1]));
						}
					}
				}
			});
		}
		if (battle instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight mid = (AbstractBattleMidnight) battle;
			ToolUtils.notNullThenHandle(mid.getTouchPlane(), tp -> {
				if (tp[0] == true || tp[1] == true) {
					addOneBattleInformation(composite, "触接", tp[0] ? "有" : "", tp[1] ? "有" : "");
				}
			});
			ToolUtils.notNullThenHandle(mid.getFlare(), flare -> {
				if (flare[0] == true || flare[1] == true) {
					addOneBattleInformation(composite, "照明弹", flare[0] ? "有" : "", flare[1] ? "有" : "");
				}
			});
		}

		if (composite.getChildren().length == 0) {
			composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(composite, "无");
		}
	}

	private static void addOneBattleInformation(Composite parent, String... infos) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		ToolUtils.forEach(infos, info -> addNewLabel(composite, info));
	}

	private static void addBattleAttack(Composite parent, AbstractBattle battle, ArrayList<BattleOneAttack> battleAttacks, Function<Boolean, Boolean> fun) {
		if (battleAttacks.stream().mapToInt(ba -> ba.attackIndex).filter(i -> i > 0).count() == 0) {
			return;
		}

		String[] headers = { "攻击方", "攻击类型", "防御方", "伤害", "剩余", "状态" };
		int len = headers.length;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = new Composite(composite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
			parts[i] = part;
		}

		battleAttacks.forEach(oneAttack -> addOneBattleAttack(parts, battle, oneAttack, fun));
	}

	private static void addOneBattleAttack(Composite[] parts, AbstractBattle battle, BattleOneAttack oneAttack, Function<Boolean, Boolean> fun) {
		Boolean enemyAttack = oneAttack.enemyAttack;
		Boolean fcombine = fun.apply(enemyAttack);
		int attackIndex = oneAttack.attackIndex;
		int attackType = oneAttack.attackType;
		int[] defenseIndexs = oneAttack.defenseIndexs;
		int[] damages = oneAttack.dmgs;

		String[] atters = null, dmgers = null;
		if (enemyAttack == null && fcombine != null) {//敌方非联合舰队
			if (battle.isMidnight()) {
				AbstractBattleMidnight midnight = (AbstractBattleMidnight) battle;
				atters = ArrayUtils.addAll(midnight.getActiveDeck()[0].names, midnight.getActiveDeck()[1].names);
			} else {
				atters = ArrayUtils.addAll(fcombine == Boolean.TRUE ? battle.getfDeckCombine().names : battle.getfDeck().names, battle.geteDeck().names);
			}
			dmgers = atters;
		} else if (enemyAttack == Boolean.FALSE) {//敌联合舰队,我方攻击
			atters = ArrayUtils.addAll(battle.getfDeck().names, battle.getfDeckCombine().names);
			dmgers = ArrayUtils.addAll(battle.geteDeck().names, battle.geteDeckCombine().names);
		} else if (enemyAttack == Boolean.TRUE) {//敌联合舰队,敌方攻击
			atters = ArrayUtils.addAll(battle.geteDeck().names, battle.geteDeckCombine().names);
			dmgers = ArrayUtils.addAll(battle.getfDeck().names, battle.getfDeckCombine().names);
		}

		for (int i = 0; i < defenseIndexs.length; i++) {
			if (defenseIndexs[i] == -1) continue;

			String atter = atters == null ? "" : atters[attackIndex - 1];
			String dmger = dmgers == null ? "" : dmgers[defenseIndexs[i] - 1];
			String type = getBattleAttackType(battle.isMidnight(), attackType);
			if (i == 0) {
				addNewLabel(parts[0], atter);
				addNewLabel(parts[1], type);
				addNewLabel(parts[2], dmger);
			} else {
				addNewLabel(parts[0], "");
				addNewLabel(parts[1], "");
				addNewLabel(parts[2], "");
			}

			addNewLabel(parts[3], String.valueOf(damages[i]));
			addNewLabel(parts[4], "");
			addNewLabel(parts[5], "");
		}
	}

	private static void addRaigekiAttack(Composite parent, AbstractBattleDay day, OpeningAttack raigeki) {
		//frai与fydam长度相同
		int[] frai = raigeki.frai;//目标
		int[] erai = raigeki.erai;
		int[] fdam = raigeki.fdam;//伤害
		int[] edam = raigeki.edam;
		int[] fydam = raigeki.fydam;//攻击
		int[] eydam = raigeki.eydam;

		//有雷击战但无攻击
		if (Arrays.stream(frai).filter(i -> i > 0).count() == 0 && Arrays.stream(erai).filter(i -> i > 0).count() == 0) {
			return;
		}

		String[] headers = { "攻击方", "防御方", "伤害" };
		int len = headers.length;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = new Composite(composite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
			parts[i] = part;
		}

		//自方雷击
		{
			for (int i = 1; i < frai.length; i++) {
				if (frai[i] <= 0) continue;

				int attackindex = i - 1;
				int defenseindex = frai[i] - 1;
				String attacker = "", defenser = "", dmg = String.valueOf(fydam[i]);

				switch (frai.length) {
					case 1 + 12:
						attacker = ArrayUtils.addAll(day.getfDeck().names, day.getfDeckCombine().names)[attackindex];
						break;
					case 1 + 6:
						if (day.getfDeckCombine() == null) {
							attacker = day.getfDeck().names[attackindex];
						} else {
							attacker = day.getfDeckCombine().names[attackindex];
						}
						break;
				}
				switch (edam.length) {
					case 1 + 12:
						defenser = ArrayUtils.addAll(day.geteDeck().names, day.geteDeckCombine().names)[defenseindex];
						break;
					case 1 + 6:
						if (day.geteDeckCombine() == null) {
							defenser = day.geteDeck().names[defenseindex];
						} else {
							defenser = day.geteDeckCombine().names[defenseindex];
						}
						break;
				}

				addNewLabel(parts[0], attacker);
				addNewLabel(parts[1], defenser);
				addNewLabel(parts[2], dmg);
			}
		}
		//敌方雷击
		{
			for (int i = 1; i < erai.length; i++) {
				if (erai[i] <= 0) continue;

				int attackindex = i - 1;
				int defenseindex = erai[i] - 1;
				String attacker = "", defenser = "", dmg = String.valueOf(eydam[i]);

				switch (erai.length) {
					case 1 + 12:
						attacker = ArrayUtils.addAll(day.geteDeck().names, day.geteDeckCombine().names)[attackindex];
						break;
					case 1 + 6:
						if (day.geteDeckCombine() == null) {
							attacker = day.geteDeck().names[attackindex];
						} else {
							attacker = day.geteDeckCombine().names[attackindex];
						}
						break;
				}
				switch (fdam.length) {
					case 1 + 12:
						defenser = ArrayUtils.addAll(day.getfDeck().names, day.getfDeckCombine().names)[defenseindex];
						break;
					case 1 + 6:
						if (day.getfDeckCombine() == null) {
							defenser = day.getfDeck().names[defenseindex];
						} else {
							defenser = day.getfDeckCombine().names[defenseindex];
						}
						break;
				}

				addNewLabel(parts[0], attacker);
				addNewLabel(parts[1], defenser);
				addNewLabel(parts[2], dmg);
			}
		}
	}

	private static void addOnedeckNowState(Composite parent, BattleDeck bd, BattleDeckAttackDamage bdad, BattleDeckAttackDamage ad, boolean haveAttack) {
		if (bd == null || bd.exist() == false) return;

		String[] headers = haveAttack ? new String[] { "", "状态", "当前", "伤害", "攻击" } : new String[] { "", "状态", "当前", "伤害" };
		int len = headers.length;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = new Composite(composite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
			parts[i] = part;
		}

		String[] names = bd.names;
		int[] nowhps = bd.nowhps;
		int[] maxhps = bd.maxhps;
		int[] dmg1 = bdad.dmgs;
		int[] atts = ad.attack;
		int[] dmg2 = ad.dmgs;
		for (int i = 0; i < 6; i++) {
			if (nowhps[i] == -1) continue;

			String name = names[i];
			int max = maxhps[i];
			int now = nowhps[i] - dmg1[i] - dmg2[i];
			if (now < 0) now = 0;
			int att = atts[i];
			int dmg = dmg2[i];
			{
				Label nameLabel = new Label(parts[0], SWT.CENTER);
				SwtUtils.initLabel(nameLabel, name, new GridData(SWT.CENTER, SWT.CENTER, false, false), 55);
				ToolUtils.notNullThenHandle(name, n -> nameLabel.setToolTipText(n));

				String state = bd.escapes.contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(now * 1.0 / max);
				Color color = HPMessage.getColor(state);
				addNewLabel(parts[1], state, color);

				addNewLabel(parts[2], now + "/" + max);
				addNewLabel(parts[3], "" + dmg);
				if (haveAttack) addNewLabel(parts[4], "" + att);
			}
		}
	}

	private static void addNewLabel(Composite parent, String text) {
		SwtUtils.initLabel(new Label(parent, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	private static void addNewLabel(Composite parent, String text, Color color) {
		SwtUtils.initLabel(new Label(parent, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, false, false), color);
	}

	/*------------------------------------------------------------------------------*/

	public static boolean haveDamage(AbstractBattle battle) {
		Predicate<BattleDeckAttackDamage> haveDamage = bdad -> Arrays.stream(bdad.dmgs).anyMatch(i -> i != 0);
		if (battle instanceof AbstractBattleDay) {
			AbstractBattleDay day = (AbstractBattleDay) battle;
			return haveDamage.test(day.getfDeckAttackDamage()) || haveDamage.test(day.getfDeckCombineAttackDamage());
		}
		if (battle instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight midnight = (AbstractBattleMidnight) battle;
			return haveDamage.test(midnight.getBattleMidnightStage().getfAttackDamage());
		}
		return false;
	}

	public static String getBattleAttackType(boolean isMidnight, int attackType) {
		if (isMidnight) {
			switch (attackType) {
				case 0://普通单击
					return "";
				case 1:
					return "二连";
				case 2:
					return "炮雷CI";
				case 3:
					return "鱼雷CI";
				case 4:
					return "主副CI";
				case 5:
					return "主主CI";
			}
		} else {
			switch (attackType) {
				case 0://普通单击
					return "";
				case 2:
					return "二连";
				case 3:
					return "主副CI";
				case 4:
					return "主电CI";
				case 5:
					return "主撤CI";
				case 6:
					return "主主CI";
			}
		}
		return String.valueOf(attackType);
	}

}
