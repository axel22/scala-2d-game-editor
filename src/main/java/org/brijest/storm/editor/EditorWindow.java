package org.brijest.storm.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.MouseAdapter;

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
	public ToolItem removeCharacter;
	public ToolItem insertCharacter;
	public Text characterFilter;
	public Tree characterTable;
	public CTabFolder charTabs;
	public ToolBar modeToolbar;
	public CharacterTip characterTip;
	public ToolItem saveButton;
	
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
	
	public <T> Class<T> selectedChar() {
		TreeItem[] selected = characterTable.getSelection();
		if (selected.length > 0) {
			return (Class<T>)selected[0].getData();
		} else return null;
	}
	
	public void deselectRadios() {
		paintTerrain.setSelection(false);
		elevateTerrain.setSelection(false);
		insertCharacter.setSelection(false);
		removeCharacter.setSelection(false);
	}
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public EditorWindow(Display display) {
		super(display, SWT.SHELL_TRIM); 
		setLocation(new Point(50, 50));
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
			}
		});
		setMinimumSize(new Point(1250, 850));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
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
		
		MenuItem mntmArea = new MenuItem(menu, SWT.CASCADE);
		mntmArea.setText("Area");
		
		Menu menu_4 = new Menu(mntmArea);
		mntmArea.setMenu(menu_4);
		
		MenuItem mntmResize = new MenuItem(menu_4, SWT.NONE);
		mntmResize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Resize area", arg0);
			}
		});
		mntmResize.setText("Resize...");
		
		modeToolbar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		modeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		saveButton = new ToolItem(modeToolbar, SWT.NONE);
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				eventHandler.event("Save", null);
			}
		});
		
		ToolItem toolItem = new ToolItem(modeToolbar, SWT.SEPARATOR);
		
		paintTerrain = new ToolItem(modeToolbar, SWT.RADIO);
		paintTerrain.setSelection(true);
		
		elevateTerrain = new ToolItem(modeToolbar, SWT.RADIO);
		
		insertCharacter = new ToolItem(modeToolbar, SWT.RADIO);
		
		removeCharacter = new ToolItem(modeToolbar, SWT.RADIO);
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
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
		grpWorldInfo.setLayout(new GridLayout(3, false));
		
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
		
		Composite composite_5 = new Composite(grpWorldInfo, SWT.EMBEDDED);
		GridData gd_composite_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_5.heightHint = 17;
		composite_5.setLayoutData(gd_composite_5);
		
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
		new Label(grpWorldInfo, SWT.NONE);
		
		Label lblTotalPlanes = new Label(grpWorldInfo, SWT.NONE);
		lblTotalPlanes.setText("Total planes:");
		
		totalPlanesLabel = new Label(grpWorldInfo, SWT.NONE);
		totalPlanesLabel.setText("   ");
		new Label(grpWorldInfo, SWT.NONE);
		sashForm_1.setWeights(new int[] {3, 1});
		leftTabs.setSelection(0);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		
		SashForm sashForm_2 = new SashForm(composite, SWT.VERTICAL);
		sashForm_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		CTabFolder rightTabs = new CTabFolder(sashForm_2, SWT.BORDER);
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
		
		Composite composite_4 = new Composite(composite_3, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_4 = new GridLayout(1, false);
		gl_composite_4.verticalSpacing = 0;
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		gl_composite_4.horizontalSpacing = 0;
		composite_4.setLayout(gl_composite_4);
		
		terrainFilter = new Text(composite_4, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		terrainFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		terrainFilter.setSize(178, 19);
		terrainFilter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				eventHandler.event("Terrain filter", arg0);
			}
		});
		
		terrainTable = new Table(composite_3, SWT.BORDER | SWT.FULL_SELECTION);
		terrainTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				deselectRadios();
				paintTerrain.setSelection(true);
			}
		});
		GridData gd_terrainTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_terrainTable.widthHint = 125;
		terrainTable.setLayoutData(gd_terrainTable);
		terrainTable.setHeaderVisible(true);
		
		TableColumn tableColumn = new TableColumn(terrainTable, SWT.NONE);
		tableColumn.setWidth(51);
		tableColumn.setText("Image");
		
		TableColumn tableColumn_1 = new TableColumn(terrainTable, SWT.NONE);
		tableColumn_1.setWidth(123);
		tableColumn_1.setText("Name");
		
		TableColumn tableColumn_2 = new TableColumn(terrainTable, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("Full name");
		rightTabs.setSelection(0);
		
		charTabs = new CTabFolder(sashForm_2, SWT.BORDER);
		charTabs.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmCharacters = new CTabItem(charTabs, SWT.NONE);
		tbtmCharacters.setText("Characters");
		
		charTabs.setSelection(0);
		
		Composite composite_1 = new Composite(charTabs, SWT.NONE);
		tbtmCharacters.setControl(composite_1);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);
		
		Composite composite_6 = new Composite(composite_1, SWT.NONE);
		GridLayout gl_composite_6 = new GridLayout(1, false);
		gl_composite_6.verticalSpacing = 0;
		gl_composite_6.marginWidth = 0;
		gl_composite_6.marginHeight = 0;
		gl_composite_6.horizontalSpacing = 0;
		composite_6.setLayout(gl_composite_6);
		GridData gd_composite_6 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_6.heightHint = 22;
		composite_6.setLayoutData(gd_composite_6);
		
		characterFilter = new Text(composite_6, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		characterFilter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				eventHandler.event("Character filter", arg0);
			}
		});
		GridData gd_characterFilter = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_characterFilter.widthHint = 196;
		characterFilter.setLayoutData(gd_characterFilter);
		
		characterTable = new Tree(composite_1, SWT.BORDER);
		characterTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				deselectRadios();
				insertCharacter.setSelection(true);
			}
		});
		characterTable.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				if (characterTip != null) {
					characterTip.dispose();
					characterTip = null;
				}
			}
		});
		characterTable.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				eventHandler.event("Character hover", new Point(e.x, e.y));
			}
		});
		characterTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm_2.setWeights(new int[] {279, 499});
		sashForm.setWeights(new int[] {988, 251});
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
