package logbook.dto.word;

import java.io.Serializable;

import javax.json.JsonObject;

import logbook.dto.AbstractWord;

/**
 * 玩家所持装备
 * @author MoeKagari
 */
public class ItemDto extends AbstractWord implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int id;
	private final int slotitemId;
	private boolean isLocked;
	private int level;
	private int alv;

	public ItemDto(JsonObject json) {
		this.id = json.getInt("api_id");
		this.slotitemId = json.getInt("api_slotitem_id");
		this.isLocked = json.containsKey("api_locked") ? (json.getInt("api_locked") == 1) : false;
		this.level = json.containsKey("api_level") ? json.getInt("api_level") : 0;
		this.alv = json.containsKey("api_alv") ? json.getInt("api_alv") : -1;
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
