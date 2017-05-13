package logbook.server.proxy;

import java.net.BindException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import logbook.config.AppConfig;
import logbook.gui.window.ApplicationMain;
import logbook.internal.LoggerHolder;

/**
 * プロキシサーバーです
 *
 */
public final class ProxyServer {

	private static final LoggerHolder LOG = new LoggerHolder(ProxyServer.class);

	private static Server server;

	private static String host;
	private static int port;
	private static String proxyHost;
	private static int proxyPort;

	public static void start() {
		try {
			server = new Server();
			updateSetting();
			setConnector();

			// httpsをプロキシできるようにConnectHandlerを設定
			ConnectHandler proxy = new ConnectHandler();
			server.setHandler(proxy);

			// httpはこっちのハンドラでプロキシ
			ServletContextHandler context = new ServletContextHandler(proxy, "/", ServletContextHandler.SESSIONS);
			ServletHolder holder = new ServletHolder(new CacheReverseProxyServlet());
			//holder = new ServletHolder(new ReverseProxyServlet());
			holder.setInitParameter("maxThreads", "256");
			holder.setInitParameter("timeout", "600000");
			context.addServlet(holder, "/*");

			try {
				server.start();
			} catch (Exception e) {
				handleException(e);
			}
		} catch (Exception e) {
			LOG.get().fatal("Proxyサーバーの起動に失敗しました", e);
			throw new RuntimeException(e);
		}
	}

	public static void restart() {
		try {
			if (server == null) {
				return;
			}
			if (updateSetting()) {
				server.stop();
				setConnector();
				server.start();
				ApplicationMain.main.printMessage("代理已再启动", false);
			}
		} catch (Exception e) {
			LOG.get().fatal("Proxyサーバーの起動に失敗しました", e);
			throw new RuntimeException(e);
		}
	}

	public static void end() {
		try {
			if (server != null) {
				server.stop();
				server.join();
				server = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * AppConfigの設定をローカルにコピーします。その際、更新があったか判定します。
	 * @return 更新があった
	 */
	private static boolean updateSetting() {
		String newHost = null;
		if (AppConfig.get().isAllowOnlyFromLocalhost() && AppConfig.get().isCloseOutsidePort()) {
			newHost = "localhost";
		}
		int newPort = AppConfig.get().getListenPort();
		String newProxyHost = null;
		int newProxyPort = 0;
		if (AppConfig.get().isUseProxy()) {
			newProxyHost = AppConfig.get().getProxyHost();
			newProxyPort = AppConfig.get().getProxyPort();
		}

		if (StringUtils.equals(newHost, host) && (newPort == port) && StringUtils.equals(newProxyHost, proxyHost) && (newProxyPort == proxyPort)) {
			return false;
		}

		host = newHost;
		port = newPort;
		proxyHost = newProxyHost;
		proxyPort = newProxyPort;
		return true;
	}

	private static void setConnector() {
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		connector.setHost(host);
		server.setConnectors(new Connector[] { connector });
	}

	private static void handleException(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("代理终了").append("\r\n");
		sb.append("例外 : " + e.getClass().getName()).append("\r\n");
		sb.append("原因 : " + e.getMessage()).append("\r\n");
		if (e instanceof BindException) {
			sb.append("很有可能是由于其他软件使用同一端口导致的").append("\r\n");
		}

		ApplicationMain.main.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox box = new MessageBox(ApplicationMain.main.getShell(), SWT.YES | SWT.ICON_ERROR);
				box.setText("代理终了");
				box.setMessage(sb.toString());
				box.open();
			}
		});
	}
}
