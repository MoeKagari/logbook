package logbook.context.dto.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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

import logbook.config.AppConfig;
import logbook.config.AppConstants;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.AbstractBattleDay.BattleDayStageType;
import logbook.context.dto.battle.AbstractBattleDay.InjectionKouko;
import logbook.context.dto.battle.AbstractBattleDay.Kouko;
import logbook.context.dto.battle.AbstractBattleDay.OpeningAttack;
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

	public static void newBattleComposite(Composite composite, Consumer<SelectionEvent> handler, boolean hasDownArrow, BattleDto lastOne, BattleDto lastTwo) {
		BTResult btr = BattleDtoTranslator.getBattle(lastOne);
		if (btr != null) {
			if (lastOne.getBattleType() == BattleType.PRACTICE_DAY) {
				SwtUtils.initLabel(new Label(composite, SWT.CENTER), "演习", new GridData(GridData.FILL_HORIZONTAL));
				SwtUtils.initLabel(new Label(composite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
			} else if (hasDownArrow && lastOne.hasDownArrow(lastTwo)) {
				SwtUtils.initLabel(new Label(composite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
			}

			Composite base = new Composite(composite, SWT.CENTER);
			base.setLayout(SwtUtils.makeGridLayout(1, 0, 2, 0, 0));
			base.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			ToolUtils.notNullThenHandle(btr.getDeckInformations(), di -> newLabels(base, di));
			if (btr.getBefore() != null && btr.getAfter() != null) {
				Composite stateComposite = new Composite(base, SWT.NONE);
				stateComposite.setLayout(SwtUtils.makeGridLayout(3, 4, 0, 0, 0));
				stateComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

				newStateComposite(stateComposite, btr.getBefore());
				SwtUtils.initLabel(new Label(stateComposite, SWT.CENTER), "→", new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 2));
				newStateComposite(stateComposite, btr.getAfter());
			}

			if (lastOne instanceof AbstractBattle) {
				if (AppConfig.get().isAutoUpdateBattleFlow()) handler.accept(null);

				MenuItem show = new MenuItem(new Menu(base), SWT.NONE);
				show.setText("战斗流程");
				show.addSelectionListener(new ControlSelectionListener(handler));
				SwtUtils.setMenu(base, show.getParent());
			}
		}
	}

	private static void newLabels(Composite composite, ArrayList<String> deckInformation) {
		deckInformation.forEach(text -> SwtUtils.initLabel(new Label(composite, SWT.CENTER), text, new GridData(GridData.FILL_HORIZONTAL)));
	}

	private static void newStateComposite(Composite composite, ArrayList<String[]> shipInformations) {
		int count = shipInformations.stream().mapToInt(strs -> strs.length).max().orElse(0);
		if (count == 0) return;

		BiFunction<Integer, Integer, String> getText = (i, j) -> {
			if (j >= shipInformations.size() || j < 0) return "";
			String[] strs = shipInformations.get(j);
			if (i >= strs.length || i < 0) return "";
			return strs[i];
		};

		Composite oneSide = new Composite(composite, SWT.BORDER);
		oneSide.setLayout(SwtUtils.makeGridLayout(count, 4, 0, 0, 0));
		oneSide.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		for (int i = 0; i < count; i++) {
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
			if (battleDto instanceof InfoBattleShipdeckDto) {
				return newBattleShipdeck(battleDto);
			}
		}
		System.out.println(battleDto == null ? "battleDto == null" : battleDto.getBattleType());
		return null;
	}

	private static BTResult newBattleStartNext(BattleDto battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();

		if (battleDto instanceof InfoBattleStartDto) {
			InfoBattleStartDto battleStartDto = (InfoBattleStartDto) battleDto;

			String deckString;
			if (battleStartDto.isCombined() && battleStartDto.getDeckId() == 1) {
				deckString = "联合舰队";
			} else {
				deckString = AppConstants.DEFAULT_FLEET_NAME[battleStartDto.getDeckId() - 1];
			}
			String text = deckString + " → " + battleStartDto.getMap();
			deckInformations.add(text);

			//大破检查
			DeckDto[] decks;
			if (battleStartDto.isCombined() && battleStartDto.getDeckId() == 1) {
				decks = new DeckDto[] { GlobalContext.getDeckRoom()[0].getDeck(), GlobalContext.getDeckRoom()[1].getDeck() };
			} else {
				decks = new DeckDto[] { GlobalContext.getDeckRoom()[battleStartDto.getDeckId() - 1].getDeck() };
			}
			if (Arrays.stream(decks).anyMatch(ToolUtils::isNull)) {
				text = "出击舰队状态未知,请注意";
				deckInformations.add(text);
			} else {
				boolean hasDapo = Arrays.stream(decks).anyMatch(DeckDtoTranslator::hasDapo);
				if (hasDapo) {
					text = "出击舰队中有大破舰娘,请注意";
					deckInformations.add(text);
				}
			}

			text = "起点:" + battleStartDto.getStart();
			deckInformations.add(text);
		}
		if (battleDto instanceof AbstractInfoBattleStartNext) {
			AbstractInfoBattleStartNext battleNextDto = (AbstractInfoBattleStartNext) battleDto;
			String text = "地图:" + battleNextDto.getMap() + ",Cell:" + battleNextDto.getNext() +//
					"(" + battleNextDto.getNextType() +//
					battleNextDto.getItems().stream().map(item -> new StringBuilder(",").append(item.toString())).reduce(new StringBuilder(""), StringBuilder::append) +//
					(battleNextDto.isGoal() ? ",终点" : "") + ")";
			deckInformations.add(text);
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleResult(BattleDto battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();

		AbstractInfoBattleResult battleResult = (AbstractInfoBattleResult) battleDto;
		String text = "战斗结果:" + battleResult.getRank() + " " + "MVP:" + battleResult.getMvp() + (battleResult.getMvpCombined() > 0 ? ("," + battleResult.getMvpCombined()) : "");
		deckInformations.add(text);

		if (battleResult.haveNewShip()) {
			BattleResult_GetShip ship = battleResult.getNewShip();
			text = ship.getType() + "-" + ship.getName() + " 加入镇守府";
			deckInformations.add(text);
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleShipdeck(BattleDto battleDto) {
		InfoBattleShipdeckDto battleShipdeck = (InfoBattleShipdeckDto) battleDto;
		if (battleShipdeck.hasDapo()) {
			ArrayList<String> deckInformations = new ArrayList<>();
			String text = "出击舰队中有大破舰娘,请注意";
			deckInformations.add(text);
			return new BTResult(deckInformations, null, null);
		}
		return null;
	}

	private static BTResult newBattleDayMidnight(BattleDto battleDto) {
		ArrayList<String> deckInformations = null;
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

			if (battleMidnight.isMidnightOnly() == false) {
				deckInformations = new ArrayList<>();
				deckInformations.add("夜战");
			}
		}

		return new BTResult(deckInformations, before, after);
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

	public static void createBattleFlow(Composite parent, AbstractBattle battle) {
		//本次战斗结束后,所有船的状态
		SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗结束-各船状态", new GridData(GridData.FILL_HORIZONTAL));
		addShipState(parent, battle);

		//与战斗相关的一些信息
		SwtUtils.insertBlank(parent);
		SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗信息", new GridData(GridData.FILL_HORIZONTAL));
		addBattleInformation(parent, battle);

		SwtUtils.insertBlank(parent);
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
					addBattleAttack(parent, stage.getBattleAttacks());
				} else if (stage.getType() == BattleDayStageType.OPENINGATTACK || stage.getType() == BattleDayStageType.RAIGEKI) {
					haveAttack = true;
					addRaigekiAttack(parent, battleDay, (OpeningAttack) stage);
				} else if (stage.getType() == BattleDayStageType.INJECTIONKOUKO) {
					String text;
					InjectionKouko ik = (InjectionKouko) stage;
					Function<int[], String> getPLSString = pls -> pls == null ? "" : (pls[0] + "→" + (pls[0] - pls[1]));

					int[][] planeLostStage1 = ik.getPlaneLostStage1();
					text = "自:" + getPLSString.apply(planeLostStage1[0]) + "," + getPLSString.apply(planeLostStage1[1]);
					SwtUtils.initLabel(new Label(parent, SWT.LEFT), text, new GridData(GridData.FILL_HORIZONTAL));

					int[][] planeLostStage2 = ik.getPlaneLostStage2();
					text = "敌:" + getPLSString.apply(planeLostStage2[0]) + "," + getPLSString.apply(planeLostStage2[1]);
					SwtUtils.initLabel(new Label(parent, SWT.LEFT), text, new GridData(GridData.FILL_HORIZONTAL));
				}

				//stage结束后,各船状态
				Composite deckNowState = new Composite(parent, SWT.NONE);
				deckNowState.setLayout(new RowLayout());
				if (battleDay.getfDeck() != null) {
					addOnedeckNowState(deckNowState, battleDay.getfDeck(), fbdad, stage.getfAttackDamage(), haveAttack);
					fbdad.add(stage.getfAttackDamage());
				}
				if (battleDay.getfDeckCombine() != null) {
					addOnedeckNowState(deckNowState, battleDay.getfDeckCombine(), fbdadco, stage.getfAttackDamagecombine(), haveAttack);
					fbdadco.add(stage.getfAttackDamagecombine());
				}
				if (battleDay.geteDeck() != null) {
					addOnedeckNowState(deckNowState, battleDay.geteDeck(), ebdad, stage.geteAttackDamage(), haveAttack);
					ebdad.add(stage.geteAttackDamage());
				}
				if (battleDay.geteDeckCombine() != null) {
					addOnedeckNowState(deckNowState, battleDay.geteDeckCombine(), ebdadco, stage.geteAttackDamagecombine(), haveAttack);
					ebdadco.add(stage.geteAttackDamagecombine());
				}
			});
		}
		if (battle instanceof AbstractBattleMidnight) {
			SwtUtils.initLabel(new Label(parent, SWT.CENTER), "夜战", new GridData(GridData.FILL_HORIZONTAL));

			//夜战只有一个stage,所以只加入详细的攻击流程
			AbstractBattleMidnight battleMidnight = ((AbstractBattleMidnight) battle);
			addBattleAttack(parent, battleMidnight.getBattleMidnightStage().getBattleAttacks());
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
		if (bd == null) return;

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

		String[] names = bd.getName();
		int[] nowhps = bd.getNowhp();
		int[] maxhps = bd.getMaxhp();
		int[] dmgs = bdad.getDamage();
		int[] attacks = bdad.getAttack();
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
				addNewLabel(parts[0], name == null ? "" : name);

				state = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(before * 1.0 / max);
				color = HPMessage.getColor(state);
				addNewLabel(parts[1], state, color);

				addNewLabel(parts[2], before + "/" + max);
				addNewLabel(parts[3], "" + dmg);
				addNewLabel(parts[4], after + "/" + max);

				state = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(after * 1.0 / max);
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
			AbstractBattleDay battleDay = (AbstractBattleDay) battle;
			battleDay.getBattleDayStage().forEach(stage -> {
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
					}
				}
			});
		}
		if (battle instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight battleMidnight = (AbstractBattleMidnight) battle;
			ToolUtils.notNullThenHandle(battleMidnight.getTouchPlane(), tp -> {
				if (tp[0] == true || tp[1] == true) {
					addOneBattleInformation(composite, "触接", tp[0] ? "有" : "", tp[1] ? "有" : "");
				}
			});
			ToolUtils.notNullThenHandle(battleMidnight.getFlare(), flare -> {
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

	private static void addBattleAttack(Composite parent, ArrayList<BattleOneAttack> battleAttacks) {
		if (battleAttacks.stream().mapToInt(ba -> ba.getAttackIndex()).filter(i -> i > 0).count() == 0) {
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

		battleAttacks.forEach(oneAttack -> addOneBattleAttack(parts, oneAttack));
	}

	private static void addOneBattleAttack(Composite[] parts, BattleOneAttack oneAttack) {
		int attackIndex = oneAttack.getAttackIndex();
		int attackType = oneAttack.getAttackType();
		int[] defenseIndexs = oneAttack.getDefenseIndexs();
		int[] damages = oneAttack.getDamages();
		for (int i = 0; i < defenseIndexs.length; i++) {
			if (defenseIndexs[i] == -1) continue;

			if (i == 0) {
				addNewLabel(parts[0], "" + attackIndex);
				addNewLabel(parts[1], "" + attackType);
			} else {
				addNewLabel(parts[0], "");
				addNewLabel(parts[1], "");
			}

			addNewLabel(parts[2], "" + defenseIndexs[i]);
			addNewLabel(parts[3], "" + damages[i]);
			addNewLabel(parts[4], "now/max");
			addNewLabel(parts[5], "状态");
		}
	}

	private static void addRaigekiAttack(Composite parent, AbstractBattleDay day, OpeningAttack raigeki) {
		//frai与fydam长度相同
		int[] frai = raigeki.getFrai();//目标
		int[] erai = raigeki.getErai();
		int[] fdam = raigeki.getFdam();//伤害
		int[] edam = raigeki.getEdam();
		int[] fydam = raigeki.getFydam();//攻击
		int[] eydam = raigeki.getEydam();

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
						attacker = ArrayUtils.addAll(day.getfDeck().getName(), day.getfDeckCombine().getName())[attackindex];
						break;
					case 1 + 6:
						if (day.getfDeckCombine() == null) {
							attacker = day.getfDeck().getName()[attackindex];
						} else {
							attacker = day.getfDeckCombine().getName()[attackindex];
						}
						break;
				}
				switch (edam.length) {
					case 1 + 12:
						defenser = ArrayUtils.addAll(day.geteDeck().getName(), day.geteDeckCombine().getName())[defenseindex];
						break;
					case 1 + 6:
						if (day.geteDeckCombine() == null) {
							defenser = day.geteDeck().getName()[defenseindex];
						} else {
							defenser = day.geteDeckCombine().getName()[defenseindex];
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
						attacker = ArrayUtils.addAll(day.geteDeck().getName(), day.geteDeckCombine().getName())[attackindex];
						break;
					case 1 + 6:
						if (day.geteDeckCombine() == null) {
							attacker = day.geteDeck().getName()[attackindex];
						} else {
							attacker = day.geteDeckCombine().getName()[attackindex];
						}
						break;
				}
				switch (fdam.length) {
					case 1 + 12:
						defenser = ArrayUtils.addAll(day.getfDeck().getName(), day.getfDeckCombine().getName())[defenseindex];
						break;
					case 1 + 6:
						if (day.getfDeckCombine() == null) {
							defenser = day.getfDeck().getName()[defenseindex];
						} else {
							defenser = day.getfDeckCombine().getName()[defenseindex];
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

		String[] names = bd.getName();
		int[] nowhps = bd.getNowhp();
		int[] maxhps = bd.getMaxhp();
		int[] dmg1 = bdad.getDamage();
		int[] atts = ad.getAttack();
		int[] dmg2 = ad.getDamage();
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
				SwtUtils.initLabel(nameLabel, name == null ? "" : name, new GridData(SWT.CENTER, SWT.CENTER, false, false), 55);
				ToolUtils.notNullThenHandle(name, n -> nameLabel.setToolTipText(n));

				String state = bd.getEscapes().contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(now * 1.0 / max);
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
		Predicate<BattleDeckAttackDamage> haveDamage = bdad -> Arrays.stream(bdad.getDamage()).anyMatch(i -> i != 0);
		if (battle instanceof AbstractBattleDay) {
			AbstractBattleDay day = (AbstractBattleDay) battle;
			return haveDamage.test(day.getfDeckAttackDamage()) && haveDamage.test(day.getfDeckCombineAttackDamage());
		}
		if (battle instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight midnight = (AbstractBattleMidnight) battle;
			return haveDamage.test(midnight.getBattleMidnightStage().getfAttackDamage());
		}
		return false;
	}

}
