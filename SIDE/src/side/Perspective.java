package side;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import side.gui.FileListView;
import side.gui.FileUploadView;
import side.gui.MessageView;
import side.gui.InterMessageView;

/**
 * Configures the perspective layout. This class is contributed 
 * through the plugin.xml.
 */
public class Perspective implements IPerspectiveFactory {
	
//	public static final String ID = "side.perspective";


	public void createInitialLayout(IPageLayout layout) {
		
		layout.addStandaloneView(FileUploadView.ID,  false, IPageLayout.LEFT, 0.20f, layout.getEditorArea());
		layout.addStandaloneView(FileListView.ID,  false, IPageLayout.BOTTOM, 0.15f, FileUploadView.ID);

		IFolderLayout folder = layout.createFolder("MessageFolder", IPageLayout.RIGHT, 0.80f, layout.getEditorArea());
		folder.addPlaceholder(MessageView.ID + ":*");
		folder.addView(MessageView.ID);
		folder.addView(InterMessageView.ID);
		
		layout.setFixed(true);
		layout.setEditorAreaVisible(false);
		
		layout.getViewLayout(FileUploadView.ID).setCloseable(false);
		layout.getViewLayout(FileUploadView.ID).setMoveable(false);
		
		layout.getViewLayout(FileListView.ID).setCloseable(false);
		layout.getViewLayout(FileListView.ID).setMoveable(false);
		
		layout.getViewLayout(InterMessageView.ID).setCloseable(false);
		layout.getViewLayout(InterMessageView.ID).setMoveable(false);
		
		layout.getViewLayout(MessageView.ID).setCloseable(false);
		layout.getViewLayout(MessageView.ID).setMoveable(false);
	}
}
