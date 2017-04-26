package logbook.context.update.room;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.context.dto.data.DeckDto;
import logbook.context.dto.translator.DeckDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.Data;
import logbook.util.ToolUtils;

public class DeckRoom extends Room {
	private final int id;
	private DeckDto deck = null;

	public DeckRoom(int id) {
		this.id = id;
	}

	/*---------------------------------------------------------------------------------------------------------*/

	public void doDeck(Data data, JsonValue json) {
		try {
			for (JsonValue value : (JsonArray) json) {
				JsonObject jo = (JsonObject) value;
				if (jo.getInt("api_id") == this.id) {
					this.deck = new DeckDto(jo);
					break;
				}
			}
		} catch (Exception e) {
			this.getLog().get().warn("doDeck" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doChange(Data data, JsonValue json) {
		try {
			if (this.deck == null) return;
			if (Integer.parseInt(data.getField("api_id")) != this.id) return;//变更舰队,1,2,3,4

			int index = Integer.parseInt(data.getField("api_ship_idx"));//变更位置,0开始
			int shipId = Integer.parseInt(data.getField("api_ship_id"));
			this.deck.change(index, shipId);

			if (DeckDtoTranslator.isAkashiFlagship(this.deck) && index != -1) {//变更之后明石旗舰,并且不是[随伴舰一括解除]
				ToolUtils.ifHandle(GlobalContext.getAkashiTimer(), ToolUtils::isNull, GlobalContext::setAkashiTimer);
				GlobalContext.getAkashiTimer().resetAkashiFlagshipWhenChange();
			}
		} catch (Exception e) {
			this.getLog().get().warn("doChange" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doUpdatedeckname(Data data, JsonValue json) {
		try {
			if (this.deck == null) return;
			if (Integer.parseInt(data.getField("api_deck_id")) != this.id) return;//1,2,3,4

			this.deck.setName(data.getField("api_name"));
		} catch (Exception e) {
			this.getLog().get().warn("doUpdatedeckname" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	public void doPresetSelect(Data data, JsonValue json) {
		try {
			JsonObject jo = (JsonObject) json;
			if (jo.getInt("api_id") == this.id) {
				this.deck = new DeckDto(jo);
			}
		} catch (Exception e) {
			this.getLog().get().warn("doPresetSelect" + this.id + "处理错误", e);
			this.getLog().get().warn(data);
		}
	}

	/*---------------------------------------------------------------------------------------------------------*/

	public DeckDto getDeck() {
		return this.deck;
	}

}
