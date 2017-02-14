package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.GlobalContext;
import logbook.context.dto.data.QuestDto;
import logbook.gui.logic.TableColumnManager;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.RecordTable;

/**
 * 所有任务
 * @author MoeKagari
 */
public class QuestTable extends RecordTable<QuestDto> {

	public QuestTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager<QuestDto>> tcms) {
		tcms.add(new TableColumnManager<>("No", rd -> rd.getInformation().getNo()));
		tcms.add(new TableColumnManager<>("状态", rd -> rd.getInformation().getStateString()));
		tcms.add(new TableColumnManager<>("任务名", rd -> rd.getInformation().getTitle()));
		tcms.add(new TableColumnManager<>("种类", rd -> rd.getInformation().getCategoryString()));
		tcms.add(new TableColumnManager<>("类型", rd -> rd.getInformation().getTypeString()));
		Function<Integer, Object> materialString = material -> material <= 0 ? "" : material;
		tcms.add(new TableColumnManager<>("油", rd -> materialString.apply(rd.getInformation().getMaterial()[0])));
		tcms.add(new TableColumnManager<>("弹", rd -> materialString.apply(rd.getInformation().getMaterial()[1])));
		tcms.add(new TableColumnManager<>("钢", rd -> materialString.apply(rd.getInformation().getMaterial()[2])));
		tcms.add(new TableColumnManager<>("铝", rd -> materialString.apply(rd.getInformation().getMaterial()[3])));
		tcms.add(new TableColumnManager<>("描述", rd -> rd.getInformation().getDetail()));
	}

	@Override
	protected List<QuestDto> getList() {
		return GlobalContext.getQuestlist();
	}

}
