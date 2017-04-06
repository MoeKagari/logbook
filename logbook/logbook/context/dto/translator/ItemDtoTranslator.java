package logbook.context.dto.translator;

import java.util.Arrays;

import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.MasterDataDto.MasterSlotitemDataDto;
import logbook.context.update.GlobalContext;
import logbook.gui.logic.data.ItemDataMap;
import logbook.gui.logic.data.ItemDataMap.ItemData;
import logbook.util.ToolUtils;

public class ItemDtoTranslator {

	public static String getName(ItemDto item) {
		return ToolUtils.notNullThenHandle(item, it -> getName(it.getSlotitemId()), "");
	}

	public static String getName(int slotitemId) {
		return MasterDataDtoTranslator.getSlotitemName(slotitemId);
	}

	public static String getTypeString(ItemDto item) {
		return ToolUtils.notNullThenHandle(item, it -> getTypeString(it.getSlotitemId()), "");
	}

	public static String getTypeString(int slotitemId) {
		MasterSlotitemDataDto msd = MasterDataDtoTranslator.getMasterSlotitemDataDto(slotitemId);
		return ToolUtils.notNullThenHandle(msd, m -> Arrays.toString(m.getType()), "");
	}

	public static String getNameWithLevel(int id) {
		return id == -1 ? "" : getNameWithLevel(GlobalContext.getItemMap().get(id));
	}

	public static String getNameWithLevel(ItemDto item) {
		if (item == null) return "";
		int star = item.getLevel();
		int alv = item.getAlv();
		return getName(item) + (alv > 0 ? (" 熟" + alv) : "") + (star > 0 ? (" ★" + star) : "");
	}

	public static char getOneWordName(ItemDto item) {
		ItemData itemData = ItemDataMap.get(item.getSlotitemId());
		return itemData == null ? ' ' : itemData.getOneWordName();
	}

	public static int getSuodi(int id) {
		return id == -1 ? 0 : getSuodi(GlobalContext.getItemMap().get(id));
	}

	public static int getSuodi(ItemDto item) {
		int suodi = 0;

		if (item != null) {
			//TODO
		}

		return suodi;
	}

	public static int getZhikong(int id, int count) {
		return id == -1 ? 0 : getZhikong(GlobalContext.getItemMap().get(id), count);
	}

	public static int getZhikong(ItemDto item, int count) {
		int zhikong = 0;

		if (item != null) {
			MasterSlotitemDataDto msd = MasterDataDtoTranslator.getMasterSlotitemDataDto(item.getSlotitemId());
			if (msd != null) {
				int[] type = msd.getType();

				//自带对空(含改修)
				int lv = item.getLevel();
				if (type[0] == 3 && type[1] == 5 && type[2] == 6 && type[3] == 6) {//舰战
					zhikong += Math.floor((msd.getTyku() + 0.2 * lv) * Math.sqrt(count));
				}
				if (type[0] == 3 && type[1] == 5 && type[2] == 7 && type[3] == 7) {//舰爆
					if (type[4] == 12) {//爆战
						zhikong += Math.floor((msd.getTyku() + 0.25 * lv) * Math.sqrt(count));
					} else {
						zhikong += Math.floor(msd.getTyku() * Math.sqrt(count));
					}
				}
				if ((type[0] == 3 && type[1] == 5 && type[2] == 8 && type[3] == 8) ||//舰攻
						(type[0] == 5 && type[1] == 36 && type[2] == 45 && type[3] == 10) ||//水战
						(type[0] == 5 && type[1] == 7 && type[2] == 11 && type[3] == 10) ||//水爆
						(type[0] == 3 && type[1] == 40) //喷气机
				) {
					zhikong += Math.floor(msd.getTyku() * Math.sqrt(count));
				}

				//熟练度制空
				int alv = item.getAlv();
				if (alv >= 0) {
					if ((type[0] == 3 && type[1] == 5 && type[2] == 6 && type[3] == 6) ||//舰战
							(type[0] == 5 && type[1] == 36 && type[2] == 45 && type[3] == 10) //水战
					) {
						zhikong += new int[] { 0, 1, 3, 7, 11, 16, 17, 25 }[alv];
					}
					if (type[0] == 5 && type[1] == 7 && type[2] == 11 && type[3] == 10) {//水爆
						zhikong += new int[] { 0, 1, 2, 3, 3, 5, 6, 9 }[alv];
					}
					if ((type[0] == 3 && type[1] == 5 && type[2] == 7 && type[3] == 7) ||//舰爆
							(type[0] == 3 && type[1] == 5 && type[2] == 8 && type[3] == 8) ||//舰攻
							(type[0] == 3 && type[1] == 40) //喷气机
					) {
						if (alv == 7) {//alv<7时,不知
							zhikong += 3;
						}
					}
				}
			}
		}

		return zhikong;
	}

}
