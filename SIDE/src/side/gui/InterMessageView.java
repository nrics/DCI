package side.gui;

import java.beans.EventHandler;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Element;


import side.model.ComplexElement;
import side.model.ComplexElementType;
import side.model.ComplexType;
import side.model.Dependency;
import side.model.MessageTypes;
import side.model.SimpleElement;
import side.service.impl.FileManager;
import side.service.impl.SortListenerFactory;

public class InterMessageView extends ViewPart {
	
	public static final String ID = "side.gui.InterMessageView";
	
	private Composite top;
	private ScrolledComposite scrollComposite;
	private Composite scrollCompositeChild;
	private ScrolledComposite scrollComposite2;
	private Composite scrollCompositeChild2;
	private ScrolledComposite scrollComposite3;
	private Composite scrollCompositeChild3;
	private ScrolledComposite scrollComposite4;
	private Composite scrollCompositeChild4;
	private TableViewer tableViewer;
	private TableViewer tableViewer2;
	private TableViewer tableViewer3;
	private TableViewer tableViewer4;
	private Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);    
	private Label vsLabel;
	private Text dciScore;
	private Text dci3Score;
	private CTabItem tab2;
	private int numberOfComplexTypes = 0;

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub

		CTabFolder  folder = new CTabFolder(parent, SWT.NONE);
		folder.setBorderVisible(true);
//		folder.setTabHeight(25);
		folder.setSelectionBackground(new Color[] {
		        parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
		        parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
		        parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND) }, new int[] { 4, 60 });
		folder.setSelectionForeground(parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND)); 

		GridLayout gridTop = new GridLayout();
		gridTop.marginHeight = 0;
		gridTop.marginWidth = 0;
		gridTop.horizontalSpacing = 10;
		gridTop.numColumns = 2;
		
	    //Tab 1
	    CTabItem tab1 = new CTabItem(folder, SWT.NONE);
	    tab1.setText(" Metrics ");
	    
		top = new Composite(folder, SWT.NONE);
		top.setLayout(gridTop);
		
		GridData gridDciScore = new GridData();
		gridDciScore.widthHint = 300;
		gridDciScore.horizontalSpan = 2;
	    
	    dciScore = new Text (top, SWT.NONE);
	    dciScore.setLayoutData(gridDciScore);
	    
	    dci3Score = new Text (top, SWT.NONE);
	    dci3Score.setLayoutData(gridDciScore);
//	    dciScore.setText("DCI Metric : "+df.format(metricResult));
	    
		tab1.setControl(top);
		 
	    //Tab 2
		tab2 = new CTabItem(folder, SWT.NONE);
	    tab2.setText(" Message Combinations ");
	    
	    gridTop = new GridLayout();
	    gridTop.marginHeight = 0;
	    gridTop.marginWidth = 0;
	    
		top = new Composite(folder, SWT.NONE);
		top.setLayout(gridTop);
		
    	scrollComposite = new ScrolledComposite(top, SWT.V_SCROLL | SWT.BORDER);
 		scrollComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

 		scrollCompositeChild = new Composite(scrollComposite, SWT.NONE);
		scrollCompositeChild.setLayout(new GridLayout(1, true));

		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setContent(scrollCompositeChild);
		
		tab2.setControl(top);
		
	    //Tab 3
		CTabItem tab3 = new CTabItem(folder, SWT.NONE);
	    tab3.setText(" DCI 3 Combinations ");
	    
	    gridTop = new GridLayout();
	    gridTop.marginHeight = 0;
	    gridTop.marginWidth = 0;
	    
		top = new Composite(folder, SWT.NONE);
		top.setLayout(gridTop);
		
    	scrollComposite2 = new ScrolledComposite(top, SWT.V_SCROLL | SWT.BORDER);
 		scrollComposite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

 		scrollCompositeChild2 = new Composite(scrollComposite2, SWT.NONE);
		scrollCompositeChild2.setLayout(new GridLayout(1, true));

		scrollComposite2.setExpandHorizontal(true);
		scrollComposite2.setExpandVertical(true);
		scrollComposite2.setContent(scrollCompositeChild2);
		
		tab3.setControl(top);

	    //Tab 4
		CTabItem tab4 = new CTabItem(folder, SWT.NONE);
	    tab4.setText(" Element Sharing Counts ");
	    
	    gridTop = new GridLayout();
	    gridTop.marginHeight = 0;
	    gridTop.marginWidth = 0;
	    
		top = new Composite(folder, SWT.NONE);
		top.setLayout(gridTop);
		
    	scrollComposite3 = new ScrolledComposite(top, SWT.V_SCROLL | SWT.BORDER);
 		scrollComposite3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

 		scrollCompositeChild3 = new Composite(scrollComposite3, SWT.NONE);
		scrollCompositeChild3.setLayout(new GridLayout(2, false));

		scrollComposite3.setExpandHorizontal(true);
		scrollComposite3.setExpandVertical(true);
		scrollComposite3.setContent(scrollCompositeChild3);
		
		tab4.setControl(top);
		
	    //Tab 5
		CTabItem tab5 = new CTabItem(folder, SWT.NONE);
	    tab5.setText(" Number of Complex Types ");
	    
	    gridTop = new GridLayout();
	    gridTop.marginHeight = 0;
	    gridTop.marginWidth = 0;
	    
		top = new Composite(folder, SWT.NONE);
		top.setLayout(gridTop);
		
    	scrollComposite4 = new ScrolledComposite(top, SWT.V_SCROLL | SWT.BORDER);
 		scrollComposite4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

 		scrollCompositeChild4 = new Composite(scrollComposite4, SWT.NONE);
		scrollCompositeChild4.setLayout(new GridLayout(2, false));

		scrollComposite4.setExpandHorizontal(true);
		scrollComposite4.setExpandVertical(true);
		scrollComposite4.setContent(scrollCompositeChild4);
		
		tab5.setControl(top);

		folder.setSelection(tab1);
//		// add this view as a selection listener to the workbench page
//		getSite().getPage().addSelectionListener(FileUploadView.ID,(ISelectionListener) this);
	}
	
	private void createViewer() {
		tableViewer = new TableViewer(scrollCompositeChild, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(scrollCompositeChild);
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(new ArrayContentProvider());

//		 Make the selection available to other views
//		getSite().setSelectionProvider(tableViewer);
		// Set the sorter for the table

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
//		gridData.horizontalSpan = 2;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
	}
	
	private void createViewer2() {
		tableViewer2 = new TableViewer(scrollCompositeChild2, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns2(scrollCompositeChild2);
		final Table table2 = tableViewer2.getTable();
		table2.setHeaderVisible(true);
		table2.setLinesVisible(true);

		tableViewer2.setContentProvider(new ArrayContentProvider());

//		 Make the selection available to other views
//		getSite().setSelectionProvider(tableViewer);
		// Set the sorter for the table

		// Layout the viewer
		GridData gridData2 = new GridData();
		gridData2.verticalAlignment = GridData.BEGINNING;
//		gridData.horizontalSpan = 2;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
		gridData2.horizontalAlignment = GridData.FILL;
		tableViewer2.getControl().setLayoutData(gridData2);
	}
	
	private void createViewer3() {
		
		tableViewer3 = new TableViewer(scrollCompositeChild3, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns3();
		final Table table3 = tableViewer3.getTable();
		table3.setHeaderVisible(true);
		table3.setLinesVisible(true);

		tableViewer3.setContentProvider(new ArrayContentProvider());

		// Layout the viewer
		GridData gridData3 = new GridData();
		gridData3.verticalAlignment = GridData.BEGINNING;
		tableViewer3.getControl().setLayoutData(gridData3);
	}
	
	private void createViewer4() {
		
		tableViewer4 = new TableViewer(scrollCompositeChild4, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns4();
		final Table table4 = tableViewer4.getTable();
		table4.setHeaderVisible(true);
		table4.setLinesVisible(true);

		tableViewer4.setContentProvider(new ArrayContentProvider());

		// Layout the viewer
		GridData gridData4 = new GridData();
		gridData4.verticalAlignment = GridData.BEGINNING;
		tableViewer4.getControl().setLayoutData(gridData4);
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	// This will create the columns for the table
		private void createColumns(final Composite parent) {
			String[] titles = { "Type", "Quantity" };
			int[] bounds = { 600, 100 };

			// First column is for the first name
			TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Dependency p = (Dependency) element;
					return p.getType();
				}
			});

			// Second column is for the last name
			col = createTableViewerColumn(titles[1], bounds[1], 1);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Dependency p = (Dependency) element;
					return p.getQuantity();
				}
			});

		}
		
		private void createColumns2(final Composite parent) {
			String[] titles = { "Type", "Quantity" };
			int[] bounds = { 600, 100 };
			
			// First column is for the first name
			TableViewerColumn col2 = createTableViewerColumn2(titles[0], bounds[0], 0);
			col2.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Dependency p = (Dependency) element;
					return p.getType();
				}
			});

			// Second column is for the last name
			col2 = createTableViewerColumn2(titles[1], bounds[1], 1);
			col2.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Dependency p = (Dependency) element;
					return p.getQuantity();
				}
			});
		}

		private void createColumns3() {
			String[] titles = { "Type", "Quantity" };
			int[] bounds = { 400, 100 };
			
			// First column is for the first name
			TableViewerColumn col3 = createTableViewerColumn3(titles[0], bounds[0], 0);
			col3.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Dependency p = (Dependency) element;
					return p.getType();
				}
			});

			// Second column is for the last name
			col3 = createTableViewerColumn3(titles[1], bounds[1], 1);
			col3.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Dependency p = (Dependency) element;
					return p.getQuantity();
				}
			});
			
			
		}
		
		private void createColumns4() {
			String[] titles = { "Message Name", "Number of Complex Types", "Number of Simple Types" };
			int[] bounds = { 200, 100 };
			
			// First column is for the first name
			TableViewerColumn col4 = createTableViewerColumn4(titles[0], bounds[0], 0);
			col4.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {

					MessageTypes p = (MessageTypes) element;
					return p.getMessageName();
				}
			});

			// Second column is for the Complex Element
			col4 = createTableViewerColumn4(titles[1], bounds[0], 1);
			col4.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					
					MessageTypes p = (MessageTypes) element;
					return Integer.toString(p.getNumberOfComplexTypes());
				}
			});
			
			// Third column is for the quantity
			col4 = createTableViewerColumn4(titles[2], bounds[1], 2);
			col4.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					
					MessageTypes p = (MessageTypes) element;
					return Integer.toString(p.getNumberOfSimpleTypes());
				}
			});
			
			
		}
		
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setMoveable(false);
		column.setResizable(false);
		return viewerColumn;
	}
	
	private TableViewerColumn createTableViewerColumn2(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer2, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setMoveable(false);
		column.setResizable(false);
		return viewerColumn;
	}
	
	private TableViewerColumn createTableViewerColumn3(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer3, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setMoveable(false);
		column.setResizable(false);
		if (colNumber == 0) {
			column.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.STRING_COMPARATOR));
		} else {
			column.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.INT_COMPARATOR));
		}
		
		return viewerColumn;
	}
	
	private TableViewerColumn createTableViewerColumn4(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer4, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setMoveable(false);
		column.setResizable(false);
		if (colNumber == 0) {
			column.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.STRING_COMPARATOR));
		} else {
			column.addListener(SWT.Selection, SortListenerFactory.getListener(SortListenerFactory.INT_COMPARATOR));
		}
		
		return viewerColumn;
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
	            	return width ? 1300 : preferredResult;
	            }
	        };
	    }
	    return super.getAdapter(adapter);
	}

//	@Override
//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		// TODO Auto-generated method stub
//		
//		if (part instanceof FileUploadView) {  
//			
////			   side.model.File uploadedFile = (side.model.File) ((StructuredSelection) selection).getFirstElement(); 
//
////			   if (uploadedFile != null) {
//
////			   }
//		}  	
//	}
	
	private void rearrangeInterface() {
		   scrollComposite.setMinSize(scrollCompositeChild.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		   scrollCompositeChild.layout();
		   
		   scrollComposite2.setMinSize(scrollCompositeChild2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		   scrollCompositeChild2.layout();
		   
		   scrollComposite3.setMinSize(scrollCompositeChild3.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		   scrollCompositeChild3.layout();
		   
		   scrollComposite4.setMinSize(scrollCompositeChild4.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		   scrollCompositeChild4.layout();
	}
	
	private void disposeOldWidgets() {

		 for (int i=scrollCompositeChild.getChildren().length-1; i >= 0; i--) {
			 if ((scrollCompositeChild.getChildren()[i] != null) && (!scrollCompositeChild.getChildren()[i].isDisposed())) {
				 scrollCompositeChild.getChildren()[i].dispose();
			 }
		 }
		 for (int i=scrollCompositeChild2.getChildren().length-1; i >= 0; i--) {
			 if ((scrollCompositeChild2.getChildren()[i] != null) && (!scrollCompositeChild2.getChildren()[i].isDisposed())) {
				 scrollCompositeChild2.getChildren()[i].dispose();
			 }
		 }
		 for (int i=scrollCompositeChild3.getChildren().length-1; i >= 0; i--) {
			 if ((scrollCompositeChild3.getChildren()[i] != null) && (!scrollCompositeChild3.getChildren()[i].isDisposed())) {
				 scrollCompositeChild3.getChildren()[i].dispose();
			 }
		 }
		 for (int i=scrollCompositeChild4.getChildren().length-1; i >= 0; i--) {
			 if ((scrollCompositeChild4.getChildren()[i] != null) && (!scrollCompositeChild4.getChildren()[i].isDisposed())) {
				 scrollCompositeChild4.getChildren()[i].dispose();
			 }
		 }
	}

	public void updateView(TreeMap<String, List<ComplexElementType>> complexElListMap, String schemaType) {		
	    disposeOldWidgets();
	    
	    if (schemaType.equals("Message-Level Elements")) {
		    tab2.setText("DCI Combinations)");
	    }
	    
	    else {
		    tab2.setText("DCI 2 (All Elements Combinations)");
			computeNumberOfTypes(complexElListMap);
	    }
	    compareFiles(complexElListMap, schemaType);
	    rearrangeInterface();
	}
	
	public void updateView(List<side.model.File> fileList, String schemaType) {		
	    disposeOldWidgets();
	    
	    if (schemaType.equals("Message-Level Elements")) {
		    tab2.setText("DCI Combinations)");
	    }
	    
	    else {
		    tab2.setText("DCI 2 (All Elements Combinations)");
	    }
	    compareMessageElements(fileList);

//	    compareFiles(complexElListMap, schemaType);
	    rearrangeInterface();
	}
	
	public TreeMap<String, List<ComplexElementType>> compareMessageElements(List<side.model.File> fileList) {
		
		TreeMap<String, List<ComplexElementType>> complexElListMap = new TreeMap<String, List<ComplexElementType>>();
		
	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
		ResourceSet resourceSet = new ResourceSetImpl();
		int numberOfComplexTypes =0;
		int numberOfSimpleTypes =0;
		List<MessageTypes> resultList = new ArrayList<MessageTypes>();

		
		for (int i=0; i < fileList.size(); i++) {
			
			List<ComplexElement> complexElementList = new ArrayList<ComplexElement>();
			List<SimpleElement> simpleElementList = new ArrayList<SimpleElement>();
			MessageTypes messageTypes = new MessageTypes();
	
			XSDResourceImpl xsdSchemaResource = (XSDResourceImpl)resourceSet.getResource(URI.createFileURI(fileList.get(i).getPath()), true);
			XSDConcreteComponent concreteComponent = null;

			for (Iterator iter = xsdSchemaResource.getSchema().eContents().iterator(); iter.hasNext(); ) {
			    concreteComponent = (XSDConcreteComponent)iter.next();
			    
			    if (concreteComponent instanceof XSDElementDeclaration) {
			    	concreteComponent = (XSDElementDeclaration) concreteComponent;
			    	break;
			    }
			}

			numberOfComplexTypes = searchComplexElements(concreteComponent, complexElementList).size();
			numberOfSimpleTypes = searchSimpleElements(concreteComponent, simpleElementList).size();
			
//			System.out.print(numberOfComplexTypes+" Counted\n");

			Collections.sort(complexElementList, 
			new Comparator<ComplexElement>() {
				public int compare(ComplexElement el1, ComplexElement el2) {
					return el1.getName().compareTo(el2.getName());
				}
			});
//			complexElListMap = rearrangeComplexElementTypes(fileList.get(i).getName().substring(0, fileList.get(i).getName().length()-4), tempComplexElementList, complexElListMap);
			
			messageTypes.setMessageName(((XSDElementDeclaration)concreteComponent).getName());
			messageTypes.setNumberOfComplexTypes(numberOfComplexTypes);
			messageTypes.setNumberOfSimpleTypes(numberOfSimpleTypes);

			resultList.add(messageTypes);
			
		}
		
		setTypesResults(resultList);

		return complexElListMap;
	}

	public int searchBaseComplexTypes(XSDConcreteComponent concreteComponent) {
		int count=0;
		
		for (Iterator iter = concreteComponent.eAllContents(); iter.hasNext(); ) {
			XSDConcreteComponent concreteChildComponent = (XSDConcreteComponent)iter.next();

		    if (concreteChildComponent instanceof XSDComplexTypeDefinition) {
		    	count++;
		    }
		}
		return count;
	}

	
	public List<ComplexElement> searchComplexElements(XSDConcreteComponent concreteComponent, List<ComplexElement> complexElementList) {

		for (Iterator iter = concreteComponent.eAllContents(); iter.hasNext(); ) {
			XSDConcreteComponent concreteChildComponent = (XSDConcreteComponent)iter.next();
			
		    if (concreteChildComponent instanceof XSDElementDeclaration
		    		&& ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition() instanceof XSDComplexTypeDefinition) {
		    	
				concreteChildComponent.resolveTypeDefinition(((XSDElementDeclaration)concreteChildComponent).getType().getName());
//				System.out.print("1111===== "+concreteChildComponent+"\n");
				
		    	if (((XSDElementDeclaration)concreteChildComponent).getType().getName() == null) {
		    		XSDComplexTypeDefinition complexTypeDef = (XSDComplexTypeDefinition) ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition();
		    		
		    		if (complexTypeDef.isSetDerivationMethod()) {
		    	    	ComplexElement complexElement = new ComplexElement();
		    	    	complexElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
		        		complexElement.setType(complexTypeDef.getBaseType().getName());
		        		complexElement.setExtension(true);
		        		complexElementList.add(complexElement);


//		        		System.out.print(complexTypeDef.getBaseType().getName()+" aaaa\n");

		    	    	if (complexTypeDef.getBaseType().getBaseType() != null) {
		    	    		searchComplexElements(complexTypeDef.getBaseType().getBaseType(), complexElementList);
		    	    	} 
		    	    	searchComplexElements(complexTypeDef.getBaseType(), complexElementList);
		    		} else {
				    	ComplexElement complexElement = new ComplexElement();
				    	complexElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
			    		complexElement.setType("NONE");
			    		complexElement.setExtension(false);
			    		complexElementList.add(complexElement);
		    		}

		    	} else {
//		    		System.out.print(((XSDElementDeclaration)concreteChildComponent).getType().getName()+" WWWWWWWW\n");
			    	ComplexElement complexElement = new ComplexElement();
			    	complexElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
		    		complexElement.setType(((XSDElementDeclaration)concreteChildComponent).getType().getName());
		    		complexElement.setExtension(false);
		    		complexElementList.add(complexElement);
			    	
			    	searchComplexElements(((XSDElementDeclaration)concreteChildComponent).getType(), complexElementList);
		    	}
		    }
		}
		return complexElementList;
	}
	
	public List<SimpleElement> searchSimpleElements(XSDConcreteComponent concreteComponent, List<SimpleElement> simpleElementList) {

		for (Iterator iter = concreteComponent.eAllContents(); iter.hasNext(); ) {
			XSDConcreteComponent concreteChildComponent = (XSDConcreteComponent)iter.next();
			
		    if (concreteChildComponent instanceof XSDElementDeclaration
		    		&& ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition() instanceof XSDSimpleTypeDefinition) {
		    	
				concreteChildComponent.resolveTypeDefinition(((XSDElementDeclaration)concreteChildComponent).getType().getName());
				System.out.print("Simple type:===== "+concreteChildComponent+"\n");
				
				
		    	if (((XSDElementDeclaration)concreteChildComponent).getType().getName() == null) {
		    		XSDSimpleTypeDefinition simpleTypeDef = (XSDSimpleTypeDefinition) ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition();

//		    		if (simpleTypeDef.is) {
//		    			SimpleElement simpleElement = new SimpleElement();
//		    			simpleElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
//		    			simpleElement.setType(simpleTypeDef.getBaseType().getName());
//		    			simpleElement.setExtension(true);
//		        		simpleElementList.add(simpleElement);
//
//
////		        		System.out.print(complexTypeDef.getBaseType().getName()+" aaaa\n");
//
//		    	    	if (simpleTypeDef.getBaseType().getBaseType() != null) {
//		    	    		searchSimpleElements(simpleTypeDef.getBaseType().getBaseType(), simpleElementList);
//		    	    	} 
//		    		} else {
				    	SimpleElement simpleElement = new SimpleElement();
				    	simpleElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
				    	simpleElement.setType("NONE");
				    	simpleElement.setExtension(false);
			    		simpleElementList.add(simpleElement);
//		    		}

		    	} else {
//		    		System.out.print(((XSDElementDeclaration)concreteChildComponent).getType().getName()+" WWWWWWWW\n");
		    		SimpleElement simpleElement = new SimpleElement();
		    		simpleElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
		    		simpleElement.setType(((XSDElementDeclaration)concreteChildComponent).getType().getName());
		    		simpleElement.setExtension(false);
		    		simpleElementList.add(simpleElement);
			    	
		    		searchSimpleElements(((XSDElementDeclaration)concreteChildComponent).getType(), simpleElementList);
		    	}
		    }
		}
		return simpleElementList;
	}
	
	private void computeNumberOfTypes(TreeMap<String, List<ComplexElementType>> complexElListMap) {
		
		Object[] file = complexElListMap.entrySet().toArray();
//		double totalMessageCombinations = 0;
//		double totalSharedCElements = 0;
//		double totalSharedCElements2 = 0;
//		List<Dependency> commonTypes = new ArrayList<Dependency>();
//		List<Dependency> commonTypes2 = new ArrayList<Dependency>(); 

		for (int i=0; i < file.length; i++) {
			TreeMap<String, Integer> elementTypeMap = new TreeMap<String, Integer>();
			List<Dependency> dependencies = new ArrayList<Dependency>();

			double totalSharedCElements = 0.00;
			
			Entry<String, List<ComplexElementType>> messageFile = (Entry<String, List<ComplexElementType>>) file[i];

			for (int l=0; l < messageFile.getValue().size(); l++) {
				
				ComplexElementType complexElementType = messageFile.getValue().get(l);
				
				ComplexType complexType = new ComplexType();
				complexType.setName(complexElementType.getName());
				complexType.setComplexElement(complexElementType.getComplexElement());	
				complexType.setQuantity(complexElementType.getQuantity());
				
//				setTypesResults(complexElementType);
				
			}
//				int quantity = 0;
//				ComplexElementType file1CElementType = messageFile.getValue().get(l);
//				
//				for (int k=0; k < secondFile.getValue().size(); k++) {
//					ComplexElementType file2CElementType = secondFile.getValue().get(k);
//					
//					if (file1CElementType.getName().equals(file2CElementType.getName())) {
//						quantity = file1CElementType.getQuantity()*file2CElementType.getQuantity();
//						Dependency dependency = new Dependency();
//						dependency.setType(file2CElementType.getName());
//						dependency.setQuantity(Integer.toString(quantity));	
//						
//						dependencies.add(dependency);
//						commonTypes.add(dependency);
//						
//						totalSharedCElements = quantity+totalSharedCElements;
//					}
//				} 
//				elementTypeMap.put(file1CElementType.getName(), quantity);
//			}
//			setCombinationResults(firstFile, secondFile, dependencies);
//			return totalSharedCElements;
			
		}
		
	}
		
	public void setTypesResults(List<MessageTypes> resultList) {

		if (resultList != null) {
//			Object[][] object = (Object[][]) result;
    		createViewer4();
    		tableViewer4.setInput(resultList);
		}
	}
	
	public void setCombinationResults(Entry<String, List<ComplexElementType>> firstFile, Entry<String, List<ComplexElementType>> secondFile, List<Dependency> dependencies) {
   		vsLabel = new Label(scrollCompositeChild, SWT.NONE);
   		vsLabel.setText("====="+firstFile.getKey()+" vs "+secondFile.getKey()+"=====\n");
   		vsLabel.setFont(boldFont);
   		
		if (dependencies.size() != 0) {
    		createViewer();
    		tableViewer.setInput(dependencies);
		}
	}
	
	public void setCombination2Results(Entry<String, List<ComplexElementType>> firstFile, Entry<String, List<ComplexElementType>> secondFile, List<Dependency> dependencies) {
   		vsLabel = new Label(scrollCompositeChild2, SWT.NONE);
   		vsLabel.setText("====="+firstFile.getKey()+" vs "+secondFile.getKey()+"=====\n");
   		vsLabel.setFont(boldFont);
   		
		if (dependencies.size() != 0) {
    		createViewer2();
    		tableViewer2.setInput(dependencies);
		}
	}
	
	private double linkAndComputeComplexElements(Object file1, Object file2, List<Dependency> commonTypes) {
		
		TreeMap<String, Integer> elementTypeMap = new TreeMap<String, Integer>();
		List<Dependency> dependencies = new ArrayList<Dependency>();

		double totalSharedCElements = 0.00;
		
		Entry<String, List<ComplexElementType>> firstFile = (Entry<String, List<ComplexElementType>>) file1;
		Entry<String, List<ComplexElementType>> secondFile = (Entry<String, List<ComplexElementType>>) file2;

		for (int l=0; l < firstFile.getValue().size(); l++) {
			int quantity = 0;
			ComplexElementType file1CElementType = firstFile.getValue().get(l);
			
			for (int k=0; k < secondFile.getValue().size(); k++) {
				ComplexElementType file2CElementType = secondFile.getValue().get(k);
				
				if (file1CElementType.getName().equals(file2CElementType.getName())) {
					quantity = file1CElementType.getQuantity()*file2CElementType.getQuantity();
					Dependency dependency = new Dependency();
					dependency.setType(file2CElementType.getName());
					dependency.setQuantity(Integer.toString(quantity));	
					
					dependencies.add(dependency);
					commonTypes.add(dependency);
					
					totalSharedCElements = quantity+totalSharedCElements;
				}
			} 
			elementTypeMap.put(file1CElementType.getName(), quantity);
		}
		setCombinationResults(firstFile, secondFile, dependencies);
		return totalSharedCElements;
	}
	
	private double linkAndComputeComplexElements2(Object file1, Object file2, List<Dependency> commonTypes2) {
		
		List<Dependency> dependencies2 = new ArrayList<Dependency>();

		double totalSharedCElements = 0.00;
		
		Entry<String, List<ComplexElementType>> firstFile = (Entry<String, List<ComplexElementType>>) file1;
		Entry<String, List<ComplexElementType>> secondFile = (Entry<String, List<ComplexElementType>>) file2;

		for (int l=0; l < firstFile.getValue().size(); l++) {
			int quantity = 0;
			ComplexElementType file1CElementType = firstFile.getValue().get(l);
			
			for (int k=0; k < secondFile.getValue().size(); k++) {
				ComplexElementType file2CElementType = secondFile.getValue().get(k);
				
				if (file1CElementType.getName().equals(file2CElementType.getName())) {
//					quantity = file1CElementType.getQuantity()*file2CElementType.getQuantity();
					
					Dependency dependency = new Dependency();
					dependency.setType(file2CElementType.getName());
					dependency.setQuantity(Integer.toString(1));	
					
					dependencies2.add(dependency);
					commonTypes2.add(dependency);
					
					totalSharedCElements = 1+totalSharedCElements;
				}
			} 
		}
		
		setCombination2Results(firstFile, secondFile, dependencies2);
		
		return totalSharedCElements;
	}
	
	public void compareFiles(TreeMap<String, List<ComplexElementType>> complexElListMap, String schemaType) {
		
		Object[] file = complexElListMap.entrySet().toArray();
		double totalMessageCombinations = 0;
		double totalSharedCElements = 0;
		double totalSharedCElements2 = 0;
		List<Dependency> commonTypes = new ArrayList<Dependency>();
		List<Dependency> commonTypes2 = new ArrayList<Dependency>();

		for (int i=0; i < file.length; i++) {
//        	System.out.println("Key 1 : " + complexElList[i]+"=============\n");
    		for (int k=i+1; k < file.length; k++) {
//            	System.out.println("Key 2 : " + complexElList[k]+"=============\n");
    			totalSharedCElements = totalSharedCElements+linkAndComputeComplexElements(file[i], file[k], commonTypes);
    			totalSharedCElements2 = totalSharedCElements2+linkAndComputeComplexElements2(file[i], file[k], commonTypes2);
        		totalMessageCombinations++;
    		}
		}
		Label tableTitleTxt = new Label(scrollCompositeChild3, SWT.NONE);
		tableTitleTxt.setText("Based on DCI 1 or 2");
		tableTitleTxt.setLayoutData(new GridData(700, SWT.DEFAULT));
		
		tableTitleTxt = new Label(scrollCompositeChild3, SWT.NONE);
		tableTitleTxt.setText("Based on DCI 3");
		
		setSharingCountsOnDCI2(commonTypes);
		setSharingCountsOnDCI3(commonTypes2);
		
		DecimalFormat df = new DecimalFormat("#.##");
		double dciResult = totalSharedCElements/totalMessageCombinations;
		double dciResult2 = totalSharedCElements2/totalMessageCombinations;

		if (schemaType.equals("Message-Level Elements"))
			dciScore.setText("DCI 1 (Message-Lvl): "+df.format(dciResult));
		else
			dciScore.setText("DCI 2 (All Elements): "+df.format(dciResult));

		dci3Score.setText("DCI 3 Score : "+df.format(dciResult2));
	}
	
	private void setSharingCountsOnDCI2(List<Dependency> commonTypes) {
		
		for (int i=0; i < commonTypes.size(); i++) {
			Dependency dependency1 = commonTypes.get(i);
			int quantity = 0;
			boolean isMatch = false;
			
			for (int k=commonTypes.size()-1; k > i; k--) {
				Dependency dependency2 = commonTypes.get(k);
				
				if (dependency1.getType().equals(dependency2.getType())) {
//					commonTypes.get(i).setQuantity(Integer.toString(Integer.parseInt(commonTypes.get(i).getQuantity())+quantity));
					quantity = Integer.parseInt(commonTypes.get(k).getQuantity())+quantity;
//					commonTypes.get(i).setQuantity(Integer.toString(quantity));
					commonTypes.remove(k);
					
					isMatch = true;
				}
				
			}
			if (isMatch) {
				commonTypes.get(i).setQuantity(Integer.toString(Integer.parseInt(commonTypes.get(i).getQuantity())+quantity));
			}
		}
		
		Collections.sort(commonTypes, 
		new Comparator<Dependency>() {
			public int compare(Dependency dependency1, Dependency dependency2) {
				return dependency1.getType().compareTo(dependency2.getType());
			}
		});

		if (commonTypes.size() != 0) {
    		createViewer3();
    		tableViewer3.setInput(commonTypes);
		}
	}
	
	private void setSharingCountsOnDCI3(List<Dependency> commonTypes2) {
		int quantity = 1;
		
		for (int i=0; i < commonTypes2.size(); i++) {
			Dependency dependency1 = commonTypes2.get(i);
			
			for (int k=commonTypes2.size()-1; k > i; k--) {
				Dependency dependency2 = commonTypes2.get(k);
				
				if (dependency1.getType().equals(dependency2.getType())) {
					commonTypes2.get(i).setQuantity(Integer.toString(Integer.parseInt(commonTypes2.get(i).getQuantity())+1));
					commonTypes2.remove(k);
				}

			}
		}
		
		Collections.sort(commonTypes2, 
		new Comparator<Dependency>() {
			public int compare(Dependency dependency1, Dependency dependency2) {
				return dependency1.getType().compareTo(dependency2.getType());
			}
		});

		if (commonTypes2.size() != 0) {
    		createViewer3();
    		tableViewer3.setInput(commonTypes2);
		}
	}
}
