package logbook.internal;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Label;

import logbook.config.AppConstants;
import logbook.context.dto.data.DeckDto;
import logbook.context.dto.data.DeckDto.DeckMissionDto;
import logbook.context.dto.data.MaterialDto;
import logbook.context.dto.data.NdockDto;
import logbook.context.dto.data.ShipDto;
import logbook.context.dto.data.record.MaterialRecordDto;
import logbook.context.dto.translator.ShipDtoTranslator;
import logbook.context.update.GlobalContext;
import logbook.gui.logic.TimeString;
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

					UpdateMaterialRecord.update(this.main, currentTime);
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

	public static class UpdateMaterialRecord {
		//2017-2-21 0:00:00
		//1487606400000
		private static final TimerCounter timerCounter = new TimerCounter(1487606400000L, 30 * 60);

		public static void update(ApplicationMain main, long currentTime) {
			if (timerCounter.needNotify(currentTime)) {
				MaterialDto currentMaterial = GlobalContext.getCurrentMaterial();
				if (currentMaterial != null) {
					GlobalContext.getMaterialRecord().add(new MaterialRecordDto("定时记录", currentTime, currentMaterial));
				}
			}
		}
	}

	//new day时,在console输出
	private static class UpdateNewDayConsole {
		//2017-2-21 0:00:00
		//1487606400000
		private static final TimerCounter timerCounter = new TimerCounter(1487606400000L, 24 * 60 * 60);

		public static void update(ApplicationMain main, long currentTime) {
			if (timerCounter.needNotify(currentTime)) {
				main.printNewDay(currentTime + TimeUnit.HOURS.toMillis(1));
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
					if (dmd.getTimerCounter().needNotify(currentTime)) {
						box.add("远征", AppConstants.DEFAULT_FLEET_NAME[i] + "-远征已归还");
					}

					nameLabelText = dmd.getName();
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
					ShipDto ship = GlobalContext.getShipMap().get(ndock.getShipId());
					String name = ShipDtoTranslator.getName(ship);
					if (ship != null) {
						long rest = (ndock.getTime() - currentTime) / 1000;
						if (ndock.getTimerCounter().needNotify(currentTime)) {
							box.add("入渠", name + "(Lv." + ship.getLv() + ")" + "-入渠已完了");
						}

						nameLabelText = name + "(Lv." + ship.getLv() + ")";
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
