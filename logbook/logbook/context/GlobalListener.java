package logbook.context;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import logbook.config.AppConfig;
import logbook.config.AppConstants;
import logbook.context.data.DataType;
import logbook.context.dto.data.BasicDto;
import logbook.gui.window.ApplicationMain;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

/**
 * 负责在{@link GlobalContext}更新数据之后更新一些界面
 * @author MoeKagari
 */
public class GlobalListener {

	private static final ApplicationMain main = ApplicationMain.main;

	public static void update(DataType type) {
		UpdateItemCountTask.update();
		UpdateShipCountTask.update();
		UpdateTitleTask.update();
		UpdateResourceTask.update();
	}

	//更新标题
	private static class UpdateTitleTask {
		public static void update() {
			Shell shell = main.getShell();
			if (shell.isDisposed()) return;

			String title = AppConstants.MAINWINDOWNAME;

			BasicDto basic = GlobalContext.getBasicInformation();
			if (basic != null && AppConfig.get().isShowNameOnTitle()) {
				String userName = basic.getUserName();
				if (userName != null) {
					title = userName + " - " + title;
				}
			}

			if (StringUtils.equals(shell.getText(), title) == false) {
				shell.setText(title);
			}
		}
	}

	//更新主面板的 所有舰娘 按钮
	private static class UpdateShipCountTask {
		public static void update() {
			if (main.getShell().isDisposed()) return;

			BasicDto basic = GlobalContext.getBasicInformation();
			Button shipList = main.getShipList();
			String setText = "舰娘(" + GlobalContext.getShipmap().size() + "/" + (basic == null ? 0 : basic.getMaxChara()) + ")";
			if (StringUtils.equals(shipList.getText(), setText) == false) shipList.setText(setText);
		}
	}

	//更新主面板的 所有装备 按钮
	private static class UpdateItemCountTask {
		public static void update() {
			if (main.getShell().isDisposed()) return;

			BasicDto basic = GlobalContext.getBasicInformation();
			Button itemList = main.getItemList();
			String setText = "装备(" + (GlobalContext.getItemMap().size() + 0) + "/" + (basic == null ? 0 : basic.getMaxSlotItem()) + ")";
			if (StringUtils.equals(itemList.getText(), setText) == false) itemList.setText(setText);
		}
	}

	//更新主面板的 资源
	private static class UpdateResourceTask {
		public static void update() {
			if (main.getShell().isDisposed()) return;
			if (GlobalContext.getCurrentMaterial() == null) return;

			Label[] resourceLabels = main.getResourceLabel();
			int[] resources = GlobalContext.getCurrentMaterial().getMaterialForWindow();

			ToolUtils.forEach(resourceLabels, resources, (label, resource) -> SwtUtils.setText(label, String.valueOf(resource)));
		}
	}

}
