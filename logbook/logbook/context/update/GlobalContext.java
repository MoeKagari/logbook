package logbook.context.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;

import logbook.config.AppConstants;
import logbook.context.dto.battle.BattleDto;
import logbook.context.dto.data.AirbaseDto;
import logbook.context.dto.data.BasicDto;
import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.MapinfoDto;
import logbook.context.dto.data.MasterDataDto;
import logbook.context.dto.data.MaterialDto;
import logbook.context.dto.data.PracticeEnemyDto;
import logbook.context.dto.data.PresetDeckDto;
import logbook.context.dto.data.QuestDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.data.UseItemDto;
import logbook.context.dto.data.record.CreateItemDto;
import logbook.context.dto.data.record.CreateshipDto;
import logbook.context.dto.data.record.DestroyItemDto;
import logbook.context.dto.data.record.DestroyShipDto;
import logbook.context.dto.data.record.MaterialRecordDto;
import logbook.context.dto.data.record.MissionResultDto;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.data.Data;
import logbook.context.update.data.DataType;
import logbook.context.update.room.BattleRoom;
import logbook.context.update.room.CreateItemRoom;
import logbook.context.update.room.CreateShipRoom;
import logbook.context.update.room.DeckRoom;
import logbook.context.update.room.DestroyItemRoom;
import logbook.context.update.room.DestroyShipRoom;
import logbook.context.update.room.HokyoRoom;
import logbook.context.update.room.KaisouRoom;
import logbook.context.update.room.MainRoom;
import logbook.context.update.room.MissionRoom;
import logbook.context.update.room.NyukyoRoom;
import logbook.context.update.room.PracticeRoom;
import logbook.context.update.room.QuestRoom;
import logbook.context.update.room.RemodelRoom;
import logbook.gui.window.ApplicationMain;
import logbook.internal.LoggerHolder;
import logbook.util.ToolUtils;

/**
 * 负责更新全局数据
 * @author MoeKagari
 */
public class GlobalContext {
	private static final LoggerHolder LOG = new LoggerHolder(GlobalContext.class);

	/** 服务器ip */
	private static String serverName = null;
	/** 司令部等级 玩家名字 最大保有舰娘数 最大保有装备数 */
	private static BasicDto basicInformation = null;

	/** 是否结成联合舰队 */
	private static boolean combined = false;
	/**
	 * 通过返回母港时第一舰队无疲劳变化来更新此值
	 * 包括下限,不包括上限
	 */
	private static PLTime PLTIME = null;

	/** 路基详情 */
	private static AirbaseDto airbase = null;
	/** 地图详情 */
	private static MapinfoDto mapinfo = null;
	/** 当前资源 */
	private static MaterialDto currentMaterial = null;
	/** 演习对手 */
	private static PracticeEnemyDto practiceEnemy = null;

	/** 资源记录 */
	private final static List<MaterialRecordDto> materialList = new ArrayList<>();
	/** 远征记录 */
	private final static List<MissionResultDto> missionList = new ArrayList<>();
	/** 开发记录 */
	private final static List<CreateItemDto> createItemList = new ArrayList<>();
	/** 建造记录 */
	private final static List<CreateshipDto> createShipList = new ArrayList<>();

	/** 战斗记录 */
	private final static BattleList battleList = new BattleList();

	/** 所有任务 */
	private final static List<QuestDto> questList = new ArrayList<>();
	/** 所有装备 */
	private final static Map<Integer, ItemDto> itemMap = new HashMap<>();
	/** 所有舰娘 */
	private final static Map<Integer, ShipDto> shipMap = new HashMap<>();
	/** 所有useitem */
	private final static Map<Integer, UseItemDto> useItemMap = new HashMap<>();

	/** 解体记录 */
	private final static List<DestroyShipDto> destroyShipList = new ArrayList<>();
	/** 废弃记录 */
	private final static List<DestroyItemDto> destroyItemList = new ArrayList<>();

	/** 编成记录 */
	private final static List<PresetDeckDto> presetDeckList = new ArrayList<>();

	/** master data */
	private static MasterDataDto masterData = null;

	public static void load() {
		try {
			masterData = new MasterDataDto(Json.createReader(new BufferedReader(new InputStreamReader(new FileInputStream(AppConstants.MASTERDATAFILEPATH), Charset.forName("utf-8")))).readObject());
		} catch (Exception e) {
			ApplicationMain.main.logPrint("MasterData读取失败");
			LOG.get().warn("masterdata读取失败", e);
		}
	}

	public static void store() {
		try {
			if (masterData != null) {
				FileUtils.write(new File(AppConstants.MASTERDATAFILEPATH), masterData.getJson().toString(), Charset.forName("utf-8"));
			}
		} catch (Exception e) {
			LOG.get().warn("masterdata保存失败", e);
		}
	}

	/** 更新GlobalContext  */
	public static void updateContext(DataType type, Data data, String serverName) {
		GlobalContext.serverName = serverName;

		JsonValue json = data.getJsonObject().get("api_data");
		switch (type) {
			//start mainRoom
			case MASTERDATA:
				mainRoom.doMasterData(data, json);
				break;
			case BASIC:
				mainRoom.doBasic(data, json);
				break;
			case SLOT_ITEM:
				mainRoom.doSlotItem(data, json);
				break;
			case SHIP_LOCK:
				mainRoom.doShipLock(data, json);
				break;
			case PORT:
				mainRoom.doPort(data, json);
				break;
			case REQUIRE_INFO:
				mainRoom.doRequireInfo(data, json);
				break;
			case MATERIAL:
				mainRoom.doMaterial(data, json);
				break;
			case MAPINFO:
				mainRoom.doMapinfo(data, json);
				break;
			case USEITEM:
				mainRoom.doUseitem(data, json);
				break;
			case SHIP2:
				mainRoom.doShip2(data, json);
				break;
			//end
			//start questRoom
			case QUEST_LIST:
				questRoom.doQuestList(data, json);
				break;
			case QUEST_CLEAR://后接各种主要数据的刷新api,无需处理
				break;
			case QUEST_START://后接QUESTLIST,无需处理
				break;
			case QUEST_STOP://后接QUESTLIST,无需处理
				break;
			//end
			//start createItemRoom
			case CREATEITEM:
				createItemRoom.doCreateitem(data, json);
				break;
			//end
			//start destroyItemRoom
			case DESTROYITEM:
				destroyItemRoom.doDestroyItem(data, json);
				break;
			//end
			//start kaisouRoom
			case MARRIAGE://后接ship2,无需处理				
			case SLOTSET://后接ship3,无需处理
			case UNSETSLOT_ALL://后接ship3,无需处理
			case SLOTSET_EX://后接ship3,无需处理
			case REMODELING://后接ship3,无需处理
				break;
			case SLOT_EXCHANGE:
				kaisouRoom.doSlotExchange(data, json);
				break;
			case SLOT_DEPRIVE:
				kaisouRoom.doSlotDeprive(data, json);
				break;
			case OPEN_EXSLOT:
				kaisouRoom.doOpenSlotex(data, json);
			case POWERUP:
				kaisouRoom.doPowerup(data, json);
				break;
			case SLOT_ITEM_LOCK:
				kaisouRoom.doSlotItemLock(data, json);
				break;
			case SHIP3:
				kaisouRoom.doShip3(data, json);
				break;
			//end
			//start deckRoom
			case DECK:
				ToolUtils.forEach(deckRoom, dr -> dr.doDeck(data, json));
				break;
			case CHANGE:
				ToolUtils.forEach(deckRoom, dr -> dr.doChange(data, json));
				break;
			case UPDATEDECKNAME:
				ToolUtils.forEach(deckRoom, dr -> dr.doUpdatedeckname(data, json));
				break;
			case PRESET_SELECT:
				ToolUtils.forEach(deckRoom, dr -> dr.doPresetSelect(data, json));
				break;
			//end
			//start createShipRoom
			case KDOCK:
				ToolUtils.forEach(createShipRoom, csr -> csr.doKdock(data, json));
				break;
			case CREATESHIP:
				ToolUtils.forEach(createShipRoom, csr -> csr.doCreateship(data, json));
				break;
			case CREATESHIP_SPEEDCHANGE:
				ToolUtils.forEach(createShipRoom, csr -> csr.doCreateshipSpeedchange(data, json));
				break;
			case CREATESHIP_GETSHIP:
				ToolUtils.forEach(createShipRoom, csr -> csr.doGetShip(data, json));
				break;
			//end
			//start destroyShipRoom
			case DESTROYSHIP:
				destroyShipRoom.doDestroyShip(data, json);
				break;
			//end
			//start missionRoom				
			case MISSION:
			case MISSIONSTART://后接DECK,无需处理
			case MISSIONRETURN:
				break;
			case MISSIONRESULT://后接PORT,所以只需记录远征信息
				missionRoom.doMissionResulut(data, json);
				break;
			//end
			//start hokyoRoom
			case CHARGE:
				hokyoRoom.doCharge(data, json);
				break;
			//end
			//start nyukyoRoom
			case NDOCK:
				ToolUtils.forEach(nyukyoRoom, nr -> nr.doNdock(data, json));
				break;
			case NYUKYO_START:
				ToolUtils.forEach(nyukyoRoom, nr -> nr.doNyukyoStart(data, json));
				break;
			case NYUKYO_SPEEDCHANGE:
				ToolUtils.forEach(nyukyoRoom, nr -> nr.doNyukyoSpeedchange(data, json));
				break;
			//end
			//start remodelRoom
			case REMODEL_SLOTLIST:
			case REMODEL_SLOTLIST_DETAIL:
				break;
			case REMODEL_SLOT:
				remodelRoom.doRemodelSlot(data, json);
				break;
			//end
			//start battleRoom
			case BATTLE_START:
				battleRoom.doBattleStart(data, json);
				break;
			case BATTLE_NEXT:
				battleRoom.doBattleNext(data, json);
				break;

			case BATTLE_AIRBATTLE:
				battleRoom.doBattleAirbattle(data, json);
				break;
			case BATTLE_AIRBATTLE_LD:
				battleRoom.doBattleAirbattleLD(data, json);
				break;
			case BATTLE_DAY:
				battleRoom.doBattleDay(data, json);
				break;
			case BATTLE_MIDNIGHT:
				battleRoom.doBattleMidnight(data, json);
				break;
			case BATTLE_MIDNIGHT_SP:
				battleRoom.doBattleMidnightSP(data, json);
				break;
			case BATTLE_RESULT:
				battleRoom.doBattleResult(data, json);
				break;

			case BATTLE_SHIPDECK:
				battleRoom.doBattleShipdeck(data, json);
				break;
			/*------------------------------------------------------------------------*/
			case COMBINEBATTLE_AIRBATTLE:
				battleRoom.doCombinebattleAirbattle(data, json);
				break;
			case COMBINEBATTLE_AIRBATTLE_LD:
				battleRoom.doCombinebattleAirbattleLD(data, json);
				break;
			//12vs6
			case COMBINEBATTLE_DAY:
				battleRoom.doCombinebattleDay(data, json);
				break;
			case COMBINEBATTLE_DAY_WATER:
				battleRoom.doCombinebattleDayWater(data, json);
				break;
			case COMBINEBATTLE_MIDNIGHT:
				battleRoom.doCombinebattleMidnight(data, json);
				break;
			//6vs12
			case COMBINEBATTLE_EC_DAY:
				battleRoom.doCombinebattleECDay(data, json);
				break;
			case COMBINEBATTLE_EACH_DAY:
				battleRoom.doCombinebattleEachDay(data, json);
				break;
			case COMBINEBATTLE_EACH_DAY_WATER:
				battleRoom.doCombinebattleEachDayWater(data, json);
				break;
			case COMBINEBATTLE_EC_MIDNIGHT:
				battleRoom.doCombinebattleECMidnight(data, json);
				break;
			//开幕夜战-联合舰队
			case COMBINEBATTLE_MIDNIGHT_SP:
				battleRoom.doCombinebattleMidnightSP(data, json);
				break;
			//战斗结果-联合舰队
			case COMBINEBATTLE_RESULT:
				battleRoom.doCombinebattleResult(data, json);
				break;
			case GOBACK_PORT:
				battleRoom.doCombinebattleGobackPort(data, json);
				break;
			case BATTLE_START_AIR_BASE:
				battleRoom.doBattleStartAirBase(data, json);
				break;
			//end
			//start practiceRoom
			case PRACTICE_LIST:
				break;
			case PRACTICE_ENEMYINFO:
				practiceRoom.doPracticeEnemyInfo(data, json);
				break;
			case BATTLE_PRACTICE_DAY:
				practiceRoom.doPracticeBattleDay(data, json);
				break;
			case BATTLE_PRACTICE_MIDNIGHT:
				practiceRoom.doPracticeBattleMidnight(data, json);
				break;
			case BATTLE_PRACTICE_RESULT:
				practiceRoom.doPracticeBattleResult(data, json);
				break;
			//end

			/*-------------------------------------------------------------------------------------*/
			//start 现无任何可供显示的信息
			case PRACTICE_CHANGE_MATCHING_KIND:
			case EVENTMAP_RANK_SELECT:
			case UNSETSLOT:
			case PRESET_DECK:
			case PRESET_REGISTER:
			case PRESET_DELETE:
			case GET_INCENTIVE:
			case PAYITEM:
			case RECORD:
			case PICTURE_BOOK:
			case MXLTVKPYUKLH:
			case UPDATECOMMENT:
			case PAYITEMUSE:
			case ITEMUSE:
			case MUSIC_LIST:
			case MUSIC_PLAY:
			case SET_PORTBGM:
			case ITEMUSE_COND:
			case BUY_FURNITURE:
			case PAYCHECK:
			case RADIO_PLAY:
			case FURNITURE_CHANGE:
			case COMBINED:
			case SORTIE_CONDITIONS:
			case SUPPLY:
			case AIRBASE_EXPAND:
			case AIRBASE_CHANGENAME:
			case AIRBASE_SETPLANE:
			case AIRBASE_INFORMATION:
				break;
			//end
		}
	}

	/*----------------------------------------------静态方法------------------------------------------------------------------*/

	public static void updatePLTIME(long oldtime, int[] oldconds, long newtime, int[] newconds) {
		if (oldtime <= 0 || newtime <= 0) {
			return;
		}

		if (PLTime.need(oldtime, oldconds, newtime, newconds)) {
			if (PLTIME == null) {
				PLTIME = new PLTime(newtime - 3 * 60 * 1000, oldtime);
			} else {
				PLTIME.update(oldtime, oldconds, newtime, newconds);
			}
		}
	}

	public static void setMaterial(int[] mm) {
		if (currentMaterial != null) {
			int[] material = ToolUtils.arrayCopy(currentMaterial.getMaterial());
			for (int i = 0; i < mm.length; i++) {
				material[i] = mm[i];
			}
			currentMaterial = new MaterialDto(material);
		}
	}

	public static void addMaterial(int[] mm) {
		if (currentMaterial != null) {
			int[] material = ToolUtils.arrayCopy(currentMaterial.getMaterial());
			for (int i = 0; i < mm.length; i++) {
				material[i] += mm[i];
			}
			currentMaterial = new MaterialDto(material);
		}
	}

	public static void reduceMaterial(int[] mm) {
		if (currentMaterial == null) {
			for (int i = 0; i < mm.length; i++) {
				mm[i] *= -1;
			}
			addMaterial(mm);
		}
	}

	public static void destroyShip(long time, String event, int id) {
		ShipDto ship = shipMap.get(id);
		if (ship != null) {
			final int count = ShipDtoTranslator.getSlotCount(ship);
			ToolUtils.forEach(ship.getSlots(), item -> destroyItem(time, event, item, count));
			destroyItem(time, event, ship.getSlotex(), -1);

			destroyShipList.add(new DestroyShipDto(time, event, ship));
			shipMap.remove(ship.getId());
		}
		ToolUtils.forEach(GlobalContext.getDeckRoom(), dr -> ToolUtils.notNullThenHandle(dr.getDeck(), deck -> deck.remove(id)));
	}

	public static void destroyItem(long time, String event, int id, int group) {
		ItemDto item = itemMap.get(id);
		if (item != null) {
			destroyItemList.add(new DestroyItemDto(time, event, item, group));
			itemMap.remove(item.getId());
		}
	}

	public static ShipDto addNewShip(JsonObject json) {
		ShipDto ship = new ShipDto(json);
		shipMap.put(ship.getId(), ship);
		return ship;
	}

	public static ItemDto addNewItem(JsonObject json) {
		ItemDto item = new ItemDto(json);
		itemMap.put(item.getId(), item);
		return item;
	}

	public static UseItemDto addNewUseItem(JsonObject json) {
		UseItemDto useItem = new UseItemDto(json);
		useItemMap.put(useItem.getId(), useItem);
		return useItem;
	}

	public static void updateShip(int id, Consumer<ShipDto> handler) {
		ToolUtils.notNullThenHandle(shipMap.get(id), handler);
	}

	public static ShipDto getSecretaryship() {
		return ToolUtils.notNullThenHandle(deckRoom[0].getDeck(), deck -> GlobalContext.shipMap.get(deck.getShips()[0]), null);
	}

	/*----------------------------------------------getter------------------------------------------------------------------*/

	public static PLTime getPLTIME() {
		return PLTIME;
	}

	public static boolean isCombined() {
		return combined;
	}

	public static void setCombined(boolean combined) {
		GlobalContext.combined = combined;
	}

	public static String getServerName() {
		return serverName;
	}

	public static Map<Integer, UseItemDto> getUseitemmap() {
		return useItemMap;
	}

	public static Map<Integer, ItemDto> getItemMap() {
		return itemMap;
	}

	public static Map<Integer, ShipDto> getShipMap() {
		return shipMap;
	}

	public static List<QuestDto> getQuestlist() {
		return questList;
	}

	public static List<DestroyShipDto> getDestroyshiplist() {
		return destroyShipList;
	}

	public static List<DestroyItemDto> getDestroyitemlist() {
		return destroyItemList;
	}

	public static List<CreateItemDto> getCreateitemlist() {
		return createItemList;
	}

	public static List<CreateshipDto> getCreateshiplist() {
		return createShipList;
	}

	public static List<MissionResultDto> getMissionlist() {
		return missionList;
	}

	public static List<MaterialRecordDto> getMaterialRecord() {
		return materialList;
	}

	public static BattleList getBattlelist() {
		return battleList;
	}

	public static BasicDto getBasicInformation() {
		return basicInformation;
	}

	public static List<PresetDeckDto> getPresetdecklist() {
		return presetDeckList;
	}

	public static void setBasicInformation(BasicDto basicInformation) {
		GlobalContext.basicInformation = basicInformation;
	}

	public static MaterialDto getCurrentMaterial() {
		return currentMaterial;
	}

	public static void setCurrentMaterial(MaterialDto currentMaterial) {
		GlobalContext.currentMaterial = currentMaterial;
	}

	public static PracticeEnemyDto getPracticeEnemy() {
		return practiceEnemy;
	}

	public static void setPracticeEnemy(PracticeEnemyDto practiceEnemyDto) {
		GlobalContext.practiceEnemy = practiceEnemyDto;
	}

	public static MasterDataDto getMasterData() {
		return masterData;
	}

	public static void setMasterData(MasterDataDto masterData) {
		GlobalContext.masterData = masterData;
	}

	public static MapinfoDto getMapinfo() {
		return mapinfo;
	}

	public static void setMapinfo(MapinfoDto mapinfo) {
		GlobalContext.mapinfo = mapinfo;
	}

	public static AirbaseDto getAirbase() {
		return airbase;
	}

	public static void setAirbase(AirbaseDto airbase) {
		GlobalContext.airbase = airbase;
	}

	//start ----------------------------------------------各个设施------------------------------------------------------------------
	private final static MainRoom mainRoom = new MainRoom();
	private final static DeckRoom[] deckRoom = { new DeckRoom(1), new DeckRoom(2), new DeckRoom(3), new DeckRoom(4) };
	private final static HokyoRoom hokyoRoom = new HokyoRoom();
	private final static KaisouRoom kaisouRoom = new KaisouRoom();
	private final static NyukyoRoom[] nyukyoRoom = { new NyukyoRoom(1), new NyukyoRoom(2), new NyukyoRoom(3), new NyukyoRoom(4), };
	private final static CreateShipRoom[] createShipRoom = { new CreateShipRoom(1), new CreateShipRoom(2), new CreateShipRoom(3), new CreateShipRoom(4) };
	private final static DestroyShipRoom destroyShipRoom = new DestroyShipRoom();
	private final static CreateItemRoom createItemRoom = new CreateItemRoom();
	private final static DestroyItemRoom destroyItemRoom = new DestroyItemRoom();
	private final static MissionRoom missionRoom = new MissionRoom();
	private static final RemodelRoom remodelRoom = new RemodelRoom();
	private static final PracticeRoom practiceRoom = new PracticeRoom();
	private static final QuestRoom questRoom = new QuestRoom();
	private static final BattleRoom battleRoom = new BattleRoom();

	public static BattleRoom getBattleroom() {
		return battleRoom;
	}

	public static QuestRoom getQuestroom() {
		return questRoom;
	}

	public static RemodelRoom getRemodelroom() {
		return remodelRoom;
	}

	public static MainRoom getMainRoom() {
		return mainRoom;
	}

	public static DeckRoom[] getDeckRoom() {
		return deckRoom;
	}

	public static HokyoRoom getHokyoRoom() {
		return hokyoRoom;
	}

	public static KaisouRoom getKaisouRoom() {
		return kaisouRoom;
	}

	public static NyukyoRoom[] getNyukyoRoom() {
		return nyukyoRoom;
	}

	public static CreateShipRoom[] getCreateShipRoom() {
		return createShipRoom;
	}

	public static DestroyShipRoom getDestroyShipRoom() {
		return destroyShipRoom;
	}

	public static CreateItemRoom getCreateItemRoom() {
		return createItemRoom;
	}

	public static DestroyItemRoom getDestroyItemRoom() {
		return destroyItemRoom;
	}

	public static PracticeRoom getPracticeroom() {
		return practiceRoom;
	}
	//end

	public static class BattleList {
		private ArrayList<BattleDto> battles = new ArrayList<>();
		private BattleDto lastOne = null;
		private BattleDto lastTwo = null;

		public void add(BattleDto battleDto) {
			this.lastTwo = this.lastOne;
			this.lastOne = battleDto;
			this.battles.add(battleDto);
		}

		public ArrayList<BattleDto> getBattleList() {
			return this.battles;
		}

		public BattleDto getLastTwo() {
			return this.lastTwo;
		}

		public BattleDto getLastOne() {
			return this.lastOne;
		}

		public void clearLast() {
			this.lastOne = null;
			this.lastTwo = null;
		}
	}

	public static class PLTime {
		private long floor, ceil;
		private TreeSet<long[]> notuse = new TreeSet<>((a, b) -> Long.compare(a[0], b[0]));

		public PLTime(long floor, long ceil) {
			this.floor = floor;
			this.ceil = ceil;
		}

		public long getTime() {
			return (this.floor + this.ceil) / 2;
		}

		public long getRange() {
			return (this.ceil - this.floor) / 2;
		}

		public void update(long oldtime, int[] oldconds, long newtime, int[] newconds) {
			long time1 = oldtime;
			long time2 = newtime;
			System.out.println("刷新:" + "time1=" + time1 + ",time2=" + time2);

			while (time1 >= this.ceil && time2 >= this.ceil) {
				time1 -= 3 * 60 * 1000;
				time2 -= 3 * 60 * 1000;
			}

			if (time2 <= this.floor) {
				System.out.println("不需要:" + "time1=" + time1 + ",time2=" + time2);
				//如果不在预测时间段内,不需要
			} else {//有交集
				this.notuse.add(new long[] { time1, time2 });
				this.update();
			}

		}

		private void update() {
			TreeSet<long[]> temps = new TreeSet<>((a, b) -> Long.compare(a[0], b[0]));
			temps.addAll(this.notuse);
			System.out.println("notuse:");
			this.notuse.forEach(one -> System.out.println(Arrays.toString(one)));
			this.notuse.clear();

			//整合
			long time1 = -1, time2 = -1;
			for (long[] temp : temps) {
				if (time1 == -1) time1 = temp[0];
				if (time2 == -1) time2 = temp[1];
				if (time1 == temp[0] && time2 == temp[1]) continue;

				if (time2 >= temp[0]) {
					time2 = temp[1];
				} else {
					this.update(time1, time2);
					time1 = -1;
					time2 = -1;
				}
			}

			if (time1 != -1 && time2 != -1) {
				this.update(time1, time2);
			}

			System.out.println("notuse:");
			this.notuse.forEach(one -> System.out.println(Arrays.toString(one)));
		}

		private void update(long time1, long time2) {
			System.out.println("start:\n" + "time1=" + time1 + ",time2=" + time2);
			System.out.println("" + "floor=" + this.floor + ",ceil=" + this.ceil);

			if (time1 >= this.floor && time2 <= this.ceil) {
				if (time1 == this.floor && time2 < this.ceil) {
					this.floor = time2;
				} else if (time1 > this.floor && time2 == this.ceil) {
					this.ceil = time1;
				} else {
					//时间段是预测时间段的子集,需收集之后整合
					this.notuse.add(new long[] { time1, time2 });
					System.out.println("add");
				}
			} else {
				//只有交集
				if (time1 >= this.floor) {
					this.ceil = time1;
				} else if (time2 <= this.ceil) {
					this.floor = time2;
				}
			}

			System.out.println("" + "floor=" + this.floor + ",ceil=" + this.ceil);
			System.out.println("end");
		}

		public static boolean need(long oldtime, int[] oldconds, long newtime, int[] newconds) {
			if (newtime - oldtime > 60 * 1000) {
				//两次刷新时间大于一分钟,则忽略此次刷新母港
				//尽管理论上可以最大三分钟,但是根据游戏情况,缩小范围显得更优
				return false;
			}

			if (oldconds != null && newconds != null) {
				Predicate<int[]> need = conds -> Arrays.stream(oldconds).anyMatch(i -> (i >= 0) && (i < 49));
				if (need.test(oldconds) && need.test(newconds) && Arrays.equals(oldconds, newconds)) {
					//有舰娘的疲劳处于[0,49),并且两个time没有发生疲劳变化
					return true;
				}
			}

			return false;
		}

	}

}
