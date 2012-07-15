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

public class PlaneAreaChooser extends Dialog {

	protected Object result;
	protected Shell shlChooseArea;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PlaneAreaChooser(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlChooseArea.open();
		shlChooseArea.layout();
		Display display = getParent().getDisplay();
		while (!shlChooseArea.isDisposed()) {
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
		shlChooseArea = new Shell(getParent(), getStyle());
		shlChooseArea.setSize(136, 121);
		shlChooseArea.setText("Choose Area");
		shlChooseArea.setLayout(new GridLayout(2, false));
		
		Label lblXPosition = new Label(shlChooseArea, SWT.NONE);
		lblXPosition.setText("X position:");
		
		final Spinner xspinner = new Spinner(shlChooseArea, SWT.BORDER);
		GridData gd_xspinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_xspinner.widthHint = 36;
		xspinner.setLayoutData(gd_xspinner);
		
		Label lblYPosition = new Label(shlChooseArea, SWT.NONE);
		lblYPosition.setText("Y position:");
		
		final Spinner yspinner = new Spinner(shlChooseArea, SWT.BORDER);
		GridData gd_yspinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_yspinner.widthHint = 36;
		yspinner.setLayoutData(gd_yspinner);
		
		Button btnNewButton = new Button(shlChooseArea, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = new Point(xspinner.getSelection(), yspinner.getSelection());
				shlChooseArea.dispose();
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("OK");
		
		Button btnCancel = new Button(shlChooseArea, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText("Cancel");

	}

}
