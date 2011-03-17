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

    public Timer tmr;
    public WatchService service;
    private String path;
    public Path watchedPath;
    public LesscView view;

    private final static String newline = "\n";

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        view = new LesscView(this);
        show(view);

        System.out.println("v0.1");
        
        initWatcher();
    }

    public void startWatching(String path){
        this.path = path;

        view.txtLog.append("Monitoring: " + this.path + newline);

        service = FileSystems.getDefault().newWatchService();
        watchedPath = Paths.get(path);

        WatchKey key = null;
        try {
            key = watchedPath.register(service, StandardWatchEventKind.ENTRY_MODIFY);
        } catch (UnsupportedOperationException uox){
            System.err.println("file watching not supported!");
            // handle this error here
        } catch (IOException iox){
            System.err.println("I/O errors");
        }

        tmr = new Timer();
        tmr.scheduleAtFixedRate(new CheckDirTask(service), 0, 50);
    }

    /**
     * 
     */
    private void initWatcher() {
      
    }

    class CheckDirTask extends TimerTask {
        private WatchService s;

        public CheckDirTask(WatchService service){
            this.s = service;
        }

        public void run(){
            WatchKey signalledKey;
            try {
                signalledKey = s.take();
                // get list of events from key
                List<WatchEvent<?>> list = signalledKey.pollEvents();

                // VERY IMPORTANT! call reset() AFTER pollEvents() to allow the
                // key to be reported again by the watch service
                signalledKey.reset();

                for(WatchEvent e : list){
                    String message = "";
                    if(e.kind() == StandardWatchEventKind.ENTRY_MODIFY){
                        Path context = (Path)e.context();
                        
                        String file = context.toString();
                        String filename = file.substring(0, file.lastIndexOf("."));
                        String ext = file.substring(file.lastIndexOf(".") + 1, file.length());

                        if(ext.equals("less")){
                            String target = filename + ".css";
                            String command = "cmd.exe /k cd " + path + " && lessc.cmd \"" + file + "\" \"" + target + "\"";
                            
                            view.txtLog.append("Sent " + file + " to the lessc compiler" + newline);

                            Runtime.getRuntime().exec(command);
                        }
                    } else if(e.kind() == StandardWatchEventKind.OVERFLOW){
                        message = "OVERFLOW: more changes happened than we could retreive";
                    }
                }
                 
            }  catch (IOException io){
                System.out.println("IO exc:" + io.toString());
            } catch (InterruptedException ix){
                System.out.println("InterruptedException");
            } catch (ClosedWatchServiceException cwse){
                // other thread closed watch service
                System.out.println("watch service closed, terminating.");
            }
             
             
        }
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

    @org.jdesktop.application.Action
    public void updatePath() {
    }
}
