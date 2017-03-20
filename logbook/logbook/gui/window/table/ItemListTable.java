package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.ItemDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.translator.ItemDtoTranslator;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.util.ToolUtils;

/**
 * 所有装备
 * @author MoeKagari
 */
public class ItemListTable extends AbstractTable<ItemListTable.SortItem> {

	public ItemListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("装备", SortItem::getName));
		tcms.add(new TableColumnManager("种类", SortItem::getType));
		IntFunction<String> levelString = level -> level > 0 ? String.valueOf(level) : "";
		tcms.add(new TableColumnManager("改修", true, rd -> levelString.apply(rd.getLevel())));
		tcms.add(new TableColumnManager("熟练度", true, rd -> levelString.apply(rd.getAlv())));
		tcms.add(new TableColumnManager("个数", true, SortItem::getCount));
		tcms.add(new TableColumnManager("装备着的舰娘", rd -> rd.getWhichShipWithItem()));
	}

	@Override
	protected void updateData(List<SortItem> datas) {
		Function<ItemDto, ShipDto> whichShipWithItem = item -> {
			for (ShipDto ship : GlobalContext.getShipMap().values()) {
				if (Arrays.stream(ArrayUtils.addAll(ship.getSlots(), ship.getSlotex())).anyMatch(slot -> slot == item.getId())) {
					return ship;
				}
			}
			return null;
		};

		GlobalContext.getItemMap().values().stream().collect(Collectors.groupingBy(ItemDto::getSlotitemId)).forEach((slotitemId, nameResult) -> {
			nameResult.stream().collect(Collectors.groupingBy(ItemDto::getLevel)).forEach((level, levelResult) -> {
				levelResult.stream().collect(Collectors.groupingBy(ItemDto::getAlv)).forEach((alv, alvResult) -> {
					ArrayList<String> sb = new ArrayList<>();
					alvResult.stream().map(whichShipWithItem).filter(ToolUtils::isNotNull).collect(Collectors.toMap(ship -> ship, ship -> 1, Integer::sum)).forEach((ship, count) -> {
						sb.add(String.format("%s(Lv.%d)(%d)", ShipDtoTranslator.getName(ship), ship.getLv(), count.intValue()));
					});
					datas.add(new SortItem(alvResult.size(), level, alv, slotitemId, StringUtils.join(sb, ",")));
				});
			});
		});
	}

	public class SortItem {
		private int count;
		private int level;
		private int alv;
		private String type;
		private String name;
		private String whichShipWithItem;

		public SortItem(int count, int level, int alv, int slotitemId, String whichShipWithItem) {
			this.count = count;
			this.level = level;
			this.alv = alv;
			this.type = ItemDtoTranslator.getTypeString(slotitemId);
			this.name = ItemDtoTranslator.getName(slotitemId);
			this.whichShipWithItem = whichShipWithItem;
		}

		public String getType() {
			return this.type;
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
