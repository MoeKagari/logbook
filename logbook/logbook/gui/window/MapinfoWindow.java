package logbook.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;

import logbook.context.dto.data.MapinfoDto;
import logbook.context.dto.data.MapinfoDto.EventMap;
import logbook.context.update.GlobalContext;
import logbook.context.update.data.DataType;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

public class MapinfoWindow extends WindowBase {

	private ScrolledComposite sc;
	private Composite contentComposite;

	public MapinfoWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		this.sc = new ScrolledComposite(this.getComposite(), SWT.V_SCROLL);
		this.sc.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.sc.setExpandHorizontal(true);
		this.sc.setExpandVertical(true);
		this.sc.setAlwaysShowScrollBars(true);

		this.contentComposite = new Composite(this.sc, SWT.NONE);
		this.contentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 8, 0, 0, 5, 5));
		this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.sc.setContent(this.contentComposite);
	}

	@Override
	public void update(DataType type) {
		if (type == DataType.MAPINFO && this.isVisible()) {
			this.updateWindow();
		}
	}

	private void updateWindow() {
		this.getShell().setRedraw(false);
		ToolUtils.forEach(this.contentComposite.getChildren(), Control::dispose);

		MapinfoDto mapinfo = GlobalContext.getMapinfo();
		if (mapinfo != null) mapinfo.getMaps().forEach(map -> {
			int now = 0, max = 0;

			if (map.isExboss() && map.isClear() == false && map.getDefeatCount() != -1) {
				max = map.getMaxCount();
				now = max - map.getDefeatCount();
			} else if (map.isEventMap()) {
				EventMap eventMap = map.getEventMap();
				max = eventMap.getMaxhp();
				now = eventMap.getNowhp();
			}

			if (now != 0) {
				StringBuilder text = new StringBuilder(map.getArea() + "-" + map.getNo());
				text.append(",").append(map.isEventMap() ? map.getEventMap().getRank() : "");
				text.append(",").append(map.isEventMap() ? map.getEventMap().getHptype() : "");
				text.append(",").append(now + "/" + max);
				this.newOneMapComposite(now, max, text.toString());
			}
		});

		this.contentComposite.layout();
		this.getShell().setRedraw(true);
	}

	private void newOneMapComposite(int now, int max, String text) {
		Composite oneMapComposite = new Composite(this.contentComposite, SWT.NONE);
		oneMapComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		oneMapComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ProgressBar pb = new ProgressBar(oneMapComposite, SWT.HORIZONTAL | SWT.SMOOTH);
		pb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		pb.setMinimum(0);
		pb.setMaximum(max);
		pb.setSelection(now);

		SwtUtils.initLabel(new Label(oneMapComposite, SWT.CENTER), text, new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) this.updateWindow();
		super.setVisible(visible);
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

}
