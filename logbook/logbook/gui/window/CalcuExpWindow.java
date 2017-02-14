package logbook.gui.window;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;

import logbook.config.AppConfig;
import logbook.context.GlobalContext;
import logbook.context.data.DataType;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ShipDto;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.SpinnerMouseWheelListener;
import logbook.gui.logic.CalcuExp;
import logbook.gui.logic.data.EvalMap;
import logbook.gui.logic.data.SeaExpMap;
import logbook.gui.logic.data.ShipExpTable;
import logbook.util.SwtUtils;

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
				this.beforelv.setMaximum(ShipExpTable.getMaxLevel());
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
				this.afterlv.setMaximum(ShipExpTable.getMaxLevel());
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
			if (this.ships.size() > 0) {
				this.selectLvAndExp();
				this.calcu();
			}
		}));
		this.seacombo.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.evalcombo.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.flagbtn.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.mvpbtn.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.secretary.addSelectionListener(new ControlSelectionListener(ev -> {
			if (this.ships.size() > 0) {
				this.selectSecretaryShip();
				this.selectLvAndExp();
				this.calcu();
			}
		}));

		this.beforelv.addMouseWheelListener(new SpinnerMouseWheelListener(this.beforelv, ev -> {
			this.beforexp.setText(Integer.toString(ShipExpTable.getExp(this.beforelv.getSelection())));
			this.calcu();
		}));
		this.beforelv.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));

		this.afterlv.addMouseWheelListener(new SpinnerMouseWheelListener(this.afterlv, ev -> {
			this.afterexp.setText(Integer.toString(ShipExpTable.getExp(this.afterlv.getSelection())));
			this.calcu();
		}));
		this.afterlv.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
	}

	/*-----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 300));
	}

	/*-----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		switch (type) {
			case PORT:
				if (this.isVisible()) this.update();
				break;
			default:
				break;
		}
	}

	private void update() {
		this.shipcombo.removeAll();
		this.ships.clear();
		this.ships.addAll(GlobalContext.getShipmap().values());
		if (AppConfig.get().isNotCalcuExpForLevel99Ship()) this.ships.removeIf(ship -> ship.getLv() == 99);
		Collections.sort(this.ships, (a, b) -> -Integer.compare(a.getLv(), b.getLv()));

		int size = this.ships.size();
		if (size <= 0) return;
		for (int i = 0; i < size; i++) {
			ShipDto ship = this.ships.get(i);
			this.shipcombo.add(ship.getName() + "(" + ship.getLv() + ")");
		}

		this.selectSecretaryShip();
		this.selectLvAndExp();
		this.calcu();
	}

	private void selectSecretaryShip() {
		int slectIndex = 0;
		int secretaryShipIndex = this.getSecretaryShipIndex();
		if (secretaryShipIndex >= 0 && secretaryShipIndex < this.shipcombo.getItemCount()) slectIndex = secretaryShipIndex;
		this.shipcombo.select(slectIndex);
	}

	private void selectLvAndExp() {
		int slectIndex = this.shipcombo.getSelectionIndex();

		ShipDto ship = this.ships.get(slectIndex);
		if (this.ships == null) return;

		int beforelv = ship.getLv();
		int afterlv = beforelv == 155 ? 155 : (beforelv + 1);
		this.beforelv.setSelection(beforelv);
		this.afterlv.setSelection(afterlv);

		int beforexp = ship.getCurrentExp();
		int afterexp = ShipExpTable.getExp(afterlv);
		this.beforexp.setText(Integer.toString(beforexp));
		this.afterexp.setText(Integer.toString(afterexp));
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
		if (secretaryShip != -1) {
			for (int i = 0; i < this.ships.size(); i++) {
				if (this.ships.get(i).getId() == secretaryShip) {
					return i;
				}
			}
		}
		return -1;
	}

}
