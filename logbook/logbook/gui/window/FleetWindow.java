package logbook.gui.window;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import logbook.config.AppConstants;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.translator.DeckDtoTranslator;
import logbook.context.dto.translator.ItemDtoTranslator;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.context.update.GlobalContextUpdater;
import logbook.context.update.data.DataType;
import logbook.context.update.data.EventListener;
import logbook.gui.logic.HPMessage;
import logbook.gui.logic.TimeString;
import logbook.internal.TrayMessageBox;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class FleetWindow implements EventListener {
	private static final int MAXCHARA = 6;
	private static final int MAXEQUIP = 5;

	/** 舰队名 */
	private Label fleetNameLabel;
	/** 舰队速度 */
	private Label sokuLabel;
	/** 泊地修理timer */
	private Label akashiLabel;
	/** 制空 */
	private Label zhikongLabel;
	/** 索敌 */
	private Label suodiLabel;
	/** 总等级 */
	private Label totallvLabel;

	private final ShipComposite[] shipComposites = new ShipComposite[MAXCHARA];

	private int id;//1,2,3,4
	private final String defaultFleetName;
	private final Composite composite;
	private final boolean notifiyAkashitimer;
	private final FleetAkashiTimer akashiTimer = new FleetAkashiTimer();

	public FleetWindow(Composite composite, int id) {
		this(composite, id, true);
	}

	public FleetWindow(Composite composite, int id, boolean notifiyAkashitimer) {
		this.id = id;
		this.defaultFleetName = AppConstants.DEFAULT_FLEET_NAME[id - 1];
		this.composite = composite;
		this.notifiyAkashitimer = notifiyAkashitimer;
		this.init();
		GlobalContextUpdater.addEventListener(this);
	}

	public void init() {
		this.initComposite();
		this.initFleetNameComposite();
		SwtUtils.insertHSeparator(this.composite);
		this.initInfoComposite();
		SwtUtils.insertHSeparator(this.composite);
		this.initInfoComposite2();
		SwtUtils.insertHSeparator(this.composite);
		this.initFleetComposite();
	}

	private void initComposite() {
		this.composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void initFleetNameComposite() {
		this.fleetNameLabel = new Label(this.composite, SWT.CENTER);
		SwtUtils.initLabel(this.fleetNameLabel, this.defaultFleetName, new GridData(GridData.FILL_HORIZONTAL));
	}

	private void initInfoComposite() {
		Composite infoComposite = new Composite(this.composite, SWT.NONE);
		GridLayout infoCompositeGridData = SwtUtils.makeGridLayout(10, 0, 0, 0, 0);
		infoCompositeGridData.marginTop = -3;
		infoCompositeGridData.marginBottom = -2;
		infoComposite.setLayout(infoCompositeGridData);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			SwtUtils.initLabel(new Label(infoComposite, SWT.NONE), "制空:", new GridData());

			this.zhikongLabel = new Label(infoComposite, SWT.LEFT);
			SwtUtils.initLabel(this.zhikongLabel, "0000", new GridData(), 32);

			SwtUtils.insertBlank(infoComposite, 5);
			SwtUtils.initLabel(new Label(infoComposite, SWT.NONE), "索敌:", new GridData());

			this.suodiLabel = new Label(infoComposite, SWT.LEFT);
			SwtUtils.initLabel(this.suodiLabel, "000", new GridData(), 24);

			SwtUtils.insertBlank(infoComposite, 5);
			SwtUtils.initLabel(new Label(infoComposite, SWT.NONE), "总等级:", new GridData());

			this.totallvLabel = new Label(infoComposite, SWT.LEFT);
			SwtUtils.initLabel(this.totallvLabel, "000", new GridData(), 24);

			SwtUtils.insertBlank(infoComposite);
		}
	}

	private void initInfoComposite2() {
		Composite infoComposite2 = new Composite(this.composite, SWT.NONE);
		GridLayout infoComposite2GridLayout = SwtUtils.makeGridLayout(3, 0, 0, 0, 0);
		infoComposite2GridLayout.marginTop = -3;
		infoComposite2GridLayout.marginBottom = -2;
		infoComposite2.setLayout(infoComposite2GridLayout);
		infoComposite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			this.sokuLabel = new Label(infoComposite2, SWT.NONE);
			SwtUtils.initLabel(this.sokuLabel, "高速", new GridData());

			SwtUtils.insertBlank(infoComposite2);

			this.akashiLabel = new Label(infoComposite2, SWT.RIGHT);
			this.akashiLabel.setToolTipText("泊地修理");
			SwtUtils.initLabel(this.akashiLabel, "000时00分00秒", new GridData(), 87);
		}
	}

	private void initFleetComposite() {
		Composite fleetComposite = new Composite(this.composite, SWT.NONE);
		fleetComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		fleetComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (int i = 0; i < MAXCHARA; i++) {
			this.shipComposites[i] = new ShipComposite(fleetComposite);
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		DeckDto deck = this.getDeck();

		switch (type) {
			case UPDATEDECKNAME:
				ToolUtils.notNullThenHandle(deck, d -> SwtUtils.setText(this.fleetNameLabel, d.getName()));
				return;
			case PORT:
				this.akashiTimer.reset();
				break;
			case CHANGE:
				if (deck != null && DeckDtoTranslator.isAkashiFlagship(deck)) {
					if (DeckDtoTranslator.isOnlyAkashi(deck) == false) {
						this.akashiTimer.resetAkashiFlagship();
					}
				}
				break;
			default:
				break;
		}

		this.composite.setRedraw(false);
		this.updateDeck(deck);
		this.composite.setRedraw(true);
	}

	private void updateDeck(DeckDto deck) {
		if (deck == null) return;
		//舰队名
		SwtUtils.setText(this.fleetNameLabel, deck.getName());
		//制空,索敌,总等级,舰队速度
		SwtUtils.setText(this.zhikongLabel, String.valueOf(DeckDtoTranslator.getZhikong(deck)));
		SwtUtils.setText(this.suodiLabel, String.valueOf(DeckDtoTranslator.getSuodi(deck)));
		SwtUtils.setText(this.totallvLabel, String.valueOf(DeckDtoTranslator.getTotalLv(deck)));
		SwtUtils.setText(this.sokuLabel, DeckDtoTranslator.highspeed(deck) ? "高速" : "低速");
		//ship 状态
		int[] ships = deck.getShips();
		for (int i = 0; i < ships.length; i++) {
			ShipDto ship = GlobalContext.getShipMap().get(ships[i]);
			FleetWindow.updateShipInformation(this.shipComposites[i], ship);
		}
	}

	private static void updateShipInformation(ShipComposite shipComposite, ShipDto ship) {
		SwtUtils.setText(shipComposite.iconlabel, "");
		SwtUtils.setText(shipComposite.namelabel, ship != null ? ShipDtoTranslator.getName(ship) : "");
		SwtUtils.setToolTipText(shipComposite.namelabel, ship != null ? ShipDtoTranslator.getName(ship) : "");
		SwtUtils.setText(shipComposite.hplabel, ship != null ? (ship.getNowHp() + "/" + ship.getMaxHp()) : "");
		SwtUtils.setText(shipComposite.hpmsglabel, ship != null ? HPMessage.getString(ship.getNowHp() * 1.0 / ship.getMaxHp()) : "");
		SwtUtils.setText(shipComposite.lvlabel, ship != null ? ("Lv." + ship.getLv()) : "");
		SwtUtils.setText(shipComposite.condlabel, ship != null ? String.valueOf(ship.getCond()) : "");

		//五个装备
		Character[] equipTexts = new Character[MAXEQUIP];
		ArrayList<String> equipTooltipTexts = new ArrayList<>();
		if (ship != null) {
			int[] slots = ArrayUtils.addAll(Arrays.copyOfRange(ship.getSlots(), 0, 4), ship.getSlotex());
			for (int index = 0; index < MAXEQUIP; index++) {
				ItemDto item = GlobalContext.getItemMap().get(slots[index]);
				if (item != null) {
					int star = item.getLevel();
					int alv = item.getAlv();
					equipTexts[index] = ItemDtoTranslator.getOneWordName(item);
					equipTooltipTexts.add(ItemDtoTranslator.getName(item) + (alv > 0 ? (" 熟" + alv) : "") + (star > 0 ? (" ★" + star) : ""));
				}
			}
		}
		String tooltip = StringUtils.join(equipTooltipTexts, "\n");
		for (int index = 0; index < MAXEQUIP; index++) {
			Label label = shipComposite.equipslabel[index];
			Character ch = equipTexts[index];
			SwtUtils.setText(label, ch != null ? ch.toString() : "");
			label.setToolTipText(ch != null ? tooltip : "");
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	private DeckDto getDeck() {
		return GlobalContext.getDeckRoom()[this.id - 1].getDeck();
	}

	public FleetAkashiTimer getAkashiTimer() {
		return this.akashiTimer;
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	/** 泊地修理计时器 */
	public class FleetAkashiTimer {
		private long time = TimeString.getCurrentTime();
		private final static int RESET_LIMIT = 20;
		private final static int ONE_MINUTE = 60;

		public void update(TrayMessageBox box, long currentTime) {
			long rest = (currentTime - this.time) / 1000;
			FleetWindow.this.akashiLabel.setText(TimeString.toDateRestString(rest));
			if (FleetWindow.this.notifiyAkashitimer) {
				if (rest == RESET_LIMIT * ONE_MINUTE) {
					DeckDto deck = FleetWindow.this.getDeck();
					if (deck != null && DeckDtoTranslator.shouldNotifyAkashiTimer(deck) && DeckDtoTranslator.canAkashiRepair(deck)) {
						box.add("泊地修理", FleetWindow.this.defaultFleetName + "∶泊地修理已20分钟");
					}
				}
			}
		}

		public void reset() {
			long currentTime = TimeString.getCurrentTime();
			if ((currentTime - this.time) / 1000 >= RESET_LIMIT * ONE_MINUTE) {
				this.time = currentTime;
			}
		}

		public void resetAkashiFlagship() {
			this.time = TimeString.getCurrentTime();
		}
	}

	private class ShipComposite extends Composite {
		Label iconlabel, namelabel, lvlabel, hplabel, hpmsglabel, condlabel;
		Label[] equipslabel;
		Composite upsideBase, downsideBase, equipBase;

		public ShipComposite(Composite fleetComposite) {
			super(fleetComposite, SWT.NONE);
			this.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.iconlabel = new Label(this, SWT.CENTER);
			SwtUtils.initLabel(this.iconlabel, "!", new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2), 16);

			this.upsideBase = new Composite(this, SWT.NONE);
			GridLayout upsideBaseGridLayout = SwtUtils.makeGridLayout(5, 0, 0, 0, 0);
			upsideBaseGridLayout.marginTop = 1;
			upsideBaseGridLayout.marginBottom = -1;
			this.upsideBase.setLayout(upsideBaseGridLayout);
			this.upsideBase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				this.namelabel = new Label(this.upsideBase, SWT.LEFT);
				SwtUtils.initLabel(this.namelabel, "舰娘名", new GridData(GridData.FILL_HORIZONTAL));

				SwtUtils.insertBlank(this.upsideBase, 5);

				this.hplabel = new Label(this.upsideBase, SWT.RIGHT);
				SwtUtils.initLabel(this.hplabel, "000/000", new GridData(), 48);

				SwtUtils.insertBlank(this.upsideBase, 5);

				this.hpmsglabel = new Label(this.upsideBase, SWT.RIGHT);
				SwtUtils.initLabel(this.hpmsglabel, "健在", new GridData(), 24);
			}

			this.downsideBase = new Composite(this, SWT.NONE);
			GridLayout downsideBaseGridLayout = SwtUtils.makeGridLayout(4, 0, 0, 0, 0);
			downsideBaseGridLayout.marginTop = -2;
			downsideBaseGridLayout.marginBottom = -2;
			this.downsideBase.setLayout(downsideBaseGridLayout);
			this.downsideBase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				this.lvlabel = new Label(this.downsideBase, SWT.LEFT);
				SwtUtils.initLabel(this.lvlabel, "Lv.100", new GridData(), 40);

				int len = MAXEQUIP;
				this.equipBase = new Composite(this.downsideBase, SWT.NONE);
				this.equipBase.setLayout(SwtUtils.makeGridLayout(len, 0, 0, 0, 0));
				{
					this.equipslabel = new Label[len];
					for (int j = 0; j < this.equipslabel.length; j++) {
						this.equipslabel[j] = new Label(this.equipBase, SWT.CENTER);
						SwtUtils.initLabel(this.equipslabel[j], "装", new GridData(), 12);
					}
				}

				SwtUtils.insertBlank(this.downsideBase);

				this.condlabel = new Label(this.downsideBase, SWT.RIGHT);
				SwtUtils.initLabel(this.condlabel, "100", new GridData(), 24);
			}
		}
	}

}
