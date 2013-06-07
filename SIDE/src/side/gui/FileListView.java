package side.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import side.model.ComplexElementType;
import side.service.impl.FileManager;

public class FileListView extends ViewPart {
	
	public static final String ID = "side.gui.FileListView";

	private Table table;
	private	FileManager fileMgr = new FileManager();
	private String selectedButton = "Message-Level Elements";
	private Button checkButton;

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 10;
		layout.numColumns = 3;
		top.setLayout(layout);
 		
		
		table = new Table (top, SWT.CHECK | SWT.V_SCROLL | SWT.BORDER);
		table.setHeaderVisible (true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				try {
					IViewPart messageView = getSite().getWorkbenchWindow().getActivePage().showView(MessageView.ID);
				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//				((MessageCombinationTab)messageCombinationTab).updateView(complexElListMap);
			}
			
		});
		
		GridData gridTable =new GridData();	
		gridTable.heightHint = 500;
		gridTable.horizontalSpan = 3;
		table.setLayoutData(gridTable);
		
		TableColumn column = new TableColumn (table, SWT.NONE);
	    column.setText (" File ");
	    column.setWidth(200);
	    column.setResizable(false);
	    
	    Group schemaGroup = new Group(top, SWT.SHADOW_NONE);
    	FontData[] fD = schemaGroup.getFont().getFontData();
    	fD[0].setHeight(10);
	    GridData gridGroup = new GridData();
	    gridGroup.widthHint = 210;
	    gridGroup.horizontalSpan = 3;
	    schemaGroup.setFont( new Font(schemaGroup.getDisplay(),fD[0]));
	    schemaGroup.setLayoutData(gridGroup);
	    schemaGroup.setText("Search for");
	    
	    
		    Listener selectionListener = new Listener() {
	
		        public void handleEvent(Event event) {
	
		            Button button = (Button) event.widget;
		            if (!button.getSelection()) return;
	
		            selectedButton = (String) button.getData();
		        }
	
		    };
	    
	    	final Button normalSchemaButton = new Button (schemaGroup, SWT.RADIO);

	    	normalSchemaButton.setFont( new Font(schemaGroup.getDisplay(),fD[0]));
	    	normalSchemaButton.setLocation(10, 20);
	    	normalSchemaButton.setText("DCI 1 (Message-Lvl Elements)");
	    	normalSchemaButton.setData("Message-Level Elements");
	    	normalSchemaButton.setSelection(true);
	    	normalSchemaButton.pack();
	    	normalSchemaButton.addListener(SWT.Selection, selectionListener);

	    	final Button flattenedSchemaButton = new Button (schemaGroup, SWT.RADIO);
	    	flattenedSchemaButton.setFont( new Font(top.getDisplay(),fD[0]));
	    	flattenedSchemaButton.setLocation(10, 45);
	    	flattenedSchemaButton.setText("DCI 2 (All Elements)");
	    	flattenedSchemaButton.setData("All Elements");
	    	flattenedSchemaButton.pack();
	    	
	    	flattenedSchemaButton.addListener(SWT.Selection, selectionListener);
	    
	    
	    checkButton = new Button(top, SWT.PUSH);
	    checkButton.setText("Check All");
	    checkButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (table.getItemCount() > 0) {
					if (checkButton.getText().equals("Check All")) {
						String fileName = "";
						
						for (int i=0; i < table.getItems().length; i++) {
							fileName = table.getItem(i).getText();
					
							if (fileName.equals("FS_OTA_AirCheckIn.xsd") || fileName.equals("FS_OTA_AirCommonTypes.xsd")
									|| fileName.equals("FS_OTA_AirPreferences.xsd") || fileName.equals("FS_OTA_CommonPrefs.xsd")
									|| fileName.equals("FS_OTA_CommonTypes.xsd") || fileName.equals("FS_OTA_SimpleTypes.xsd")
										|| fileName.equals("OTA_AirCheckIn.xsd") || fileName.equals("OTA_AirCommonTypes.xsd")
										|| fileName.equals("OTA_AirPreferences.xsd") || fileName.equals("OTA_CommonPrefs.xsd")
										|| fileName.equals("OTA_CommonTypes.xsd") || fileName.equals("OTA_SimpleTypes.xsd")) {
								table.getItem(i).setChecked(false);
							} else {
								table.getItem(i).setChecked(true);
							}
						}								
						checkButton.setText("Uncheck All");
					} else {
						for (int i=0; i < table.getItems().length; i++) {
							table.getItem(i).setChecked(false);
						}
						checkButton.setText("Check All");
					}
				} else {
					MessageDialog.openError(table.getShell(), "Error", "No files exist!");
				}
			}
	    });
	    
	    final Button compareButton = new Button(top, SWT.PUSH);
	    compareButton.setText(" Compare ");
	    compareButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				int checked = 0;
				List<side.model.File> fileList = new ArrayList<side.model.File>();
				side.model.File fileModel = null;
				

					if (table.getItemCount() > 0) {
						for (int i=0; i < table.getItems().length; i++) {
							if (table.getItem(i).getChecked()) {
								fileModel = new side.model.File();
								checked++;
								
								fileModel.setName(table.getItem(i).getText());
								fileModel.setPath((String) table.getItem(i).getData());
								fileList.add(fileModel);
							}
						}
//						if (checked > 1) {
							try {
								TreeMap<String, List<ComplexElementType>> complexElListMap = null;
								
								if (selectedButton.equals("Message-Level Elements") && checked <= 1 ) {
									MessageDialog.openError(table.getShell(), "Error", "You must select 2 files for DCI 1 !");
								} else if (checked != 0 && !(selectedButton.equals("Message-Level Elements") && checked <= 1 )) {
									if (selectedButton.equals("Message-Level Elements")) {
										complexElListMap = fileMgr.compareMessageElementsSchemas(fileList);
									}
									
									if (selectedButton.equals("All Elements")){
//										complexElListMap = fileMgr.compareAllElementsSchemas(fileList);
//										complexElListMap = fileMgr.compareAllElementsSchemas(fileList);
									}
									
									IViewPart interMessageView = getSite().getWorkbenchWindow().getActivePage().showView(InterMessageView.ID);
//									((InterMessageView)interMessageView).updateView(complexElListMap, selectedButton);
									((InterMessageView)interMessageView).updateView(fileList, selectedButton);
								}
								
							} catch (PartInitException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
//						} else if (checked == 1) {
//						    if (schemaType.equals("Message-Level Elements") && checked <= 1) {
//								MessageDialog.openError(table.getShell(), "Error", "No files exist!");
//
//						    }
//						}
					} else {
						MessageDialog.openError(table.getShell(), "Error", "No files exist!");
					}
				} 

				


//				boolean isSuccessful = ((MessageCombinationTab)MessageCombinationTab).updateView(fileList);
//			}
	    });
	    
	    parent.addDisposeListener( new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				// TODO Auto-generated method stub
				deleteTempFiles();
			}
    	} );
	    
//	    final Button deleteButton = new Button(top, SWT.PUSH);
//	    deleteButton.setText("Delete All");
//	    deleteButton.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//				File destinationTempFolder = new File((String) table.getData());
//				
//				if (table.getItemCount() > 0) {
//					disposeOldWidgets();
//				}
//				IViewPart fileUploadView = getSite().getWorkbenchWindow().getActivePage().findView(FileUploadView.ID);
//				((FileUploadView) fileUploadView).setFileUploadText("");
//				fileMgr.deleteFile(destinationTempFolder);
//			}
//	    });
	}
	
	private void disposeOldWidgets() {

		 for (int i=table.getItems().length-1; i >= 0; i--) {

			 if ((table.getItems()[i] != null) && (!table.getItems()[i].isDisposed())) {
				 table.getItems()[i].dispose();
			 }
		 }
	}
	
	public void updateView(List<side.model.File> fileList, String destinationTempDirectory) {
		
		disposeOldWidgets();
		checkButton.setText("Check All");
		
		for (int i=0; i<fileList.size(); i++) {
			TableItem item = new TableItem (table, 0);
			
			item.setText (fileList.get(i).getName());
			item.setData(fileList.get(i).getPath());
		}
		table.setData(destinationTempDirectory);
	}

	public void deleteTempFiles() {
		
		if (table.getData() != null) {
			File destinationTempDirectory  = new File((String) table.getData());
			fileMgr.deleteFile(destinationTempDirectory);
		}
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
	            	return width ? 250 : 650;
	            }
	        };
	    }
	    return super.getAdapter(adapter);
	}

}
