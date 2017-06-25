package logbook.update;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import logbook.config.AppConfig;
import logbook.config.AppConstants;
import logbook.dto.word.BasicDto;
import logbook.dto.word.MapinfoDto;
import logbook.dto.word.MapinfoDto.OneMap;
import logbook.dto.word.MaterialDto;
import logbook.gui.window.ApplicationMain;
import logbook.update.data.DataType;
import logbook.utils.SwtUtils;
import logbook.utils.ToolUtils;

/**
 * 负责在{@link GlobalContext}更新数据之后更新一些界面
 * @author MoeKagari
 */
public class GlobalListener {
	public static void update(DataType type) {
		if (ApplicationMain.main.getShell().isDisposed() == false) {
			UpdateCountTask.update();
			UpdateTitleTask.update();
			UpdateResourceTask.update();

			//print活动海域HP到console
			if (type == DataType.MAPINFO) {
				MapinfoDto mapinfo = GlobalContext.getMapinfo();
				if (mapinfo != null) {
					mapinfo.getMaps().stream().filter(OneMap::isEventMap).filter(map -> map.getHP()[0] != 0).forEach(map -> {
						int now = map.getHP()[0];
						int max = map.getHP()[1];
						String message = String.format("%d-%d-%s: [%d,%d]", map.getArea(), map.getNo(), map.getEventMap().getRank(), now, max);
						ApplicationMain.main.printMessage(message, false);
					});
				}
			}
		}
	}

	//更新标题 
	private static class UpdateTitleTask {
		public static void update() {
			Shell shell = ApplicationMain.main.getShell();
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
	//更新主面板的 所有装备 按钮
	private static class UpdateCountTask {
		public static void update() {
			String text;
			BasicDto basic = GlobalContext.getBasicInformation();
			if (basic != null) {
				Button shipList = ApplicationMain.main.getShipList();
				text = String.format("舰娘(%d/%d)", GlobalContext.getShipMap().size(), basic.getMaxChara());
				if (StringUtils.equals(shipList.getText(), text) == false) {
					shipList.setText(text);
				}

				Button itemList = ApplicationMain.main.getItemList();
				text = String.format("装备(%d/%d)", GlobalContext.getItemMap().size(), basic.getMaxSlotItem());
				if (StringUtils.equals(itemList.getText(), text) == false) {
					itemList.setText(text);
				}
			}
		}
	}

	//更新主面板的 资源
	private static class UpdateResourceTask {
		public static void update() {
			MaterialDto currentMaterial = GlobalContext.getCurrentMaterial();
			if (currentMaterial != null) {
				Label[] resourceLabels = ApplicationMain.main.getResourceLabel();
				int[] resources = currentMaterial.getMaterialForWindow();

				ToolUtils.forEach(resourceLabels, resources, (label, resource) -> SwtUtils.setText(label, String.valueOf(resource)));
			}
		}
	}

}
