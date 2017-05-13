package logbook.dto.translator;

import java.util.Map;
import java.util.function.Function;

import logbook.dto.word.MasterDataDto;
import logbook.dto.word.MasterDataDto.MasterMissionDataDto;
import logbook.dto.word.MasterDataDto.MasterShipDataDto;
import logbook.dto.word.MasterDataDto.MasterSlotitemDataDto;
import logbook.dto.word.MasterDataDto.MasterUserItemDto;
import logbook.update.GlobalContext;
import logbook.util.ToolUtils;

public class MasterDataDtoTranslator {

	public static String getMissionName(int id) {
		return ToolUtils.notNullThenHandle(getMasterMissionDataDto(id), MasterMissionDataDto::getName, "");
	}

	public static String getShipName(int id) {
		return ToolUtils.notNullThenHandle(getMasterShipDataDto(id), MasterShipDataDto::getName, "");
	}

	public static String getSlotitemName(int id) {
		return ToolUtils.notNullThenHandle(getMasterSlotitemDataDto(id), MasterSlotitemDataDto::getName, "");
	}

	public static String getUseitemName(int id) {
		return ToolUtils.notNullThenHandle(getMasterUserItemDto(id), MasterUserItemDto::getName, "");
	}

	public static MasterShipDataDto getMasterShipDataDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterShipDataMap);
	}

	public static MasterSlotitemDataDto getMasterSlotitemDataDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterSlotitemDataMap);
	}

	public static MasterMissionDataDto getMasterMissionDataDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterMissionDataMap);
	}

	public static MasterUserItemDto getMasterUserItemDto(int id) {
		return getMasterData(id, MasterDataDto::getMasterUserItemDtoMap);
	}

	private static <T> T getMasterData(int id, Function<MasterDataDto, Map<Integer, T>> fun) {
		return ToolUtils.notNullThenHandle(GlobalContext.getMasterData(), md -> fun.apply(md).get(id), null);
	}
}
