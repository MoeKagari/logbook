package logbook.gui.window;

import java.util.ArrayList;
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

import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.battle.BattleType;
import logbook.context.dto.translator.BattleDtoTranslator;
import logbook.context.dto.translator.BattleDtoTranslator.BTResult;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.gui.logic.HPMessage;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class BattleWindow extends WindowBase {
	private BattleDto battle = null;

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

	private void newBattleComposite(boolean hasDownArrow, BattleDto lastOne, BattleDto lastTwo) {
		if (lastOne.getBattleType() == BattleType.PRACTICE_DAY) {
			SwtUtils.initLabel(new Label(this.contentComposite, SWT.CENTER), "演习", new GridData(GridData.FILL_HORIZONTAL));
			SwtUtils.initLabel(new Label(this.contentComposite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
		} else if (lastOne.isPracticeBattle() || (hasDownArrow && lastOne.hasDownArrow(lastTwo))) {
			SwtUtils.initLabel(new Label(this.contentComposite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
		}

		BTResult btr = BattleDtoTranslator.getBattle(lastOne);
		if (btr != null) {
			Composite base = new Composite(this.contentComposite, SWT.NONE);
			base.setLayout(SwtUtils.makeGridLayout(3, 0, 0, 0, 0));
			base.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			SwtUtils.insertBlank(base);
			Composite composite = new Composite(base, SWT.NONE);
			composite.setLayout(SwtUtils.makeGridLayout(1, 0, 2, 0, 0));
			composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			SwtUtils.insertBlank(base);

			this.newLabels(composite, btr.getDeckInformations());
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
	}

	/*-------------------------------------------------------------------*/

	private void newLabels(Composite composite, ArrayList<String> deckInformation) {
		if (deckInformation == null) return;
		for (String text : deckInformation)
			SwtUtils.initLabel(new Label(composite, SWT.CENTER), text, new GridData(GridData.FILL_HORIZONTAL));
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
		BiFunction<Integer, Integer, String> getText = (i, j) -> {
			if (j >= shipInformations.size() || j < 0) return "";
			String[] strs = shipInformations.get(j);
			if (i >= strs.length || i < 0) return "";
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
				String text = getText.apply(i, j);
				Color background = getBackground.apply(text);
				SwtUtils.initLabel(new Label(oneState, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, false, false), background);
			}
		}
	}

	/*-------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		Control[] childs = this.contentComposite.getChildren();
		BattleDto lastOne = GlobalContext.getBattlelist().getLastOne();
		BattleDto lastTwo = GlobalContext.getBattlelist().getLastTwo();
		if (type == DataType.PORT) {//返回母港,清空此窗口
			GlobalContext.getBattlelist().clearLast();
			ToolUtils.forEach(childs, child -> child.dispose());
		} else if (lastOne != this.battle && lastOne != null) {
			this.newBattleComposite(childs.length != 0, lastOne, lastTwo);
		}
		this.battle = lastOne;
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

}
