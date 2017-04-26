package logbook.context.dto.translator;

import java.util.Map;
import java.util.function.Function;

import logbook.context.dto.data.MasterDataDto;
import logbook.context.dto.data.MasterDataDto.MasterMissionDataDto;
import logbook.context.dto.data.MasterDataDto.MasterShipDataDto;
import logbook.context.dto.data.MasterDataDto.MasterSlotitemDataDto;
import logbook.context.dto.data.MasterDataDto.MasterUserItemDto;
import logbook.context.update.GlobalContext;
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
