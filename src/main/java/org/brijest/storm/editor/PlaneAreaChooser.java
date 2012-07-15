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
		
		Spinner spinner = new Spinner(shlChooseArea, SWT.BORDER);
		GridData gd_spinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spinner.widthHint = 36;
		spinner.setLayoutData(gd_spinner);
		
		Label lblYPosition = new Label(shlChooseArea, SWT.NONE);
		lblYPosition.setText("Y position:");
		
		Spinner spinner_1 = new Spinner(shlChooseArea, SWT.BORDER);
		GridData gd_spinner_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spinner_1.widthHint = 36;
		spinner_1.setLayoutData(gd_spinner_1);
		
		Button btnNewButton = new Button(shlChooseArea, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnNewButton.setText("OK");
		
		Button btnCancel = new Button(shlChooseArea, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText("Cancel");

	}

}
