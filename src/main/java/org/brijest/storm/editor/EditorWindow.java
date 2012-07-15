package org.brijest.storm.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import java.awt.Frame;
import org.eclipse.swt.awt.SWT_AWT;
import java.awt.Panel;
import java.awt.BorderLayout;
import javax.swing.JRootPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class EditorWindow extends Shell {
	public Panel areaCanvasPane;
	public Table terrainTable;
	
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
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorWindow.this.dispose();
			}
		});
		mntmExit.setText("Exit");
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmMap = new CTabItem(tabFolder, SWT.NONE);
		tbtmMap.setText("Map");
		tabFolder.setSelection(0);
		
		Composite composite = new Composite(tabFolder, SWT.EMBEDDED);
		tbtmMap.setControl(composite);
		
		Frame frame = SWT_AWT.new_Frame(composite);
		
		areaCanvasPane = new Panel();
		frame.add(areaCanvasPane);
		areaCanvasPane.setLayout(new BorderLayout(0, 0));
		
		CTabFolder righttabs = new CTabFolder(sashForm, SWT.BORDER);
		righttabs.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmTerrain = new CTabItem(righttabs, SWT.NONE);
		tbtmTerrain.setText("Terrain");
		righttabs.setSelection(0);
		
		terrainTable = new Table(righttabs, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmTerrain.setControl(terrainTable);
		terrainTable.setHeaderVisible(true);
		terrainTable.setLinesVisible(true);
		
		TableColumn tblclmnImage = new TableColumn(terrainTable, SWT.NONE);
		tblclmnImage.setWidth(51);
		tblclmnImage.setText("Image");
		
		TableColumn tblclmnName = new TableColumn(terrainTable, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnNewColumn = new TableColumn(terrainTable, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("Full name");
		sashForm.setWeights(new int[] {85, 15});
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Editor");
		setSize(760, 669);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
