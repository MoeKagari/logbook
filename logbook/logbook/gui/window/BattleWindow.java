package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;

import logbook.config.AppConfig;
import logbook.context.dto.battle.AbstractBattle;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.translator.BattleDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

/**
 * 战斗窗口
 * @author MoeKagari
 */
public class BattleWindow extends WindowBase {
	private BattleDto lastInWindow = null;//最后一个battleDto(此面板中的)
	private final ScrolledBattleComposite sbc;//战斗窗口
	private final BattleFlowWindow bfw;

	public BattleWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title, true);
		this.sbc = new ScrolledBattleComposite(this.getComposite());
		this.bfw = new BattleFlowWindow(main);
	}

	@Override
	public void update(DataType type) {
		if (type == DataType.PORT) {
			this.sbc.clearWindow();
			this.bfw.sbc.clearWindow();
			GlobalContext.getBattlelist().clearLast();
		} else {
			BattleDto last = GlobalContext.getBattlelist().getLast();
			if (last != this.lastInWindow && last != null) {
				ToolUtils.ifHandle(AppConfig.get().isAutoUpdateBattleFlow(), () -> this.bfw.updateBattle(last, null));//自动更新
				BattleDtoTranslator.newBattleComposite(this.sbc.contentComposite, (battle, ev) -> this.bfw.updateBattle(battle, ev), this.sbc.contentComposite.getChildren().length != 0, last, this.lastInWindow);
			}
		}
		this.lastInWindow = GlobalContext.getBattlelist().getLast();
		this.sbc.layout(true);
	}

	private class BattleFlowWindow extends WindowBase {
		private final ScrolledBattleComposite sbc;//战斗流程窗口

		public BattleFlowWindow(ApplicationMain main) {
			super(main, null, "战斗流程", true);
			this.sbc = new ScrolledBattleComposite(this.getComposite());
		}

		@Override
		protected void handlerAfterHidden() {
			this.sbc.clearWindow();//隐藏窗口时,清空
		}

		private void updateBattle(BattleDto battleDto, SelectionEvent ev) {
			if (battleDto instanceof AbstractBattle) {
				this.sbc.clearWindow();
				if (ev != null) {
					this.updateBattle((AbstractBattle) battleDto);
					this.displayWindow();
				} else if (ev == null && this.getShell().isVisible()) {//自动更新(ev=null)时,需要此界面处于显示状态(最小化状态也可)
					this.updateBattle((AbstractBattle) battleDto);
				}
				this.sbc.layout(false);
			}
		}

		private void updateBattle(AbstractBattle battle) {
			this.updateWindowRedraw(() -> BattleDtoTranslator.createBattleFlow(this.sbc.contentComposite, battle));
		}
	}

	private class ScrolledBattleComposite {
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

		public void clearWindow() {
			ToolUtils.forEach(this.contentComposite.getChildren(), Control::dispose);
			this.layout(true);
		}
	}
}
