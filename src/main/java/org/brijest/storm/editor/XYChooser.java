package org.brijest.storm.editor;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

public class XYChooser extends Dialog {

	protected Point result;
	public Shell chooserShell;
	public int xinit;
	public int yinit;
	public int width;
	public int height;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public XYChooser(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Point open() {
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
		chooserShell.setSize(149, 121);
		chooserShell.setText("Choose XY");
		chooserShell.setLayout(new GridLayout(2, false));
		Point loc = Display.getCurrent().getCursorLocation();
		chooserShell.setLocation(loc.x, loc.y);
		
		Label lblXPosition = new Label(chooserShell, SWT.NONE);
		lblXPosition.setText("X position:");
		
		final Spinner xspinner = new Spinner(chooserShell, SWT.BORDER);
		xspinner.setMaximum(width);
		xspinner.setSelection(xinit);
		GridData gd_xspinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_xspinner.widthHint = 36;
		xspinner.setLayoutData(gd_xspinner);
		
		Label lblYPosition = new Label(chooserShell, SWT.NONE);
		lblYPosition.setText("Y position:");
		
		final Spinner yspinner = new Spinner(chooserShell, SWT.BORDER);
		yspinner.setMaximum(height);
		yspinner.setSelection(yinit);
		GridData gd_yspinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_yspinner.widthHint = 36;
		yspinner.setLayoutData(gd_yspinner);
		
		Button btnNewButton = new Button(chooserShell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = new Point(xspinner.getSelection(), yspinner.getSelection());
				chooserShell.dispose();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("OK");
		
		Button btnCancel = new Button(chooserShell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = null;
				chooserShell.dispose();
			}
		});
		btnCancel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText("Cancel");

	}

}

