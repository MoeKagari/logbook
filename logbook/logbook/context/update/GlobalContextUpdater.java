package logbook.context.update;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import logbook.context.update.data.ActionData;
import logbook.context.update.data.Data;
import logbook.context.update.data.DataType;
import logbook.context.update.data.EventListener;
import logbook.context.update.data.UndefinedData;
import logbook.internal.LoggerHolder;

/**
 * 对原始数据进行取舍,处理,然后发给{@link GlobalContext}<br>
 * 和各个注册的{@link EventListener} 以及{@link GlobalListener}
 * @author MoeKagari
 */
public class GlobalContextUpdater {
	/** 游戏现有的服务器 */
	public final static List<String> SERVER_LIST = Arrays.asList(new String[] { //
			"125.6.184.16", "125.6.187.205", "125.6.187.229", "125.6.187.253", //
			"125.6.188.25", "125.6.189.7", "125.6.189.39", "125.6.189.71", //
			"125.6.189.103", "125.6.189.135", "125.6.189.167", "125.6.189.215", //
			"125.6.189.247", "203.104.209.71", "203.104.209.87", "203.104.248.135", //
			"203.104.209.23", "203.104.209.39", "203.104.209.55", "203.104.209.102" //
	});

	private final static SimpleDateFormat format = new SimpleDateFormat("dd_HHmmss.SSS");
	private final static LoggerHolder LOG = new LoggerHolder(GlobalContextUpdater.class);
	private static List<EventListener> eventListeners = new ArrayList<>();

	public static void update(UndefinedData undefinedData, String serverName, String contentType) {
		String url = undefinedData.getUrl();

		if (SERVER_LIST.contains(serverName) == false) {
			//	System.out.println("非游戏服务器: " + undefinedData.getFullUrl());
			return;
		}

		if ("text/plain".equals(contentType) == false) {
			//	System.out.println("非游戏数据类型,可能为游戏资源文件: " + url);
			return;
		}

		Data data = undefinedData.toDefinedData();
		DataType type = data.getDataType();
		if (type == null) {
			saveJson(data, true, url);
			System.out.println("X定义的api: " + url);
			return;
		}

		System.out.println("O定义的api: " + url);
		saveJson(data, false, url);

		GlobalContext.updateContext(type, data, serverName);
		eventListeners.forEach(listener -> listener.update(type));
		GlobalListener.update(type);
	}

	public static void addEventListener(EventListener listener) {
		if (eventListeners.indexOf(listener) == -1) {
			eventListeners.add(listener);
		}
	}

	public static void removeEventListener(EventListener listener) {
		eventListeners.remove(listener);
	}

	/**
	 * 保存已定义的api数据到  json\\<br>
	 * 保存未定义的api数据到  json\\undefined\\
	 * @param data api数据
	 * @param undefined 是否未定义,于{@link DataType}
	 * @param url api的url,(/kcsapi/...)形式
	 */
	private static void saveJson(Data data, boolean undefined, String url) {
		if (shouldnotSave(data.getDataType())) return;

		try {
			Date date = data.getCreateDate();
			DataType type = data.getDataType();
			String typeString = url.substring(url.lastIndexOf('/') + 1);
			if (type != null) typeString = type.toString();
			JsonObject json = null;
			Map<String, String> fileds = null;

			String fdir = "json" + (undefined ? "\\undefined" : "");
			String fname = new StringBuilder().append(format.format(date)).append("_").append(typeString.toUpperCase()).append(".json").toString();
			File file = new File(FilenameUtils.concat(fdir, fname));

			if (data instanceof UndefinedData) {
				json = UndefinedData.responseToJsonObiect(((UndefinedData) data).getResponse());
				fileds = UndefinedData.getQueryMap(URLDecoder.decode(new String(((UndefinedData) data).getRequest()).trim(), "UTF-8"));
			} else if (data instanceof ActionData) {
				json = data.getJsonObject();
				fileds = ((ActionData) data).getFields();
			}

			StringBuilder content = new StringBuilder(url).append("\r\n");
			if (fileds != null) {
				ArrayList<String> filedsString = new ArrayList<>();
				fileds.forEach((key, value) -> {
					if ("api_token".equals(key) == false && "api_verno".equals(key) == false) {
						filedsString.add(key + ":" + value);
					}
				});
				content.append("{").append(StringUtils.join(filedsString, ',')).append("}");
			}
			content.append("\r\n");
			if (json != null) {
				content.append(json.toString());
			}

			FileUtils.write(file, content.toString(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			LOG.get().warn("JSON对象保存失败", e);
			LOG.get().warn(data);
		}
	}

	/**
	 * 开发阶段,已写完对应的处理方法({@link GlobalContext}中)时不必记录其apidata
	 */
	@SuppressWarnings("incomplete-switch")
	private static boolean shouldnotSave(DataType type) {
		if (type == null) return false;
		switch (type) {
			case EVENTMAP_RANK_SELECT:
			case UNSETSLOT:
			case QUEST_START:
			case QUEST_STOP:
			case QUEST_CLEAR:
			case QUEST_LIST:
			case POWERUP:
			case FURNITURE_CHANGE:
			case GET_INCENTIVE:
			case USEITEM:
			case SLOT_ITEM:
			case DESTROYSHIP:
			case KDOCK:
			case PORT:
			case REQUIRE_INFO:
			case BASIC:
			case NDOCK:
			case UPDATEDECKNAME:
			case MATERIAL:
			case PRESET_DECK:
			case PRESET_REGISTER:
			case DECK:
			case CHANGE:
			case PRESET_SELECT:
			case CREATESHIP:
			case CREATESHIP_SPEEDCHANGE:
			case CREATESHIP_GETSHIP:
			case CREATEITEM:
			case MISSION:
			case MISSIONRESULT:
			case MISSIONSTART:
			case CHARGE:
			case NYUKYO_SPEEDCHANGE:
			case NYUKYO_START:
			case PAYITEM:
			case DESTROYITEM:
			case REMODEL_SLOT:
			case REMODEL_SLOTLIST:
			case REMODEL_SLOTLIST_DETAIL:
			case RECORD:
			case PICTURE_BOOK:
			case PRESET_DELETE:
			case ITEMUSE:
			case MARRIAGE:
			case MISSIONRETURN:
			case MUSIC_LIST:
			case MXLTVKPYUKLH:
			case PAYITEMUSE:
			case PRACTICE_LIST:
			case PRACTICE_ENEMYINFO:
			case UPDATECOMMENT:
			case MUSIC_PLAY:
			case SET_PORTBGM:
			case ITEMUSE_COND:
			case BUY_FURNITURE:
			case PAYCHECK:
			case RADIO_PLAY:
			case MASTERDATA:
			case SHIP_LOCK:
			case SLOT_ITEM_LOCK:
			case OPEN_EXSLOT:
			case REMODELING:
			case SHIP3:
			case SLOTSET:
			case SLOTSET_EX:
			case SLOT_DEPRIVE:
			case SLOT_EXCHANGE:
			case UNSETSLOT_ALL:
				return true;
		}
		return false;
	}

}
