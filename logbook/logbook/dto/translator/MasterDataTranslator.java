package logbook.dto.translator;

import java.util.Map;
import java.util.function.Function;

import logbook.dto.word.MasterDataDto;
import logbook.dto.word.MasterDataDto.MasterMissionDto;
import logbook.dto.word.MasterDataDto.MasterShipDto;
import logbook.dto.word.MasterDataDto.MasterSlotitemDto;
import logbook.dto.word.MasterDataDto.MasterUserItemDto;
import logbook.update.GlobalContext;
import logbook.utils.ToolUtils;

public class MasterDataTranslator {
	public static String getMissionName(int id) {
		return ToolUtils.notNull(getMasterMissionDto(id), MasterMissionDto::getName, "");
	}

	public static String getShipName(int id) {
		return ToolUtils.notNull(getMasterShipDto(id), MasterShipDto::getName, "");
	}

	public static String getSlotitemName(int id) {
		return ToolUtils.notNull(getMasterSlotitemDto(id), MasterSlotitemDto::getName, "");
	}

	public static String getUseitemName(int id) {
		return ToolUtils.notNull(getMasterUserItemDto(id), MasterUserItemDto::getName, "");
	}

	public static MasterShipDto getMasterShipDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterShipDataMap);
	}

	public static MasterSlotitemDto getMasterSlotitemDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterSlotitemDataMap);
	}

	public static MasterMissionDto getMasterMissionDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterMissionDataMap);
	}

	public static MasterUserItemDto getMasterUserItemDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterUserItemDtoMap);
	}

	private static <T> T getMasterData(int id, Function<MasterDataDto, Map<Integer, T>> fun) {
		return ToolUtils.notNull(GlobalContext.getMasterData(), md -> fun.apply(md).get(id), null);
	}
}
