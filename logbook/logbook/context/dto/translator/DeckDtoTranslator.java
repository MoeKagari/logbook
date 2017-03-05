package logbook.context.dto.translator;

import java.util.Arrays;

import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.util.ToolUtils;

public class DeckDtoTranslator {

	public static int getZhikong(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(shipId -> ToolUtils.notNullThenHandle(GlobalContext.getShipMap().get(shipId), ShipDtoTranslator::getZhikong, 0)).sum();
	}

	public static int getSuodi(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(shipId -> ToolUtils.notNullThenHandle(GlobalContext.getShipMap().get(shipId), ShipDtoTranslator::getSuodi, 0)).sum();
	}

	public static int getTotalLv(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(shipId -> ToolUtils.notNullThenHandle(GlobalContext.getShipMap().get(shipId), ShipDto::getLv, 0)).sum();
	}

	public static boolean highspeed(DeckDto deck) {
		return Arrays.stream(deck.getShips()).mapToObj(shipId -> GlobalContext.getShipMap().get(shipId)).filter(ship -> ship != null).allMatch(ShipDtoTranslator::highspeed);
	}

	/**
	 * 1,2,3,4
	 */
	public static String[] getShipNames(int id) {
		return getShipNames(GlobalContext.getDeckRoom()[id - 1].getDeck());
	}

	public static String[] getShipNames(DeckDto deck) {
		String[] names = new String[6];
		if (deck != null) {
			int[] ships = deck.getShips();
			for (int i = 0; i < 6; i++) {
				names[i] = ToolUtils.notNullThenHandle(GlobalContext.getShipMap().get(ships[i]), ShipDtoTranslator::getName, null);
			}
		}
		return names;
	}

	/*-------------------------------------------------------------------------------------*/

}
