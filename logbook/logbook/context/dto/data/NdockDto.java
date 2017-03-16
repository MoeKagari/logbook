package logbook.context.dto.data;

import javax.json.JsonObject;

import logbook.internal.TimerCounter;

/**
 * 渠位
 * @author MoeKagari
 */
public class NdockDto {

	private final JsonObject json;

	private final int state;
	private final int shipId;
	private final long time;
	private final TimerCounter timerCounter;

	public NdockDto(JsonObject json) {
		this.json = json;
		this.state = this.json.getInt("api_state");
		this.shipId = this.json.getInt("api_ship_id");
		this.time = this.json.getJsonNumber("api_complete_time").longValue();
		this.timerCounter = new TimerCounter(this.time, 60, false, -1);
	}

	public int getState() {
		return this.state;
	}

	public int getShipId() {
		return this.shipId;
	}

	public long getTime() {
		return this.time;
	}

	public TimerCounter getTimerCounter() {
		return this.timerCounter;
	}
}
