package logbook.internal;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Label;

import logbook.config.AppConfig;
import logbook.config.AppConstants;
import logbook.dto.memory.MaterialRecordDto;
import logbook.dto.translator.ShipDtoTranslator;
import logbook.dto.word.DeckDto;
import logbook.dto.word.DeckDto.DeckMissionDto;
import logbook.dto.word.MaterialDto;
import logbook.dto.word.NdockDto;
import logbook.dto.word.ShipDto;
import logbook.gui.logic.TimeString;
import logbook.gui.window.ApplicationMain;
import logbook.gui.window.WindowBase;
import logbook.update.GlobalContext;
import logbook.update.GlobalContext.PLTime;
import logbook.utils.SwtUtils;
import logbook.utils.ToolUtils;

public class AsyncExecApplicationMain extends Thread {
	private static final LoggerHolder LOG = new LoggerHolder(AsyncExecApplicationMain.class);
	private final ApplicationMain main;

	public AsyncExecApplicationMain(ApplicationMain main) {
		this.main = main;
		this.setDaemon(true);
		this.setName("Thread_AsyncExecApplicationMain");
	}

	@Override
	public void run() {
		try {
			long nextUpdateTime = 0;
			while (true) {
				final long currentTime = TimeString.getCurrentTime();
				if (this.main.getDisplay().isDisposed() == false) this.main.getDisplay().asyncExec(() -> {
					TrayMessageBox box = new TrayMessageBox();

					UpdateMaterialRecord.update(this.main, currentTime);
					UpdateNewDayConsole.update(this.main, currentTime);
					UpdateDeckNdockTask.update(this.main, box, currentTime);
					ToolUtils.notNull(GlobalContext.getAkashiTimer(), akashiTimer -> akashiTimer.update(box, currentTime));
					ToolUtils.forEach(this.main.getWindows(), WindowBase::storeWindowConfig);

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
		private static final TimerCounter timerCounter = new TimerCounter(1487606400000L, -1, true, 30 * 60);

		public static void update(ApplicationMain main, long currentTime) {
			if (timerCounter.needNotify(currentTime)) {
				MaterialDto currentMaterial = GlobalContext.getCurrentMaterial();
				if (currentMaterial != null) {
					GlobalContext.getMemorylist().add(new MaterialRecordDto("定时记录", currentTime, currentMaterial));
				}
			}
		}
	}

	/** 新的一天时,在console输出 */
	private static class UpdateNewDayConsole {
		//2017-2-21 0:00:00
		//1487606400000
		private static final TimerCounter timerCounter = new TimerCounter(1487606400000L, -1, true, 24 * 60 * 60);

		public static void update(ApplicationMain main, long currentTime) {
			if (timerCounter.needNotify(currentTime)) {
				main.printNewDay(currentTime + TimeUnit.HOURS.toMillis(1));//姑且加上一个小时
			}
		}
	}

	//更新主面板的 远征(或者疲劳)和入渠
	private static class UpdateDeckNdockTask {
		public static void update(ApplicationMain main, TrayMessageBox box, long currentTime) {
			if (main.getShell().isDisposed()) return;

			main.getDeckGroup().setRedraw(false);
			updateDeck(main, box, currentTime);
			main.getDeckGroup().setRedraw(true);

			main.getNdockGroup().setRedraw(false);
			updateNdock(main, box, currentTime);
			main.getNdockGroup().setRedraw(true);
		}

		private static void updateDeck(ApplicationMain main, TrayMessageBox box, long currentTime) {
			Label[] nameLabels = main.getDeckNameLabel();
			Label[] timeLabels = main.getDeckTimeLabel();

			for (int i = 0; i < 4; i++) {
				DeckDto deck = GlobalContext.deckRoom[i].getDeck();
				if (deck == null) continue;

				String nameLabelText = "", timeLabelText = "", timeLabelTooltipText = "";
				DeckMissionDto dmd = deck.getDeckMission();
				if (dmd.getState() != 0) {
					long rest = (dmd.getTime() - currentTime) / 1000;
					if (dmd.getTimerCounter().needNotify(currentTime) && (//
					(AppConfig.get().isNoticeDeckmission() && rest >= 0) ||//
							(AppConfig.get().isNoticeDeckmissionAgain() && rest < 0)//
					)) {
						box.add("远征", String.format("%s-远征已归还", AppConstants.DEFAULT_FLEET_NAME[i]));
					}

					nameLabelText = dmd.getName();
					timeLabelText = TimeString.toDateRestString(rest, "远征已归还");
					if (rest > 24 * 60 * 60) {//超过24小时,显示日期
						timeLabelTooltipText = AppConstants.DECK_NDOCK_COMPLETE_TIME_FORMAT_LONG.format(dmd.getTime());
					} else {
						timeLabelTooltipText = AppConstants.DECK_NDOCK_COMPLETE_TIME_FORMAT.format(dmd.getTime());
					}
				} else {//疲劳回复时间
					int pl = Arrays.stream(deck.getShips()).mapToObj(GlobalContext::getShip).filter(ToolUtils::isNotNull).mapToInt(ShipDto::getCond).min().orElse(Integer.MAX_VALUE);
					if (pl < AppConfig.get().getNoticeCondWhen()) {
						PLTime PLTIME = GlobalContext.getPLTIME();
						if (PLTIME != null) {
							int count = (AppConfig.get().getNoticeCondWhen() - pl - 1) / 3 + 1;
							long end = PLTIME.getTime() + 3 * 60 * 1000 * ((deck.getTime() - PLTIME.getTime() - 1) / (3 * 60 * 1000) + count);
							long rest = (end - currentTime) / 1000;
							if (rest == 0 && AppConfig.get().isNoticeCond()) {
								if (AppConfig.get().isNoticeCondOnlyMainFleet() && i != 0) {
									//只通知第一舰队,并且此deck非第一舰队
								} else {
									box.add("疲劳", String.format("%s-疲劳已恢复", AppConstants.DEFAULT_FLEET_NAME[i]));
								}
							}
							nameLabelText = String.format("疲劳恢复中(±%d秒)", (PLTIME.getRange() / 1000));
							timeLabelText = TimeString.toDateRestString(rest, "疲劳已恢复");
							timeLabelTooltipText = AppConstants.DECK_NDOCK_COMPLETE_TIME_FORMAT.format(end);
						}
					}
				}

				SwtUtils.setText(nameLabels[i], nameLabelText);
				SwtUtils.setText(timeLabels[i], timeLabelText);
				SwtUtils.setToolTipText(timeLabels[i], timeLabelTooltipText);

				SwtUtils.setText((Label) timeLabels[i].getData(), timeLabelText);
				SwtUtils.setToolTipText((Label) timeLabels[i].getData(), timeLabelTooltipText);
			}
		}

		private static void updateNdock(ApplicationMain main, TrayMessageBox box, long currentTime) {
			Label[] nameLabels = main.getNdockNameLabel();
			Label[] timeLabels = main.getNdockTimeLabel();

			for (int i = 0; i < 4; i++) {
				NdockDto ndock = GlobalContext.nyukyoRoom[i].getNdock();
				if (ndock == null) continue;

				String nameLabelText = "", timeLabelText = "", timeLabelTooltipText = "";
				if (ndock.getState() == 1) {
					ShipDto ship = GlobalContext.getShip(ndock.getShipId());
					if (ship != null) {
						String name = ShipDtoTranslator.getName(ship);
						long rest = (ndock.getTime() - currentTime) / 1000;
						if (AppConfig.get().isNoticeNdock() && ndock.getTimerCounter().needNotify(currentTime)) {
							box.add("入渠", String.format("%s(Lv.%d)-入渠已完了", name, ship.getLevel()));
						}

						nameLabelText = String.format("%s(Lv.%d)", name, ship.getLevel());
						timeLabelText = TimeString.toDateRestString(rest, "入渠已完了");
						timeLabelTooltipText = AppConstants.DECK_NDOCK_COMPLETE_TIME_FORMAT.format(ndock.getTime());
					}
				}

				SwtUtils.setText(nameLabels[i], nameLabelText);
				SwtUtils.setText(timeLabels[i], timeLabelText);
				SwtUtils.setToolTipText(timeLabels[i], timeLabelTooltipText);

				SwtUtils.setText((Label) timeLabels[i].getData(), timeLabelText);
				SwtUtils.setToolTipText((Label) timeLabels[i].getData(), timeLabelTooltipText);
			}
		}
	}

}
