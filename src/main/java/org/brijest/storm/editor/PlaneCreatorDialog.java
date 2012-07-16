package org.brijest.storm.editor;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

public class PlaneCreatorDialog extends Dialog {

	protected Object[] result;
	protected Shell shlAddPlane;
	private Text text;
	private Spinner spinner;
	private Combo combo;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PlaneCreatorDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object[] open() {
		createContents();
		shlAddPlane.open();
		shlAddPlane.layout();
		Display display = getParent().getDisplay();
		while (!shlAddPlane.isDisposed()) {
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
		shlAddPlane = new Shell(getParent(), getStyle());
		shlAddPlane.setSize(225, 207);
		shlAddPlane.setText("Add plane");
		Point loc = Display.getCurrent().getCursorLocation();
		shlAddPlane.setLocation(loc.x, loc.y);
		RowLayout rl_shlAddPlane = new RowLayout(SWT.VERTICAL);
		rl_shlAddPlane.justify = true;
		rl_shlAddPlane.center = true;
		rl_shlAddPlane.fill = true;
		shlAddPlane.setLayout(rl_shlAddPlane);
		
		Composite composite = new Composite(shlAddPlane, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Name:");
		
		text = new Text(composite, SWT.BORDER);
		text.setText("New plane");
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 120;
		text.setLayoutData(gd_text);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setText("Size:");
		
		spinner = new Spinner(composite, SWT.BORDER);
		GridData gd_spinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spinner.widthHint = 120;
		spinner.setLayoutData(gd_spinner);
		spinner.setMaximum(200);
		spinner.setMinimum(1);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("Area type:");
		
		combo = new Combo(composite, SWT.NONE);
		combo.setItems(new String[] {"Strict"});
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 120;
		combo.setLayoutData(gd_combo);
		combo.select(0);
		
		Composite composite_1 = new Composite(shlAddPlane, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = new Object[] {
						text.getText(), spinner.getSelection(), combo.getText()
				};
				shlAddPlane.dispose();
			}
		});
		button.setText("OK");
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = null;
				shlAddPlane.dispose();
			}
		});
		button_1.setText("Cancel");

	}

}

