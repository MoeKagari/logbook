package logbook.context.data;

import java.util.HashMap;
import java.util.Map;

public enum DataType {
//start battle相关
	/** 基地航空队-信息*/
	AIRBASE_INFORMATION("/kcsapi/api_get_member/base_air_corps"),
	/** 基地航空队-设置飞机 */
	AIRBASE_SETPLANE("/kcsapi/api_req_air_corps/set_plane"),
	/** 基地航空队-变更队名 */
	AIRBASE_CHANGENAME("/kcsapi/api_req_air_corps/change_name"),
	/** 基地航空队-扩张 */
	AIRBASE_EXPAND("/kcsapi/api_req_air_corps/expand_base"),
	/** 陆航补给 */
	SUPPLY("/kcsapi/api_req_air_corps/supply"),
	/** 活动海域的出击条件(点击决定按钮与选择出击舰队两者之间)(两次返回母港之间只会发起一次) */
	SORTIE_CONDITIONS("/kcsapi/api_get_member/sortie_conditions"),
	/** 联合舰队结成 */
	COMBINED("/kcsapi/api_req_hensei/combined"),
	/** 退避 */
	GOBACK_PORT("/kcsapi/api_req_combined_battle/goback_port"),
	/** 设置陆航去处 */
	BATTLE_START_AIR_BASE("/kcsapi/api_req_map/start_air_base"),

	/** 演习昼战 */
	BATTLE_PRACTICE_DAY("/kcsapi/api_req_practice/battle"),
	/** 演习夜战 */
	BATTLE_PRACTICE_MIDNIGHT("/kcsapi/api_req_practice/midnight_battle"),
	/** 演习结果 */
	BATTLE_PRACTICE_RESULT("/kcsapi/api_req_practice/battle_result"),

	/** 舰队出击 */
	BATTLE_START("/kcsapi/api_req_map/start"),
	/** 战斗结束后的舰队刷新 */
	BATTLE_SHIPDECK("/kcsapi/api_get_member/ship_deck"),
	/** 战斗结束后的下一点 */
	BATTLE_NEXT("/kcsapi/api_req_map/next"),

	/** 空袭战 */
	BATTLE_AIRBATTLE("/kcsapi/api_req_sortie/airbattle"),
	/** 长距离空袭战 */
	BATTLE_AIRBATTLE_LD("/kcsapi/api_req_sortie/ld_airbattle"),
	/** 昼战 */
	BATTLE_DAY("/kcsapi/api_req_sortie/battle"),
	/** 夜战 */
	BATTLE_MIDNIGHT("/kcsapi/api_req_battle_midnight/battle"),
	/** 开幕夜战 */
	BATTLE_MIDNIGHT_SP("/kcsapi/api_req_battle_midnight/sp_midnight"),
	/** 战斗结果 */
	BATTLE_RESULT("/kcsapi/api_req_sortie/battleresult"),

	/** 空袭战-联合舰队 */
	COMBINEBATTLE_AIRBATTLE("/kcsapi/api_req_combined_battle/airbattle"),
	/** 长距离空袭战-联合舰队 */
	COMBINEBATTLE_AIRBATTLE_LD("/kcsapi/api_req_combined_battle/ld_airbattle"),
	/** 昼战-12vs6-机动 */
	COMBINEBATTLE_DAY("/kcsapi/api_req_combined_battle/battle"),
	/** 昼战-12vs6-水打 */
	COMBINEBATTLE_DAY_WATER("/kcsapi/api_req_combined_battle/battle_water"),
	/** 夜战-12vs6 */
	COMBINEBATTLE_MIDNIGHT("/kcsapi/api_req_combined_battle/midnight_battle"),
	/** 昼战-6vs12 */
	COMBINEBATTLE_EC_DAY("/kcsapi/api_req_combined_battle/ec_battle"),
	/** 昼战-12vs12-机动 */
	COMBINEBATTLE_EACH_DAY("/kcsapi/api_req_combined_battle/each_battle"),
	/** 昼战-12vs12-水打 */
	COMBINEBATTLE_EACH_DAY_WATER("/kcsapi/api_req_combined_battle/each_battle_water"),
	/** 夜战-6vs12-12vs12 */
	COMBINEBATTLE_EC_MIDNIGHT("/kcsapi/api_req_combined_battle/ec_midnight_battle"),
	/** 开幕夜战-联合舰队 */
	COMBINEBATTLE_MIDNIGHT_SP("/kcsapi/api_req_combined_battle/sp_midnight"),
	/** 战斗结果-联合舰队 */
	COMBINEBATTLE_RESULT("/kcsapi/api_req_combined_battle/battleresult"),
//end

	/** 变更签名 */
	UPDATECOMMENT("/kcsapi/api_req_member/updatecomment"),
	/** 战果排行list */
	MXLTVKPYUKLH("/kcsapi/api_req_ranking/mxltvkpyuklh"),
	/** 使用氪金道具(取出到普通道具栏???) */
	PAYITEMUSE("/kcsapi/api_req_member/payitemuse"),
	/** 氪金道具(未取出) */
	PAYITEM("/kcsapi/api_get_member/payitem"),
	/** 战绩 */
	RECORD("/kcsapi/api_get_member/record"),
	/** 图鉴 */
	PICTURE_BOOK("/kcsapi/api_get_member/picture_book"),
	/** 道具奖励(启动时) */
	GET_INCENTIVE("/kcsapi/api_req_member/get_incentive"),
	/** 点唱机list */
	MUSIC_LIST("/kcsapi/api_req_furniture/music_list"),
	/** 点唱机play */
	MUSIC_PLAY("/kcsapi/api_req_furniture/music_play"),
	/** 母港bgm设定 */
	SET_PORTBGM("/kcsapi/api_req_furniture/set_portbgm"),
	/** 給粮舰道具使用 */
	ITEMUSE_COND("/kcsapi/api_req_member/itemuse_cond"),
	/** 购买家具 */
	BUY_FURNITURE("/kcsapi/api_req_furniture/buy"),
	/** 购买道具是否成功 */
	PAYCHECK("/kcsapi/api_dmm_payment/paycheck"),
	/** 母港bgm的replay? */
	RADIO_PLAY("/kcsapi/api_req_furniture/radio_play"),
	/** 更换家具 */
	FURNITURE_CHANGE("/kcsapi/api_req_furniture/change"),
	/** 选择活动难度 */
	EVENTMAP_RANK_SELECT("api_req_map/select_eventmap_rank"),

	/*-----------------------------*/

	/** 编成列表 */
	PRESET_DECK("/kcsapi/api_get_member/preset_deck"),
	/** 记录编成到编成列表 */
	PRESET_REGISTER("/kcsapi/api_req_hensei/preset_register"),
	/** 删除编成记录 */
	PRESET_DELETE("/kcsapi/api_req_hensei/preset_delete"),
	/** 展开编成 */
	PRESET_SELECT("/kcsapi/api_req_hensei/preset_select"),

	/** 进入改修工厂 */
	REMODEL_SLOTLIST("/kcsapi/api_req_kousyou/remodel_slotlist"),
	/** 选择改修装备之后到执行界面 */
	REMODEL_SLOTLIST_DETAIL("/kcsapi/api_req_kousyou/remodel_slotlist_detail"),
	/** 改修执行 */
	REMODEL_SLOT("/kcsapi/api_req_kousyou/remodel_slot"),

	/** 近代化改修 */
	POWERUP("/kcsapi/api_req_kaisou/powerup"),
	/** 更换装备 */
	SLOTSET("/kcsapi/api_req_kaisou/slotset"),
	/** 装备全解除 */
	UNSETSLOT_ALL("/kcsapi/api_req_kaisou/unsetslot_all"),
	/** 开启ex装备槽 */
	OPEN_EXSLOT("/kcsapi/api_req_kaisou/open_exslot"),
	/** 装备ex装备 */
	SLOTSET_EX("/kcsapi/api_req_kaisou/slotset_ex"),
	/** 交换装备 */
	SLOT_EXCHANGE("/kcsapi/api_req_kaisou/slot_exchange_index"),
	/** 从另一位舰娘身上交换装备 */
	SLOT_DEPRIVE("/kcsapi/api_req_kaisou/slot_deprive"),
	/** 舰娘改造 */
	REMODELING("/kcsapi/api_req_kaisou/remodeling"),
	/** lock 装备 */
	SLOT_ITEM_LOCK("/kcsapi/api_req_kaisou/lock"),
	/** 结婚 */
	MARRIAGE("/kcsapi/api_req_kaisou/marriage"),
	/** 执行一些kaisou_api之后刷新decks和一些ship */
	SHIP3("/kcsapi/api_get_member/ship3"),
	/** MARRIAGE和ITEMUSE_COND之后刷新ship和deck */
	SHIP2("/kcsapi/api_get_member/ship2"),

	/** 任务列表 */
	QUEST_LIST("/kcsapi/api_get_member/questlist"),
	/** 完成任务 */
	QUEST_CLEAR("/kcsapi/api_req_quest/clearitemget"),
	/** 开始任务 */
	QUEST_START("/kcsapi/api_req_quest/start"),
	/** 完成任务 */
	QUEST_STOP("/kcsapi/api_req_quest/stop"),

	/** 游戏的masterdata */
	MASTERDATA("/kcsapi/api_start2"),
	/** 演习列表 */
	PRACTICE_LIST("/kcsapi/api_get_member/practice"),
	/** 演习对象信息 */
	PRACTICE_ENEMYINFO("/kcsapi/api_req_member/get_practice_enemyinfo"),
	/** 更换演习群 */
	PRACTICE_CHANGE_MATCHING_KIND("/kcsapi/api_req_practice/change_matching_kind"),
	/** 资源 */
	MATERIAL("/kcsapi/api_get_member/material"),
	/** 返回母港 */
	PORT("/kcsapi/api_port/port"),
	/** 司令部数据集 */
	BASIC("/kcsapi/api_get_member/basic"),
	/** 当前的舰队编成 */
	DECK("/kcsapi/api_get_member/deck"),
	/** 当前的入渠列表 */
	NDOCK("/kcsapi/api_get_member/ndock"),
	/** 当前的建造列表 */
	KDOCK("/kcsapi/api_get_member/kdock"),
	/** 所有装备 */
	SLOT_ITEM("/kcsapi/api_get_member/slot_item"),

	/** 远征列表 */
	MISSION("/kcsapi/api_get_member/mission"),
	/** 远征开始 */
	MISSIONSTART("/kcsapi/api_req_mission/start"),
	/** 远征结果 */
	MISSIONRESULT("/kcsapi/api_req_mission/result"),
	/** 远征中止归还 */
	MISSIONRETURN("/kcsapi/api_req_mission/return_instruction"),

	/** lock 舰娘 */
	SHIP_LOCK("/kcsapi/api_req_hensei/lock"),
	/** 改变编成 */
	CHANGE("/kcsapi/api_req_hensei/change"),
	/** 变更编成的名字 */
	UPDATEDECKNAME("/kcsapi/api_req_member/updatedeckname"),

	/** 入渠开始 */
	NYUKYO_START("/kcsapi/api_req_nyukyo/start"),
	/** 使用高速修复材 */
	NYUKYO_SPEEDCHANGE("/kcsapi/api_req_nyukyo/speedchange"),

	/** 所有道具 */
	USEITEM("/kcsapi/api_get_member/useitem"),
	/** 使用道具 */
	ITEMUSE("/kcsapi/api_req_member/itemuse"),
	/** 解体 */
	DESTROYSHIP("/kcsapi/api_req_kousyou/destroyship"),
	/** 废弃 */
	DESTROYITEM("/kcsapi/api_req_kousyou/destroyitem2"),

	/** 补给 */
	CHARGE("/kcsapi/api_req_hokyu/charge"),

	/** 建造 */
	CREATESHIP("/kcsapi/api_req_kousyou/createship"),
	/** 使用高速建造材 */
	CREATESHIP_SPEEDCHANGE("/kcsapi/api_req_kousyou/createship_speedchange"),
	/** 建造获得舰娘 */
	CREATESHIP_GETSHIP("/kcsapi/api_req_kousyou/getship"),

	/** 开发装备 */
	CREATEITEM("/kcsapi/api_req_kousyou/createitem"),

	/** 地图与路基详情 */
	MAPINFO("/kcsapi/api_get_member/mapinfo"),
	/** 启动时的一堆数据 */
	REQUIRE_INFO("/kcsapi/api_get_member/require_info"),
	/** ?????? */
	UNSETSLOT("/kcsapi/api_get_member/unsetslot");

	/*--------------------------------------------------------------------------------------------------------------------------*/

	public static final Map<String, DataType> TYPEMAP = new HashMap<>();

	static {
		for (DataType type : DataType.values()) {
			TYPEMAP.put(type.getUrl(), type);
		}
	}

	/*--------------------------------------------------------------------------------------------------------------------------*/

	private final String url;

	private DataType(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

}
