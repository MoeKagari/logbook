package logbook.internal;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Label;

import logbook.config.AppConfig;
import logbook.config.AppConstants;
import logbook.context.GlobalContext;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.DeckDto.DeckMissionDto;
import logbook.context.dto.data.NdockDto;
import logbook.context.dto.data.ShipDto;
import logbook.gui.logic.TimeString;
import logbook.gui.logic.data.MissionMap;
import logbook.gui.window.ApplicationMain;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class SyncExecApplicationMain extends Thread {
	private static final LoggerHolder LOG = new LoggerHolder(SyncExecApplicationMain.class);
	private final ApplicationMain main;

	public SyncExecApplicationMain(ApplicationMain main) {
		this.main = main;
		this.setDaemon(true);
		this.setName("Thread_AsyncExecApplicationMain");
	}

	@Override
	public void run() {
		try {
			long nextUpdateTime = 0;
			while (true) {
				long currentTime = TimeString.getCurrentTime();
				this.main.getDisplay().asyncExec(() -> {
					TrayMessageBox box = new TrayMessageBox();
					UpdateNewDayConsole.update(this.main, currentTime);
					UpdateDeckNdockTask.update(this.main, box, currentTime);
					UpdateFleetTask.update(this.main, box, currentTime);
					TrayMessageBox.show(this.main, box);
				});
				if (nextUpdateTime <= currentTime) nextUpdateTime = currentTime;
				nextUpdateTime += TimeUnit.SECONDS.toMillis(1);
				Thread.sleep(nextUpdateTime - currentTime);
			}
		} catch (Exception e) {
			LOG.get().fatal(this.getName() + "进程异常终止", e);
			throw new RuntimeException(e);
		}
	}

	//new day时,在console输出
	private static class UpdateNewDayConsole {
		private static boolean haveUpdated = false;

		public static void update(ApplicationMain main, long currentTime) {
			switch (AppConstants.CONSOLE_TIME_FORMAT.format(new Date(currentTime))) {
				case "23:59:59":
				case "00:00:00"://为了防止误差,应该±1秒
				case "00:00:01":
					if (!haveUpdated) main.printNewDay(currentTime + TimeUnit.HOURS.toMillis(1));
					haveUpdated = true;
					break;
				default:
					haveUpdated = false;
					break;
			}
		}
	}

	//更新主面板的 远征和入渠
	private static class UpdateDeckNdockTask {
		public static void update(ApplicationMain main, TrayMessageBox box, long currentTime) {
			if (main.getShell().isDisposed()) return;
			updateDeck(main, box, currentTime);
			updateNdock(main, box, currentTime);
		}

		private static void updateDeck(ApplicationMain main, TrayMessageBox box, long currentTime) {
			Label[] nameLabels = main.getDeckNameLabel();
			Label[] timeLabels = main.getDeckTimeLabel();

			for (int i = 0; i < 4; i++) {
				DeckDto deck = GlobalContext.getDeckRoom()[i].getDeck();
				if (deck == null) continue;

				DeckMissionDto dmd = deck.getDeckMission();
				String nameLabelText = "", timeLabelText = "", timeLabelTooltipText = "";
				if (dmd.getState() != 0) {
					long rest = (dmd.getTime() - currentTime) / 1000;
					if (rest == 1 * 60) {
						box.add("远征", AppConstants.DEFAULT_FLEET_NAME[i] + "-远征已归还");
					} else if (AppConfig.get().isNoticeDeckmissionAgain() && (rest < 0 && rest % 120 == 0)) {
						box.add("远征", AppConstants.DEFAULT_FLEET_NAME[i] + "-远征已归还★");
					}

					nameLabelText = MissionMap.get(dmd.getId());
					timeLabelText = TimeString.toDateRestString(rest, "远征已归还");
					timeLabelTooltipText = AppConstants.DECK_NDOCK_COMPLETE_TIME_FORMAT.format(dmd.getTime());
				}

				SwtUtils.setText(nameLabels[i], nameLabelText);
				SwtUtils.setText(timeLabels[i], timeLabelText);
				SwtUtils.setToolTipText(timeLabels[i], timeLabelTooltipText);
			}
		}

		private static void updateNdock(ApplicationMain main, TrayMessageBox box, long currentTime) {
			Label[] nameLabels = main.getNdockNameLabel();
			Label[] timeLabels = main.getNdockTimeLabel();

			for (int i = 0; i < 4; i++) {
				NdockDto ndock = GlobalContext.getNyukyoRoom()[i].getNdock();
				if (ndock == null) continue;

				String nameLabelText = "", timeLabelText = "", timeLabelTooltipText = "";
				if (ndock.getState() == 1) {
					ShipDto ship = GlobalContext.getShipmap().get(ndock.getShipId());
					if (ship != null) {
						long rest = (ndock.getTime() - currentTime) / 1000;
						if (rest == 1 * 60) {
							box.add("入渠", ship.getName() + "(Lv." + ship.getLv() + ")" + "-入渠已完了");
						}

						nameLabelText = ship.getName() + "(Lv." + ship.getLv() + ")";
						timeLabelText = TimeString.toDateRestString(rest, "入渠已完了");
						timeLabelTooltipText = AppConstants.DECK_NDOCK_COMPLETE_TIME_FORMAT.format(ndock.getTime());
					}
				}

				SwtUtils.setText(nameLabels[i], nameLabelText);
				SwtUtils.setText(timeLabels[i], timeLabelText);
				SwtUtils.setToolTipText(timeLabels[i], timeLabelTooltipText);
			}
		}
	}

	//更新 FleetWindow 的 akashiTimer
	private static class UpdateFleetTask {
		public static void update(ApplicationMain main, TrayMessageBox box, long currentTime) {
			if (main.getShell().isDisposed()) return;

			ToolUtils.forEach(main.getFleetWindows(), fw -> fw.getAkashiTimer().update(box, currentTime));
			ToolUtils.forEach(main.getFleetWindowOuts(), fwo -> fwo.getFleetWindow().getAkashiTimer().update(box, currentTime));
			ToolUtils.forEach(main.getFleetWindowAll().getFleetWindows(), fw -> fw.getAkashiTimer().update(box, currentTime));
		}
	}

}
