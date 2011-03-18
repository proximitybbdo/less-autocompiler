/*
 * LesscApp.java
 */

package lessc;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import name.pachler.nio.file.*;
import org.jdesktop.application.FrameView;
import sun.rmi.rmic.iiop.DirectoryLoader;
import sun.tools.util.CommandLine;

/**
 * The main class of the application.
 */
public class LesscApp extends SingleFrameApplication {

    private Watcher watcher;
    private LesscView view;

    private final static String newline = "\n";

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        view = new LesscView(this);
        show(view);
        
        watcher = new Watcher(view.tblLog);
    }

    public void startWatching(String path){
        watcher.watch(path);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of LesscApp
     */
    public static LesscApp getApplication() {
        return Application.getInstance(LesscApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(LesscApp.class, args);
    }
}
