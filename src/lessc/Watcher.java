/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lessc;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import name.pachler.nio.file.ClosedWatchServiceException;
import name.pachler.nio.file.FileSystems;
import name.pachler.nio.file.Path;
import name.pachler.nio.file.Paths;
import name.pachler.nio.file.StandardWatchEventKind;
import name.pachler.nio.file.WatchEvent;
import name.pachler.nio.file.WatchKey;
import name.pachler.nio.file.WatchService;

/**
 *
 * @author jeroenb
 */
public class Watcher {
    private Timer tmr;
    private WatchService service;
    private String path;
    private Path watchedPath;
    private LesscView view;
    private JTable logger;

    private String LESS_EXTENSION = "less";
    private Integer TIMER_INTERVAL = 50;

    private final static String newline = "\n";

    public Watcher(JTable logger) {
        initTimer();
        this.logger = logger;
    }

    /**
     * starts watching a folder for less files
     * @param path
     */
    public void watch(String path) {
        this.path = path;

        service = FileSystems.getDefault().newWatchService();
        watchedPath = Paths.get(path);

        WatchKey key = null;
        try {
            key = watchedPath.register(service, StandardWatchEventKind.ENTRY_MODIFY);
            addLogline("Monitoring: " + path + newline);
        } catch (UnsupportedOperationException uox){
            addLogline("file watching not supported!");
        } catch (IOException iox){
            addLogline("I/O error: " + iox.toString());
        }

        startTimer();
    }
    
    private void addLogline(String log) {
        DefaultTableModel sc = (DefaultTableModel) logger.getModel();
        sc.addRow(new Object[] { now(), log });

        System.err.println(now() + " " + log);
    }

    private String now() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    class CheckDirTask extends TimerTask {
        private WatchService service;

        public CheckDirTask(WatchService service){
            this.service = service;
        }

        public void run(){
            WatchKey signalledKey;
            try {
                signalledKey = service.take();
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

                            addLogline("Sent " + file + " to the lessc compiler" + newline);

                            Runtime.getRuntime().exec(command);
                        }
                    } else if(e.kind() == StandardWatchEventKind.OVERFLOW){
                        addLogline("OVERFLOW: more changes happened than we could retreive");
                    }
                }

                list = null;

            }  catch (IOException io){
                addLogline("IO exc:" + io.toString());
            } catch (InterruptedException ix){
                addLogline("InterruptedException: " + ix.toString());
            } catch (ClosedWatchServiceException cwse){
                // other thread closed watch service
                addLogline("watch service closed, terminating.");
            }
        }
    }

    private void initTimer() {
        tmr = new Timer();
    }

    private void startTimer() {
        tmr.purge();
        tmr.scheduleAtFixedRate(new CheckDirTask(service), 0, TIMER_INTERVAL);
    }
}
