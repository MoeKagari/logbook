package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;

import logbook.dto.word.PracticeEnemyDto.PracticeEnemyShip;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.SpinnerMouseWheelListener;
import logbook.gui.logic.CalcuPracticeExp;
import logbook.gui.logic.data.ShipExpMap;
import logbook.update.GlobalContext;
import logbook.update.data.DataType;
import logbook.utils.SwtUtils;

public class CalcuPracticeExpWindow extends WindowBase {
	private final String[] shipNames = { "对方旗舰", "对方2号舰", "对方3号舰", "对方4号舰", "对方5号舰", "对方6号舰" };
	private final String[] results = { "基本经验", "旗舰", "MVP", "旗舰MVP" };
	private final String[] ranks = { "S", "AorB", "C", "D" };
	private final boolean[] flagship = { false, true, false, true };
	private final boolean[] mvp = { false, false, true, true };
	private final double[] eval = { 1.2, 1.0, 0.64, 0.56 };

	private final Label[] shipNameLabels = new Label[6];
	private Spinner firstShipLevel;
	private Spinner secondShipLevel;
	private final Label[] shipLvLabels = new Label[4];
	private final Label[][] expTableText = new Label[4][4]; // [result][rank]

	public CalcuPracticeExpWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
		this.initControl();
		this.initControlEvent();
		this.getShell().pack();
	}

	private void initControl() {
		this.initUpBase();
		SwtUtils.insertHSeparator(this.getComposite());
		this.initDownBase();
	}

	private void initUpBase() {
		Composite practiceinfo = new Composite(this.getComposite(), SWT.NONE);
		practiceinfo.setLayout(SwtUtils.makeGridLayout(6, 0, 0, 0, 0));
		practiceinfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.shipNameLabels[0] = new Label(practiceinfo, SWT.NONE);
		SwtUtils.initLabel(this.shipNameLabels[0], this.shipNames[0], new GridData(), 60);

		this.firstShipLevel = new Spinner(practiceinfo, SWT.BORDER | SWT.CENTER);
		this.firstShipLevel.setMaximum(ShipExpMap.getMaxLevel());
		this.firstShipLevel.setMinimum(1);
		this.firstShipLevel.setSelection(1);
		SwtUtils.initControl(this.firstShipLevel, new GridData(), 45);

		SwtUtils.initLabel(new Label(practiceinfo, SWT.NONE), "Lv", new GridData(), 25);

		this.shipNameLabels[1] = new Label(practiceinfo, SWT.NONE);
		SwtUtils.initLabel(this.shipNameLabels[1], this.shipNames[1], new GridData(), 60);

		this.secondShipLevel = new Spinner(practiceinfo, SWT.BORDER | SWT.CENTER);
		this.secondShipLevel.setMaximum(ShipExpMap.getMaxLevel());
		this.secondShipLevel.setMinimum(1);
		this.firstShipLevel.setSelection(1);
		SwtUtils.initControl(this.secondShipLevel, new GridData(), 45);

		SwtUtils.initLabel(new Label(practiceinfo, SWT.NONE), "Lv", new GridData(), 25);

		for (int i = 0; i < 4; i++) {
			this.shipNameLabels[i + 2] = new Label(practiceinfo, SWT.NONE);
			SwtUtils.initLabel(this.shipNameLabels[i + 2], this.shipNames[i + 2], new GridData(), 60);

			this.shipLvLabels[i] = new Label(practiceinfo, SWT.BORDER | SWT.CENTER);
			SwtUtils.initLabel(this.shipLvLabels[i], "", new GridData(), 60);

			SwtUtils.initLabel(new Label(practiceinfo, SWT.NONE), "Lv", new GridData(), 25);
		}
	}

	private void initDownBase() {
		Composite expTable = new Composite(this.getComposite(), SWT.NONE);
		expTable.setLayout(SwtUtils.makeGridLayout(5, 0, 0, 0, 0));
		expTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridData gdResult = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		GridData gdExp = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);

		SwtUtils.initLabel(new Label(expTable, SWT.NONE), "", gdResult);
		for (int i = 0; i < this.ranks.length; i++) {
			SwtUtils.initLabel(new Label(expTable, SWT.CENTER), this.ranks[i], gdExp, 60);
		}
		for (int i = 0; i < this.results.length; i++) {
			SwtUtils.initLabel(new Label(expTable, SWT.CENTER), this.results[i], gdResult, 60);
			for (int j = 0; j < this.ranks.length; j++) {
				this.expTableText[i][j] = new Label(expTable, SWT.RIGHT | SWT.BORDER);
				this.expTableText[i][j].setLayoutData(gdExp);
			}
		}
	}

	private void initControlEvent() {
		this.firstShipLevel.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.firstShipLevel.addMouseWheelListener(new SpinnerMouseWheelListener(this.firstShipLevel, ev -> this.calcu()));

		this.secondShipLevel.addSelectionListener(new ControlSelectionListener(ev -> this.calcu()));
		this.secondShipLevel.addMouseWheelListener(new SpinnerMouseWheelListener(this.secondShipLevel, ev -> this.calcu()));
	}

	/*------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		switch (type) {
			case PRACTICE_ENEMYINFO:
				this.update();
				this.calcu();
				break;
			default:
				break;
		}
	}

	private void update() {
		if (GlobalContext.getPracticeEnemy() == null) return;
		PracticeEnemyShip[] ships = GlobalContext.getPracticeEnemy().getShips();

		for (int i = 0; i < ships.length; i++) {
			PracticeEnemyShip ship = ships[i];

			String name = ship.exist() ? ship.getName() : this.shipNames[i];
			this.shipNameLabels[i].setText(name);
			this.shipNameLabels[i].setToolTipText(name);

			if (i == 0) {
				this.firstShipLevel.setSelection(ship.exist() ? ship.getLv() : 1);
			} else if (i == 1) {
				this.secondShipLevel.setSelection(ship.exist() ? ship.getLv() : 1);
			} else {
				this.shipLvLabels[i - 2].setText(ship.exist() ? Integer.toString(ship.getLv()) : "");
			}
		}
	}

	private void calcu() {
		int firstLevel = Integer.parseInt(this.firstShipLevel.getText());
		int secondLevel = Integer.parseInt(this.secondShipLevel.getText());
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.expTableText[i][j].setText(Integer.toString(CalcuPracticeExp.calcu(firstLevel, secondLevel, this.flagship[i], this.mvp[i], this.eval[j])));
			}
		}
	}
}
