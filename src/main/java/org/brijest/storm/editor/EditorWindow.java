package org.brijest.storm.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import java.awt.Frame;
import org.eclipse.swt.awt.SWT_AWT;
import java.awt.Panel;
import java.awt.BorderLayout;
import javax.swing.JRootPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.custom.CLabel;

public class EditorWindow extends Shell {
	public EditorEventHandler eventHandler;
	
	public Text worldNameLabel;
	public Table planeTable;
	public Combo mainPlaneCombo;
	public Label totalPlanesLabel;
	public AreaPanel areaPanel;
	public MenuItem openAreaMenuItem;
	public CTabFolder leftTabs;
	public Table terrainTable;
	public ToolItem paintTerrain;
	public ToolItem elevateTerrain;
	public Text terrainFilter;
	public CLabel searchLabel;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			EditorWindow shell = new EditorWindow(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String selectedTerrain() {
		TableItem[] selected = terrainTable.getSelection();
		if (selected.length > 0) {
			return selected[0].getText(2);
		} else return null;
	}
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public EditorWindow(Display display) {
		super(display, SWT.SHELL_TRIM); 
		setMinimumSize(new Point(1250, 850));
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmSave = new MenuItem(menu_1, SWT.NONE);
		mntmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Save", null);
			}
		});
		mntmSave.setText("Save");
		
		MenuItem mntmSaveAs = new MenuItem(menu_1, SWT.NONE);
		mntmSaveAs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Save as", null);
			}
		});
		mntmSaveAs.setText("Save as...");
		
		MenuItem menuItem = new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorWindow.this.dispose();
			}
		});
		mntmExit.setText("Exit");
		
		MenuItem mntmWorld = new MenuItem(menu, SWT.CASCADE);
		mntmWorld.setText("World");
		
		Menu menu_2 = new Menu(mntmWorld);
		mntmWorld.setMenu(menu_2);
		
		MenuItem mntmAddPlane = new MenuItem(menu_2, SWT.NONE);
		mntmAddPlane.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Add plane", arg0);
			}
		});
		mntmAddPlane.setText("Add plane...");
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		leftTabs = new CTabFolder(sashForm, SWT.BORDER);
		leftTabs.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmWorld = new CTabItem(leftTabs, SWT.NONE);
		tbtmWorld.setText("World");
		
		SashForm sashForm_1 = new SashForm(leftTabs, SWT.NONE);
		sashForm_1.setOrientation(SWT.VERTICAL);
		tbtmWorld.setControl(sashForm_1);
		
		planeTable = new Table(sashForm_1, SWT.BORDER | SWT.FULL_SELECTION);
		planeTable.setLinesVisible(true);
		planeTable.setHeaderVisible(true);
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(planeTable, SWT.NONE);
		tblclmnNewColumn_1.setWidth(67);
		tblclmnNewColumn_1.setText("Plane ID");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(planeTable, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("Plane");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(planeTable, SWT.NONE);
		tblclmnNewColumn_3.setWidth(645);
		tblclmnNewColumn_3.setText("Details");
		
		Menu menu_3 = new Menu(planeTable);
		planeTable.setMenu(menu_3);
		
		openAreaMenuItem = new MenuItem(menu_3, SWT.NONE);
		openAreaMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Open area", arg0);
			}
		});
		openAreaMenuItem.setText("Open area...");
		
		MenuItem mntmAddPlane_1 = new MenuItem(menu_3, SWT.NONE);
		mntmAddPlane_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Add plane", arg0);
			}
		});
		mntmAddPlane_1.setText("Add plane...");
		
		MenuItem mntmRemovePlane = new MenuItem(menu_3, SWT.NONE);
		mntmRemovePlane.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Remove plane", arg0);
			}
		});
		mntmRemovePlane.setText("Remove plane...");
		
		Composite composite_2 = new Composite(sashForm_1, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpWorldInfo = new Group(composite_2, SWT.NONE);
		grpWorldInfo.setText("World info");
		grpWorldInfo.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(grpWorldInfo, SWT.NONE);
		lblName.setText("Name:");
		
		worldNameLabel = new Text(grpWorldInfo, SWT.BORDER);
		worldNameLabel.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				eventHandler.event("World name", arg0);
			}
		});
		GridData gd_worldNameLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_worldNameLabel.widthHint = 150;
		worldNameLabel.setLayoutData(gd_worldNameLabel);
		
		Label lblMainPlane = new Label(grpWorldInfo, SWT.NONE);
		lblMainPlane.setText("Main plane:");
		
		mainPlaneCombo = new Combo(grpWorldInfo, SWT.NONE);
		mainPlaneCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Main plane", arg0);
			}
		});
		mainPlaneCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				eventHandler.event("Main plane", arg0);
			}
		});
		GridData gd_mainPlaneCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_mainPlaneCombo.widthHint = 150;
		mainPlaneCombo.setLayoutData(gd_mainPlaneCombo);
		
		Label lblTotalPlanes = new Label(grpWorldInfo, SWT.NONE);
		lblTotalPlanes.setText("Total planes:");
		
		totalPlanesLabel = new Label(grpWorldInfo, SWT.NONE);
		totalPlanesLabel.setText("   ");
		sashForm_1.setWeights(new int[] {3, 1});
		leftTabs.setSelection(0);
		
		CTabFolder rightTabs = new CTabFolder(sashForm, SWT.BORDER);
		rightTabs.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmTerrain = new CTabItem(rightTabs, SWT.NONE);
		tbtmTerrain.setText("Terrain");
		
		Composite composite_3 = new Composite(rightTabs, SWT.NONE);
		tbtmTerrain.setControl(composite_3);
		GridLayout gl_composite_3 = new GridLayout(1, false);
		gl_composite_3.verticalSpacing = 1;
		gl_composite_3.marginWidth = 1;
		gl_composite_3.marginHeight = 1;
		gl_composite_3.horizontalSpacing = 1;
		composite_3.setLayout(gl_composite_3);
		
		ToolBar toolBar = new ToolBar(composite_3, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		paintTerrain = new ToolItem(toolBar, SWT.RADIO);
		paintTerrain.setSelection(true);
		paintTerrain.setText("Paint");
		
		elevateTerrain = new ToolItem(toolBar, SWT.RADIO);
		elevateTerrain.setText("Elevate");
		
		Composite composite_4 = new Composite(composite_3, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_4 = new GridLayout(2, false);
		gl_composite_4.verticalSpacing = 0;
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		gl_composite_4.horizontalSpacing = 0;
		composite_4.setLayout(gl_composite_4);
		
		terrainFilter = new Text(composite_4, SWT.BORDER);
		terrainFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		terrainFilter.setSize(178, 19);
		terrainFilter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				eventHandler.event("Terrain filter", arg0);
			}
		});
		
		searchLabel = new CLabel(composite_4, SWT.NONE);
		GridData gd_searchLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_searchLabel.widthHint = 22;
		searchLabel.setLayoutData(gd_searchLabel);
		searchLabel.setText("");
		
		terrainTable = new Table(composite_3, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_terrainTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_terrainTable.widthHint = 125;
		terrainTable.setLayoutData(gd_terrainTable);
		terrainTable.setHeaderVisible(true);
		
		TableColumn tableColumn = new TableColumn(terrainTable, SWT.NONE);
		tableColumn.setWidth(51);
		tableColumn.setText("Image");
		
		TableColumn tableColumn_1 = new TableColumn(terrainTable, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("Name");
		
		TableColumn tableColumn_2 = new TableColumn(terrainTable, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("Full name");
		rightTabs.setSelection(0);
		
		CTabItem tbtmAreaDetails = new CTabItem(rightTabs, SWT.NONE);
		tbtmAreaDetails.setText("Area Details");
		
		Composite composite_1 = new Composite(rightTabs, SWT.NONE);
		tbtmAreaDetails.setControl(composite_1);
		composite_1.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(composite_1, SWT.EMBEDDED);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 13;
		gd_composite.widthHint = 147;
		composite.setLayoutData(gd_composite);
		
		Frame frame = SWT_AWT.new_Frame(composite);
		frame.setVisible(false);
		
		Panel panel = new Panel();
		panel.setVisible(false);
		frame.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JRootPane rootPane = new JRootPane();
		rootPane.getContentPane().setVisible(false);
		rootPane.setVisible(false);
		panel.add(rootPane);
		sashForm.setWeights(new int[] {85, 15});
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Storm-Enroute Editor");
		setSize(1100, 750);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
