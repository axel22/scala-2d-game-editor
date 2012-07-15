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
import org.eclipse.swt.widgets.Text;

public class EditorWindow extends Shell {
	public Table terrainTable;
	public Text worldNameLabel;
	public Table planeTable;
	public Combo mainPlaneCombo;
	public Label totalPlanesLabel;
	public AreaPanel areaPanel;
	public MenuItem openAreaMenuItem;
	public CTabFolder leftTabs;
	
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

	/**
	 * Create the shell.
	 * @param display
	 */
	public EditorWindow(Display display) {
		super(display, SWT.SHELL_TRIM); 
		setMinimumSize(new Point(1000, 750));
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmSave = new MenuItem(menu_1, SWT.NONE);
		mntmSave.setText("Save");
		
		MenuItem mntmSaveAs = new MenuItem(menu_1, SWT.NONE);
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
		openAreaMenuItem.setText("Open area...");
		
		Composite composite_2 = new Composite(sashForm_1, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpWorldInfo = new Group(composite_2, SWT.NONE);
		grpWorldInfo.setText("World info");
		grpWorldInfo.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(grpWorldInfo, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		worldNameLabel = new Text(grpWorldInfo, SWT.BORDER);
		GridData gd_worldNameLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_worldNameLabel.widthHint = 190;
		worldNameLabel.setLayoutData(gd_worldNameLabel);
		
		Label lblMainPlane = new Label(grpWorldInfo, SWT.NONE);
		lblMainPlane.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMainPlane.setText("Main plane:");
		
		mainPlaneCombo = new Combo(grpWorldInfo, SWT.NONE);
		GridData gd_mainPlaneCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_mainPlaneCombo.widthHint = 173;
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
		rightTabs.setSelection(0);
		
		terrainTable = new Table(rightTabs, SWT.BORDER | SWT.FULL_SELECTION);
		terrainTable.setHeaderVisible(true);
		tbtmTerrain.setControl(terrainTable);
		
		TableColumn tblclmnImage = new TableColumn(terrainTable, SWT.NONE);
		tblclmnImage.setWidth(51);
		tblclmnImage.setText("Image");
		
		TableColumn tblclmnName = new TableColumn(terrainTable, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnNewColumn = new TableColumn(terrainTable, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("Full name");
		
		CTabItem tbtmAreaDetails = new CTabItem(rightTabs, SWT.NONE);
		tbtmAreaDetails.setText("Area Details");
		sashForm.setWeights(new int[] {85, 15});
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Storm-Enroute Editor");
		setSize(1000, 750);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
