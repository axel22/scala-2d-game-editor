package org.brijest.storm.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import java.awt.Frame;
import org.eclipse.swt.awt.SWT_AWT;
import java.awt.Panel;
import java.awt.BorderLayout;
import javax.swing.JRootPane;
import org.eclipse.swt.layout.FillLayout;

public class AreaPanel extends Composite {
	public Panel areaCanvasPane;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AreaPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(this, SWT.EMBEDDED);
		
		Frame frame = SWT_AWT.new_Frame(composite);
		frame.setLayout(new BorderLayout(0, 0));
		
		areaCanvasPane = new Panel();
		frame.add(areaCanvasPane, BorderLayout.CENTER);
		areaCanvasPane.setLayout(new BorderLayout(0, 0));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
