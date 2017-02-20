package logbook.context.dto.translator;

import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.MasterDataDto;
import logbook.context.dto.data.MasterDataDto.MasterSlotitemDataDto;
import logbook.context.update.GlobalContext;
import logbook.gui.logic.data.ItemDataMap;
import logbook.gui.logic.data.ItemDataMap.ItemData;

public class ItemDtoTranslator {

	public static char getOneWordName(ItemDto item) {
		ItemData itemData = ItemDataMap.get(item.getSlotitemId());
		return itemData == null ? ' ' : itemData.getOneWordName();
	}

	public static int getSuodi(ItemDto item) {
		return 0;
	}

	public static int getZhikong(ItemDto item) {
		return 0;
	}

	public static String getName(ItemDto item) {
		return getName(item.getSlotitemId());
	}

	public static String getName(int slotitemId) {
		MasterDataDto mdd = GlobalContext.getMasterData();
		MasterSlotitemDataDto msd = mdd != null ? mdd.getMasterSlotitemDataMap().get(slotitemId) : null;
		return msd != null ? msd.getName() : "";
	}

}
