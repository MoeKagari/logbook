package logbook.gui.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;

import logbook.config.AppConfig;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.SpinnerMouseWheelListener;
import logbook.gui.logic.CalcuExp;
import logbook.gui.logic.data.EvalMap;
import logbook.gui.logic.data.SeaExpMap;
import logbook.gui.logic.data.ShipExpMap;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class CalcuExpWindow extends WindowBase {
	private Combo shipcombo;
	private Button secretary;
	private Spinner beforelv;
	private Label beforexp;
	private Spinner afterlv;
	private Label afterexp;
	private Combo seacombo;
	private Combo evalcombo;
	private Button flagbtn;
	private Button mvpbtn;
	private Label getexp;
	private Label needexp;
	private Label battlecount;

	private ArrayList<ShipDto> ships = new ArrayList<>();

	public CalcuExpWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
		this.initControl();
		this.initControlEvent();
		this.getShell().pack();
	}

	private void initControl() {
		Composite select = new Composite(this.getComposite(), SWT.NONE);
		select.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
		select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			this.shipcombo = new Combo(select, SWT.READ_ONLY);

			this.secretary = new Button(select, SWT.NONE);
			this.secretary.setText("秘书舰");
		}

		Composite plan = new Composite(this.getComposite(), SWT.NONE);
		plan.setLayout(SwtUtils.makeGridLayout(5, 0, 0, 0, 0));
		plan.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			SwtUtils.initLabel(new Label(plan, SWT.NONE), "现在等级", new GridData());
			{
				this.beforelv = new Spinner(plan, SWT.BORDER);
				GridData gdBeforelv = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gdBeforelv.widthHint = SwtUtils.DPIAwareWidth(45);
				this.beforelv.setLayoutData(gdBeforelv);
				this.beforelv.setMaximum(ShipExpMap.getMaxLevel());
				this.beforelv.setMinimum(1);
			}
			SwtUtils.initLabel(new Label(plan, SWT.NONE), "", new GridData(), 10);
			{
				this.beforexp = new Label(plan, SWT.BORDER);
				SwtUtils.initLabel(this.beforexp, "0", new GridData(), 60);
			}
			SwtUtils.initLabel(new Label(plan, SWT.NONE), "exp", new GridData());
			SwtUtils.initLabel(new Label(plan, SWT.NONE), "目标等级", new GridData());
			{
				this.afterlv = new Spinner(plan, SWT.BORDER);
				GridData gdAfterlv = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gdAfterlv.widthHint = SwtUtils.DPIAwareWidth(45);
				this.afterlv.setLayoutData(gdAfterlv);
				this.afterlv.setMaximum(ShipExpMap.getMaxLevel());
				this.afterlv.setMinimum(1);
			}
			SwtUtils.initLabel(new Label(plan, SWT.NONE), "", new GridData(), 10);
			{
				this.afterexp = new Label(plan, SWT.BORDER);
				SwtUtils.initLabel(this.afterexp, "0", new GridData(), 60);
			}
			SwtUtils.initLabel(new Label(plan, SWT.NONE), "exp", new GridData());
		}

		Composite plan2 = new Composite(this.getComposite(), SWT.NONE);
		plan2.setLayout(SwtUtils.makeGridLayout(5, 0, 0, 0, 0));
		plan2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			SwtUtils.initLabel(new Label(plan2, SWT.NONE), "海域∶", new GridData());
			{
				this.seacombo = new Combo(plan2, SWT.READ_ONLY);
				for (String entry : SeaExpMap.get().keySet()) {
					this.seacombo.add(entry);
				}
				this.seacombo.select(0);
			}
			SwtUtils.insertBlank(plan2, 10);
			SwtUtils.initLabel(new Label(plan2, SWT.NONE), "评价∶", new GridData());
			{
				this.evalcombo = new Combo(plan2, SWT.READ_ONLY);
				for (String entry : EvalMap.get().keySet()) {
					this.evalcombo.add(entry);
				}
				this.evalcombo.select(0);
			}
		}

		Composite plan3 = new Composite(this.getComposite(), SWT.NONE);
		plan3.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
		plan3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			this.flagbtn = new Button(plan3, SWT.CHECK);
			this.flagbtn.setText("旗舰");
			this.mvpbtn = new Button(plan3, SWT.CHECK);
			this.mvpbtn.setText("MVP");
		}

		SwtUtils.insertHSeparator(this.getComposite());

		Composite result = new Composite(this.getComposite(), SWT.NONE);
		result.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0));
		result.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			SwtUtils.initLabel(new Label(result, SWT.NONE), "一回所得", new GridData());
			{
				this.getexp = new Label(result, SWT.BORDER);
				SwtUtils.initLabel(this.getexp, "", new GridData(), 55);
			}
			new Label(result, SWT.NONE);
			new Label(result, SWT.NONE);
			SwtUtils.initLabel(new Label(result, SWT.NONE), "升级所需", new GridData());
			{
				this.needexp = new Label(result, SWT.BORDER);
				SwtUtils.initLabel(this.needexp, "", new GridData(), 55);
			}
			SwtUtils.initLabel(new Label(result, SWT.NONE), "战斗回数", new GridData());
			{
				this.battlecount = new Label(result, SWT.BORDER);
				SwtUtils.initLabel(this.battlecount, "", new GridData(), 55);
			}
		}
	}

	private void initControlEvent() {
		this.shipcombo.addSelectionListener(new ControlSelectionListener(ev -> {
			this.setLvAndExp();
			this.calcu();
		}));
		this.seacombo.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.evalcombo.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.flagbtn.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.mvpbtn.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.secretary.addSelectionListener(new ControlSelectionListener(ev -> {
			if (this.ships.size() == 0) {
				this.updateDatas();
			} else {
				this.selectSecretaryShip();
				this.setLvAndExp();
				this.calcu();
			}
		}));

		BiConsumer<Label, Spinner> combiner = (label, spinner) -> {
			label.setText(Integer.toString(ShipExpMap.getExp(spinner.getSelection())));
			this.calcu();
		};
		this.beforelv.addMouseWheelListener(new SpinnerMouseWheelListener(this.beforelv, ev -> combiner.accept(this.beforexp, this.beforelv)));
		this.beforelv.addSelectionListener(new ControlSelectionListener(ev -> combiner.accept(this.beforexp, this.beforelv)));

		this.afterlv.addMouseWheelListener(new SpinnerMouseWheelListener(this.afterlv, ev -> combiner.accept(this.afterexp, this.afterlv)));
		this.afterlv.addSelectionListener(new ControlSelectionListener(ev -> combiner.accept(this.afterexp, this.afterlv)));
	}

	/*-----------------------------------------------------------------------------------------------------------------*/

	@Override
	protected void handlerBeforeDisplay() {
		this.updateDatas();
	}

	@Override
	protected void handlerAfterHidden() {
		this.ships.clear();
		this.shipcombo.removeAll();
	}

	/*-----------------------------------------------------------------------------------------------------------------*/

	private void updateDatas() {
		this.shipcombo.removeAll();
		this.ships.clear();
		this.ships.addAll(GlobalContext.getShipMap().values());
		ToolUtils.ifHandle(AppConfig.get().isNotCalcuExpForLevel99Ship(), () -> this.ships.removeIf(ship -> ship.getLv() == 99));
		Collections.sort(this.ships, (a, b) -> Integer.compare(b.getLv(), a.getLv()));

		for (int i = 0; i < this.ships.size(); i++) {
			ShipDto ship = this.ships.get(i);
			this.shipcombo.add(ShipDtoTranslator.getName(ship) + "(" + ship.getLv() + ")");
		}

		if (this.ships.size() != 0) {
			this.selectSecretaryShip();
			this.setLvAndExp();
			this.calcu();
		}
	}

	private void selectSecretaryShip() {
		if (this.getItemCount() == 0) return;
		int secretaryShipIndex = this.getSecretaryShipIndex();//旗舰99级,并且启用过滤99级,返回-1
		if (secretaryShipIndex >= 0) this.shipcombo.select(secretaryShipIndex);
	}

	private void setLvAndExp() {
		if (this.getItemCount() == 0) return;
		int slectIndex = this.shipcombo.getSelectionIndex();
		slectIndex = slectIndex < 0 ? 0 : slectIndex;
		this.shipcombo.select(slectIndex);
		ShipDto ship = this.ships.get(slectIndex);

		int beforelv = ship.getLv();
		int afterlv = beforelv == 155 ? 155 : (beforelv + 1);
		this.beforelv.setSelection(beforelv);
		this.afterlv.setSelection(afterlv);
		this.beforexp.setText(Integer.toString(ship.getCurrentExp()));
		this.afterexp.setText(Integer.toString(ShipExpMap.getExp(afterlv)));
	}

	private void calcu() {
		int oneTimeExp = CalcuExp.calcu(SeaExpMap.get(this.seacombo.getText()), this.flagbtn.getSelection(), this.mvpbtn.getSelection(), EvalMap.get(this.evalcombo.getText()));
		int need = Integer.parseInt(this.afterexp.getText()) - Integer.parseInt(this.beforexp.getText());
		int count = Math.abs(need) / oneTimeExp + ((Math.abs(need) % oneTimeExp != 0) ? 1 : 0);

		this.getexp.setText(Integer.toString(oneTimeExp));
		this.needexp.setText(Integer.toString(need));
		this.battlecount.setText(Integer.toString(count));
	}

	private int getSecretaryShipIndex() {
		DeckDto deck = GlobalContext.getDeckRoom()[0].getDeck();
		int secretaryShip = deck != null ? deck.getShips()[0] : -1;
		for (int i = 0; i < this.ships.size(); i++) {
			if (this.ships.get(i).getId() == secretaryShip) {
				return i;
			}
		}
		return -1;
	}

	private int getItemCount() {
		return this.shipcombo.getItemCount();
	}

}
