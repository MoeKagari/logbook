package logbook.context.dto.translator;

import java.util.Arrays;

import logbook.context.dto.data.MasterDataDto.MasterShipDataDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.util.ToolUtils;

public class ShipDtoTranslator {

	public static String getType(ShipDto ship) {
		return ship == null ? "" : getType(ship.getShipId());
	}

	public static String getType(int shipId) {
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
		return ship == null ? "" : getName(ship.getShipId());
	}

	public static String getName(int shipId) {
		return MasterDataDtoTranslator.getShipName(shipId);
	}

	/** 装备个数,不含ex装备 */
	public static int getSlotCount(ShipDto ship) {
		return (int) Arrays.stream(ship.getSlots()).filter(slot -> slot > 0).count();
	}

	public static String getSokuString(int id) {
		return id == -1 ? "" : getSokuString(GlobalContext.getShipMap().get(id));
	}

	public static String getSokuString(ShipDto ship) {
		if (ship == null) return "??";
		int soku = ship.getSoku();
		switch (soku) {
			case 5:
				return "低速";
			case 10:
				return "高速";
			case 15:
				return "高速+";
			case 20:
				return "最速";
			default:
				return Integer.toString(soku);
		}
	}

	public static boolean highspeed(int id) {
		return id == -1 ? true : highspeed(GlobalContext.getShipMap().get(id));
	}

	public static boolean highspeed(ShipDto ship) {
		return ship.getSoku() != 5;
	}

	public static int getSuodi(int id) {
		return id == -1 ? 0 : getSuodi(GlobalContext.getShipMap().get(id));
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
		return id == -1 ? 0 : getZhikong(GlobalContext.getShipMap().get(id));
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

	public static boolean dapo(int id) {
		return id == -1 ? false : dapo(GlobalContext.getShipMap().get(id));
	}

	public static boolean dapo(ShipDto ship) {
		return ship == null ? false : ((ship.getNowHp() * 1.0 / ship.getMaxHp()) <= 0.25);
	}

	public static boolean isAkashi(int id) {
		return id == -1 ? false : isAkashi(GlobalContext.getShipMap().get(id));
	}

	public static boolean isAkashi(ShipDto ship) {
		return ship == null ? false : (ship.getShipId() == 182 || ship.getShipId() == 187);
	}

	public static boolean canAkashiRepair(int id) {
		return id == -1 ? false : canAkashiRepair(GlobalContext.getShipMap().get(id));
	}

	public static boolean canAkashiRepair(ShipDto ship) {
		return ship == null ? false : ((ship.getNowHp() * 1.0 / ship.getMaxHp()) > 0.5);
	}

	public static int needNotifyPL(int id) {
		return id == -1 ? -1 : needNotifyPL(GlobalContext.getShipMap().get(id));
	}

	public static int needNotifyPL(ShipDto ship) {
		return ship == null ? -1 : (40 - ship.getCond());
	}

}
