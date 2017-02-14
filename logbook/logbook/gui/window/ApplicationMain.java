package logbook.gui.window;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import logbook.config.AppConfig;
import logbook.config.AppConstants;
import logbook.gui.listener.ControlSelectionListener;
import logbook.gui.listener.TrayItemMenuListener;
import logbook.gui.logic.TimeString;
import logbook.gui.window.table.CreateItemTable;
import logbook.gui.window.table.CreateShipTable;
import logbook.gui.window.table.DestroyItemTable;
import logbook.gui.window.table.DestroyShipTable;
import logbook.gui.window.table.ItemListTable;
import logbook.gui.window.table.MaterialRecordTable;
import logbook.gui.window.table.MissionResultTable;
import logbook.gui.window.table.QuestTable;
import logbook.gui.window.table.ShipListTable;
import logbook.internal.ApplicationLock;
import logbook.internal.LoggerHolder;
import logbook.internal.ShutdownHookThread;
import logbook.internal.SyncExecApplicationMain;
import logbook.server.proxy.ProxyServer;
import logbook.util.SwtUtils;

public class ApplicationMain {

	public static void main(String[] args) {
		//多重启动检查
		if (applicationLockCheck()) return;
		//启动程序
		startLogbook();
	}

	private static boolean applicationLockCheck() {
		if (!applicationLock.isError() && !applicationLock.isLocked()) {
			{
				Shell shell = new Shell(Display.getDefault(), SWT.TOOL);
				MessageBox mes = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				mes.setText("");
				mes.setMessage("请勿多重启动");
				mes.open();
				shell.dispose();
			}
			applicationLock.release();
			return true;
		}
		return false;
	}

	private static void startLogbook() {
		try {
			AppConfig.load();
			Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
			main = new ApplicationMain();
			new SyncExecApplicationMain(main).start();
			ProxyServer.start();
			main.display();//程序堵塞在这里
		} catch (Exception | Error e) {
			LOG.get().fatal("main thread 异常中止", e);
		} finally {
			main.dispose();
			applicationLock.release();
			ProxyServer.end();
			AppConfig.store();
		}
	}

	/*------------------------------------------------------------------------------------------------------*/

	public static ApplicationMain main;
	private static final ApplicationLock applicationLock = new ApplicationLock();
	private static final LoggerHolder LOG = new LoggerHolder(ApplicationMain.class);
	private static final LoggerHolder userLogger = new LoggerHolder("user");

	/*------------------------------------------------------------------------------------------------------*/

	/** 外置舰队面板 */
	private FleetWindowOut[] fleetWindowOuts;
	/** 舰队面板-全 */
	private FleetWindowAll fleetWindowAll;

	/** 经验计算器 */
	private CalcuExpWindow calcuExpWindow;
	/** 演习经验计算器 */
	private CalcuPracticeExpWindow calcuPracticeExpWindow;

	/** 战斗窗口 */
	private BattleWindow battleWindow;

	/** 开发记录 */
	private CreateItemTable createItemTable;
	/** 建造记录 */
	private CreateShipTable createShipTable;
	/** 远征记录 */
	private MissionResultTable missionResultTable;
	/** 资源记录 */
	private MaterialRecordTable materialRecordTable;
	/** 解体记录 */
	private DestroyShipTable destroyShipTable;
	/** 废弃记录 */
	private DestroyItemTable destroyItemTable;

	/** 所有任务 */
	private QuestTable questTable;
	/** 所有舰娘 */
	private ShipListTable shipListTable;
	/** 所有装备 */
	private ItemListTable itemListTable;

	/*------------------------------------------------------------------------------------------------------*/

	private Display display = Display.getDefault();
	private Image logo = new Image(this.display, this.getClass().getResourceAsStream(AppConstants.LOGO));
	private final Shell shell;//主面板shell
	private final Shell subShell;//辅助shell,不显示,用于其他呼出式窗口
	private final Menu menubar;//菜单栏
	private final TrayItem trayItem;

	private Composite leftComposite;//左面板
	private Button itemList;
	private Button shipList;
	private Group resourceGroup;
	private Label[] resourceLabels;
	private String[] resourceStrings = { "油", "钢", "高速修复材", "开发资材", "弹", "铝", "螺丝", "高速建造材" };
	private Group notifySettingGroup;
	private Button deckNotice;
	private Button ndockNotice;
	private Button akashiNotice;
	private Button condNotice;
	private Group deckGroup;
	private Label[] decknameLabels;
	private Label[] decktimeLabels;
	private Group ndockGroup;
	private Label[] ndocknameLabels;
	private Label[] ndocktimeLabels;
	private org.eclipse.swt.widgets.List console;

	private Composite rightComposite;//右面板
	private final int fleetLength = 2;
	private FleetWindow[] fleetWindows;//舰队面板

	private ApplicationMain() {
		this.shell = new Shell(this.display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.RESIZE);
		this.initShell();
		this.subShell = new Shell(this.display, SWT.TOOL);
		this.initLeftComposite();
		this.initRightComposite();

		this.menubar = new Menu(this.shell, SWT.BAR);
		this.initMenuBar();
		this.shell.setMenuBar(this.menubar);

		this.trayItem = new TrayItem(this.display.getSystemTray(), SWT.NONE);
		this.initTrayItem();
	}

	private void initShell() {
		this.shell.setText(AppConstants.MAINWINDOWNAME);
		this.shell.setSize(SwtUtils.DPIAwareSize(new Point(411, 523)));
		this.shell.setLocation(1100, 200);
		this.shell.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.shell.setImage(this.logo);
		this.shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if (AppConfig.get().isCheckDoit()) {
					MessageBox box = new MessageBox(ApplicationMain.this.shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
					box.setText("退出");
					box.setMessage("要退出航海日志吗?");
					e.doit = box.open() == SWT.YES;
				}
			}
		});
	}

	//左面板
	private void initLeftComposite() {
		this.leftComposite = new Composite(this.shell, SWT.NONE);
		this.leftComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 1, 1));
		SwtUtils.initControl(this.leftComposite, new GridData(GridData.FILL_VERTICAL), 210);//左边面板的宽度在此控制
		{
			Composite buttonComposite = new Composite(this.leftComposite, SWT.NONE);
			buttonComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
			buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				this.itemList = new Button(buttonComposite, SWT.PUSH);
				this.itemList.setText("装备(0/0)");
				this.itemList.addSelectionListener(new ControlSelectionListener(ev -> this.itemListTable.setVisible(true)));
			}
			{
				this.shipList = new Button(buttonComposite, SWT.PUSH);
				this.shipList.setText("舰娘(0/0)");
				this.shipList.addSelectionListener(new ControlSelectionListener(ev -> this.shipListTable.setVisible(true)));
			}
		}
		{
			this.resourceGroup = new Group(this.leftComposite, SWT.NONE);
			this.resourceGroup.setText("资源");
			this.resourceGroup.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0));
			this.resourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			int len = 8;
			this.resourceLabels = new Label[len];
			for (int i = 0; i < len; i++) {
				this.resourceLabels[i] = new Label(this.resourceGroup, SWT.RIGHT);
				this.resourceLabels[i].setToolTipText(this.resourceStrings[i]);
				SwtUtils.initLabel(this.resourceLabels[i], "0", new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		{
			this.notifySettingGroup = new Group(this.leftComposite, SWT.NONE);
			this.notifySettingGroup.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0));
			this.notifySettingGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.notifySettingGroup.setText("通知设定");
			{
				this.deckNotice = new Button(this.notifySettingGroup, SWT.CHECK);
				this.deckNotice.setText("远征");
				this.deckNotice.setSelection(AppConfig.get().isNoticeDeckmission());
				this.deckNotice.addSelectionListener(new ControlSelectionListener(ev -> AppConfig.get().setNoticeDeckmission(this.deckNotice.getSelection())));
			}
			{
				this.ndockNotice = new Button(this.notifySettingGroup, SWT.CHECK);
				this.ndockNotice.setText("入渠");
				this.ndockNotice.setSelection(AppConfig.get().isNoticeNdock());
				this.ndockNotice.addSelectionListener(new ControlSelectionListener(ev -> AppConfig.get().setNoticeNdock(this.ndockNotice.getSelection())));
			}
			{
				this.akashiNotice = new Button(this.notifySettingGroup, SWT.CHECK);
				this.akashiNotice.setText("泊地修理");
				this.akashiNotice.setSelection(AppConfig.get().isNoticeAkashi());
				this.akashiNotice.addSelectionListener(new ControlSelectionListener(ev -> AppConfig.get().setNoticeAkashi(this.akashiNotice.getSelection())));
			}
			{
				this.condNotice = new Button(this.notifySettingGroup, SWT.CHECK);
				this.condNotice.setText("疲劳");
				this.condNotice.setSelection(AppConfig.get().isNoticeCond());
				this.condNotice.addSelectionListener(new ControlSelectionListener(ev -> AppConfig.get().setNoticeCond(this.condNotice.getSelection())));
			}
		}
		{
			this.deckGroup = new Group(this.leftComposite, SWT.NONE);
			this.deckGroup.setText("远征");
			this.deckGroup.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.deckGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			int len = 4;
			this.decknameLabels = new Label[len];
			this.decktimeLabels = new Label[len];
			for (int i = 0; i < len; i++) {
				this.decknameLabels[i] = new Label(this.deckGroup, SWT.NONE);
				SwtUtils.initLabel(this.decknameLabels[i], AppConstants.DEFAULT_FLEET_NAME[i] + "的远征名", new GridData(GridData.FILL_HORIZONTAL));

				this.decktimeLabels[i] = new Label(this.deckGroup, SWT.RIGHT);
				SwtUtils.initLabel(this.decktimeLabels[i], "00时00分00秒", new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1), 78);
			}
		}
		{
			this.ndockGroup = new Group(this.leftComposite, SWT.NONE);
			this.ndockGroup.setText("入渠");
			this.ndockGroup.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.ndockGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			int len = 4;
			this.ndocknameLabels = new Label[len];
			this.ndocktimeLabels = new Label[len];
			for (int i = 0; i < len; i++) {
				this.ndocknameLabels[i] = new Label(this.ndockGroup, SWT.NONE);
				SwtUtils.initLabel(this.ndocknameLabels[i], "渠" + (i + 1) + "中的舰娘", new GridData(GridData.FILL_HORIZONTAL));

				this.ndocktimeLabels[i] = new Label(this.ndockGroup, SWT.RIGHT);
				SwtUtils.initLabel(this.ndocktimeLabels[i], "00时00分00秒", new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1), 78);
			}
		}
		{
			this.console = new org.eclipse.swt.widgets.List(this.leftComposite, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
			this.console.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.console.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (e.button == 3) {
						ApplicationMain.this.console.deselectAll();
						ApplicationMain.this.rightComposite.setFocus();
					}
				}
			});
		}
	}

	//右面板
	private void initRightComposite() {
		this.rightComposite = new Composite(this.shell, SWT.NONE);
		this.rightComposite.setLayout(SwtUtils.makeGridLayout(new int[] { 0, 1, 1, 2, 2 }[this.fleetLength], 2, 2, 0, 0));
		this.rightComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.fleetWindows = new FleetWindow[this.fleetLength];
		for (int i = 0; i < this.fleetLength; i++) {
			this.fleetWindows[i] = new FleetWindow(new Composite(this.rightComposite, SWT.BORDER), i + 1);
		}
	}

	//托盘图标
	private void initTrayItem() {
		this.trayItem.setImage(this.logo);
		this.trayItem.addListener(SWT.Selection, ev -> this.setVisible(true));
		this.trayItem.addMenuDetectListener(new TrayItemMenuListener(this));
	}

	//菜单栏
	private void initMenuBar() {
		MenuItem cmdMenuItem = new MenuItem(this.menubar, SWT.CASCADE);
		cmdMenuItem.setText("主菜单");
		Menu cmdMenu = new Menu(cmdMenuItem);
		cmdMenuItem.setMenu(cmdMenu);
		{
			MenuItem ship = new MenuItem(cmdMenu, SWT.CHECK);
			ship.setText("所有舰娘");
			this.shipListTable = new ShipListTable(this, ship, ship.getText());
		}
		{
			MenuItem item = new MenuItem(cmdMenu, SWT.CHECK);
			item.setText("所有装备");
			this.itemListTable = new ItemListTable(this, item, item.getText());
		}
		{
			MenuItem quest = new MenuItem(cmdMenu, SWT.CHECK);
			quest.setText("任务列表");
			this.questTable = new QuestTable(this, quest, quest.getText());
		}
		new MenuItem(cmdMenu, SWT.SEPARATOR);
		{
			MenuItem battle = new MenuItem(cmdMenu, SWT.CHECK);
			battle.setText("战斗");
			this.battleWindow = new BattleWindow(this, battle, battle.getText());
		}
		new MenuItem(cmdMenu, SWT.SEPARATOR);
		{
			MenuItem dispose = new MenuItem(cmdMenu, SWT.NONE);
			dispose.setText("退出");
			dispose.addSelectionListener(new ControlSelectionListener(ev -> this.shell.close()));
		}

		MenuItem recordMenuItem = new MenuItem(this.menubar, SWT.CASCADE);
		recordMenuItem.setText("记录");
		Menu recordMenu = new Menu(recordMenuItem);
		recordMenuItem.setMenu(recordMenu);
		{
			MenuItem createship = new MenuItem(recordMenu, SWT.CHECK);
			createship.setText("建造记录");
			this.createShipTable = new CreateShipTable(this, createship, createship.getText());
		}
		{
			MenuItem createitem = new MenuItem(recordMenu, SWT.CHECK);
			createitem.setText("开发记录");
			this.createItemTable = new CreateItemTable(this, createitem, createitem.getText());
		}
		{
			MenuItem mission = new MenuItem(recordMenu, SWT.CHECK);
			mission.setText("远征记录");
			this.missionResultTable = new MissionResultTable(this, mission, mission.getText());
		}
		{
			MenuItem material = new MenuItem(recordMenu, SWT.CHECK);
			material.setText("资源记录");
			this.materialRecordTable = new MaterialRecordTable(this, material, material.getText());
		}
		new MenuItem(recordMenu, SWT.SEPARATOR);
		{
			MenuItem destroyShip = new MenuItem(recordMenu, SWT.CHECK);
			destroyShip.setText("解体记录");
			this.destroyShipTable = new DestroyShipTable(this, destroyShip, destroyShip.getText());
		}
		{
			MenuItem destroyItem = new MenuItem(recordMenu, SWT.CHECK);
			destroyItem.setText("废弃记录");
			this.destroyItemTable = new DestroyItemTable(this, destroyItem, destroyItem.getText());
		}

		MenuItem fleetMenuItem = new MenuItem(this.menubar, SWT.CASCADE);
		fleetMenuItem.setText("舰队");
		Menu fleetMenu = new Menu(fleetMenuItem);
		fleetMenuItem.setMenu(fleetMenu);
		{//外置舰队面板
			MenuItem fleetWindowAllMenuItem = new MenuItem(fleetMenu, SWT.CHECK);
			fleetWindowAllMenuItem.setText("全舰队");
			this.fleetWindowAll = new FleetWindowAll(this, fleetWindowAllMenuItem, fleetWindowAllMenuItem.getText());

			this.fleetWindowOuts = new FleetWindowOut[4];
			for (int i = 0; i < this.fleetWindowOuts.length; i++) {
				MenuItem fleetWindowOutMenuItem = new MenuItem(fleetMenu, SWT.CHECK);
				fleetWindowOutMenuItem.setText(AppConstants.DEFAULT_FLEET_NAME[i]);
				this.fleetWindowOuts[i] = new FleetWindowOut(this, fleetWindowOutMenuItem, i + 1);
			}
		}

		MenuItem calcMenuItem = new MenuItem(this.menubar, SWT.CASCADE);
		calcMenuItem.setText("计算器");
		Menu calcMenu = new Menu(calcMenuItem);
		calcMenuItem.setMenu(calcMenu);
		{
			MenuItem expcalu = new MenuItem(calcMenu, SWT.CHECK);
			expcalu.setText("经验计算器");
			this.calcuExpWindow = new CalcuExpWindow(this, expcalu, expcalu.getText());
		}
		{
			MenuItem practiceexpcalu = new MenuItem(calcMenu, SWT.CHECK);
			practiceexpcalu.setText("演习经验计算器");
			this.calcuPracticeExpWindow = new CalcuPracticeExpWindow(this, practiceexpcalu, practiceexpcalu.getText());
		}

		MenuItem etcMenuItem = new MenuItem(this.menubar, SWT.CASCADE);
		etcMenuItem.setText("其它");
		Menu etcMenu = new Menu(etcMenuItem);
		etcMenuItem.setMenu(etcMenu);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public Image getLogo() {
		return this.logo;
	}

	public Shell getShell() {
		return this.shell;
	}

	public Shell getSubShell() {
		return this.subShell;
	}

	public Display getDisplay() {
		return this.display;
	}

	public TrayItem getTrayItem() {
		return this.trayItem;
	}

	public Button getItemList() {
		return this.itemList;
	}

	public Button getShipList() {
		return this.shipList;
	}

	public Label[] getResourceLabel() {
		return this.resourceLabels;
	}

	public Label[] getDeckNameLabel() {
		return this.decknameLabels;
	}

	public Label[] getDeckTimeLabel() {
		return this.decktimeLabels;
	}

	public Label[] getNdockNameLabel() {
		return this.ndocknameLabels;
	}

	public Label[] getNdockTimeLabel() {
		return this.ndocktimeLabels;
	}

	public FleetWindow[] getFleetWindows() {
		return this.fleetWindows;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public FleetWindowOut[] getFleetWindowOuts() {
		return this.fleetWindowOuts;
	}

	public FleetWindowAll getFleetWindowAll() {
		return this.fleetWindowAll;
	}

	public CalcuExpWindow getCalcuExpWindow() {
		return this.calcuExpWindow;
	}

	public CalcuPracticeExpWindow getCalcuPracticeExpWindow() {
		return this.calcuPracticeExpWindow;
	}

	public BattleWindow getBattleWindow() {
		return this.battleWindow;
	}

	public RecordTable<?>[] getRecordTables() {
		return new RecordTable[] {//
				this.createItemTable, this.createShipTable, this.missionResultTable, this.materialRecordTable, //
				this.questTable, this.shipListTable, this.itemListTable,//
				this.destroyShipTable, this.destroyItemTable,//
		};
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void logPrint(String mes) {
		String message = AppConstants.CONSOLE_TIME_FORMAT.format(new Date()) + "  " + mes;
		if (Thread.currentThread() == this.display.getThread()) {
			this.printMessage(message);
		} else {
			this.display.asyncExec(() -> this.printMessage(message));
		}
		userLogger.get().info(mes);
	}

	public void printNewDay(long currentTime) {
		this.printMessage(new SimpleDateFormat("yyyy-MM-dd").format(new Date(currentTime)));
	}

	private void printMessage(String message) {
		if (this.console.isDisposed()) return;
		if (this.console.getItemCount() >= 200) this.console.remove(0);

		this.console.add(message);
		this.console.setSelection(this.console.getItemCount() - 1);
		this.console.deselectAll();
	}

	private void display() {
		this.printNewDay(TimeString.getCurrentTime());
		this.logPrint("航海日志启动");
		this.setVisible(true);
		while (this.shell.isDisposed() == false) {
			if (this.display.readAndDispatch() == false) {
				this.display.sleep();
			}
		}
	}

	private void setVisible(boolean visible) {
		this.shell.setVisible(visible);
		if (visible) {
			this.shell.setMinimized(false);
			this.shell.setActive();
			this.shell.forceActive();
		}
	}

	private void dispose() {
		this.trayItem.dispose();
		this.subShell.dispose();
		this.logo.dispose();
	}

}
