package logbook.gui.window;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import logbook.config.AppConstants;
import logbook.context.GlobalContext;
import logbook.context.GlobalContextUpdater;
import logbook.context.data.DataType;
import logbook.context.data.EventListener;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.ShipDto;
import logbook.gui.logic.HPMessage;
import logbook.gui.logic.ShipTranslator;
import logbook.gui.logic.TimeString;
import logbook.internal.TrayMessageBox;
import logbook.util.SwtUtils;

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

	private final Composite[] upsideBases = new Composite[MAXCHARA];
	private final Label[] iconLabels = new Label[MAXCHARA];
	private final Label[] nameLabels = new Label[MAXCHARA];
	private final Label[] lvLabels = new Label[MAXCHARA];
	private final Label[] hpLabels = new Label[MAXCHARA];
	private final Label[] hpmsgLabels = new Label[MAXCHARA];
	private final Label[][] equipsLabels = new Label[MAXCHARA][MAXEQUIP];
	private final Label[] condLabels = new Label[MAXCHARA];

	private int id;//1,2,3,4
	private final String defaultFleetName;
	private Composite composite;
	private final FleetAkashiTimer akashiTimer;
	private boolean notifiyAkashitimer = true;

	public FleetWindow(Composite composite, int id, boolean notifiyAkashitimer) {
		this(composite, id);
		this.notifiyAkashitimer = notifiyAkashitimer;
	}

	public FleetWindow(Composite composite, int id) {
		this.id = id;
		this.defaultFleetName = AppConstants.DEFAULT_FLEET_NAME[id - 1];
		this.composite = composite;
		this.akashiTimer = new FleetAkashiTimer();
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

		SwtUtils.initLabel(new Label(infoComposite, SWT.NONE), "制空:", new GridData());
		{
			this.zhikongLabel = new Label(infoComposite, SWT.LEFT);
			SwtUtils.initLabel(this.zhikongLabel, "0000", new GridData(), 32);
		}
		SwtUtils.insertBlank(infoComposite, 5);
		SwtUtils.initLabel(new Label(infoComposite, SWT.NONE), "索敌:", new GridData());
		{
			this.suodiLabel = new Label(infoComposite, SWT.LEFT);
			SwtUtils.initLabel(this.suodiLabel, "000", new GridData(), 24);
		}
		SwtUtils.insertBlank(infoComposite, 5);
		SwtUtils.initLabel(new Label(infoComposite, SWT.NONE), "总等级:", new GridData());
		{
			this.totallvLabel = new Label(infoComposite, SWT.LEFT);
			SwtUtils.initLabel(this.totallvLabel, "000", new GridData(), 24);
		}
		SwtUtils.insertBlank(infoComposite);
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
		}
		SwtUtils.insertBlank(infoComposite2);
		{
			this.akashiLabel = new Label(infoComposite2, SWT.RIGHT);
			this.akashiLabel.setToolTipText("泊地修理");
			SwtUtils.initLabel(this.akashiLabel, "000时00分00秒", new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1), 87);
		}
	}

	private void initFleetComposite() {
		Composite fleetComposite = new Composite(this.composite, SWT.NONE);
		fleetComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		fleetComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (int i = 0; i < MAXCHARA; i++) {
			this.initOneShipComposite(i, fleetComposite);
		}
	}

	private void initOneShipComposite(int i, Composite fleetComposite) {
		Composite shipComposite = new Composite(fleetComposite, SWT.NONE);
		shipComposite.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
		shipComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label iconlabel, namelabel, lvlabel, hplabel, hpmsglabel, condlabel;
		Label[] equipslabel;
		Composite upsideBase, downsideBase, equipBase;

		iconlabel = new Label(shipComposite, SWT.CENTER);
		SwtUtils.initLabel(iconlabel, "!", new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2), 16);

		upsideBase = new Composite(shipComposite, SWT.NONE);
		GridLayout upsideBaseGridLayout = SwtUtils.makeGridLayout(5, 0, 0, 0, 0);
		upsideBaseGridLayout.marginTop = 1;
		upsideBaseGridLayout.marginBottom = -1;
		upsideBase.setLayout(upsideBaseGridLayout);
		upsideBase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			namelabel = new Label(upsideBase, SWT.LEFT);
			SwtUtils.initLabel(namelabel, "名前", new GridData(GridData.FILL_HORIZONTAL));
		}
		SwtUtils.insertBlank(upsideBase, 5);
		{
			hplabel = new Label(upsideBase, SWT.RIGHT);
			SwtUtils.initLabel(hplabel, "000/000", new GridData(), 48);
		}
		SwtUtils.insertBlank(upsideBase, 5);
		{
			hpmsglabel = new Label(upsideBase, SWT.CENTER);
			SwtUtils.initLabel(hpmsglabel, "健在", new GridData(), 24);
		}

		downsideBase = new Composite(shipComposite, SWT.NONE);
		GridLayout downsideBaseGridLayout = SwtUtils.makeGridLayout(4, 0, 0, 0, 0);
		downsideBaseGridLayout.marginTop = -2;
		downsideBaseGridLayout.marginBottom = -2;
		downsideBase.setLayout(downsideBaseGridLayout);
		downsideBase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			lvlabel = new Label(downsideBase, SWT.LEFT);
			SwtUtils.initLabel(lvlabel, "Lv.100", new GridData(), 40);
		}
		{
			int len = MAXEQUIP;
			equipBase = new Composite(downsideBase, SWT.NONE);
			equipBase.setLayout(SwtUtils.makeGridLayout(len, 0, 0, 0, 0));
			{
				equipslabel = new Label[len];
				for (int j = 0; j < equipslabel.length; j++) {
					equipslabel[j] = new Label(equipBase, SWT.CENTER);
					SwtUtils.initLabel(equipslabel[j], "装", new GridData(), 12);
				}
			}
		}
		SwtUtils.insertBlank(downsideBase);
		{
			condlabel = new Label(downsideBase, SWT.RIGHT);
			SwtUtils.initLabel(condlabel, "100", new GridData(), 24);
		}

		this.iconLabels[i] = iconlabel;
		this.nameLabels[i] = namelabel;
		this.lvLabels[i] = lvlabel;
		this.hpLabels[i] = hplabel;
		this.hpmsgLabels[i] = hpmsglabel;
		this.equipsLabels[i] = equipslabel;
		this.condLabels[i] = condlabel;
		this.upsideBases[i] = upsideBase;
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		DeckDto deck = this.getDeck();

		switch (type) {
			case UPDATEDECKNAME:
				if (deck != null) {
					String deckName = deck.getName();
					this.updateDeckName(deckName);
				}
				return;
			case PORT:
				this.akashiTimer.reset();
				break;
			case CHANGE:
				if (deck != null && deck.isAkashiFlagship()) {
					this.akashiTimer.resetAkashiFlagship();
				}
				break;
			default:
				break;
		}

		this.composite.setRedraw(false);
		this.updateDeck();
		this.composite.setRedraw(true);
	}

	private void updateDeck() {
		DeckDto deck = this.getDeck();
		if (deck == null) return;

		//舰队名
		this.updateDeckName(deck.getName());

		//每一艘船的information
		int suodi = 0, zhikong = 0, totallv = 0;
		boolean highspeed = true;

		int[] ships = deck.getShips();
		for (int i = 0; i < ships.length; i++) {
			ShipDto ship = GlobalContext.getShipmap().get(ships[i]);
			this.updateShipInformation(i, ship);
			if (ship != null) {
				suodi += ShipTranslator.getSuodi(ship);
				zhikong += ShipTranslator.getZhikong(ship);
				totallv += ship.getLv();
				highspeed &= ShipTranslator.highspeed(ship);
			}
		}

		//舰队速度,制空,索敌,总等级
		SwtUtils.setText(this.sokuLabel, highspeed ? "高速" : "低速");
		SwtUtils.setText(this.suodiLabel, String.valueOf(suodi));
		SwtUtils.setText(this.zhikongLabel, String.valueOf(zhikong));
		SwtUtils.setText(this.totallvLabel, String.valueOf(totallv));
	}

	private void updateDeckName(String deckName) {
		if (deckName != null) {
			SwtUtils.setText(this.fleetNameLabel, deckName);
		}
	}

	private void updateShipInformation(int i, ShipDto ship) {
		String iconText = "", nameText = "", hpText = "", hpmsgText = "", lvText = "", condText = "";
		ArrayList<String> equipTooltipTexts = new ArrayList<>();
		Character[] equipTexts = new Character[MAXEQUIP];

		BiConsumer<ItemDto, Integer> addNewItem = (item, index) -> {
			int star = item.getLevel();
			int alv = item.getAlv();
			equipTexts[index] = item.getOneWordName();
			equipTooltipTexts.add(item.getName() + (alv > 0 ? (" 熟" + alv) : "") + (star > 0 ? (" ★" + star) : ""));
		};
		if (ship != null) {
			nameText = ship.getName();
			hpText = ship.getNowHP() + "/" + ship.getMaxHp();
			hpmsgText = HPMessage.get(ship.getNowHP() * 1.0 / ship.getMaxHp());
			lvText = "Lv." + ship.getLv();
			condText = "" + ship.getCond();
			//4个装备
			int[] equips = ship.getSlots();
			for (int index = 0; index < equips.length; index++) {
				ItemDto item = GlobalContext.getItemMap().get(equips[index]);
				if (item != null) addNewItem.accept(item, index);
			}
			//ex装备
			int equipex = ship.getSlotex();
			if (equipex > 0) {
				ItemDto item = GlobalContext.getItemMap().get(equipex);
				if (item != null) addNewItem.accept(item, 4);
			}
		}

		SwtUtils.setText(this.iconLabels[i], iconText);
		SwtUtils.setText(this.nameLabels[i], nameText);
		SwtUtils.setToolTipText(this.nameLabels[i], nameText);
		SwtUtils.setText(this.hpLabels[i], hpText);
		SwtUtils.setText(this.hpmsgLabels[i], hpmsgText);
		SwtUtils.setText(this.lvLabels[i], lvText);
		SwtUtils.setText(this.condLabels[i], condText);
		String tooltip = StringUtils.join(equipTooltipTexts, "\n");
		for (int index = 0; index < MAXEQUIP; index++) {
			Label label = this.equipsLabels[i][index];
			Character ch = equipTexts[index];
			SwtUtils.setText(label, ch != null ? String.valueOf(ch) : "");
			label.setToolTipText(ch != null ? tooltip : "");
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	private DeckDto getDeck() {
		return GlobalContext.getDeckRoom()[this.id - 1].getDeck();
	}

	private boolean notifiyAkashitimer() {
		return this.notifiyAkashitimer;
	}

	public FleetAkashiTimer getAkashiTimer() {
		return this.akashiTimer;
	}

	public int getId() {
		return this.id;
	}

	public Composite getComposite() {
		return this.composite;
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
			if (rest == RESET_LIMIT * ONE_MINUTE) {
				if (FleetWindow.this.notifiyAkashitimer()) {
					DeckDto deck = FleetWindow.this.getDeck();
					if (deck != null && deck.shouldNotifyAkashiTimer()) {
						box.add("泊地修理", FleetWindow.this.defaultFleetName + "-泊地修理已20分钟");
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

}
