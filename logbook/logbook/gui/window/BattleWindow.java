package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;

import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.translator.BattleDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class BattleWindow extends WindowBase {
	private BattleDto battle = null;//最后一个battleDto(此面板中的)

	private final ScrolledBattleComposite sbc;//战斗窗口
	private final BattleFlowWindow bfw;

	public BattleWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title, true);

		this.sbc = new ScrolledBattleComposite(this.getComposite());
		this.bfw = new BattleFlowWindow(main);
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
			this.bfw.updateBattle(null, null);
		} else if (lastOne != this.battle && lastOne != null) {
			BattleDtoTranslator.newBattleComposite(this.sbc.contentComposite, ev -> this.bfw.updateBattle(lastOne, ev), childs.length != 0, lastOne, lastTwo);
		}
		this.battle = lastOne;
		this.sbc.layout(true);
	}

	public class BattleFlowWindow extends WindowBase {
		private final ScrolledBattleComposite sbc;//战斗窗口

		public BattleFlowWindow(ApplicationMain main) {
			super(main, null, "战斗流程", true);
			this.sbc = new ScrolledBattleComposite(this.getComposite());
		}

		public void updateBattle(BattleDto battleDto, SelectionEvent ev) {
			ToolUtils.forEach(this.sbc.contentComposite.getChildren(), Control::dispose);
			if (ev == null && this.getShell().isVisible()) {//自动更新(ev=null)时,需要此界面处于显示状态(最小化状态也可以)
				this.updateBattle(battleDto);
			} else if (ev != null) {
				this.updateBattle(battleDto);
				this.setVisible(true);
			}
			this.sbc.layout(false);
		}

		private void updateBattle(BattleDto battleDto) {
			if (battleDto != null && (battleDto instanceof AbstractBattle)) {
				this.getShell().setRedraw(false);
				BattleDtoTranslator.createBattleFlow(this.sbc.contentComposite, (AbstractBattle) battleDto);
				this.getShell().setRedraw(true);
			}
		}

		@Override
		public int getShellStyle() {
			return super.getShellStyle() | SWT.MAX;
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
