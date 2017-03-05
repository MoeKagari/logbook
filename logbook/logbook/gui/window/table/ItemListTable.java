package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.translator.ItemDtoTranslator;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.table.ItemListTable.ItemSort;

/**
 * 所有装备
 * @author MoeKagari
 */
public class ItemListTable extends AbstractTable<ItemSort> {

	public ItemListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("装备", ItemSort::getName));
		tcms.add(new TableColumnManager("改修等级", rd -> {
			int level = rd.getLevel();
			return level > 0 ? level : "";
		}));
		tcms.add(new TableColumnManager("熟练度", rd -> {
			int alv = rd.getAlv();
			return alv > 0 ? alv : "";
		}));
		tcms.add(new TableColumnManager("个数", ItemSort::getCount));
		tcms.add(new TableColumnManager("装备着的舰娘", rd -> rd.getWhichShipWithItem()));
	}

	@Override
	protected List<ItemSort> getList() {
		Function<ItemDto, ShipDto> whichShipWithItem = item -> {
			int id = item.getId();
			for (ShipDto ship : GlobalContext.getShipMap().values()) {
				for (int slot : ship.getSlots()) {
					if (slot == id) {
						return ship;
					}
				}
				if (ship.getSlotex() == id) {
					return ship;
				}
			}
			return null;
		};

		List<ItemSort> items = new ArrayList<>();
		GlobalContext.getItemMap().values().stream().collect(Collectors.groupingBy(ItemDto::getSlotitemId)).forEach((slotitemId, nameResult) -> {
			nameResult.stream().collect(Collectors.groupingBy(ItemDto::getLevel)).forEach((level, levelResult) -> {
				levelResult.stream().collect(Collectors.groupingBy(ItemDto::getAlv)).forEach((alv, alvResult) -> {
					Map<ShipDto, Integer> shipWithItemCount = new HashMap<>();
					alvResult.forEach(item -> {
						ShipDto ship = whichShipWithItem.apply(item);
						Integer count = shipWithItemCount.get(ship);
						if (count == null) {
							shipWithItemCount.put(ship, 1);
						} else {
							shipWithItemCount.put(ship, count + 1);
						}
					});

					ArrayList<String> sb = new ArrayList<>();
					shipWithItemCount.forEach((ship, count) -> {
						if (ship != null) {
							sb.add(ShipDtoTranslator.getName(ship) + "(Lv." + ship.getLv() + ")" + "(" + count + ")");
						}
					});
					items.add(new ItemSort(alvResult.size(), level, alv, slotitemId, StringUtils.join(sb, ",")));
				});
			});
		});
		return items;
	}

	public class ItemSort {
		private int count;
		private int level;
		private int alv;
		private String name;
		private String whichShipWithItem;

		public ItemSort(int count, int level, int alv, int slotitemId, String whichShipWithItem) {
			this.count = count;
			this.level = level;
			this.alv = alv;
			this.name = ItemDtoTranslator.getName(slotitemId);
			this.whichShipWithItem = whichShipWithItem;
		}

		public String getWhichShipWithItem() {
			return this.whichShipWithItem;
		}

		public int getCount() {
			return this.count;
		}

		public int getLevel() {
			return this.level;
		}

		public int getAlv() {
			return this.alv;
		}

		public String getName() {
			return this.name;
		}

	}

}
