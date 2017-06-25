package logbook.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import logbook.dto.AbstractMemory;
import logbook.dto.memory.DestroyItemDto;
import logbook.dto.memory.DestroyShipDto;
import logbook.dto.memory.battle.BattleDto;
import logbook.dto.translator.DeckDtoTranslator;
import logbook.dto.word.AirbaseDto;
import logbook.dto.word.BasicDto;
import logbook.dto.word.ItemDto;
import logbook.dto.word.MapinfoDto;
import logbook.dto.word.MasterDataDto;
import logbook.dto.word.MaterialDto;
import logbook.dto.word.PracticeEnemyDto;
import logbook.dto.word.PresetDeckDto;
import logbook.dto.word.QuestDto;
import logbook.dto.word.ShipDto;
import logbook.dto.word.UseItemDto;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.internal.LoggerHolder;
import logbook.internal.TrayMessageBox;
import logbook.update.data.Data;
import logbook.update.data.DataType;
import logbook.update.room.BattleRoom;
import logbook.update.room.CreateItemRoom;
import logbook.update.room.CreateShipRoom;
import logbook.update.room.DeckRoom;
import logbook.update.room.DestroyItemRoom;
import logbook.update.room.DestroyShipRoom;
import logbook.update.room.HokyoRoom;
import logbook.update.room.KaisouRoom;
import logbook.update.room.MainRoom;
import logbook.update.room.MissionRoom;
import logbook.update.room.NyukyoRoom;
import logbook.update.room.PracticeRoom;
import logbook.update.room.PresetDeckRoom;
import logbook.update.room.QuestRoom;
import logbook.update.room.RemodelRoom;
import logbook.utils.ToolUtils;

/**
 * 负责更新全局数据
 * @author MoeKagari
 */
public class GlobalContext {
	private static final LoggerHolder LOG = new LoggerHolder(GlobalContext.class);

	public static void load() {
		try {
			InputStream is;
			File file = AppConstants.MASTERDATA_FILE;
			if (file.exists() && file.isFile()) {
				is = new FileInputStream(file);
			} else {//读取程序内置的备份
				is = GlobalContext.class.getResourceAsStream(AppConstants.MASTERDATAFILE_BACKUP);
			}
			JsonObject json = Json.createReader(new InputStreamReader(is, Charset.forName("utf-8"))).readObject();
			masterData = new MasterDataDto(json);
			is.close();
		} catch (Exception e) {
			ApplicationMain.main.printMessage("MasterData读取失败", false);
			LOG.get().warn("MasterData读取失败", e);
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AppConstants.MEMORY_FILE))) {
			((List<?>) ois.readObject()).forEach(ele -> {
				if (ele instanceof AbstractMemory) {
					memoryList.memorys.add((AbstractMemory) ele);
				}
			});
		} catch (Exception e) {
			LOG.get().warn("memory读取失败", e);
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AppConstants.ITEM_FILE))) {
			((List<?>) ois.readObject()).forEach(ele -> {
				if (ele instanceof ItemDto) {
					ItemDto item = (ItemDto) ele;
					itemMap.put(item.getId(), item);
				}
			});
		} catch (Exception e) {
			LOG.get().warn("item读取失败", e);
		}
	}

	public static void store() {
		try {
			if (masterData != null) {
				FileUtils.write(AppConstants.MASTERDATA_FILE, masterData.getJson().toString(), Charset.forName("utf-8"));
			}
		} catch (Exception e) {
			LOG.get().warn("MasterData保存失败", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AppConstants.MEMORY_FILE))) {
			oos.writeObject(memoryList.memorys);
		} catch (Exception e) {
			LOG.get().warn("memory保存失败", e);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AppConstants.ITEM_FILE))) {
			oos.writeObject(new ArrayList<>(itemMap.values()));
		} catch (Exception e) {
			LOG.get().warn("item保存失败", e);
		}
	}

	//start ----------------------------------------------各个设施------------------------------------------------------------------
	public final static MainRoom mainRoom = new MainRoom();
	public final static DeckRoom[] deckRoom = { new DeckRoom(1), new DeckRoom(2), new DeckRoom(3), new DeckRoom(4) };
	public final static HokyoRoom hokyoRoom = new HokyoRoom();
	public final static KaisouRoom kaisouRoom = new KaisouRoom();
	public final static NyukyoRoom[] nyukyoRoom = { new NyukyoRoom(1), new NyukyoRoom(2), new NyukyoRoom(3), new NyukyoRoom(4), };
	public final static CreateShipRoom[] createShipRoom = { new CreateShipRoom(1), new CreateShipRoom(2), new CreateShipRoom(3), new CreateShipRoom(4) };
	public final static DestroyShipRoom destroyShipRoom = new DestroyShipRoom();
	public final static CreateItemRoom createItemRoom = new CreateItemRoom();
	public final static DestroyItemRoom destroyItemRoom = new DestroyItemRoom();
	public final static MissionRoom missionRoom = new MissionRoom();
	public final static RemodelRoom remodelRoom = new RemodelRoom();
	public final static PracticeRoom practiceRoom = new PracticeRoom();
	public final static QuestRoom questRoom = new QuestRoom();
	public final static BattleRoom battleRoom = new BattleRoom();
	public final static PresetDeckRoom presetDeckRoom = new PresetDeckRoom();
	//end

	/** 服务器ip */
	private static String serverName = null;
	/** 司令部等级 提督名字 最大保有舰娘数 最大保有装备数 */
	private static BasicDto basicInformation = null;
	/** 是否结成联合舰队 */
	private static boolean combined = false;
	/** 通过返回母港时第一舰队无疲劳变化来更新此值 */
	private static PLTime PLTIME = null;
	/** 泊地修理 */
	private static FleetAkashiTimer akashiTimer = null;

	/** 路基详情 */
	private static AirbaseDto airbase = null;
	/** 地图详情 */
	private static MapinfoDto mapinfo = null;
	/** 当前资源 */
	private static MaterialDto currentMaterial = null;
	/** 演习对手 */
	private static PracticeEnemyDto practiceEnemy = null;
	/** master data */
	private static MasterDataDto masterData = null;

	/** 所有记录({@link AbstractMemory}的子类) */
	private final static MemoryList memoryList = new MemoryList();

	/** 所有任务 */
	private final static List<QuestDto> questList = new ArrayList<>();
	/** 所有装备 */
	private final static Map<Integer, ItemDto> itemMap = new HashMap<>();
	/** 所有舰娘 */
	private final static Map<Integer, ShipDto> shipMap = new HashMap<>();
	/** 所有useitem */
	private final static Map<Integer, UseItemDto> useItemMap = new HashMap<>();

	/** 编成记录(游戏中的) */
	private final static PresetDeckList presetDeckList = new PresetDeckList();

	/** 更新GlobalContext  */
	public static void updateContext(DataType type, Data data, String serverName) {
		GlobalContext.serverName = serverName;

		JsonObject json = data.getJsonObject();
		int api_result = json.getInt("api_result");
		if (api_result != 1) {
			ApplicationMain.main.printMessage(String.format("猫了,猫娘: %d", api_result), true);
			return;
		}

		JsonValue api_data = json.get("api_data");
		switch (type) {
			//start mainRoom
			case MASTERDATA:
				mainRoom.doMasterData(data, api_data);
				break;
			case BASIC:
				mainRoom.doBasic(data, api_data);
				break;
			case SLOT_ITEM:
				mainRoom.doSlotItem(data, api_data);
				break;
			case SHIP_LOCK:
				mainRoom.doShipLock(data, api_data);
				break;
			case PORT:
				mainRoom.doPort(data, api_data);
				break;
			case REQUIRE_INFO:
				mainRoom.doRequireInfo(data, api_data);
				break;
			case MATERIAL:
				mainRoom.doMaterial(data, api_data);
				break;
			case MAPINFO:
				mainRoom.doMapinfo(data, api_data);
				break;
			case USEITEM:
				mainRoom.doUseitem(data, api_data);
				break;
			case SHIP2:
				mainRoom.doShip2(data, api_data);
				break;
			//end
			//start questRoom
			case QUEST_LIST:
				questRoom.doQuestList(data, api_data);
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
				createItemRoom.doCreateitem(data, api_data);
				break;
			//end
			//start destroyItemRoom
			case DESTROYITEM:
				destroyItemRoom.doDestroyItem(data, api_data);
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
				kaisouRoom.doSlotExchange(data, api_data);
				break;
			case SLOT_DEPRIVE:
				kaisouRoom.doSlotDeprive(data, api_data);
				break;
			case OPEN_EXSLOT:
				kaisouRoom.doOpenSlotex(data, api_data);
			case POWERUP:
				kaisouRoom.doPowerup(data, api_data);
				break;
			case SLOT_ITEM_LOCK:
				kaisouRoom.doSlotItemLock(data, api_data);
				break;
			case SHIP3:
				kaisouRoom.doShip3(data, api_data);
				break;
			//end
			//start deckRoom
			case DECK:
				ToolUtils.forEach(deckRoom, dr -> dr.doDeck(data, api_data));
				break;
			case CHANGE:
				ToolUtils.forEach(deckRoom, dr -> dr.doChange(data, api_data));
				break;
			case UPDATEDECKNAME:
				ToolUtils.forEach(deckRoom, dr -> dr.doUpdatedeckname(data, api_data));
				break;
			case PRESET_SELECT:
				ToolUtils.forEach(deckRoom, dr -> dr.doPresetSelect(data, api_data));
				break;
			//end
			//start presetdeckroom
			case PRESET_DECK:
				presetDeckRoom.doPresetDeck(data, api_data);
				break;
			case PRESET_REGISTER:
				presetDeckRoom.doPresetRegister(data, api_data);
				break;
			case PRESET_DELETE:
				presetDeckRoom.doPresetDelete(data, api_data);
				break;
			//end
			//start createShipRoom
			case KDOCK:
				ToolUtils.forEach(createShipRoom, csr -> csr.doKdock(data, api_data));
				break;
			case CREATESHIP:
				ToolUtils.forEach(createShipRoom, csr -> csr.doCreateship(data, api_data));
				break;
			case CREATESHIP_SPEEDCHANGE:
				ToolUtils.forEach(createShipRoom, csr -> csr.doCreateshipSpeedchange(data, api_data));
				break;
			case CREATESHIP_GETSHIP:
				ToolUtils.forEach(createShipRoom, csr -> csr.doGetShip(data, api_data));
				break;
			//end
			//start destroyShipRoom
			case DESTROYSHIP:
				destroyShipRoom.doDestroyShip(data, api_data);
				break;
			//end
			//start missionRoom
			case MISSION:
			case MISSIONSTART://后接DECK,无需处理
			case MISSIONRETURN:
				break;
			case MISSIONRESULT://后接PORT,所以只需记录远征信息
				missionRoom.doMissionResulut(data, api_data);
				break;
			//end
			//start hokyoRoom
			case CHARGE:
				hokyoRoom.doCharge(data, api_data);
				break;
			//end
			//start nyukyoRoom
			case NDOCK:
				ToolUtils.forEach(nyukyoRoom, nr -> nr.doNdock(data, api_data));
				break;
			case NYUKYO_START:
				ToolUtils.forEach(nyukyoRoom, nr -> nr.doNyukyoStart(data, api_data));
				break;
			case NYUKYO_SPEEDCHANGE:
				ToolUtils.forEach(nyukyoRoom, nr -> nr.doNyukyoSpeedchange(data, api_data));
				break;
			//end
			//start remodelRoom
			case REMODEL_SLOTLIST:
			case REMODEL_SLOTLIST_DETAIL:
				break;
			case REMODEL_SLOT:
				remodelRoom.doRemodelSlot(data, api_data);
				break;
			//end
			//start battleRoom
			case BATTLE_START:
				battleRoom.doBattleStart(data, api_data);
				break;
			case BATTLE_NEXT:
				battleRoom.doBattleNext(data, api_data);
				break;

			case BATTLE_AIRBATTLE:
				battleRoom.doBattleAirbattle(data, api_data);
				break;
			case BATTLE_AIRBATTLE_LD:
				battleRoom.doBattleAirbattleLD(data, api_data);
				break;
			case BATTLE_DAY:
				battleRoom.doBattleDay(data, api_data);
				break;
			case BATTLE_MIDNIGHT:
				battleRoom.doBattleMidnight(data, api_data);
				break;
			case BATTLE_MIDNIGHT_SP:
				battleRoom.doBattleMidnightSP(data, api_data);
				break;
			case BATTLE_RESULT:
				battleRoom.doBattleResult(data, api_data);
				break;

			case BATTLE_SHIPDECK:
				battleRoom.doBattleShipdeck(data, api_data);
				break;
			/*------------------------------------------------------------------------*/
			case COMBINEBATTLE_AIRBATTLE:
				battleRoom.doCombinebattleAirbattle(data, api_data);
				break;
			case COMBINEBATTLE_AIRBATTLE_LD:
				battleRoom.doCombinebattleAirbattleLD(data, api_data);
				break;
			//12vs6
			case COMBINEBATTLE_DAY:
				battleRoom.doCombinebattleDay(data, api_data);
				break;
			case COMBINEBATTLE_DAY_WATER:
				battleRoom.doCombinebattleDayWater(data, api_data);
				break;
			case COMBINEBATTLE_MIDNIGHT:
				battleRoom.doCombinebattleMidnight(data, api_data);
				break;
			//6vs12
			case COMBINEBATTLE_EC_DAY:
				battleRoom.doCombinebattleECDay(data, api_data);
				break;
			case COMBINEBATTLE_EACH_DAY:
				battleRoom.doCombinebattleEachDay(data, api_data);
				break;
			case COMBINEBATTLE_EACH_DAY_WATER:
				battleRoom.doCombinebattleEachDayWater(data, api_data);
				break;
			case COMBINEBATTLE_EC_MIDNIGHT:
				battleRoom.doCombinebattleECMidnight(data, api_data);
				break;
			//开幕夜战-联合舰队
			case COMBINEBATTLE_MIDNIGHT_SP:
				battleRoom.doCombinebattleMidnightSP(data, api_data);
				break;
			//战斗结果-联合舰队
			case COMBINEBATTLE_RESULT:
				battleRoom.doCombinebattleResult(data, api_data);
				break;
			case GOBACK_PORT:
				battleRoom.doCombinebattleGobackPort(data, api_data);
				break;
			case BATTLE_START_AIR_BASE:
				battleRoom.doBattleStartAirBase(data, api_data);
				break;
			//end
			//start practiceRoom
			case PRACTICE_LIST:
				break;
			case PRACTICE_ENEMYINFO:
				practiceRoom.doPracticeEnemyInfo(data, api_data);
				break;
			case BATTLE_PRACTICE_DAY:
				practiceRoom.doPracticeBattleDay(data, api_data);
				break;
			case BATTLE_PRACTICE_MIDNIGHT:
				practiceRoom.doPracticeBattleMidnight(data, api_data);
				break;
			case BATTLE_PRACTICE_RESULT:
				practiceRoom.doPracticeBattleResult(data, api_data);
				break;
			//end
			//start 现无任何可供显示的信息
			case PRACTICE_CHANGE_MATCHING_KIND:
			case EVENTMAP_RANK_SELECT:
			case UNSETSLOT:
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
		if (PLTIME != null && PLTIME.getRange() < 2 * 1000) return;
		if (oldtime <= 0 || newtime <= 0) return;

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
		if (currentMaterial != null) {
			int[] material = ToolUtils.arrayCopy(currentMaterial.getMaterial());
			for (int i = 0; i < mm.length; i++) {
				material[i] -= mm[i];
			}
			currentMaterial = new MaterialDto(material);
		}
	}

	public static void destroyShip(long time, String event, int id) {
		ShipDto ship = shipMap.get(id);
		if (ship != null) {
			final int count = Arrays.stream(ship.getSlots()).filter(slot -> slot > 0).map(i -> 1).sum();
			ToolUtils.forEach(ship.getSlots(), item -> destroyItem(time, event, item, count));
			destroyItem(time, event, ship.getSlotex(), -1);

			memoryList.add(new DestroyShipDto(time, event, ship));
			shipMap.remove(ship.getId());
		}
		ToolUtils.forEach(deckRoom, dr -> ToolUtils.notNull(dr.getDeck(), deck -> deck.remove(id)));
	}

	public static void destroyItem(long time, String event, int id, int group) {
		ItemDto item = itemMap.get(id);
		if (item != null) {
			memoryList.add(new DestroyItemDto(time, event, item, group));
			itemMap.remove(item.getId());
		}
	}

	public static ShipDto getShip(int id) {
		return shipMap.get(id);
	}

	public static ShipDto addNewShip(JsonValue value) {
		ShipDto ship = null;
		if (value instanceof JsonObject) {
			ship = new ShipDto((JsonObject) value);
			shipMap.put(ship.getId(), ship);
		}
		return ship;
	}

	public static ItemDto getItem(int id) {
		return itemMap.get(id);
	}

	public static ItemDto addNewItem(JsonValue value) {
		ItemDto item = null;
		if (value instanceof JsonObject) {
			item = new ItemDto((JsonObject) value);
			itemMap.put(item.getId(), item);
		}
		return item;
	}

	public static UseItemDto getUseItem(int id) {
		return useItemMap.get(id);
	}

	public static UseItemDto addNewUseItem(JsonValue value) {
		UseItemDto useItem = null;
		if (value instanceof JsonObject) {
			useItem = new UseItemDto((JsonObject) value);
			useItemMap.put(useItem.getId(), useItem);
		}
		return useItem;
	}

	public static void updateShip(int id, Consumer<ShipDto> handler) {
		ToolUtils.notNull(shipMap.get(id), handler);
	}

	public static ShipDto getSecretaryship() {
		return ToolUtils.notNull(deckRoom[0].getDeck(), deck -> getShip(deck.getShips()[0]), null);
	}

	public static void setAkashiTimer() {
		GlobalContext.akashiTimer = new FleetAkashiTimer();
	}

	/*----------------------------------------------getter,setter------------------------------------------------------------------*/

	public static PLTime getPLTIME() {
		return PLTIME;
	}

	public static FleetAkashiTimer getAkashiTimer() {
		return akashiTimer;
	}

	public static String getServerName() {
		return serverName;
	}

	public static boolean isCombined() {
		return combined;
	}

	public static void setCombined(boolean combined) {
		GlobalContext.combined = combined;
	}

	public static Map<Integer, UseItemDto> getUseitemMap() {
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

	public static MemoryList getMemorylist() {
		return memoryList;
	}

	public static PresetDeckList getPresetdecklist() {
		return presetDeckList;
	}

	public static BasicDto getBasicInformation() {
		return basicInformation;
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

	/*----------------------------------------------------------------------------------------------------------------*/

	public static class MemoryList {
		public final ArrayList<AbstractMemory> memorys = new ArrayList<>();

		public void add(AbstractMemory memory) {
			if (memory instanceof BattleDto) {
				this.haveNewBattle = true;
				this.lastBattle = (BattleDto) memory;
			}
			this.memorys.add(memory);
		}

		//start battle		
		private BattleDto lastBattle = null;
		private boolean haveNewBattle = false;

		public boolean haveNewBattle() {
			return this.haveNewBattle;
		}

		public BattleDto getLastBattle() {
			this.haveNewBattle = false;
			return this.lastBattle;
		}
		//end
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

			//循环到预测时间段
			while (time1 >= this.ceil && time2 >= this.ceil) {
				time1 -= 3 * 60 * 1000;
				time2 -= 3 * 60 * 1000;
			}

			if (time2 <= this.floor) {
				//如果不在预测时间段内,不需要
			} else {//有交集
				this.notuse.add(new long[] { time1, time2 });
				this.update();
			}
		}

		private void update() {
			TreeSet<long[]> temps = new TreeSet<>((a, b) -> Long.compare(a[0], b[0]));
			temps.addAll(this.notuse);
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
		}

		private void update(long time1, long time2) {
			if (time1 >= this.floor && time2 <= this.ceil) {
				if (time1 == this.floor && time2 < this.ceil) {
					this.floor = time2;
				} else if (time1 > this.floor && time2 == this.ceil) {
					this.ceil = time1;
				} else {//时间段是预测时间段的子集,需收集之后整合					
					this.notuse.add(new long[] { time1, time2 });
				}
			} else {//只有交集				
				if (time1 >= this.floor) {
					this.ceil = time1;
				} else if (time2 <= this.ceil) {
					this.floor = time2;
				}
			}
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

	public static class FleetAkashiTimer {
		private final static int RESET_LIMIT = 20 * 60;
		private long time = -1;

		public void update(TrayMessageBox box, long currentTime) {
			if (this.time == -1) return;
			long rest = (currentTime - this.time) / 1000;
			ApplicationMain.main.getAkashiTimerLabel().setText(TimeString.toDateRestString(rest));
			if (rest == RESET_LIMIT) {
				if (Arrays.stream(deckRoom).map(DeckRoom::getDeck).filter(ToolUtils::isNotNull).anyMatch(DeckDtoTranslator::shouldNotifyAkashiTimer)) {
					box.add("泊地修理", "泊地修理已20分钟");
				}
			}
		}

		public void resetWhenPort() {
			if (this.time == -1) return;
			long currentTime = TimeString.getCurrentTime();
			if ((currentTime - this.time) / 1000 >= RESET_LIMIT) {
				this.time = currentTime;
			}
		}

		public void resetAkashiFlagshipWhenChange() {
			this.time = TimeString.getCurrentTime();
		}
	}

	public static class PresetDeckList {
		private int max = -1;
		public final PresetDeckDto[] decks = new PresetDeckDto[10];

		public PresetDeckDto add(PresetDeckDto deck) {
			this.decks[deck.getNo() - 1] = deck;
			return deck;
		}

		public void remove(int no) {
			this.decks[no - 1] = null;
		}

		public void init(JsonObject json) {
			Arrays.fill(this.decks, null);
			json.forEach((no, value) -> this.add(new PresetDeckDto(Integer.parseInt(no), (JsonObject) value)));
		}

		public int getMax() {
			return this.max;
		}

		public void setMax(int max) {
			this.max = max;
		}
	}

}
