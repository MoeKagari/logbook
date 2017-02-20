package logbook.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public final class SwtUtils {

	private final static int DPI_BASE = 96;
	private final static Point DPI = Display.getDefault().getDPI();

	public static int DPIAwareWidth(int width) {
		return (width * DPI.x) / DPI_BASE;
	}

	public static int DPIAwareHeight(int height) {
		return (height * DPI.y) / DPI_BASE;
	}

	public static Point DPIAwareSize(Point size) {
		return new Point(DPIAwareWidth(size.x), DPIAwareHeight(size.y));
	}

	public static GridLayout makeGridLayout(int numColumns, int horizontalSpacing, int verticalSpacing, int marginWidth, int marginHeight) {
		GridLayout gl = new GridLayout(numColumns, false);
		gl.horizontalSpacing = horizontalSpacing;
		gl.verticalSpacing = verticalSpacing;
		gl.marginWidth = marginWidth;
		gl.marginHeight = marginHeight;
		return gl;
	}

	public static GridLayout makeGridLayout(int numColumns, int horizontalSpacing, int verticalSpacing, int marginWidth, int marginHeight, int marginTop, int marginBottom) {
		GridLayout gl = new GridLayout(numColumns, false);
		gl.horizontalSpacing = horizontalSpacing;
		gl.verticalSpacing = verticalSpacing;
		gl.marginWidth = marginWidth;
		gl.marginHeight = marginHeight;
		gl.marginTop = marginTop;
		gl.marginBottom = marginBottom;
		return gl;
	}

	public static void layoutCompositeRecursively(Composite composite) {
		for (org.eclipse.swt.widgets.Control control : composite.getChildren()) {
			if (control instanceof Composite) {
				layoutCompositeRecursively((Composite) control);
			}
		}
		composite.layout();
	}

	public static void initLabel(Label label, String text, GridData gd) {
		label.setText(text);
		label.setLayoutData(gd);
	}

	public static void initLabel(Label label, String text, GridData gd, Color background) {
		label.setText(text);
		label.setLayoutData(gd);
		if (background != null) label.setBackground(background);
	}

	public static void initLabel(Label label, String text, GridData gd, int width) {
		gd.widthHint = SwtUtils.DPIAwareWidth(width);
		initLabel(label, text, gd);
	}

	public static void insertBlank(Composite composite, int width) {
		initLabel(new Label(composite, SWT.NONE), "", new GridData(), width);
	}

	public static void insertBlank(Composite composite) {
		initLabel(new Label(composite, SWT.NONE), "", new GridData(GridData.FILL_HORIZONTAL));
	}

	public static void insertHSeparator(Composite composite) {
		Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public static void insertHSeparator(Composite composite, GridData gd) {
		Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(gd);
	}

	public static void insertVSeparator(Composite composite) {
		Label separator = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		separator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	}

	public static void insertVSeparator(Composite composite, GridData gd) {
		Label separator = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		separator.setLayoutData(gd);
	}

	public static void initControl(Control control, GridData gd, int width) {
		gd.widthHint = SwtUtils.DPIAwareWidth(width);
		control.setLayoutData(gd);
	}

	public static void initControl(Control control, GridData gd) {
		control.setLayoutData(gd);
	}

	public static void setText(Label label, String text) {
		if (!text.equals(label.getText())) {
			label.setText(text);
		}
	}

	public static void setToolTipText(Label label, String text) {
		if (!text.equals(label.getToolTipText())) {
			label.setToolTipText(text);
		}
	}

}
