package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.dto.data.MaterialDto;
import logbook.context.dto.data.record.MaterialRecordDto;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

/**
 * 资源记录
 * @author MoeKagari
 */
public class MaterialRecordTable extends RecordTable<MaterialRecordDto> {

	public MaterialRecordTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<MaterialRecordDto>> tcms) {
		tcms.add(new TableColumnManager<>("描述", MaterialRecordDto::getDescription));
		tcms.add(new TableColumnManager<>("日期", rd -> TimeString.timeToStringForTable(rd.getTime())));
		String[] materialStrings = MaterialDto.getMaterialStrings();
		for (int i = 0; i < materialStrings.length; i++) {
			final int index = i;
			tcms.add(new TableColumnManager<>(materialStrings[i], rd -> TimeString.timeToStringForTable(rd.getMaterial().getMaterial()[index])));
		}
	}

	@Override
	protected List<MaterialRecordDto> getList() {
		return GlobalContext.getMaterialRecord();
	}

}
