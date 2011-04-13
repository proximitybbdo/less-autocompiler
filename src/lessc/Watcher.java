/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lessc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private String lastline;

    private String LESS_EXTENSION = "less";
    private Integer TIMER_INTERVAL = 500;

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

    public void addLogline(String log) {
        DefaultTableModel sc = (DefaultTableModel) logger.getModel();
        sc.addRow(new Object[] { now(), log });

        // scroll to last line
        logger.scrollRectToVisible(logger.getCellRect(logger.getRowCount()-1, 0, true));

        System.err.println(now() + " " + log);
    }

    private String now() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    class CheckDirTask extends TimerTask {
        private WatchService service;
        private Watcher watcher;

        public CheckDirTask(WatchService service, Watcher watcher){
            this.service = service;
            this.watcher = watcher;
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

                watcher.lastline = "";

                for(WatchEvent e : list){
                    String message = "";
                    if(e.kind() == StandardWatchEventKind.ENTRY_MODIFY){
                        Path context = (Path)e.context();

                        String file = context.toString();
                        String filename = file.substring(0, file.lastIndexOf("."));
                        String ext = file.substring(file.lastIndexOf(".") + 1, file.length());

                        if(ext.equals("less")){

                            // check for fast double messages
                            if(context.toString().equals(watcher.lastline))
                                return;
                            else
                                watcher.lastline = context.toString();

                            String target = filename + ".css";
                            
                            String command = "cmd.exe /C cd " + path + " && lessc \"" + file + "\" \"" + target + "\"";
                            addLogline("Sent " + file + " to the lessc compiler" + newline);

                            try {
                                Runtime rt = Runtime.getRuntime();
                                Process proc = rt.exec(command);

                                // any error message?
                                StreamGobbler errorGobbler = new
                                    StreamGobbler(proc.getErrorStream(), "ERROR", watcher);

                                // any output?
                                StreamGobbler outputGobbler = new
                                    StreamGobbler(proc.getInputStream(), "OUTPUT", watcher);

                                // kick them off
                                errorGobbler.start();
                                outputGobbler.start();

                                // any error???
                                int exitVal = proc.waitFor();
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    } else if(e.kind() == StandardWatchEventKind.OVERFLOW){
                        addLogline("OVERFLOW: more changes happened than we could retreive");
                    }
                }

                list = null;
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
        tmr.scheduleAtFixedRate(new CheckDirTask(service, this), 0, TIMER_INTERVAL);
    }
}

class StreamGobbler extends Thread {
    InputStream is;
    String type;
    Watcher watcher;

    StreamGobbler(InputStream is, String type, Watcher watcher) {
        this.is = is;
        this.type = type;
        this.watcher = watcher;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            String total="";
            while ( (line = br.readLine()) != null)
                total = total.concat(line);

            if(!total.equals(""))
                watcher.addLogline(total);
        } catch (IOException ioe) {
                ioe.printStackTrace();
        }
    }
}