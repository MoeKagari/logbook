package logbook.context.dto.data.record;

/**
 * 建造记录
 * @author MoeKagari
 */
public class CreateshipDto implements RecordDto {

	private int[] mm;
	private long time;
	private int shipId;

	public CreateshipDto(int[] mm, long time) {
		this.mm = mm;
		this.time = time;
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

}
