package logbook.context.dto.translator;

import java.util.Arrays;

import logbook.config.AppConstants;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.util.ToolUtils;

public class DeckDtoTranslator {

	public static int getZhikong(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(ShipDtoTranslator::getZhikong).sum();
	}

	public static int getSuodi(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(ShipDtoTranslator::getSuodi).sum();
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
		if (deck != null) {
			return ToolUtils.toStringArray(deck.getShips(), ship -> ToolUtils.notNullThenHandle(GlobalContext.getShipMap().get(ship), ShipDtoTranslator::getName, ""));
		} else {
			return AppConstants.EMPTY_NAMES;
		}
	}

	public static boolean canAkashiRepair(DeckDto deck) {
		return Arrays.stream(deck.getShips()).anyMatch(ShipDtoTranslator::canAkashiRepair);
	}

	public static boolean isOnlyAkashi(DeckDto deck) {
		return isAkashiFlagship(deck) && Arrays.stream(deck.getShips()).filter(i -> i > 0).count() == 1;
	}

	public static boolean isAkashiFlagship(DeckDto deck) {
		return ShipDtoTranslator.isAkashi(deck.getShips()[0]);
	}

	public static int isShipInDeck(DeckDto deck, int shipId) {
		for (int index = 0; index < 6; index++) {
			if (deck.getShips()[index] != -1 && deck.getShips()[index] == shipId) {
				return index;
			}
		}
		return -1;
	}

	public static boolean shouldNotifyAkashiTimer(DeckDto deck) {
		return (isInMission(deck) == false) && isAkashiFlagship(deck);
	}

	public static boolean isInMission(DeckDto deck) {
		return deck.getDeckMission().getState() != 0;
	}

	public static int needNotifyPL(DeckDto deck) {
		return Arrays.stream(deck.getShips()).map(ShipDtoTranslator::needNotifyPL).max().orElse(-1);
	}

	/**
	 * 1,2,3,4
	 */
	public static boolean hasDapo(int id) {
		return hasDapo(GlobalContext.getDeckRoom()[id - 1].getDeck());
	}

	public static boolean hasDapo(DeckDto deck) {
		return deck == null ? false : Arrays.stream(deck.getShips()).anyMatch(ShipDtoTranslator::dapo);
	}

}
