package side.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Element;

import side.gui.InterMessageView;
import side.model.ComplexElement;
import side.model.ComplexElementType;
import side.model.ComplexType;
import side.model.Dependency;

public class FileManager {
	
	private XSDResourceImpl xsdSchemaResource;
	private int totalSharedCElements = 0;
	private int totalMessageCombinations = 0;
	
	
	private void setXsdSchemaResource(XSDResourceImpl xsdSchemaResource) {
		this.xsdSchemaResource = xsdSchemaResource;
	}
	
	private XSDResourceImpl getXsdSchemaResource() {
		return xsdSchemaResource;
	}
	
	public int getTotalSharedCElements() {
		return totalSharedCElements;
	}
	
	public int getTotalMessageCombinations() {
		return totalMessageCombinations;
	}
		
	public TreeMap<String, List<ComplexElementType>> compareAllElementsSchemas(List<side.model.File> fileList) {
		
		TreeMap<String, List<ComplexElementType>> complexElListMap = new TreeMap<String, List<ComplexElementType>>();

	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
		ResourceSet resourceSet = new ResourceSetImpl();
		
		for (int i=0; i < fileList.size(); i++) {
			List<ComplexElement> tempComplexElementList = new ArrayList<ComplexElement>();
	
			XSDResourceImpl xsdSchemaResource = (XSDResourceImpl)resourceSet.getResource(URI.createFileURI(fileList.get(i).getPath()), true);
			List<ComplexType> complexTypes = new ArrayList<ComplexType>();
			XSDConcreteComponent concreteComponent = null;

			for (Iterator iter = xsdSchemaResource.getSchema().eContents().iterator(); iter.hasNext(); ) {
			    concreteComponent = (XSDConcreteComponent)iter.next();
			    
			    if (concreteComponent instanceof XSDElementDeclaration) {
			    	concreteComponent = (XSDElementDeclaration) concreteComponent;
			    	break;
			    }
			}
			
			searchComplexElements(concreteComponent, tempComplexElementList);
//			System.out.print("=======================\n");
			Collections.sort(tempComplexElementList, 
			new Comparator<ComplexElement>() {
				public int compare(ComplexElement el1, ComplexElement el2) {
					return el1.getName().compareTo(el2.getName());
				}
			});
			complexElListMap = rearrangeComplexElementTypes(fileList.get(i).getName().substring(0, fileList.get(i).getName().length()-4), tempComplexElementList, complexElListMap);
		}
		return complexElListMap;
	}
	
	public void searchComplexElements(XSDConcreteComponent concreteComponent, List<ComplexElement> tempComplexElementList) {
		for (Iterator iter = concreteComponent.eAllContents(); iter.hasNext(); ) {
			XSDConcreteComponent concreteChildComponent = (XSDConcreteComponent)iter.next();
			
		    if (concreteChildComponent instanceof XSDElementDeclaration
		    		&& ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition() instanceof XSDComplexTypeDefinition) {
		    	
				concreteChildComponent.resolveTypeDefinition(((XSDElementDeclaration)concreteChildComponent).getType().getName());

		    	if (((XSDElementDeclaration)concreteChildComponent).getType().getName() == null) {
		    		XSDComplexTypeDefinition complexTypeDef = (XSDComplexTypeDefinition) ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition();

		    		if (complexTypeDef.isSetDerivationMethod()) {
		    	    	ComplexElement complexElement = new ComplexElement();
		    	    	complexElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
		        		complexElement.setType(complexTypeDef.getBaseType().getName());
		        		complexElement.setExtension(true);
		    	    	tempComplexElementList.add(complexElement);	
		    	    	
//		        		System.out.print(complexTypeDef.getBaseType().getName()+" aaaa\n");

		    	    	if (complexTypeDef.getBaseType().getBaseType() != null) {
		    	    		searchComplexElements(complexTypeDef.getBaseType().getBaseType(), tempComplexElementList);
		    	    	} 
		    	    	searchComplexElements(complexTypeDef.getBaseType(), tempComplexElementList);
		    		}

		    	} else {
//		    		System.out.print(((XSDElementDeclaration)concreteChildComponent).getType().getName()+" WWWWWWWW\n");
			    	ComplexElement complexElement = new ComplexElement();
			    	complexElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
		    		complexElement.setType(((XSDElementDeclaration)concreteChildComponent).getType().getName());
		    		complexElement.setExtension(false);
			    	tempComplexElementList.add(complexElement);
			    	
			    	searchComplexElements(((XSDElementDeclaration)concreteChildComponent).getType(), tempComplexElementList);
		    	}
		    }
		}
	}
		
	public TreeMap<String, List<ComplexElementType>> compareMessageElementsSchemas(List<side.model.File> fileList) {
		
		TreeMap<String, List<ComplexElementType>> complexElListMap = new TreeMap<String, List<ComplexElementType>>();
		
	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
		ResourceSet resourceSet = new ResourceSetImpl();
		
		for (int i=0; i < fileList.size(); i++) {
			List<ComplexElement> tempComplexElementList = new ArrayList<ComplexElement>();
	
			XSDResourceImpl xsdSchemaResource = (XSDResourceImpl)resourceSet.getResource(URI.createFileURI(fileList.get(i).getPath()), true);
			XSDConcreteComponent concreteComponent = null;

			for (Iterator iter = xsdSchemaResource.getSchema().eContents().iterator(); iter.hasNext(); ) {
			    concreteComponent = (XSDConcreteComponent)iter.next();
			    
			    if (concreteComponent instanceof XSDElementDeclaration) {
			    	concreteComponent = (XSDElementDeclaration) concreteComponent;
			    	break;
			    }
			}
			
	//		System.out.print(xsdSchemaResource.getURI().toString().end+" dsa\n");
			for (Iterator iter = concreteComponent.eAllContents(); iter.hasNext(); ) {
			    XSDConcreteComponent concreteChildComponent = (XSDConcreteComponent)iter.next();
			    
			    if (concreteChildComponent instanceof XSDElementDeclaration
			    		&& ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition() instanceof XSDComplexTypeDefinition) {
			    	XSDComplexTypeDefinition complexTypeDef = (XSDComplexTypeDefinition) ((XSDElementDeclaration)concreteChildComponent).getTypeDefinition();
	
			    	if (((XSDElementDeclaration)concreteChildComponent).getType().getName() == null) {
			    		if (complexTypeDef.isSetDerivationMethod()) {
					    	ComplexElement complexElement = new ComplexElement();
					    	complexElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
				    		complexElement.setType(complexTypeDef.getBaseTypeDefinition().getName());
				    		complexElement.setExtension(true);
					    	tempComplexElementList.add(complexElement);
	
			    		}
	//		    		} else {
	//			    		complexElement.setType("Anonymous");
	//			    		complexElement.setExtension(false);
	//		    		}
			    	} else {
	//		    		System.out.print(((XSDElementDeclaration)concreteComponent).getType().getName()+" dsa\n");
				    	ComplexElement complexElement = new ComplexElement();
				    	complexElement.setName(((XSDElementDeclaration)concreteChildComponent).getName());
			    		complexElement.setType(((XSDElementDeclaration)concreteChildComponent).getType().getName());
			    		complexElement.setExtension(false);
				    	tempComplexElementList.add(complexElement);
	
			    	}
			    }
			}
			
			Collections.sort(tempComplexElementList, 
			new Comparator<ComplexElement>() {
				public int compare(ComplexElement el1, ComplexElement el2) {
					return el1.getName().compareTo(el2.getName());
				}
			});
			complexElListMap = rearrangeComplexElementTypes(fileList.get(i).getName().substring(0, fileList.get(i).getName().length()-4), tempComplexElementList, complexElListMap);
		}
		
		return complexElListMap;
	}
	
	

	
	private void prepareComplexElements() {
		
		TreeMap<String, List<ComplexElementType>> complexElListMap = new TreeMap<String, List<ComplexElementType>>();

		XSDTypeDefinition xsdTypeDefinition = getXsdSchemaResource().getSchema().getElementDeclarations().get(0).getTypeDefinition();
		Element rootElement = getXsdSchemaResource().getSchema().getElementDeclarations().get(0).getElement();
		
		List<ComplexElement> tempComplexElementList = new ArrayList<ComplexElement>();
		
//			System.out.print("File: "+rootElement.getAttribute("name")+"\n------------------\n");
			
			XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition)xsdTypeDefinition; // this jumps to xs:complexType
//			XSDParticle xsdParticle = (XSDParticle)xsdComplexTypeDefinition.getContentType(); // this jumps to xs:sequence
			
			for (Iterator iter = xsdComplexTypeDefinition.eAllContents(); iter.hasNext(); ) {
			    XSDConcreteComponent concreteComponent = (XSDConcreteComponent)iter.next();
			    
			    if (concreteComponent instanceof XSDElementDeclaration
			    		&& ((XSDElementDeclaration)concreteComponent).getTypeDefinition() instanceof XSDComplexTypeDefinition) {
				    
			    	XSDComplexTypeDefinition complexTypeDef = (XSDComplexTypeDefinition) ((XSDElementDeclaration)concreteComponent).getTypeDefinition();
//		    		System.out.print("name: "+((XSDElementDeclaration)concreteComponent).getName()+" type: "+((XSDElementDeclaration)concreteComponent).getType().getName()+" AAA\n");
			    	
			    	if (((XSDElementDeclaration)concreteComponent).getType().getName() == null) {
			    		if (complexTypeDef.isSetDerivationMethod()) {
					    	ComplexElement complexElement = new ComplexElement();
					    	complexElement.setName(((XSDElementDeclaration)concreteComponent).getName());
				    		complexElement.setType(complexTypeDef.getBaseTypeDefinition().getName());
				    		complexElement.setExtension(true);
					    	tempComplexElementList.add(complexElement);

			    		}
//			    		} else {
//				    		complexElement.setType("Anonymous");
//				    		complexElement.setExtension(false);
//			    		}
			    	} else {
//			    		System.out.print(((XSDElementDeclaration)concreteComponent).getType().getName()+" dsa\n");
				    	ComplexElement complexElement = new ComplexElement();
				    	complexElement.setName(((XSDElementDeclaration)concreteComponent).getName());
			    		complexElement.setType(((XSDElementDeclaration)concreteComponent).getType().getName());
			    		complexElement.setExtension(false);
				    	tempComplexElementList.add(complexElement);

			    	}

			    }
			}
			
			Collections.sort(tempComplexElementList, 
			new Comparator<ComplexElement>() {
				public int compare(ComplexElement el1, ComplexElement el2) {
					return el1.getName().compareTo(el2.getName());
				}
			});
			
			rearrangeComplexElementTypes(rootElement.getAttribute("name"), tempComplexElementList, complexElListMap);

	}
	
	private TreeMap<String, List<ComplexElementType>> rearrangeComplexElementTypes(String fileName, List<ComplexElement> tempComplexElementList, TreeMap<String, List<ComplexElementType>> complexElListMap) {
		
		List<ComplexElementType> complexElementTypeList = new ArrayList<ComplexElementType>();
		
		for (int k=0; k < tempComplexElementList.size(); k++) {
			ComplexElement complexElement1 = tempComplexElementList.get(k);
			int count=1;
			ComplexElementType complexElementType = new ComplexElementType();
			List<ComplexElement> tmp = new ArrayList<ComplexElement>();
			
			for(int l = tempComplexElementList.size()-1; l > k; l--) {
				ComplexElement complexElement2 = tempComplexElementList.get(l);
				
				if (complexElement1.getType().equals(complexElement2.getType())) {
					count++;
					tmp.add(complexElement2);
//					complexElementType.setComplexElement(complexElement2);
//					System.out.print(secondComplexElementList.get(l).getName()+" comp\n");
					tempComplexElementList.remove(l);
				}
			}
				tmp.add(complexElement1);
				complexElementType.setName(complexElement1.getType());
				complexElementType.setQuantity(count);
				complexElementType.setComplexElement(tmp);
				complexElementTypeList.add(complexElementType);
				
		}

		 complexElListMap.put(fileName, complexElementTypeList);
		 
		 return complexElListMap;
//		tempComplexElListMap.put(fileName, complexElementTypeList);
		
//		return tempComplexElListMap;
	}

	private boolean isProperFile(String fullFileName, String fileName) {
		
	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
		ResourceSet resourceSet = new ResourceSetImpl();
		XSDResourceImpl xsdSchemaResource = (XSDResourceImpl)resourceSet.getResource(URI.createFileURI(fullFileName), true);		
		boolean isProper = false;
		
		for (Iterator iter = xsdSchemaResource.getSchema().eContents().iterator(); iter.hasNext(); ) {
			Object element = iter.next();
		    if (element instanceof XSDElementDeclaration
		    		&& ((XSDElementDeclaration)element).getName().equals(fileName)) {
				isProper = true;
//				System.out.print(fullFileName+" fullFileName\n");
				setXsdSchemaResource(xsdSchemaResource);
				break;
		    }
		}
		return isProper;
	}
	
	public void inspectFiles(String directory) {

		File myDirectory = new File(directory);
		
		for (int i=0; i < myDirectory.listFiles().length; i++) {
			File file = myDirectory.listFiles()[i];
			
			if (file.isFile() && file.getName().endsWith(".xsd")) {
				
//				System.out.print(file.getPath()+" XXX "+file.getName()+" ddd\n");
				boolean isProper = isProperFile(file.getPath(), file.getName().substring(0, file.getName().length()-4));

				if (isProper) {
					prepareComplexElements();
//					break;
				}
			}	
		}
		
	}
	
//	private void setComplexElements() {
//		
//		for (int i=0; i < xsdLibraryList.size(); i++) {
//			String librarySchema = xsdLibraryList.get(i);
//			
////			if ()
//		}
//	}
//	
//	private void removeLibraryDuplicates() {
//		
//		HashSet h = new HashSet(xsdLibraryList);
//		xsdLibraryList.clear();
//		xsdLibraryList.addAll(h);
//	}
//	
//	private void obtainLibrarySchemas(String fullFileName) {
//		
//	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
//		ResourceSet resourceSet = new ResourceSetImpl();
//		XSDResourceImpl xsdSchemaResource = (XSDResourceImpl)resourceSet.getResource(URI.createFileURI(fullFileName), true);
//		
////		System.out.print(xsdSchemaResource.getURI().toString().end+" dsa\n");
//		for (Iterator iter = xsdSchemaResource.getSchema().eAllContents(); iter.hasNext(); ) {
//			Object element = iter.next();
//			
//		    if (element instanceof XSDInclude || element instanceof XSDImport) {
//		    	xsdLibraryList.add(((XSDInclude) element).getSchemaLocation());
//		    	System.out.print(element+ "test\n");
//		    }
//		    
//		    if (element instanceof XSDElementDeclaration
//		    		&& ((XSDElementDeclaration)element).getTypeDefinition() instanceof XSDComplexTypeDefinition) {
////		    	System.out.print(element+ "test3\n");
//
//		    }
//		}
//		
////		addComplexElements(xsdLibraryList);
//		
//		
////		XSDTypeDefinition xsdTypeDefinition = getXsdSchemaResource().getSchema().getElementDeclarations().get(0).getTypeDefinition();
////		Element rootElement = getXsdSchemaResource().getSchema().getElementDeclarations().get(0).getElement();
////		
////		List<ComplexElement> tempComplexElementList = new ArrayList<ComplexElement>();
////		
//////			System.out.print("File: "+rootElement.getAttribute("name")+"\n------------------\n");
////			
////			XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition)xsdTypeDefinition; // this jumps to xs:complexType
//////			XSDParticle xsdParticle = (XSDParticle)xsdComplexTypeDefinition.getContentType(); // this jumps to xs:sequence
////			
////			for (Iterator iter = xsdComplexTypeDefinition.eAllContents(); iter.hasNext(); ) {
////			    XSDConcreteComponent concreteComponent = (XSDConcreteComponent)iter.next();
////			    
////			    if (concreteComponent instanceof XSDElementDeclaration
////			    		&& ((XSDElementDeclaration)concreteComponent).getTypeDefinition() instanceof XSDComplexTypeDefinition) {
////				    
////			    	XSDComplexTypeDefinition complexTypeDef = (XSDComplexTypeDefinition) ((XSDElementDeclaration)concreteComponent).getTypeDefinition();
////			    	
////			    	
////			    	if (((XSDElementDeclaration)concreteComponent).getType().getName() == null) {
////			    		if (complexTypeDef.isSetDerivationMethod()) {
////					    	ComplexElement complexElement = new ComplexElement();
////					    	complexElement.setName(((XSDElementDeclaration)concreteComponent).getName());
////				    		complexElement.setType(complexTypeDef.getBaseTypeDefinition().getName());
////				    		complexElement.setExtension(true);
////					    	tempComplexElementList.add(complexElement);
////
////			    		}
//////			    		} else {
//////				    		complexElement.setType("Anonymous");
//////				    		complexElement.setExtension(false);
//////			    		}
////			    	} else {
////				    	ComplexElement complexElement = new ComplexElement();
////				    	complexElement.setName(((XSDElementDeclaration)concreteComponent).getName());
////			    		complexElement.setType(((XSDElementDeclaration)concreteComponent).getType().getName());
////			    		complexElement.setExtension(false);
////				    	tempComplexElementList.add(complexElement);
////
////			    	}
////
////			    }
////			}
////			
////			Collections.sort(tempComplexElementList, 
////			new Comparator<ComplexElement>() {
////				public int compare(ComplexElement el1, ComplexElement el2) {
////					return el1.getName().compareTo(el2.getName());
////				}
////			});
//			
////			rearrangeComplexElementTypes(rootElement.getAttribute("name"), tempComplexElementList);
//
//	}
//	
//	public TreeMap<String, List<ComplexElementType>> inspectFiles2(String directory) {
//
//		File myDirectory = new File(directory);
//		
//		for (int i=0; i < myDirectory.listFiles().length; i++) {
//			File file = myDirectory.listFiles()[i];
//			
//			if (file.isFile() && file.getName().endsWith(".xsd")) {
//				
////				System.out.print(file.getPath()+" XXX "+file.getName()+" ddd\n");
////				boolean isProper = isProperFile(file.getPath(), file.getName().substring(0, file.getName().length()-4));
////
////				if (isProper) {
//					obtainLibrarySchemas(file.getPath());
////					break;
////				}
//			}	
//		}
//		removeLibraryDuplicates();
//		
//		return complexElListMap;
//		
//	}
	
	public String unzip2(String strZipFile, String destinationTempFolder) {
    	
		List<side.model.File> fileList = null;
		side.model.File fileModel = null;
		
        try {
                File fSourceZip = new File(strZipFile);
              
                if (isValid(fSourceZip)) {
                	
                	fileList = new ArrayList<side.model.File>();
	                ZipFile zipFile = new ZipFile(fSourceZip);
	                Enumeration e = zipFile.entries();
	               
	                while(e.hasMoreElements()) {
	                    ZipEntry entry = (ZipEntry)e.nextElement();
	                    File destinationFile = new File(destinationTempFolder, entry.getName());
	              
	                    if (destinationFile.getParentFile().getName().equalsIgnoreCase("schemas")) {
		                    //create directories if required.
	                    	destinationFile.getParentFile().mkdirs();
		
		                    //if the entry is directory, leave it. Otherwise extract it.
		                    if(entry.isDirectory()) {
		                            continue;
		                    }
		                    else {

		                			if (destinationFile.getName().endsWith(".xsd")) {
		                				
		                				fileModel = new side.model.File();
			                            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
			                                                                                                        
			                            int b;
			                            byte buffer[] = new byte[1024];
		                				
			                            FileOutputStream fos = new FileOutputStream(destinationFile);
			                            BufferedOutputStream bos = new BufferedOutputStream(fos,1024);
			                            
			                            while ((b = bis.read(buffer, 0, 1024)) != -1) {
			                                   bos.write(buffer, 0, b);
			                            }
			                            
			                            fileModel.setName(destinationFile.getName());
			                            fileModel.setPath(destinationFile.getPath());
			                            
			                            fileList.add(fileModel);
			                           
			                            //flush the output stream and close it.
			                            bos.flush();
			                            bos.close();
			                           
			                            //close the input stream.
			                            bis.close();
		                			}
		                    }
	                    }
	                }
	                zipFile.close();
                } else {
                	fileList = null;
                }
                fSourceZip.delete();
        }
        catch(IOException ioe)
        {
                System.out.println("IOError :" + ioe);
        }
        
        return destinationTempFolder+"\\schemas";
//        return fileList;
	}
	
	public List<side.model.File> unzip(String strZipFile, String destinationTempFolder) {
        	
		List<side.model.File> fileList = null;
		side.model.File fileModel = null;
		
        try {
                /*
                 * STEP 1 : Create directory with the name of the zip file
                 *
                 * For e.g. if we are going to extract c:/demo.zip create c:/demo
                 * directory where we can extract all the zip entries
                 *
                 */
                File fSourceZip = new File(strZipFile);
              
                if (isValid(fSourceZip)) {
                	
                	fileList = new ArrayList<side.model.File>();
	                /*
	                 * STEP 2 : Extract entries while creating required
	                 * sub-directories
	                 *
	                 */
	                ZipFile zipFile = new ZipFile(fSourceZip);
	                Enumeration e = zipFile.entries();
	               
	                while(e.hasMoreElements()) {
	                    ZipEntry entry = (ZipEntry)e.nextElement();
	                    File destinationFile = new File(destinationTempFolder, entry.getName());
	              
	                    if (destinationFile.getParentFile().getName().equalsIgnoreCase("schemas")) {
		                    //create directories if required.
	                    	destinationFile.getParentFile().mkdirs();
		
		                    //if the entry is directory, leave it. Otherwise extract it.
		                    if(entry.isDirectory()) {
		                            continue;
		                    }
		                    else {
		//                                System.out.println("Extracting " + destinationFilePath);
		                           
		                            /*
		                             * read the current entry from the zip file, extract it
		                             * and write the extracted file.
		                             */
		                			if (destinationFile.getName().endsWith(".xsd")) {
		                				
		                				fileModel = new side.model.File();
		                				
			                            /*
			                             * Get the InputStream for current entry
			                             * of the zip file using
			                             *
			                             * InputStream getInputStream(Entry entry) method.
			                             */
			                            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
			                                                                                                        
			                            int b;
			                            byte buffer[] = new byte[1024];
		                				
			                            FileOutputStream fos = new FileOutputStream(destinationFile);
			                            BufferedOutputStream bos = new BufferedOutputStream(fos,1024);
			                            
			                            while ((b = bis.read(buffer, 0, 1024)) != -1) {
			                                   bos.write(buffer, 0, b);
			                            }
			                            
			                            fileModel.setName(destinationFile.getName());
			                            fileModel.setPath(destinationFile.getPath());
			                            
			                            fileList.add(fileModel);
			                           
			                            //flush the output stream and close it.
			                            bos.flush();
			                            bos.close();
			                           
			                            //close the input stream.
			                            bis.close();
			                            
		                			}
		                    }
	                    }
	                }
	                zipFile.close();
                } else {
                	fileList = null;
                }
                fSourceZip.delete();
        }
        catch(IOException ioe)
        {
                System.out.println("IOError :" + ioe);
        }
        
//        return destinationFolder+"\\schemas";
        return fileList;
	}
	
	public boolean isValid(final File file) {
	    ZipFile zipfile = null;
	    try {
	        zipfile = new ZipFile(file);
	        return true;
	    } catch (ZipException e) {
	        return false;
	    } catch (IOException e) {
	        return false;
	    } finally {
	        try {
	            if (zipfile != null) {
	                zipfile.close();
	                zipfile = null;
	            }
	        } catch (IOException e) {
	        }
	    }
	}
	
	public void deleteOnExit(File file) {
		if (file.isDirectory()) {
			   if (file.list().length == 0) {
				   file.deleteOnExit();
			   } else {
				   String files[] = file.list();
				   for (String temp : files) {
					   File fileDelete = new File(file, temp);
					   deleteFile(fileDelete);
				   }
				   
				   if (file.list().length == 0) {
					   file.deleteOnExit();
				   }
			   }
		   } else {
			   file.deleteOnExit();
		   }
	}

	public void deleteFile(File file) {
		if (file.isDirectory()) {
			   if (file.list().length == 0) {
				   file.delete();
			   } else {
				   String files[] = file.list();
				   for (String temp : files) {
					   File fileDelete = new File(file, temp);
					   deleteFile(fileDelete);
				   }
				   
				   if (file.list().length == 0) {
					   file.delete();
				   }
			   }
		   } else {
			   file.delete();
		   }
	}
}
