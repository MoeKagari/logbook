package logbook.context.dto.data.record;

import logbook.context.dto.data.ShipDto;
import logbook.context.dto.translator.ShipDtoTranslator;

/**
 * 建造记录
 * @author MoeKagari
 */
public class CreateshipDto implements RecordDto {

	private String flagship = null;
	private int flagshipLevel = -1;
	private int emptyCount = -1;
	private int[] mm;
	private long time;
	private int shipId;

	public CreateshipDto(ShipDto secretary, int[] mm, long time) {
		if (secretary != null) {
			this.flagship = ShipDtoTranslator.getName(secretary.getShipId());
			this.flagshipLevel = secretary.getLevel();
		}
		this.mm = mm;
		this.time = time;
	}

	public String getFlagship() {
		return this.flagship;
	}

	public int getFlagshipLevel() {
		return this.flagshipLevel;
	}

	public void setShipId(int shipId) {
		this.shipId = shipId;
	}

	public long getTime() {
		return this.time;
	}

	public int getShipId() {
		return this.shipId;
	}

	public boolean largeflag() {
		return this.mm[0] >= 1000;
	}

	public boolean highspeed() {
		return this.mm[4] != 0;
	}

	public int zhicai() {
		return this.mm[6];
	}

	public int[] cost() {
		return new int[] { this.mm[0], this.mm[1], this.mm[2], this.mm[3] };
	}

	public int getEmptyCount() {
		return this.emptyCount;
	}

	public void setEmptyCount(int emptyCount) {
		this.emptyCount = emptyCount;
	}
}
