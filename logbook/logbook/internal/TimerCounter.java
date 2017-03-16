package logbook.internal;

public class TimerCounter {

	private final long endTime;
	private final int advance;//提前
	private final int interval;//再次提醒间隔
	private final boolean notifyZero;//计时器到0是否通知
	private boolean haveUpdatedAdvance = false;
	private boolean haveUpdated = false;
	private boolean haveUpdatedAgain = false;

	public TimerCounter(long endTime, int advance, boolean notifyZero, int interval) {
		this.endTime = endTime;
		this.advance = advance;
		this.notifyZero = notifyZero;
		this.interval = interval;
	}

	public boolean needNotify(long currentTime) {
		long space = (this.endTime - currentTime) / 1000;
		if (this.advance > 0 && (space == this.advance - 1 || space == this.advance || space == this.advance + 1)) {
			if (this.haveUpdatedAdvance == false) {
				this.haveUpdatedAdvance = true;
				return true;
			}
		} else if (this.notifyZero && (space == -1 || space == 0 || space == 1)) {
			if (this.haveUpdated == false) {
				this.haveUpdated = true;
				return true;
			}
		} else if (this.interval > 0 && space < 0) {
			long spaceInterval = (-1 * space) % this.interval;
			if (spaceInterval == 0 || spaceInterval == 1 || spaceInterval == this.interval - 1) {
				if (this.haveUpdatedAgain == false) {
					this.haveUpdatedAgain = true;
					return true;
				}
			} else {
				this.haveUpdatedAgain = false;
			}
		}
		return false;
	}

}
