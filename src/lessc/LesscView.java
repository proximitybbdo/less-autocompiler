/*
 * LesscView.java
 */

package lessc;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * The application's main frame.
 */
public class LesscView extends FrameView {

    private LesscApp app;
    private JFileChooser fc;

    public LesscView(SingleFrameApplication app) {
        super(app);

        this.app = (LesscApp) app;

        // file chooser
        fc = new JFileChooser();
        fc.setDialogTitle("Select a folder to watch");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = LesscApp.getApplication().getMainFrame();
            aboutBox = new LesscAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        LesscApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel(){
            protected void paintComponent(java.awt.Graphics g)
            {
                super.paintComponent( g );
                int panelHeight = getHeight();
                int panelWidth = getWidth();

                java.awt.Color start = new java.awt.Color(58, 82, 132);
                java.awt.Color end = new java.awt.Color(32, 47, 78);

                java.awt.GradientPaint gradientPaint = new java.awt.GradientPaint( 0 , 0 , start , 0, panelHeight , end );
                if( g instanceof java.awt.Graphics2D )
                {
                    java.awt.Graphics2D graphics2D = (java.awt.Graphics2D)g;
                    graphics2D.setPaint( gradientPaint );
                    graphics2D.fillRect( 0 , 0 , panelWidth , panelHeight );
                }
            }
        };
        lblFolder = new javax.swing.JLabel();
        txtFolder = new javax.swing.JTextField();
        btnChooseFile = new javax.swing.JButton();
        btnMonitor = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLog = new javax.swing.JTable();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(lessc.LesscApp.class).getContext().getResourceMap(LesscView.class);
        mainPanel.setBackground(resourceMap.getColor("Less Autocompiler.background")); // NOI18N
        mainPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        mainPanel.setDoubleBuffered(false);
        mainPanel.setMinimumSize(new java.awt.Dimension(650, 300));
        mainPanel.setName("Less Autocompiler"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(650, 300));
        mainPanel.setSize(new java.awt.Dimension(650, 300));
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(10650, 40));
        jPanel1.setMinimumSize(new java.awt.Dimension(650, 40));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(650, 90));
        jPanel1.setSize(new java.awt.Dimension(650, 90));

        lblFolder.setForeground(resourceMap.getColor("lblPath.foreground")); // NOI18N
        lblFolder.setText(resourceMap.getString("lblPath.text")); // NOI18N
        lblFolder.setName("lblPath"); // NOI18N
        jPanel1.add(lblFolder);

        txtFolder.setText(resourceMap.getString("txtPath.text")); // NOI18N
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(lessc.LesscApp.class).getContext().getActionMap(LesscView.class, this);
        txtFolder.setAction(actionMap.get("updatePath")); // NOI18N
        txtFolder.setMinimumSize(new java.awt.Dimension(424, 28));
        txtFolder.setName("txtPath"); // NOI18N
        txtFolder.setPreferredSize(new java.awt.Dimension(300, 28));
        txtFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFolderActionPerformed(evt);
            }
        });
        jPanel1.add(txtFolder);

        btnChooseFile.setText(resourceMap.getString("btnChooseFile.text")); // NOI18N
        btnChooseFile.setName("btnChooseFile"); // NOI18N
        btnChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseFileActionPerformed(evt);
            }
        });
        jPanel1.add(btnChooseFile);

        btnMonitor.setText(resourceMap.getString("btnMonitor.text")); // NOI18N
        btnMonitor.setName("btnMonitor"); // NOI18N
        btnMonitor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonitorActionPerformed(evt);
            }
        });
        jPanel1.add(btnMonitor);

        mainPanel.add(jPanel1);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblLog.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Time", "Logline"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLog.setGridColor(resourceMap.getColor("tblLog.gridColor")); // NOI18N
        tblLog.setName("tblLog"); // NOI18N
        tblLog.setRowHeight(20);
        tblLog.setSelectionBackground(resourceMap.getColor("tblLog.selectionBackground")); // NOI18N
        tblLog.setShowGrid(true);
        tblLog.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblLog);
        tblLog.getColumnModel().getColumn(0).setMinWidth(60);
        tblLog.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblLog.getColumnModel().getColumn(0).setMaxWidth(60);
        tblLog.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblLog.columnModel.title0")); // NOI18N
        tblLog.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblLog.columnModel.title1")); // NOI18N

        mainPanel.add(jScrollPane1);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 345, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void txtFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFolderActionPerformed
        app.startWatching(txtFolder.getText());
    }//GEN-LAST:event_txtFolderActionPerformed

    private void btnMonitorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonitorActionPerformed
        app.startWatching(txtFolder.getText());
    }//GEN-LAST:event_btnMonitorActionPerformed

    private void btnChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseFileActionPerformed
        int returnval = fc.showOpenDialog(null);
        if(returnval == fc.APPROVE_OPTION){
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();

            app.watcher.addLogline("Folder selected: " + path);
            txtFolder.setText(path);
            app.startWatching(path);
        } else {
            app.watcher.addLogline("No folder selected...");
        }
    }//GEN-LAST:event_btnChooseFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseFile;
    private javax.swing.JButton btnMonitor;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFolder;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    public javax.swing.JTable tblLog;
    public javax.swing.JTextField txtFolder;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
