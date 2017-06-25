package logbook.dto.translator;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import logbook.dto.word.MasterDataDto.MasterShipDto;
import logbook.dto.word.ShipDto;
import logbook.gui.logic.HPMessage;
import logbook.update.GlobalContext;
import logbook.update.room.DeckRoom;
import logbook.update.room.NyukyoRoom;
import logbook.utils.ToolUtils;

public class ShipDtoTranslator {

	public static double getHPPercent(ShipDto ship) {
		if (ship == null) return 1;
		return ToolUtils.division(ship.getNowHp(), ship.getMaxHp());
	}

	public static String getTypeString(ShipDto ship) {
		if (ship == null) return "";
		MasterShipDto msdd = ship.getMasterData();
		if (msdd == null) return "";
		int type = msdd.getType();
		switch (type) {
			case 1:
				return "海防艦";
			case 2:
				return "駆逐艦";
			case 3:
				return "軽巡洋艦";
			case 4:
				return "重雷装巡洋艦";
			case 5:
				return "重巡洋艦";
			case 6:
				return "航空巡洋艦";
			case 7:
				return "軽空母";
			case 8:
				return "巡洋戦艦";
			case 9:
				return "戦艦";
			case 10:
				return "航空戦艦";
			case 11:
				return "正規空母";
			case 12:
				return "超弩級戦艦";
			case 13:
				return "潜水艦";
			case 14:
				return "潜水空母";
			case 15:
				return "補給艦";//敌方
			case 16:
				return "水上機母艦";
			case 17:
				return "揚陸艦";
			case 18:
				return "装甲空母";
			case 19:
				return "工作艦";
			case 20:
				return "潜水母艦";
			case 21:
				return "練習巡洋艦";
			case 22:
				return "補給艦";//自方
			default:
				return String.valueOf(type);
		}
	}

	public static String getName(ShipDto ship) {
		if (ship == null) return "";
		return ToolUtils.notNull(ship.getMasterData(), MasterShipDto::getName, "");
	}

	public static String getDetail(ShipDto ship) {
		if (ship == null) return "";
		ArrayList<String> detail = new ArrayList<>();
		{
			detail.add(getName(ship));
			detail.add(String.format("经验: %d/%d", ship.getNextExp(), ship.getCurrentExp()));
			detail.add(String.format("速力: %s", getSokuString(ship, true)));
		}
		return StringUtils.join(detail, "\n");
	}

	public static String getSokuString(ShipDto ship, boolean showHighspeed) {
		if (ship == null) return "";
		int soku = ship.getSoku();
		switch (soku) {
			case 5:
				return "低速";
			case 10:
				return showHighspeed ? "高速" : "";
			case 15:
				return "高速+";
			case 20:
				return "最速";
			default:
				return Integer.toString(soku);
		}
	}

	public static boolean highspeed(ShipDto ship) {
		if (ship == null) return true;
		return ship.getSoku() != 5;
	}

	public static int getSuodi(ShipDto ship) {
		if (ship == null) return 0;
		int suodi = 0;
		for (int i = 0; i < 4; i++) {
			suodi += ItemDtoTranslator.getSuodi(ship.getSlots()[i]);
		}
		return suodi;
	}

	public static int getZhikong(ShipDto ship) {
		if (ship == null) return 0;
		int zhikong = 0;
		for (int i = 0; i < 4; i++) {
			zhikong += ItemDtoTranslator.getZhikong(ship.getSlots()[i], ship.getOnSlot()[i]);
		}
		return zhikong;
	}

	public static boolean isAkashi(ShipDto ship) {
		if (ship == null) return false;
		return ship.getShipId() == 182 || ship.getShipId() == 187;
	}

	public static boolean isInNyukyo(ShipDto ship) {
		if (ship == null) return false;
		return Arrays.stream(GlobalContext.nyukyoRoom).map(NyukyoRoom::getNdock).anyMatch(ndock -> //
		ToolUtils.isNotNull(ndock) &&//
				ndock.getShipId() == ship.getId()//
		);
	}

	public static boolean isInMission(ShipDto ship) {
		if (ship == null) return false;
		return Arrays.stream(GlobalContext.deckRoom).map(DeckRoom::getDeck).anyMatch(deck ->//
		ToolUtils.isNotNull(deck) &&//
				DeckDtoTranslator.isInMission(deck) &&//
				DeckDtoTranslator.isShipInDeck(deck, ship.getId()) //
		);
	}

	public static String getStateString(ShipDto ship, boolean showMax) {
		if (ship == null) return "";
		String text = HPMessage.getString(getHPPercent(ship));
		return ToolUtils.isFalse(showMax) && StringUtils.equals(text, HPMessage.getString(1)) ? "" : text;
	}

	public static boolean needHokyo(ShipDto ship) {
		if (ship == null) return false;
		MasterShipDto msdd = ship.getMasterData();
		if (msdd == null) return false;
		return msdd.getFuelMax() != ship.getFuel() || //
				msdd.getBullMax() != ship.getBull() || //
				ToolUtils.isFalse(Arrays.equals(msdd.getOnslotMax(), ship.getOnSlot()));
	}

	public static boolean perfectState(ShipDto ship) {//完好
		if (ship == null) return false;
		return ship.getNowHp() == ship.getMaxHp();
	}

	public static boolean healthyState(ShipDto ship) {//擦伤小破
		if (ship == null) return false;
		return terribleState(ship) ? false : getHPPercent(ship) < 1;
	}

	public static boolean terribleState(ShipDto ship) {//中破大破
		if (ship == null) return false;
		return getHPPercent(ship) <= 0.5;
	}

	public static boolean dapo(ShipDto ship) {
		if (ship == null) return false;
		return getHPPercent(ship) <= 0.25;
	}

	public static int whichDeck(ShipDto ship) {
		if (ship != null) {
			for (int i = 0; i < 4; i++) {
				if (DeckDtoTranslator.isShipInDeck(GlobalContext.deckRoom[i].getDeck(), ship.getId())) {
					return i;
				}
			}
		}
		return -1;
	}

}
