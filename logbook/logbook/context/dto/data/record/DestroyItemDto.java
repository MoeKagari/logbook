package logbook.context.dto.data.record;

import logbook.context.dto.data.ItemDto;
import logbook.context.dto.translator.ItemDtoTranslator;

/**
 * 废弃装备
 * @author MoeKagari
 */
public class DestroyItemDto implements RecordDto {

	private final long time;
	private final String event;
	private final int id;
	private final String name;
	private final int lv;
	private final int alv;
	private final int group;

	public DestroyItemDto(long time, String event, ItemDto item, int group) {
		this.time = time;
		this.event = event;
		this.id = item.getId();
		this.name = ItemDtoTranslator.getName(item);
		this.lv = item.getLevel();
		this.alv = item.getAlv();
		this.group = group;
	}

	public int getLv() {
		return this.lv;
	}

	public int getAlv() {
		return this.alv;
	}

	public long getTime() {
		return this.time;
	}

	public String getEvent() {
		return this.event;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getGroup() {
		return this.group;
	}

}
