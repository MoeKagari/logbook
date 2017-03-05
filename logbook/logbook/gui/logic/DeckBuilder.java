package logbook.gui.logic;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;

public class DeckBuilder {

	public static String build() {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("version", 4);
		for (int i = 0; i < 4; i++) {
			DeckDto deck = GlobalContext.getDeckRoom()[i].getDeck();
			if (deck != null) builder.add("f" + (i + 1), getFleetBuilder(deck));
		}

		return builder.build().toString();
	}

	private static JsonObjectBuilder getFleetBuilder(DeckDto deck) {
		JsonObjectBuilder fleetBuilder = Json.createObjectBuilder();

		for (int i = 0; i < 6; i++) {
			ShipDto ship = GlobalContext.getShipMap().get(deck.getShips()[i]);
			if (ship != null) fleetBuilder.add("s" + (i + 1), getShipBuilder(ship));
		}

		return fleetBuilder;
	}

	private static JsonObjectBuilder getShipBuilder(ShipDto ship) {
		JsonObjectBuilder shipBuilder = Json.createObjectBuilder();

		shipBuilder.add("id", String.valueOf(ship.getShipId()));
		shipBuilder.add("lv", ship.getLv());
		shipBuilder.add("luck", ship.getNowLuck());
		{
			JsonObjectBuilder itemsBuilder = Json.createObjectBuilder();
			for (int i = 0; i < 4; i++) {
				ItemDto item = GlobalContext.getItemMap().get(ship.getSlots()[i]);
				if (item != null) {
					itemsBuilder.add("i" + (i + 1), getItemBuilder(item));
				}
			}
			{
				ItemDto item = GlobalContext.getItemMap().get(ship.getSlotex());
				if (item != null) {
					itemsBuilder.add("ix", getItemBuilder(item));
				}
			}
			shipBuilder.add("items", itemsBuilder);
		}

		return shipBuilder;
	}

	private static JsonObjectBuilder getItemBuilder(ItemDto item) {
		JsonObjectBuilder itemBuilder = Json.createObjectBuilder();

		itemBuilder.add("id", item.getSlotitemId());
		itemBuilder.add("rf", item.getLevel());
		if (item.getAlv() > 0) itemBuilder.add("mas", item.getAlv());

		return itemBuilder;
	}

}
