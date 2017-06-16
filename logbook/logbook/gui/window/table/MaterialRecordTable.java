package logbook.gui.window.table;

import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.dto.memory.MaterialRecordDto;
import logbook.dto.word.MaterialDto;
import logbook.gui.logic.TimeString;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;
import logbook.update.GlobalContext;

/**
 * 资源记录
 * @author MoeKagari
 */
public class MaterialRecordTable extends AbstractTable<MaterialRecordDto> {

	public MaterialRecordTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("描述", MaterialRecordDto::getDescription));
		tcms.add(new TableColumnManager("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		String[] materialStrings = MaterialDto.getMaterialStrings();
		for (int i = 0; i < materialStrings.length; i++) {
			final int index = i;
			tcms.add(new TableColumnManager(materialStrings[i], true, rd -> rd.getMaterial()[index]));
		}
	}

	@Override
	protected void updateData(List<MaterialRecordDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof MaterialRecordDto) {
				datas.add((MaterialRecordDto) memory);
			}
		});
	}
}
