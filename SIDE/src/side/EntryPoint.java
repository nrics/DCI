package side;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class EntryPoint implements IEntryPoint {

    public int createUI() {
    	
    	Display display = PlatformUI.createDisplay();
    	int i = PlatformUI.createAndRunWorkbench( display, new ApplicationWorkbenchAdvisor() );
		return i;

//      Display display = new Display();
//      Shell shell = new Shell( display );
//      shell.setLayout( new GridLayout( 1, false ) );
//            shell.setMaximized(true);
//      shell.pack();
//      shell.open();
//      while( !shell.isDisposed() ) {
//        if( !display.readAndDispatch() ) {
//          display.sleep();
//        }
//      }
//      display.dispose();
//      return 0;
    }
  }
  
