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

import logbook.dto.translator.MasterDataTranslator;
import logbook.dto.translator.ShipDtoTranslator;
import logbook.dto.word.ItemDto;
import logbook.dto.word.MasterDataDto.MasterSlotitemDto;
import logbook.dto.word.ShipDto;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.update.GlobalContext;
import logbook.utils.ToolUtils;

/**
 * 所有装备
 * @author MoeKagari
 */
public class ItemListTable extends AbstractTable<ItemListTable.SortItem> {
	public ItemListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("装备", SortItem::getName));
		{
			TableColumnManager tcm = new TableColumnManager("种类", SortItem::getTypeString);
			tcm.setComparator((a, b) -> {
				int res = 0;
				int[] type_a = a.getType();
				int[] type_b = b.getType();
				for (int i = 0; i < type_a.length; i++) {
					res = Integer.compare(type_a[i], type_b[i]);
					if (res != 0) return res;
				}
				return res;
			});
			tcms.add(tcm);
		}
		IntFunction<String> levelString = level -> level > 0 ? String.valueOf(level) : "";
		tcms.add(new TableColumnManager("改修", true, rd -> levelString.apply(rd.level)));
		tcms.add(new TableColumnManager("熟练度", true, rd -> levelString.apply(rd.alv)));
		tcms.add(new TableColumnManager("个数", true, SortItem::getCount));
		tcms.add(new TableColumnManager("装备着的舰娘", SortItem::getWhichShipWithItem));
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
						sb.add(String.format("%s(Lv.%d)(%d)", ShipDtoTranslator.getName(ship), ship.getLevel(), count.intValue()));
					});
					datas.add(new SortItem(alvResult.size(), level, alv, StringUtils.join(sb, ","), MasterDataTranslator.getMasterSlotitemDto(slotitemId)));
				});
			});
		});
	}

	public class SortItem {
		private int count;
		private int level;
		private int alv;
		private String whichShipWithItem;
		private MasterSlotitemDto msdd;

		public SortItem(int count, int level, int alv, String whichShipWithItem, MasterSlotitemDto msdd) {
			this.count = count;
			this.level = level;
			this.alv = alv;
			this.msdd = msdd;
			this.whichShipWithItem = whichShipWithItem;
		}

		public int[] getType() {
			return ToolUtils.notNull(this.msdd, MasterSlotitemDto::getType, null);
		}

		public String getTypeString() {
			return ToolUtils.notNull(this.getType(), Arrays::toString, "");
		}

		public String getWhichShipWithItem() {
			return this.whichShipWithItem;
		}

		public int getCount() {
			return this.count;
		}

		public String getName() {
			return ToolUtils.notNull(this.msdd, MasterSlotitemDto::getName, "");
		}
	}
}
