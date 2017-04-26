package logbook.context.dto.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.MasterDataDto.MasterShipDataDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.room.DeckRoom;
import logbook.context.update.room.NyukyoRoom;
import logbook.gui.logic.HPMessage;
import logbook.util.ToolUtils;

public class ShipDtoTranslator {

	public static double getHPPercent(ShipDto ship) {
		return ship == null ? 0 : ToolUtils.division(ship.getNowHp(), ship.getMaxHp());
	}

	public static String getTypeString(ShipDto ship) {
		return ToolUtils.notNullThenHandle(ship, s -> getTypeString(s.getShipId()), "");
	}

	public static String getTypeString(int shipId) {
		MasterShipDataDto msdd = MasterDataDtoTranslator.getMasterShipDataDto(shipId);
		if (msdd != null) {
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
			}
		}
		return ToolUtils.notNullThenHandle(msdd, m -> String.valueOf(m.getType()), "");
	}

	public static String getName(ShipDto ship) {
		return ToolUtils.notNullThenHandle(ship, s -> getName(s.getShipId()), "");
	}

	public static String getName(int shipId) {
		return MasterDataDtoTranslator.getShipName(shipId);
	}

	/** 装备个数,不含ex装备 */
	public static int getSlotCount(ShipDto ship) {
		return ship == null ? 0 : (int) Arrays.stream(ship.getSlots()).filter(slot -> slot > 0).count();
	}

	public static String getDetail(int id) {
		return getDetail(GlobalContext.getShip(id));
	}

	public static String getDetail(ShipDto ship) {
		if (ship == null) return "";

		ArrayList<String> detail = new ArrayList<>();
		{
			detail.add(getName(ship));
			detail.add("经验: " + ship.getNextExp() + "/" + ship.getCurrentExp());
			detail.add("速力: " + getSokuString(ship, true));
		}
		return StringUtils.join(detail, "\n");
	}

	public static String getSokuString(int id, boolean showHighspeed) {
		return getSokuString(GlobalContext.getShip(id), showHighspeed);
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

	public static boolean highspeed(int id) {
		return highspeed(GlobalContext.getShip(id));
	}

	public static boolean highspeed(ShipDto ship) {
		return ToolUtils.notNullThenHandle(ship, s -> s.getSoku() != 5, true);
	}

	public static int getSuodi(int id) {
		return getSuodi(GlobalContext.getShip(id));
	}

	public static int getSuodi(ShipDto ship) {
		int suodi = 0;
		if (ship != null) {
			for (int i = 0; i < 4; i++) {
				suodi += ItemDtoTranslator.getSuodi(ship.getSlots()[i]);
			}
		}
		return suodi;
	}

	public static int getZhikong(int id) {
		return getZhikong(GlobalContext.getShip(id));
	}

	public static int getZhikong(ShipDto ship) {
		int zhikong = 0;
		if (ship != null) {
			for (int i = 0; i < 4; i++) {
				zhikong += ItemDtoTranslator.getZhikong(ship.getSlots()[i], ship.getOnSlot()[i]);
			}
		}
		return zhikong;
	}

	public static boolean isAkashi(int id) {
		return isAkashi(GlobalContext.getShip(id));
	}

	public static boolean isAkashi(ShipDto ship) {
		return ToolUtils.notNullThenHandle(ship, s -> s.getShipId() == 182 || s.getShipId() == 187, false);
	}

	public static boolean isInNyukyo(int id) {
		return isInNyukyo(GlobalContext.getShip(id));
	}

	public static boolean isInNyukyo(ShipDto ship) {
		if (ship == null) return false;
		return Arrays.stream(GlobalContext.nyukyoRoom).map(NyukyoRoom::getNdock).filter(ToolUtils::isNotNull).anyMatch(ndock -> ndock.getShipId() == ship.getId());
	}

	public static boolean isInMission(int id) {
		return isInMission(GlobalContext.getShip(id));
	}

	public static boolean isInMission(ShipDto ship) {
		if (ship == null) return false;
		Predicate<DeckDto> pre = deck -> DeckDtoTranslator.isShipInDeck(deck, ship.getId()) != -1;
		return Arrays.stream(GlobalContext.deckRoom).map(DeckRoom::getDeck).filter(ToolUtils::isNotNull).filter(DeckDtoTranslator::isInMission).anyMatch(pre);
	}

	public static String getStateString(int id, boolean showMax) {
		return getStateString(GlobalContext.getShip(id), showMax);
	}

	public static String getStateString(ShipDto ship, boolean showMax) {
		if (ship == null) return "";
		String text = HPMessage.getString(getHPPercent(ship));
		return !showMax && StringUtils.equals(text, HPMessage.getString(1)) ? "" : text;
	}

	public static boolean needHokyo(int id) {
		return id == -1 ? false : needHokyo(GlobalContext.getShip(id));
	}

	public static boolean needHokyo(ShipDto ship) {
		if (ship == null) return false;
		MasterShipDataDto msdd = MasterDataDtoTranslator.getMasterShipDataDto(ship.getShipId());
		if (msdd == null) return false;
		boolean flag = msdd.getFuelMax() == ship.getFuel() && msdd.getBullMax() == ship.getBull() && Arrays.equals(msdd.getOnslotMax(), ship.getOnSlot());
		return !flag;
	}

	public static boolean perfectState(int id) {
		return perfectState(GlobalContext.getShip(id));
	}

	public static boolean perfectState(ShipDto ship) {//完好
		return ToolUtils.notNullThenHandle(ship, s -> s.getNowHp() == s.getMaxHp(), false);
	}

	public static boolean healthyState(int id) {
		return healthyState(GlobalContext.getShip(id));
	}

	public static boolean healthyState(ShipDto ship) {//擦伤小破
		return terribleState(ship) ? false : ToolUtils.notNullThenHandle(ship, s -> getHPPercent(s) < 1, false);
	}

	public static boolean terribleState(int id) {
		return terribleState(GlobalContext.getShip(id));
	}

	public static boolean terribleState(ShipDto ship) {//中破大破
		return ToolUtils.notNullThenHandle(ship, s -> getHPPercent(s) <= 0.5, false);
	}

	public static boolean dapo(int id) {
		return dapo(GlobalContext.getShip(id));
	}

	public static boolean dapo(ShipDto ship) {
		return ToolUtils.notNullThenHandle(ship, s -> getHPPercent(s) <= 0.25, false);
	}

	public static int whichDeck(int id) {
		return whichDeck(GlobalContext.getShip(id));
	}

	public static int whichDeck(ShipDto ship) {
		for (int i = 0; i < 4; i++) {
			DeckRoom dr = GlobalContext.deckRoom[i];
			if (DeckDtoTranslator.isShipInDeck(dr.getDeck(), ship.getId()) != -1) {
				return i;
			}
		}
		return -1;
	}

}
