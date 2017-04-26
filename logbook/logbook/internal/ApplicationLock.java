package logbook.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import logbook.config.AppConstants;

public class ApplicationLock {
	private static final File LOCK_FILE = new File(AppConstants.LOCKFILEPATH);
	private static final LoggerHolder LOG = new LoggerHolder(ApplicationLock.class);

	private FileOutputStream fos;
	private FileChannel fchan;
	private FileLock flock;
	private boolean isError;

	public ApplicationLock() {
		try {
			File dir = LOCK_FILE.getParentFile();
			if (!dir.exists()) dir.mkdirs();

			this.fos = new FileOutputStream(LOCK_FILE);
			this.fchan = this.fos.getChannel();
			this.flock = this.fchan.tryLock();
			this.isError = false;
		} catch (IOException e) {
			this.isError = true;
			LOG.get().warn("filelock error", e);
		}
	}

	public boolean isLocked() {
		return (this.flock != null);
	}

	public boolean isError() {
		return this.isError;
	}

	public void release() {
		try {
			if (this.flock != null) this.flock.release();
			this.fchan.close();
			this.fos.close();
			if (this.flock != null) LOCK_FILE.delete();
		} catch (IOException e) {
			LOG.get().warn("释放锁文件发生错误", e);
		}
	}
}
