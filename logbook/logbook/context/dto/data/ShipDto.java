package logbook.context.dto.data;

import javax.json.JsonObject;

import logbook.gui.logic.TimeString;
import logbook.util.JsonUtils;

/**
 * 玩家所持舰娘
 * @author MoeKagari
 */
public class ShipDto {

	private final JsonObject json;
	/**
	 * 更新(PLTIME)是是否需要此ShipDto
	 */
	private boolean needForPLUpdate = true;
	private final long time = TimeString.getCurrentTime();

	private int id;
	private int lv;
	private int[] onSlot;
	private int[] slots;//长度5,最后位不明作用
	private int slotex;
	private boolean locked;
	private int cond;
	private int nowHp;
	private int maxHp;
	private int fuel;
	private int bull;
	private int ndockTime;
	private int soku;
	private int[] ndockCost;
	private int[] exp;
	private int[] luck;
	private int shipId;

	public ShipDto(JsonObject json) {
		this.json = json;

		this.id = this.json.getInt("api_id");
		this.lv = this.json.getInt("api_lv");
		this.onSlot = JsonUtils.getIntArray(this.json, "api_onslot");
		this.slots = JsonUtils.getIntArray(this.json, "api_slot");
		this.slotex = this.json.getInt("api_slot_ex");
		this.locked = this.json.getInt("api_locked") == 1;
		this.cond = this.json.getInt("api_cond");
		this.nowHp = this.json.getInt("api_nowhp");
		this.maxHp = this.json.getInt("api_maxhp");
		this.fuel = this.json.getInt("api_fuel");
		this.bull = this.json.getInt("api_bull");
		this.soku = this.json.getInt("api_soku");
		this.ndockTime = this.json.getInt("api_ndock_time");
		this.ndockCost = JsonUtils.getIntArray(this.json, "api_ndock_item");
		this.exp = JsonUtils.getIntArray(json, "api_exp");
		this.shipId = this.json.getInt("api_ship_id");
		this.luck = JsonUtils.getIntArray(json, "api_lucky");
	}

	/** 加入镇守府时的编号 */
	public int getId() {
		return this.id;
	}

	public int getShipId() {
		return this.shipId;
	}

	public int getLv() {
		return this.lv;
	}

	public int getCurrentExp() {
		return this.exp[0];
	}

	public int getNextExp() {
		return this.exp[1];
	}

	public double getCurrentExpPercent() {
		return this.exp[2];
	}

	public int getNowHp() {
		return this.nowHp;
	}

	public int getMaxHp() {
		return this.maxHp;
	}

	public int[] getSlots() {
		return this.slots;
	}

	public int getSlotex() {
		return this.slotex;
	}

	public int[] getOnSlot() {
		return this.onSlot;
	}

	public int getFuel() {
		return this.fuel;
	}

	public int getBull() {
		return this.bull;
	}

	public int getNdockTime() {
		return this.ndockTime;
	}

	public int[] getNdockCost() {
		return this.ndockCost;
	}

	public int getCond() {
		return this.cond;
	}

	public int getSoku() {
		return this.soku;
	}

	/**
	 		"api_kyouka": [37,
			51,
			34,
			36,
			0],
	 		"api_karyoku": [55,
			49],
			"api_raisou": [79,
			79],
			"api_taiku": [53,
			49],
			"api_soukou": [49,
			49],
			"api_kaihi": [88,
			89],
			"api_taisen": [70,
			59],
			"api_sakuteki": [38,
			39],
	 */

	public int getNowLuck() {
		return this.luck[0];
	}

	public int getMaxLuck() {
		return this.luck[1];
	}

	public boolean isLocked() {
		return this.locked;
	}

	/*----------------------------------------------------------------------------------------------------------------------*/

	public long getTime() {
		return this.time;
	}

	public boolean isNeedForPLUpdate() {
		return this.needForPLUpdate;
	}

	public void nyukyoEnd() {
		this.nowHp = this.maxHp;
		this.cond = this.cond < 40 ? 40 : this.cond;
		this.ndockCost = new int[2];
		this.needForPLUpdate = false;//入渠完毕,非自然恢复,更新(PLTIME)是,不需要此ship
	}

	public void updateWhenCharge(JsonObject jo) {
		this.fuel = jo.getInt("api_fuel");
		this.bull = jo.getInt("api_bull");
		this.onSlot = JsonUtils.getIntArray(jo, "api_onslot");
	}

	public int[] getNyukyoCost() {
		return new int[] { this.ndockCost[0], 0, this.ndockCost[1], 0, 0, 0, 0, 0 };
	}

	public void setLocked(boolean b) {
		this.locked = b;
	}

	/** 开放ex装备槽 */
	public void openSlotex() {
		//原先为0=未开放
		this.slotex = -1;
	}

	/** 交换装备 */
	public void slotExchange(int[] newSlots) {
		this.slots = newSlots;
	}

}
