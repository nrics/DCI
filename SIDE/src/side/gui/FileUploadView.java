package side.gui;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import side.service.impl.FileManager;

public class FileUploadView extends ViewPart {

	public static final String ID = "side.gui.FileUploadView";
	private Composite banner;
//	private String fileFullName = "";
	ListenerList listeners = new ListenerList(); 
	private String selection = "";  
	String[] fileNames;
	private Text fileUploadText;
	private boolean isExistingTempDirectory = false;
	private String existingTempDirectory = "";
	private FileManager fileMgr = new FileManager();
	
	public FileUploadView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		// top banner
		banner = new Composite(top, SWT.NONE);
		banner.setLayoutData(new GridData(SWT.FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 5;
		layout.numColumns = 2;
		banner.setLayout(layout);
		
		GridData gridFileUploadText =new GridData();	
		gridFileUploadText.horizontalSpan = 2;
		
		fileUploadText = new Text (banner, SWT.BORDER);
		fileUploadText.setLayoutData(gridFileUploadText);
		fileUploadText.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				fileUploadText.setSize(fileUploadText.computeSize(200, SWT.DEFAULT));
		          event.doit = true;
			}

		});
		
		
		Button uploadButton = new Button(banner, SWT.PUSH);
		uploadButton.setText(" Upload ");
		
		final Button calculateButton = new Button(banner, SWT.PUSH);
		calculateButton.setText("calculate!");
//		GridData gridCalculateButton =new GridData();	
//		gridCalculateButton.horizontalAlignment = SWT.RIGHT;
//		calculateButton.setLayoutData(gridCalculateButton);
		calculateButton.setEnabled(false);
		
		uploadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = banner.getShell();
				File temp = null;
				String zippedFile = openDialog(shell);
				String tempDirectory = "";
				
				if (isExistingTempDirectory) {
					temp = new File(existingTempDirectory);
					fileMgr.deleteFile(temp);
					existingTempDirectory = "";
					isExistingTempDirectory = false;
				}
				
				if (zippedFile != null && !zippedFile.equals("")) {
					tempDirectory = createTempDirectory();
					List<side.model.File> fileList = fileMgr.unzip(zippedFile, tempDirectory);
					
					if (fileList != null && fileList.size() > 0) {
						IViewPart fileListView = getSite().getWorkbenchWindow().getActivePage().findView(FileListView.ID);
						((FileListView)fileListView).updateView(fileList, tempDirectory);
						fileUploadText.setText(zippedFile);
						isExistingTempDirectory = true;
						existingTempDirectory = tempDirectory;
					} else {
						temp = new File(tempDirectory);
						fileMgr.deleteFile(temp);
						fileUploadText.setText("");
						MessageDialog.openError(banner.getShell(), "Error", "Please check your zip file!");
					}
				}
			}
		});
		
	}
	
	public void setFileUploadText(String uploadText) {
		fileUploadText.setText(uploadText);
	}
	
	private String createTempDirectory() {
		File tempDirectory = null;
		File sysTempDir = new File(System.getProperty("java.io.tmpdir"));
		tempDirectory = new File(sysTempDir, "temp"+Long.toString(System.nanoTime()));
		tempDirectory.mkdir();
		
		return tempDirectory.getPath();
	}
	
	private String openDialog(Shell shell) {
		
		 FileDialog fileDialog = new FileDialog(shell, SWT.TITLE | SWT.SINGLE );
		 fileDialog.setText( "Upload" );
		 fileDialog.setFilterExtensions(new String[] { "*.zip" });
		 fileDialog.open();

		 return fileDialog.getFileName();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public Object getAdapter(Class adapter) {
	    if (ISizeProvider.class == adapter) {
	        return new ISizeProvider() {
	            public int getSizeFlags(boolean width) {
	                return SWT.MIN | SWT.MAX | SWT.FILL;
	            }

	            public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular, int preferredResult) {
	            	return width ? 250 : 85;
	            }
	        };
	    }
	    return super.getAdapter(adapter);
	}
}
