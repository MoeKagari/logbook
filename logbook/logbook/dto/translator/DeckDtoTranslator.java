package logbook.dto.translator;

import java.util.Arrays;
import java.util.function.Predicate;

import logbook.config.AppConstants;
import logbook.dto.word.DeckDto;
import logbook.dto.word.ShipDto;
import logbook.update.GlobalContext;
import logbook.util.ToolUtils;

public class DeckDtoTranslator {

	/**
	 * 1,2,3,4
	 */
	public static String[] getShipNames(int id) {
		return getShipNames(GlobalContext.deckRoom[id - 1].getDeck());
	}

	public static String[] getShipNames(DeckDto deck) {
		if (deck != null) {
			return ToolUtils.toStringArray(deck.getShips(), id -> ToolUtils.notNullThenHandle(GlobalContext.getShip(id), ShipDtoTranslator::getName, ""));
		} else {
			return AppConstants.EMPTY_NAMES;
		}
	}

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

	public static int isShipInDeck(DeckDto deck, int id) {
		if (deck != null) {
			for (int index = 0; index < 6; index++) {
				int ship = deck.getShips()[index];
				if (ship != -1 && ship == id) {
					return index;
				}
			}
		}
		return -1;
	}

	public static boolean isAkashiFlagship(DeckDto deck) {
		return deck == null ? false : ShipDtoTranslator.isAkashi(deck.getShips()[0]);
	}

	/** 泊地修理到点时,是否应该提醒 */
	public static boolean shouldNotifyAkashiTimer(DeckDto deck) {
		if (deck == null) return false;
		//远征中
		if (isInMission(deck)) return false;
		//非明石旗舰
		if (isAkashiFlagship(deck) == false) return false;

		//没有入渠,擦伤小破,可以修理
		Predicate<ShipDto> can = ship -> !ShipDtoTranslator.isInNyukyo(ship) && ShipDtoTranslator.healthyState(ship);
		//中破大破入渠中,不能修理		
		Predicate<ShipDto> cannot = ship -> ShipDtoTranslator.isInNyukyo(ship) || ShipDtoTranslator.terribleState(ship);

		ShipDto flagship = GlobalContext.getShip(deck.getShips()[0]);
		//明石中破大破入渠中,不能修理其它舰娘
		if (cannot.test(flagship)) return false;

		//修理数(2+修理设施)
		int equipCount = 2 + (int) Arrays.stream(flagship.getSlots()).filter(ItemDtoTranslator::isRepairItem).count();
		return Arrays.stream(deck.getShips()).limit(equipCount).mapToObj(GlobalContext::getShip).anyMatch(can);
	}

	public static boolean isInMission(DeckDto deck) {
		return deck == null ? false : deck.getDeckMission().getState() != 0;
	}

	public static boolean hasDapo(DeckDto deck) {
		return deck == null ? false : Arrays.stream(deck.getShips()).anyMatch(ShipDtoTranslator::dapo);
	}

}
