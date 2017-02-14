package logbook.context.dto.data.record;

import logbook.context.dto.data.MaterialDto;

/**
 * 资源记录
 * @author MoeKagari
 */
public class MaterialRecordDto implements RecordDto {

	private final String description;
	private final long time;
	private final MaterialDto material;

	public MaterialRecordDto(String description, long time, MaterialDto material) {
		this.description = description;
		this.time = time;
		this.material = material;
	}

	public long getTime() {
		return this.time;
	}

	public MaterialDto getMaterial() {
		return this.material;
	}

	public String getDescription() {
		return this.description;
	}

}
