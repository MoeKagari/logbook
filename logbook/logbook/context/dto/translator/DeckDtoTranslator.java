package logbook.context.dto.translator;

import java.util.Arrays;

import logbook.config.AppConstants;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.update.GlobalContext;
import logbook.util.ToolUtils;

public class DeckDtoTranslator {

	public static int getZhikong(DeckDto deck) {
		return deck == null ? 0 : Arrays.stream(deck.getShips()).map(ShipDtoTranslator::getZhikong).sum();
	}

	public static int getSuodi(DeckDto deck) {
		return deck == null ? 0 : Arrays.stream(deck.getShips()).map(ShipDtoTranslator::getSuodi).sum();
	}

	public static int getTotalLv(DeckDto deck) {
		return deck == null ? 0 : Arrays.stream(deck.getShips()).mapToObj(GlobalContext::getShip).filter(ToolUtils::isNotNull).mapToInt(ShipDto::getLevel).sum();
	}

	public static boolean highspeed(DeckDto deck) {
		return deck == null ? true : Arrays.stream(deck.getShips()).mapToObj(GlobalContext::getShip).filter(ToolUtils::isNotNull).allMatch(ShipDtoTranslator::highspeed);
	}

	/**
	 * 1,2,3,4
	 */
	public static String[] getShipNames(int id) {
		return getShipNames(GlobalContext.getDeckRoom()[id - 1].getDeck());
	}

	public static String[] getShipNames(DeckDto deck) {
		if (deck != null) {
			return ToolUtils.toStringArray(deck.getShips(), id -> ToolUtils.notNullThenHandle(GlobalContext.getShip(id), ShipDtoTranslator::getName, ""));
		} else {
			return AppConstants.EMPTY_NAMES;
		}
	}

	public static boolean canAkashiRepair(DeckDto deck) {
		return deck == null ? false : Arrays.stream(deck.getShips()).anyMatch(ShipDtoTranslator::canAkashiRepair);
	}

	public static boolean isOnlyAkashi(DeckDto deck) {
		if (deck == null) return false;
		return isAkashiFlagship(deck) && Arrays.stream(deck.getShips()).filter(i -> i > 0).count() == 1;
	}

	public static boolean isAkashiFlagship(DeckDto deck) {
		return deck == null ? false : ShipDtoTranslator.isAkashi(deck.getShips()[0]);
	}

	public static int isShipInDeck(DeckDto deck, int id) {
		if (deck != null) {
			for (int index = 0; index < 6; index++) {
				if (deck.getShips()[index] != -1 && deck.getShips()[index] == id) {
					return index;
				}
			}
		}
		return -1;
	}

	public static boolean shouldNotifyAkashiTimer(DeckDto deck) {
		if (deck == null) return false;
		return (isInMission(deck) == false) && isAkashiFlagship(deck);
	}

	public static boolean isInMission(DeckDto deck) {
		return deck == null ? false : deck.getDeckMission().getState() != 0;
	}

	public static int needNotifyPL(DeckDto deck) {
		return deck == null ? -1 : Arrays.stream(deck.getShips()).map(ShipDtoTranslator::needNotifyPL).max().orElse(-1);
	}

	public static boolean hasDapo(DeckDto deck) {
		return deck == null ? false : Arrays.stream(deck.getShips()).anyMatch(ShipDtoTranslator::dapo);
	}

}
