package org.brijest.storm.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TerrainChooser extends Dialog {

	protected String result;
	public Shell chooserShell;
	public CCombo terrainCombo;
	public String[] terrains;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TerrainChooser(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
		createContents();
		chooserShell.open();
		chooserShell.layout();
		Display display = getParent().getDisplay();
		while (!chooserShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		chooserShell = new Shell(getParent(), getStyle());
		chooserShell.setSize(217, 106);
		chooserShell.setText("Choose terrain");
		chooserShell.setLayout(new GridLayout(1, false));
		Point loc = Display.getCurrent().getCursorLocation();
		chooserShell.setLocation(loc.x, loc.y);
		
		Composite composite = new Composite(chooserShell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblXPosition = new Label(composite, SWT.NONE);
		lblXPosition.setText("Terrain:");
		
		terrainCombo = new CCombo(composite, SWT.BORDER);
		GridData gd_terrainCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_terrainCombo.widthHint = 145;
		terrainCombo.setLayoutData(gd_terrainCombo);
		terrainCombo.setBounds(0, 0, 30, 20);
		terrainCombo.setItems(terrains);
		
		Composite composite_1 = new Composite(chooserShell, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button btnNewButton = new Button(composite_1, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = terrainCombo.getText();
				chooserShell.dispose();
			}
		});
		btnNewButton.setText("OK");
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = null;
				chooserShell.dispose();
			}
		});
		btnCancel.setText("Cancel");

	}
}

