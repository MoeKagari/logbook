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
import logbook.dto.translator.DeckDtoTranslator;
import logbook.dto.translator.ItemDtoTranslator;
import logbook.dto.translator.ShipDtoTranslator;
import logbook.dto.word.DeckDto;
import logbook.dto.word.ItemDto;
import logbook.dto.word.ShipDto;
import logbook.gui.logic.HPMessage;
import logbook.update.GlobalContext;
import logbook.update.GlobalContextUpdater;
import logbook.update.data.DataType;
import logbook.update.data.EventListener;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class FleetWindow implements EventListener {
	private static final int MAXCHARA = 6;
	private static final int MAXEQUIP = 5;

	/** 舰队名 */
	private Label fleetNameLabel;
	/** 舰队速度 */
	private Label sokuLabel;
	/** 制空 */
	private Label zhikongLabel;
	/** 索敌 */
	private Label suodiLabel;
	/** 总等级 */
	private Label totallvLabel;

	private Composite fleetComposite;
	private final ShipComposite[] shipComposites = new ShipComposite[MAXCHARA];

	private final int id;//1,2,3,4
	private final String defaultFleetName;
	private final Composite composite;

	public FleetWindow(Composite composite, int id) {
		this.id = id;
		this.defaultFleetName = AppConstants.DEFAULT_FLEET_NAME[id - 1];
		this.composite = composite;
		this.init();
		GlobalContextUpdater.addEventListener(this);
	}

	public void init() {
		this.initComposite();
		this.initFleetNameLabel();
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

	private void initFleetNameLabel() {
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
		GridLayout infoComposite2GridLayout = SwtUtils.makeGridLayout(2, 0, 0, 0, 0);
		infoComposite2GridLayout.marginTop = -3;
		infoComposite2GridLayout.marginBottom = -2;
		infoComposite2.setLayout(infoComposite2GridLayout);
		infoComposite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			this.sokuLabel = new Label(infoComposite2, SWT.NONE);
			SwtUtils.initLabel(this.sokuLabel, "高速", new GridData());

			SwtUtils.insertBlank(infoComposite2);
		}
	}

	private void initFleetComposite() {
		this.fleetComposite = new Composite(this.composite, SWT.NONE);
		this.fleetComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.fleetComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (int i = 0; i < MAXCHARA; i++) {
			this.shipComposites[i] = new ShipComposite(this.fleetComposite);
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		switch (type) {
			default:
				return;
			case UPDATEDECKNAME:
				ToolUtils.notNullThenHandle(this.getDeck(), d -> SwtUtils.setText(this.fleetNameLabel, d.getName()));
				return;

			case PORT:
			case CHANGE:
			case PRESET_SELECT:
			case POWERUP:
			case SLOTSET:
			case UNSETSLOT_ALL:
			case OPEN_EXSLOT:
			case SLOTSET_EX:
			case SLOT_EXCHANGE:
			case SLOT_DEPRIVE:
			case REMODELING:
			case MARRIAGE:
			case SHIP3:
			case SHIP2:
			case BASIC:
			case DECK:
			case NDOCK:
			case SLOT_ITEM:
			case NYUKYO_START:
			case NYUKYO_SPEEDCHANGE:
			case DESTROYSHIP:
			case CHARGE:
			case REQUIRE_INFO:
			case BATTLE_SHIPDECK:
		}

		this.composite.setRedraw(false);
		ToolUtils.notNullThenHandle(this.getDeck(), this::updateDeck);
		this.composite.setRedraw(true);
	}

	private void updateDeck(DeckDto deck) {
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
			ShipComposite sc = this.shipComposites[i];
			ShipDto ship = GlobalContext.getShip(ships[i]);
			if (ship == null) {
				sc.clear();
			} else {
				sc.updateShipInformation(ship);
			}
		}
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	private DeckDto getDeck() {
		return GlobalContext.deckRoom[this.id - 1].getDeck();
	}

	public int getId() {
		return this.id;
	}

	/*------------------------------------------------------------------------------------------------------------------------------------*/

	private class ShipComposite extends Composite {
		Label iconlabel, namelabel, lvlabel, hplabel, hpmsglabel, condlabel;
		Label[] equipslabel;
		Composite upsideBase, downsideBase, equipBase;

		public ShipComposite(Composite fleetComposite) {
			super(fleetComposite, SWT.NONE);
			this.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.iconlabel = new Label(this, SWT.CENTER);
			SwtUtils.initLabel(this.iconlabel, "!", new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2), 16);

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

		private void updateShipInformation(ShipDto ship) {
			SwtUtils.setText(this.iconlabel, (ShipDtoTranslator.terribleState(ship) || ShipDtoTranslator.needHokyo(ship)) ? "!" : "");
			this.iconlabel.setBackground(ShipDtoTranslator.dapo(ship) ? HPMessage.getColor(HPMessage.getString(0.1)) : null);

			SwtUtils.setText(this.namelabel, ShipDtoTranslator.getName(ship));
			SwtUtils.setToolTipText(this.namelabel, ShipDtoTranslator.getDetail(ship));
			SwtUtils.setText(this.hplabel, ship.getNowHp() + "/" + ship.getMaxHp());
			SwtUtils.setText(this.hpmsglabel, ShipDtoTranslator.getStateString(ship, true));
			SwtUtils.setText(this.lvlabel, "Lv." + ship.getLevel());
			SwtUtils.setText(this.condlabel, String.valueOf(ship.getCond()));

			//五个装备
			Character[] equipTexts = new Character[MAXEQUIP];
			ArrayList<String> equipTooltipTexts = new ArrayList<>();
			{
				int[] slots = ArrayUtils.addAll(Arrays.copyOfRange(ship.getSlots(), 0, 4), ship.getSlotex());
				for (int index = 0; index < MAXEQUIP; index++) {
					ItemDto item = GlobalContext.getItem(slots[index]);
					if (item != null) {
						equipTexts[index] = Character.valueOf(ItemDtoTranslator.getOneWordName(item));
						equipTooltipTexts.add(ItemDtoTranslator.getNameWithLevel(item));
					}
				}
			}
			String tooltip = StringUtils.join(equipTooltipTexts, "\n");
			for (int index = 0; index < MAXEQUIP; index++) {
				Label label = this.equipslabel[index];
				Character ch = equipTexts[index];
				SwtUtils.setText(label, ch != null ? ch.toString() : "");
				label.setToolTipText(ch != null ? tooltip : "");
			}
		}

		private void clear() {
			SwtUtils.setText(this.iconlabel, "");
			this.iconlabel.setBackground(null);

			SwtUtils.setText(this.namelabel, "");
			SwtUtils.setToolTipText(this.namelabel, "");
			SwtUtils.setText(this.hplabel, "");
			SwtUtils.setText(this.hpmsglabel, "");
			SwtUtils.setText(this.lvlabel, "");
			SwtUtils.setText(this.condlabel, "");

			ToolUtils.forEach(this.equipslabel, label -> SwtUtils.setText(label, ""));
			ToolUtils.forEach(this.equipslabel, label -> SwtUtils.setToolTipText(label, ""));
		}
	}
}
