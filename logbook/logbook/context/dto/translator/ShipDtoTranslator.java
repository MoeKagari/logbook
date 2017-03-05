package logbook.context.dto.translator;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import logbook.context.dto.data.MasterDataDto.MasterShipDataDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.util.ToolUtils;

public class ShipDtoTranslator {

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
		return Arrays.stream(ArrayUtils.addAll(ship.getSlots(), ship.getSlotex())).mapToObj(itemId -> GlobalContext.getItemMap().get(itemId)).filter(item -> item != null).mapToInt(ItemDtoTranslator::getSuodi).sum();
	}

	public static int getZhikong(ShipDto ship) {
		return Arrays.stream(ArrayUtils.addAll(ship.getSlots(), ship.getSlotex())).mapToObj(itemId -> GlobalContext.getItemMap().get(itemId)).filter(item -> item != null).mapToInt(ItemDtoTranslator::getZhikong).sum();
	}

	public static String getName(ShipDto ship) {
		return getName(ship.getShipId());
	}

	public static String getName(int shipId) {
		MasterShipDataDto msdd = ToolUtils.notNullThenHandle(GlobalContext.getMasterData(), mdd -> mdd.getMasterShipDataMap().get(shipId), null);
		return ToolUtils.notNullThenHandle(msdd, MasterShipDataDto::getName, "");
	}

}
