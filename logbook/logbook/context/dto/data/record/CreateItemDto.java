package logbook.context.dto.data.record;

import java.util.Arrays;

/**
 * 开发记录
 * @author MoeKagari
 */
public class CreateItemDto implements RecordDto {

	private final long time;
	private final boolean success;
	private final int[] material;
	private final int slotitemId;

	public CreateItemDto(long time, boolean success, int[] material, int slotitemId) {
		this.time = time;
		this.success = success;
		this.material = Arrays.copyOf(material, 4);
		this.slotitemId = slotitemId;
	}

	public long getTime() {
		return this.time;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public int[] getMaterial() {
		return this.material;
	}

	public int getSlotitemId() {
		return this.slotitemId;
	}

}
