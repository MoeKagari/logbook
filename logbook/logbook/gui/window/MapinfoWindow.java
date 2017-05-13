package logbook.gui.window;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;

import logbook.dto.word.MapinfoDto.OneMap;
import logbook.update.GlobalContext;
import logbook.update.data.DataType;
import logbook.util.SwtUtils;
import logbook.util.ToolUtils;

/**
 * 地图详情
 * @author MoeKagari
 */
public class MapinfoWindow extends WindowBase {
	private final ScrolledComposite sc;
	private final Composite contentComposite;

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
		if (type == DataType.MAPINFO) {
			this.updateWindowRedraw(this.getShell().isVisible(), this::updateWindow);
		}
	}

	private void updateWindow() {
		ToolUtils.forEach(this.contentComposite.getChildren(), Control::dispose);
		ToolUtils.notNullThenHandle(GlobalContext.getMapinfo(), mapinfo -> mapinfo.getMaps().forEach(this::updateOneMap));
		this.contentComposite.layout();
	}

	private void updateOneMap(OneMap map) {
		int[] hp = map.getHP();
		int now = hp[0], max = hp[1];
		if (now != 0) {
			StringBuilder text = new StringBuilder(map.getArea() + "-" + map.getNo());
			if (map.isEventMap()) {
				String rank = map.getEventMap().getRank();
				if (StringUtils.isNotBlank(rank)) {
					text.append("-").append(rank);
				}

				String hptype = map.getEventMap().getHptype();
				if (StringUtils.isNotBlank(hptype)) {
					text.append(",").append(hptype);
				}
			}
			text.append(":").append("[" + now + "," + max + "]");

			this.newOneMapComposite(now, max, text.toString());
		}
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
	protected void handlerBeforeDisplay() {
		this.updateWindowRedraw(this::updateWindow);
	}

	@Override
	protected void handlerAfterHidden() {
		ToolUtils.forEach(this.contentComposite.getChildren(), Control::dispose);
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

}
