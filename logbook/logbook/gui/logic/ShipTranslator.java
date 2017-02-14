package logbook.gui.logic;

import logbook.context.dto.data.ShipDto;

public class ShipTranslator {

	/** 装备个数,不含ex装备 */
	public static int getSlotCount(ShipDto ship) {
		int count = 0;
		for (int slot : ship.getSlots()) {
			if (slot > 0) {
				count++;
			}
		}
		return count;
	}

	public static String getSokuString(ShipDto ship) {
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

	public static boolean highspeed(ShipDto ship) {
		return ship.getSoku() != 5;
	}

	public static int getSuodi(ShipDto ship) {
		return 0;
	}

	public static int getZhikong(ShipDto ship) {
		return 0;
	}

}
