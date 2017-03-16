package logbook.gui.window.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.swt.widgets.MenuItem;

import logbook.context.dto.data.QuestDto;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.gui.window.AbstractTable;
import logbook.gui.window.ApplicationMain;

/**
 * 所有任务
 * @author MoeKagari
 */
public class QuestListTable extends AbstractTable<QuestDto> {

	public QuestListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(ArrayList<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("No", true, rd -> rd.getInformation().getNo()));
		tcms.add(new TableColumnManager("状态", rd -> rd.getInformation().getStateString()));
		tcms.add(new TableColumnManager("进度", rd -> rd.getInformation().getProcess()));
		tcms.add(new TableColumnManager("任务名", rd -> rd.getInformation().getTitle()));
		tcms.add(new TableColumnManager("种类", rd -> rd.getInformation().getCategoryString()));
		tcms.add(new TableColumnManager("类型", rd -> rd.getInformation().getTypeString()));
		Function<Integer, Object> materialString = material -> material <= 0 ? "" : material;
		tcms.add(new TableColumnManager("油", true, rd -> materialString.apply(rd.getInformation().getMaterial()[0])));
		tcms.add(new TableColumnManager("弹", true, rd -> materialString.apply(rd.getInformation().getMaterial()[1])));
		tcms.add(new TableColumnManager("钢", true, rd -> materialString.apply(rd.getInformation().getMaterial()[2])));
		tcms.add(new TableColumnManager("铝", true, rd -> materialString.apply(rd.getInformation().getMaterial()[3])));
		tcms.add(new TableColumnManager("描述", rd -> rd.getInformation().getDetail()));
	}

	@Override
	protected void updateData(List<QuestDto> datas) {
		datas.addAll(GlobalContext.getQuestlist());
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return type == DataType.QUEST_CLEAR || type == DataType.QUEST_LIST || type == DataType.QUEST_START || type == DataType.QUEST_STOP;
	}

}
