package logbook.context.dto.translator;

import logbook.context.dto.data.MasterDataDto.MasterMissionDataDto;
import logbook.context.update.GlobalContext;
import logbook.util.ToolUtils;

public class MasterDataDtoTranslator {

	public static String getMissionName(int id) {
		MasterMissionDataDto mmdd = ToolUtils.notNullThenHandle(GlobalContext.getMasterData(), md -> md.getMasterMissionDataMap().get(id), null);
		return ToolUtils.notNullThenHandle(mmdd, MasterMissionDataDto::getName, "");
	}

}
