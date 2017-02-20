package logbook.context.dto.data;

import java.util.Arrays;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.commons.lang3.StringUtils;

import logbook.context.dto.data.MasterDataDto.MasterMissionDataDto;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.util.JsonUtils;

/**
 * 舰队编成
 * @author MoeKagari
 */
public class DeckDto {
	private String name;
	private int[] ships;
	private DeckMissionDto deckMission;

	public DeckDto(JsonObject jo) {
		this.deckMission = new DeckMissionDto(jo.getJsonArray("api_mission"));
		this.name = jo.getString("api_name");
		this.ships = JsonUtils.getIntArray(jo, "api_ship");
	}

	/*-------------------------------------------------------------------------------------------*/

	public void change(int index, int shipId) {
		if (index == -1) { //除旗舰其余全解除
			this.ships = new int[] { this.ships[0], -1, -1, -1, -1, -1 };
			return;
		}

		int[] shipsTemp = Arrays.copyOf(this.ships, this.ships.length);

		int shipIndex = shipId != -1 ? this.isShipInDeck(shipId) : -1;
		if (shipIndex != -1) {//交换两艘船
			int temp = shipsTemp[index];
			shipsTemp[index] = shipsTemp[shipIndex];
			shipsTemp[shipIndex] = temp;
		} else {//替换某一艘船或者解除某一艘船(shipId=-1时)
			shipsTemp[index] = shipId;
		}

		//非 -1 提到前面
		int notnull = 0;
		for (int i = 0; i < shipsTemp.length; i++) {
			if (shipsTemp[i] == -1) continue;
			shipsTemp[notnull] = shipsTemp[i];
			notnull++;
		}
		for (int i = notnull; i < shipsTemp.length; i++) {
			shipsTemp[i] = -1;
		}

		this.ships = shipsTemp;
	}

	public int isShipInDeck(int shipId) {
		if (shipId != -1) {
			for (int i = 0; i < this.ships.length; i++) {
				if (this.ships[i] == shipId) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean shouldNotifyAkashiTimer() {
		return (this.isInMission() == false) && this.isAkashiFlagship();
	}

	public boolean isAkashiFlagship() {
		ShipDto flagship = GlobalContext.getShipmap().get(this.ships[0]);
		if (flagship != null) {
			String flagshipname = ShipDtoTranslator.getName(flagship);
			if (StringUtils.equals(flagshipname, "明石") || StringUtils.equals(flagshipname, "明石改")) {
				return true;
			}
		}
		return false;
	}

	public boolean isInMission() {
		return this.deckMission.getState() != 0;
	}

	/*-------------------------------------------------------------------------------------------*/

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DeckMissionDto getDeckMission() {
		return this.deckMission;
	}

	public int[] getShips() {
		return this.ships;
	}

	/*-------------------------------------------------------------------------------------------*/

	/** 舰队远征信息 */
	public static class DeckMissionDto {
		private final JsonArray json;
		private final String name;

		public DeckMissionDto(JsonArray json) {
			this.json = json;

			MasterDataDto mdd = GlobalContext.getMasterData();
			MasterMissionDataDto mmdd = mdd != null ? mdd.getMasterMissionDataMap().get(this.getId()) : null;
			this.name = mmdd != null ? mmdd.getName() : "";
		}

		public String getName() {
			return this.name;
		}

		//远征状态0=未出撃, 1=遠征中, 2=遠征帰投, 3=強制帰投中
		public int getState() {
			return this.json.getJsonNumber(0).intValue();
		}

		//远征id
		public int getId() {
			return this.json.getJsonNumber(1).intValue();
		}

		//归还时间
		public long getTime() {
			return this.json.getJsonNumber(2).longValue();
		}
	}

}
