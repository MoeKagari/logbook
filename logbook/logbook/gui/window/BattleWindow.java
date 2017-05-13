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
import logbook.dto.memory.battle.AbstractBattle;
import logbook.dto.memory.battle.BattleDto;
import logbook.dto.memory.battle.info.InfoBattleShipdeckDto;
import logbook.dto.translator.BattleDtoTranslator;
import logbook.update.GlobalContext;
import logbook.update.data.DataType;
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
		this.sbc = new ScrolledBattleComposite(this.getComposite(), 0);
		this.bfw = new BattleFlowWindow(main);
	}

	@Override
	public void update(DataType type) {
		if (GlobalContext.getMemorylist().haveNewBattle()) {
			BattleDto last = GlobalContext.getMemorylist().getLastBattle();

			//自动更新
			if (AppConfig.get().isAutoUpdateBattleFlow()) {
				this.bfw.updateBattle(last, null);
			}

			//面板没有内容时,没有downarrow
			boolean haveDownArrow = this.sbc.contentComposite.getChildren().length != 0;
			//battleresult → shipdeck → next ,后两个之间加入downarrow
			haveDownArrow &= this.lastInWindow instanceof InfoBattleShipdeckDto;
			BattleDtoTranslator.newBattleComposite(this.sbc.contentComposite, this.bfw::updateBattle, haveDownArrow, last);

			this.lastInWindow = last;
		} else {
			if (type == DataType.PORT) {
				this.sbc.clearWindow();
				this.bfw.sbc.clearWindow();
				this.lastInWindow = null;
			}
		}

		this.sbc.layout(true);
	}

	public BattleFlowWindow getBattleFlowWindow() {
		return this.bfw;
	}

	private class BattleFlowWindow extends WindowBase {
		private final ScrolledBattleComposite sbc;//战斗流程窗口

		public BattleFlowWindow(ApplicationMain main) {
			super(main, null, "战斗流程", true);
			this.sbc = new ScrolledBattleComposite(this.getComposite(), 5);
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
			this.updateWindowRedraw(ToolUtils.getRunnable(this.sbc.contentComposite, battle, BattleDtoTranslator::createBattleFlow));
		}
	}

	private class ScrolledBattleComposite {
		private final ScrolledComposite sc;
		private final Composite contentComposite;

		public ScrolledBattleComposite(Composite composite, int space) {
			this.sc = new ScrolledComposite(composite, SWT.V_SCROLL);
			this.sc.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			this.sc.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.sc.setExpandHorizontal(true);
			this.sc.setExpandVertical(true);
			this.sc.setAlwaysShowScrollBars(true);

			this.contentComposite = new Composite(this.sc, SWT.NONE);
			this.contentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, space, 0, 0, 5, 5));
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
