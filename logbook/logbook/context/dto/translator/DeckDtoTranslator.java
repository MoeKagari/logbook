package logbook.context.dto.translator;

import java.util.Arrays;

import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;

public class DeckDtoTranslator {

	public static int getZhikong(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(shipId -> {
			ShipDto ship = GlobalContext.getShipmap().get(shipId);
			return ship == null ? 0 : ShipDtoTranslator.getZhikong(ship);
		}).sum();
	}

	public static int getSuodi(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(shipId -> {
			ShipDto ship = GlobalContext.getShipmap().get(shipId);
			return ship == null ? 0 : ShipDtoTranslator.getSuodi(ship);
		}).sum();
	}

	public static int getTotalLv(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(shipId -> {
			ShipDto ship = GlobalContext.getShipmap().get(shipId);
			return ship == null ? 0 : ship.getLv();
		}).sum();
	}

	public static boolean highspeed(DeckDto deck) {
		return Arrays.stream(deck.getShips()).mapToObj(shipId -> GlobalContext.getShipmap().get(shipId)).filter(ship -> ship != null).allMatch(ShipDtoTranslator::highspeed);
	}

}
