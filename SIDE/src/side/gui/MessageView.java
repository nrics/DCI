package side.gui;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.part.ViewPart;

import side.model.ComplexElementType;
import side.service.impl.FileManager;

public class MessageView extends ViewPart {
	
	public static final String ID = "side.gui.MessageView";

	private Text dciScore;

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		
		// top banner
		Composite banner = new Composite(top, SWT.NONE);
		banner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 2;
		layout.numColumns = 2;
		banner.setLayout(layout);
		
		
		dciScore = new Text (banner, SWT.NONE);
	}
	
	public void updateView(File uploadedFile, int totalSharedCElements, int totalMessageCombinations) {
		FileManager fileMgr = new FileManager();
		DecimalFormat df = new DecimalFormat("#.##");

		double metricResult = (double)totalSharedCElements/totalMessageCombinations;
		
		dciScore.setText("DCI Score : "+df.format(metricResult));
	    fileMgr.deleteFile(uploadedFile);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
