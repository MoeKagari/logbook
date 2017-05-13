package logbook.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;

import logbook.internal.LoggerHolder;

public class WindowConfig implements Serializable {
	private static final LoggerHolder LOG = new LoggerHolder(WindowConfig.class);
	private static final long serialVersionUID = 1L;
	private static Map<String, WindowConfig> ALLWINDOWCONFIGS = new HashMap<>();
	private static final File file = new File(AppConstants.WINDOWCONFIGS_FILEPATH);

	public static Map<String, WindowConfig> get() {
		return ALLWINDOWCONFIGS;
	}

	public static void load() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			Object obj = ois.readObject();
			if (obj instanceof Map) {
				((Map<?, ?>) obj).forEach((key, value) -> {
					if (key instanceof String && value instanceof WindowConfig) {
						ALLWINDOWCONFIGS.put((String) key, (WindowConfig) value);
					}
				});
			}
		} catch (Exception e) {
			LOG.get().warn("windows配置读取失败", e);
		}
	}

	public static void store() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(ALLWINDOWCONFIGS);
		} catch (Exception e) {
			LOG.get().warn("windows配置保存失败", e);
		}
	}

	private Point location = new Point(0, 0);
	private Point size = new Point(0, 0);
	private boolean visible = false;
	private boolean minimized = false;

	public Point getLocation() {
		return this.location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Point getSize() {
		return this.size;
	}

	public void setSize(Point size) {
		this.size = size;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean getMinimized() {
		return this.minimized;
	}

	public void setMinimized(boolean minimized) {
		this.minimized = minimized;
	}
}
