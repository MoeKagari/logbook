package logbook.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import logbook.internal.LoggerHolder;

public class AppConfig implements Serializable {
	private static final LoggerHolder LOG = new LoggerHolder(AppConfig.class);
	private static final long serialVersionUID = 1L;
	private static AppConfig config = null;
	private static final File file = new File(AppConstants.APPCONFIGSFILEPATH);

	public static AppConfig get() {
		if (config == null) config = new AppConfig();
		return config;
	}

	public static void load() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			Object obj = ois.readObject();
			if (obj instanceof AppConfig) {
				config = (AppConfig) obj;
			}
		} catch (Exception e) {
			LOG.get().warn("app配置读取失败", e);
		}
	}

	public static void store() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(config);
		} catch (Exception e) {
			LOG.get().warn("app配置保存失败", e);
		}
	}

	private int listenPort = 8889;
	private boolean useProxy = true;
	private String proxyHost = "127.0.0.1";
	private int proxyPort = 8888;
	private boolean allowOnlyFromLocalhost = true;
	private boolean closeOutsidePort = true;

	private boolean noticeDeckmission = true;
	private boolean noticeNdock = true;
	private boolean noticeAkashi = true;
	private boolean noticeCond = true;
	private int noticeCondWhen = 40;
	private boolean noticeCondOnlyMainFleet = false;

	private boolean showEventMapHPInConsole = true;
	private boolean showNameOnTitle = true;
	private boolean checkDoit = true;
	private boolean minimizedToTray = false;
	private boolean notCalcuExpForLevel99Ship = true;
	private boolean noticeDeckmissionAgain = true;
	private boolean autoUpdateBattleFlow = true;

	public boolean isNoticeDeckmission() {
		return this.noticeDeckmission;
	}

	public void setNoticeDeckmission(boolean noticeDeckmission) {
		this.noticeDeckmission = noticeDeckmission;
	}

	public boolean isNoticeNdock() {
		return this.noticeNdock;
	}

	public void setNoticeNdock(boolean noticeNdock) {
		this.noticeNdock = noticeNdock;
	}

	public boolean isNoticeAkashi() {
		return this.noticeAkashi;
	}

	public void setNoticeAkashi(boolean noticeAkashi) {
		this.noticeAkashi = noticeAkashi;
	}

	public boolean isNoticeCond() {
		return this.noticeCond;
	}

	public void setNoticeCond(boolean noticeCond) {
		this.noticeCond = noticeCond;
	}

	public boolean isNoticeCondOnlyMainFleet() {
		return this.noticeCondOnlyMainFleet;
	}

	public void setNoticeCondOnlyMainFleet(boolean noticeCondOnlyMainFleet) {
		this.noticeCondOnlyMainFleet = noticeCondOnlyMainFleet;
	}

	public boolean isAllowOnlyFromLocalhost() {
		return this.allowOnlyFromLocalhost;
	}

	public void setAllowOnlyFromLocalhost(boolean allowOnlyFromLocalhost) {
		this.allowOnlyFromLocalhost = allowOnlyFromLocalhost;
	}

	public boolean isCloseOutsidePort() {
		return this.closeOutsidePort;
	}

	public void setCloseOutsidePort(boolean closeOutsidePort) {
		this.closeOutsidePort = closeOutsidePort;
	}

	public int getListenPort() {
		return this.listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public boolean isUseProxy() {
		return this.useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public String getProxyHost() {
		return this.proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isShowNameOnTitle() {
		return this.showNameOnTitle;
	}

	public void setShowNameOnTitle(boolean showNameOnTitle) {
		this.showNameOnTitle = showNameOnTitle;
	}

	public boolean isCheckDoit() {
		return this.checkDoit;
	}

	public void setCheckDoit(boolean checkDoit) {
		this.checkDoit = checkDoit;
	}

	public boolean isNotCalcuExpForLevel99Ship() {
		return this.notCalcuExpForLevel99Ship;
	}

	public void setNotCalcuExpForLevel99Ship(boolean notCalcuExpForLevel99Ship) {
		this.notCalcuExpForLevel99Ship = notCalcuExpForLevel99Ship;
	}

	public boolean isNoticeDeckmissionAgain() {
		return this.noticeDeckmissionAgain;
	}

	public void setNoticeDeckmissionAgain(boolean noticeDeckmissionAgain) {
		this.noticeDeckmissionAgain = noticeDeckmissionAgain;
	}

	public boolean isShowEventMapHPInConsole() {
		return this.showEventMapHPInConsole;
	}

	public void setShowEventMapHPInConsole(boolean showEventMapHPInConsole) {
		this.showEventMapHPInConsole = showEventMapHPInConsole;
	}

	public boolean isMinimizedToTray() {
		return this.minimizedToTray;
	}

	public void setMinimizedToTray(boolean minimizedToTray) {
		this.minimizedToTray = minimizedToTray;
	}

	public boolean isAutoUpdateBattleFlow() {
		return this.autoUpdateBattleFlow;
	}

	public void setAutoUpdateBattleFlow(boolean autoUpdateBattleFlow) {
		this.autoUpdateBattleFlow = autoUpdateBattleFlow;
	}

	public int getNoticeCondWhen() {
		return this.noticeCondWhen;
	}

	public void setNoticeCondWhen(int noticeCondWhen) {
		this.noticeCondWhen = noticeCondWhen;
	}

}
