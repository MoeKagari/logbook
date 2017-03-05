package logbook.gui.window;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;

import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleType;
import logbook.context.dto.translator.BattleDtoTranslator;
import logbook.context.dto.translator.BattleDtoTranslator.BTResult;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.logic.HPMessage;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class BattleWindow extends WindowBase {
	private BattleDto battle = null;//最后一个battleDto(此面板中的)

	private final ScrolledBattleComposite sbc;//战斗窗口
	private final BattleFlowWindow bfw;

	public BattleWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		this.sbc = new ScrolledBattleComposite(this.getComposite());
		this.bfw = new BattleFlowWindow(main);
	}

	/*-------------------------------------------------------------------*/

	private static void newBattleComposite(Composite composite, Consumer<SelectionEvent> handler, boolean hasDownArrow, BattleDto lastOne, BattleDto lastTwo) {
		if (lastOne.getBattleType() == BattleType.PRACTICE_DAY) {
			SwtUtils.initLabel(new Label(composite, SWT.CENTER), "演习", new GridData(GridData.FILL_HORIZONTAL));
			SwtUtils.initLabel(new Label(composite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
		} else if (hasDownArrow && lastOne.hasDownArrow(lastTwo)) {
			SwtUtils.initLabel(new Label(composite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
		}

		BTResult btr = BattleDtoTranslator.getBattle(lastOne);
		if (btr != null) {
			Composite base = new Composite(composite, SWT.CENTER);
			base.setLayout(SwtUtils.makeGridLayout(1, 0, 2, 0, 0));
			base.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			ToolUtils.notNullThenHandle(btr.getDeckInformations(), di -> newLabels(base, di));
			if (btr.getBefore() != null && btr.getAfter() != null) {
				Composite stateComposite = new Composite(base, SWT.NONE);
				stateComposite.setLayout(SwtUtils.makeGridLayout(3, 4, 0, 0, 0));
				stateComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
				{
					newStateComposite(stateComposite, btr.getBefore());
					SwtUtils.initLabel(new Label(stateComposite, SWT.CENTER), "→", new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 2));
					newStateComposite(stateComposite, btr.getAfter());
				}
			}

			if (lastOne instanceof AbstractBattle) {
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

	/*-------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		Control[] childs = this.sbc.contentComposite.getChildren();
		BattleDto lastOne = GlobalContext.getBattlelist().getLastOne();
		BattleDto lastTwo = GlobalContext.getBattlelist().getLastTwo();
		if (type == DataType.PORT) {//返回母港,清空此窗口
			GlobalContext.getBattlelist().clearLast();
			ToolUtils.forEach(childs, Control::dispose);
			this.bfw.clear();
		} else if (lastOne != this.battle && lastOne != null) {
			newBattleComposite(this.sbc.contentComposite, ev -> this.bfw.update(lastOne), childs.length != 0, lastOne, lastTwo);
		}
		this.battle = lastOne;
		this.sbc.layout(true);
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	@Override
	public int getShellStyle() {
		return super.getShellStyle() | SWT.ON_TOP;
	}

	public class BattleFlowWindow extends WindowBase {
		private final ScrolledBattleComposite sbc;//战斗窗口

		public BattleFlowWindow(ApplicationMain main) {
			super(main, null, "战斗流程");
			this.sbc = new ScrolledBattleComposite(this.getComposite());
		}

		@Override
		public int getShellStyle() {
			return super.getShellStyle() | SWT.ON_TOP;
		}

		public void update(BattleDto battleDto) {
			this.clear();
			BattleDtoTranslator.createBattleFlow(this.sbc.contentComposite, battleDto);
			this.setVisible(true);
			this.sbc.layout(false);
		}

		public void clear() {
			ToolUtils.forEach(this.sbc.contentComposite.getChildren(), Control::dispose);
			this.sbc.layout(false);
		}
	}

	public class ScrolledBattleComposite {
		private final ScrolledComposite sc;
		private final Composite contentComposite;

		public ScrolledBattleComposite(Composite composite) {
			this.sc = new ScrolledComposite(composite, SWT.V_SCROLL);
			this.sc.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			this.sc.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.sc.setExpandHorizontal(true);
			this.sc.setExpandVertical(true);
			this.sc.setAlwaysShowScrollBars(true);

			this.contentComposite = new Composite(this.sc, SWT.NONE);
			this.contentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 5, 0, 0, 5, 5));
			this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			this.sc.setContent(this.contentComposite);
		}

		public void layout(boolean auto_scroll) {
			this.sc.setMinSize(this.contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			this.contentComposite.layout();
			if (auto_scroll) {
				ScrollBar bar = this.sc.getVerticalBar();
				bar.setSelection(bar.getMaximum());
			}
			this.sc.layout();
		}
	}

}
