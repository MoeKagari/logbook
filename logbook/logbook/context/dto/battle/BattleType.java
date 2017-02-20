package logbook.context.dto.battle;

public enum BattleType {
	PRACTICE_DAY,
	PRACTICE_MIDNIGHT,
	PRACTICE_RESULT,

	INFOBATTLE_GOBACKPORT,
	INFOBATTLE_START_AIR_BASE,

	INFOBATTLE_START,
	INFOBATTLE_NEXT,

	BATTLE_AIRBATTLE,
	BATTLE_AIRBATTLE_LD,
	BATTLE_DAY,
	BATTLE_MIDNIGHT,
	BATTLE_MIDNIGHT_SP,
	INFOBATTLE_RESULT,

	/** 长距离空袭战 */
	COMBINEBATTLE_AIRBATTLE,
	/** 长距离空袭战-联合舰队 */
	COMBINEBATTLE_AIRBATTLE_LD,
	/** 昼战-12vs6-机动 */
	COMBINEBATTLE_DAY,
	/** 昼战-12vs6-水打 */
	COMBINEBATTLE_DAY_WATER,
	/** 夜战-12vs6 */
	COMBINEBATTLE_MIDNIGHT,
	/** 昼战-6vs12 */
	COMBINEBATTLE_EC_DAY,
	/** 昼战-12vs12-机动 */
	COMBINEBATTLE_EACH_DAY,
	/** 昼战-12vs12-水打 */
	COMBINEBATTLE_EACH_DAY_WATER,
	/** 夜战-6vs12-12vs12 */
	COMBINEBATTLE_EC_MIDNIGHT,
	/** 开幕夜战-联合舰队 */
	COMBINEBATTLE_MIDNIGHT_SP,
	/** 战斗结果-联合舰队 */
	COMBINEBATTLE_RESULT,

}
