package logbook.internal;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolTip;

import logbook.gui.window.ApplicationMain;
import logbook.util.ToolUtils;

public class TrayMessageBox {
	private LinkedHashMap<String, String> title_notice = null;//延迟初始化到add时

	public void add(String title, String notice) {
		if (this.title_notice == null) this.title_notice = new LinkedHashMap<>();
		this.title_notice.put(title, ToolUtils.notNullThenHandle(this.title_notice.get(title), value -> value + "\n" + notice, notice));
	}

	public static void show(ApplicationMain main, TrayMessageBox box) {
		if (box.title_notice == null) return;
		if (box.title_notice.size() == 0) return;

		ToolUtils.notNullThenHandle(main.getTrayItem().getToolTip(), ToolTip::dispose);
		String text = StringUtils.join(box.title_notice.keySet(), "・");
		String message = StringUtils.join(box.title_notice.values(), "\r\n");
		{
			ToolTip tip = new ToolTip(main.getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
			tip.setAutoHide(true);
			tip.setText(text);
			tip.setMessage(message);
			main.getTrayItem().setToolTip(tip);
			tip.setVisible(true);
		}

		box.title_notice.clear();
	}
}
