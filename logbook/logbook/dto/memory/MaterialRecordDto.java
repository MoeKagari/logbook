package logbook.dto.memory;

import logbook.dto.AbstractMemory;
import logbook.dto.word.MaterialDto;
import logbook.util.ToolUtils;

/**
 * 资源记录
 * @author MoeKagari
 */
public class MaterialRecordDto extends AbstractMemory {
	private static final long serialVersionUID = 1L;
	private final String description;
	private final long time;
	private final int[] material;

	public MaterialRecordDto(String description, long time, MaterialDto material) {
		this.description = description;
		this.time = time;
		this.material = ToolUtils.arrayCopy(material.getMaterial());
	}

	@Override
	public long getTime() {
		return this.time;
	}

	public int[] getMaterial() {
		return this.material;
	}

	public String getDescription() {
		return this.description;
	}
}
