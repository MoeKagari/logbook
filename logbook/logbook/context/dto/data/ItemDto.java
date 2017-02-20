package logbook.context.dto.data;

import javax.json.JsonObject;

/**
 * 玩家所持装备
 * @author MoeKagari
 */
public class ItemDto {

	private final int id;
	private boolean isLocked;
	private int level;
	private int alv;
	private int slotitemId;

	public ItemDto(JsonObject json) {
		this.id = json.getInt("api_id");
		this.isLocked = json.getInt("api_locked") == 1;
		this.level = json.getInt("api_level");
		this.alv = json.getInt("api_alv", -1);
		this.slotitemId = json.getInt("api_slotitem_id");
	}

	public ItemDto(int id, int slotitemId, boolean isLocked, int level) {
		this.id = id;
		this.isLocked = isLocked;
		this.level = level;
		this.alv = -1;
		this.slotitemId = slotitemId;
	}

	public int getSlotitemId() {
		return this.slotitemId;
	}

	public int getAlv() {
		return this.alv;
	}

	/** 获得的第几个装备 */
	public int getId() {
		return this.id;
	}

	public boolean isLocked() {
		return this.isLocked;
	}

	public int getLevel() {
		return this.level;
	}

	/*-----------------------------------------------------------------------------------------*/

	public void slotItemLock(boolean b) {
		this.isLocked = b;
	}

}
