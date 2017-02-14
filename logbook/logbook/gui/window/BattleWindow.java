package logbook.gui.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;

import logbook.config.AppConstants;
import logbook.context.GlobalContext;
import logbook.context.data.DataType;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.AbstractBattleDay;
import logbook.context.dto.battle.AbstractBattleMidnight;
import logbook.context.dto.battle.AbstractBattleMidnight.BattleMidnightStage;
import logbook.context.dto.battle.AbstractInfoBattle;
import logbook.context.dto.battle.AbstractInfoBattleNext;
import logbook.context.dto.battle.AbstractInfoBattleResult;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleDto.BattleDeck;
import logbook.context.dto.battle.BattleDto.BattleDeckAttackDamage;
import logbook.context.dto.battle.info.InfoBattleGobackPortDto;
import logbook.context.dto.battle.info.InfoBattleShipDeckDto;
import logbook.context.dto.battle.info.InfoBattleStartAirBaseDto;
import logbook.context.dto.battle.info.InfoBattleStartDto;
import logbook.gui.logic.HPMessage;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class BattleWindow extends WindowBase {
	private final Color red;
	private final Color gray;
	private final Color brown;
	private final Color cyan;
	private final Color escape_color;
	private final ScrolledComposite sc;
	private final Composite contentComposite;

	public BattleWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		this.red = new Color(null, new RGB(255, 85, 17));
		this.gray = main.getDisplay().getSystemColor(SWT.COLOR_GRAY);
		this.brown = new Color(null, new RGB(119, 102, 34));
		this.cyan = main.getDisplay().getSystemColor(SWT.COLOR_CYAN);
		this.escape_color = main.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

		this.sc = new ScrolledComposite(this.getComposite(), SWT.V_SCROLL);
		this.sc.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.sc.setExpandHorizontal(true);
		this.sc.setExpandVertical(true);
		this.sc.setAlwaysShowScrollBars(true);

		this.contentComposite = new Composite(this.sc, SWT.NONE);
		this.contentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 8, 0, 0, 5, 5));
		this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.sc.setContent(this.contentComposite);
		this.getShell().addDisposeListener(ev -> {
			this.red.dispose();
			this.brown.dispose();
		});
	}

	private void layout() {
		if (this.isVisible() == false) return;
		this.sc.setMinSize(this.contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.contentComposite.layout();
		ScrollBar bar = this.sc.getVerticalBar();
		bar.setSelection(bar.getMaximum());
		this.sc.layout();
	}

	private void newBattleComposite(BattleDto battleDto) {
		BTResult btr = BattleDtoTranslator.getBattle(battleDto);
		if (btr == null) return;

		Composite base = new Composite(BattleWindow.this.contentComposite, SWT.NONE);
		base.setLayout(SwtUtils.makeGridLayout(3, 0, 0, 0, 0));
		base.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		SwtUtils.insertBlank(base);
		Composite composite = new Composite(base, SWT.NONE);
		composite.setLayout(SwtUtils.makeGridLayout(1, 0, 2, 0, 0));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		SwtUtils.insertBlank(base);

		this.newLabels(composite, btr.getDeckInformations());
		this.newInfoComposite(composite, btr.getBattleInformations());
		if (btr.getBefore() != null && btr.getAfter() != null) {
			Composite stateComposite = new Composite(composite, SWT.NONE);
			stateComposite.setLayout(SwtUtils.makeGridLayout(3, 4, 0, 0, 0));
			stateComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			{
				this.newStateComposite(stateComposite, btr.getBefore());
				SwtUtils.initLabel(new Label(stateComposite, SWT.CENTER), "→", new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
				this.newStateComposite(stateComposite, btr.getAfter());
			}
		}
	}

	/*-------------------------------------------------------------------*/

	private void newLabels(Composite composite, ArrayList<String> deckInformation) {
		if (deckInformation == null) return;
		for (String text : deckInformation)
			SwtUtils.initLabel(new Label(composite, SWT.CENTER), text, new GridData(GridData.FILL_HORIZONTAL));
	}

	private void newInfoComposite(Composite composite, ArrayList<String[]> battleInformations) {
		if (battleInformations == null || battleInformations.size() == 0) return;

		Composite infoComposite = new Composite(composite, SWT.BORDER);
		infoComposite.setLayout(SwtUtils.makeGridLayout(battleInformations.size(), 4, 0, 0, 0));
		infoComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		for (String[] infos : battleInformations) {
			Composite oneComposite = new Composite(infoComposite, SWT.NONE);
			oneComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			oneComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			for (String info : infos) {
				SwtUtils.initLabel(new Label(oneComposite, SWT.CENTER), info, new GridData(SWT.CENTER, SWT.CENTER, false, false));
			}
		}
	}

	private void newStateComposite(Composite composite, ArrayList<String[]> shipInformations) {
		if (shipInformations == null || shipInformations.size() == 0) return;
		int count = shipInformations.stream().mapToInt(strs -> strs.length).max().orElse(0);
		if (count == 0) return;

		Function<String, Color> getBackground = text -> {
			switch (text) {
				case "击沉":
					return this.brown;
				case "大破":
					return this.red;
				case "中破":
					return this.gray;
				case "小破":
					return this.cyan;
				case HPMessage.ESCAPE_STRING:
					return this.escape_color;
				default:
					return null;
			}
		};
		BiFunction<Integer, Integer, String> getString = (i, j) -> {
			int size = shipInformations.size();
			if (j >= size || j < 0) return "";
			String[] strs = shipInformations.get(j);
			int length = strs.length;
			if (i >= length || i < 0) return "";
			return strs[i];
		};

		Composite oneSide = new Composite(composite, SWT.BORDER);
		oneSide.setLayout(SwtUtils.makeGridLayout(count, 4, 0, 0, 0));
		oneSide.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		for (int i = 0; i < count; i++) {
			Composite oneState = new Composite(oneSide, SWT.NONE);
			oneState.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			oneState.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			for (int j = 0; j < shipInformations.size(); j++) {
				String text = getString.apply(i, j);
				Color background = getBackground.apply(text);
				SwtUtils.initLabel(new Label(oneState, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, false, false), background);
			}
		}
	}

	/*-------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		Control[] childs = this.contentComposite.getChildren();
		if (type == DataType.PORT) {
			ToolUtils.forEach(childs, child -> child.dispose());
		} else {
			BattleDto battleDto = GlobalContext.getBattlelist().last();
			if (battleDto != null && (battleDto instanceof InfoBattleShipDeckDto == false)) {
				if (childs.length != 0 && (battleDto instanceof InfoBattleGobackPortDto == false) && (battleDto instanceof InfoBattleStartAirBaseDto == false)) {
					SwtUtils.initLabel(new Label(this.contentComposite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
				}
				if (type == DataType.BATTLE_PRACTICE_DAY) {
					SwtUtils.initLabel(new Label(this.contentComposite, SWT.CENTER), "演习", new GridData(GridData.FILL_HORIZONTAL));
					SwtUtils.initLabel(new Label(this.contentComposite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
				}
				this.newBattleComposite(battleDto);
			}
		}
		this.layout();
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	@Override
	public int getShellStyle() {
		return super.getShellStyle() | SWT.ON_TOP;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.layout();
	}

	public static class BattleDtoTranslator {
		public static BTResult getBattle(BattleDto battleDto) {
			if (battleDto instanceof AbstractBattle) {
				return newBattleDayMidnight(battleDto);
			}
			if (battleDto instanceof AbstractInfoBattle) {
				if (battleDto instanceof AbstractInfoBattleNext) {
					return newBattleStartNext(battleDto);
				}
				if (battleDto instanceof AbstractInfoBattleResult) {
					return newBattleResult(battleDto);
				}
				if (battleDto instanceof InfoBattleGobackPortDto) {
					ArrayList<String> deckInformations = new ArrayList<>();
					deckInformations.add("大破的舰娘已退避");
					return new BTResult(deckInformations, null, null, null);
				}
				if (battleDto instanceof InfoBattleStartAirBaseDto) {
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
					return new BTResult(deckInformations, null, null, null);
				}
				System.out.println(battleDto == null ? "battleDto == null" : battleDto.getBattleType());
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
			if (battleDto instanceof AbstractInfoBattleNext) {
				AbstractInfoBattleNext battleNextDto = (AbstractInfoBattleNext) battleDto;
				String text = "下一点:" + battleNextDto.getMap() + "(" + battleNextDto.getNext() + "," + battleNextDto.getNextType() + (battleNextDto.isGoal() ? ",终点" : "") + ")";
				text = "地图:" + battleNextDto.getMap() + ",Cell:" + battleNextDto.getNext() + "(" + battleNextDto.getNextType() + (battleNextDto.isGoal() ? ",终点" : "") + ")";
				deckInformations.add(text);
			}

			return new BTResult(deckInformations, null, null, null);
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

			return new BTResult(deckInformations, null, null, null);
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

			return new BTResult(null, null, before, after);
		}

	}

	public static class BTResult {
		private final ArrayList<String> deckInformations;
		private final ArrayList<String[]> battleInformations;
		private final ArrayList<String[]> before;
		private final ArrayList<String[]> after;

		public BTResult(ArrayList<String> deckInformations, ArrayList<String[]> battleInformations, ArrayList<String[]> before, ArrayList<String[]> after) {
			this.deckInformations = deckInformations;
			this.battleInformations = battleInformations;
			this.before = before;
			this.after = after;
		}

		public ArrayList<String> getDeckInformations() {
			return this.deckInformations;
		}

		public ArrayList<String[]> getBattleInformations() {
			return this.battleInformations;
		}

		public ArrayList<String[]> getBefore() {
			return this.before;
		}

		public ArrayList<String[]> getAfter() {
			return this.after;
		}
	}

}
