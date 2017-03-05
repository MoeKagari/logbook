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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import logbook.config.AppConfig;
import logbook.gui.window.ApplicationMain;
import logbook.internal.LoggerHolder;

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

			ConnectHandler proxy = new ConnectHandler();
			server.setHandler(proxy);

			ServletContextHandler context = new ServletContextHandler(proxy, "/", ServletContextHandler.SESSIONS);
			ServletHolder holder = new ServletHolder(new ReverseProxyServlet());
			holder.setInitParameter("maxThreads", "256");
			holder.setInitParameter("timeout", "600000");
			context.addServlet(holder, "/*");

			try {
				server.start();
			} catch (Exception e) {
				handleException(e);
			}
		} catch (Exception e) {
			LOG.get().fatal("Proxy 服务器启动失败", e);
			throw new RuntimeException(e);
		}
	}

	public static void restart() {
		try {
			if (updateSetting()) {
				server.stop();
				setConnector();
				server.start();
				ApplicationMain.main.logPrint("Proxy服务器再启动");
			}
		} catch (Exception e) {
			LOG.get().fatal("Proxy服务器再启动失败", e);
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

	private static boolean updateSetting() {
		String newHost = null;
		int newPort = AppConfig.get().getListenPort();
		if (AppConfig.get().isAllowOnlyFromLocalhost() && AppConfig.get().isCloseOutsidePort()) {
			newHost = "localhost";
		}

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
		sb.append("代理服务器异常终了");
		sb.append("\r\n").append("例外 : " + e.getClass().getName());
		sb.append("\r\n").append("原因 : " + e.getMessage());
		if (e instanceof BindException) sb.append("\r\n").append("可能是由于其它程序使用同一个端口");

		Display.getDefault().asyncExec(() -> {
			Shell shell = new Shell(Display.getDefault(), SWT.TOOL);
			MessageBox mes = new MessageBox(shell, SWT.YES | SWT.ICON_ERROR);
			mes.setText("");
			mes.setMessage(sb.toString());
			mes.open();
			shell.dispose();
		});
	}

}
