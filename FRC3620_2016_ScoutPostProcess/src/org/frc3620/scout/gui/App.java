package org.frc3620.scout.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.frc3620.scout.AllTeamStats;
import org.frc3620.scout.RenderFlags;
import org.frc3620.scout.TeamStatsExtractor;
import org.frc3620.scout.TeamStatsExtractorTearesa;
import org.frc3620.scout.gui.log4j2.DocumentAppender;
import org.slf4j.*;

public class App implements AppFrame.ActionAdapter {
  public static final Logger logger = LoggerFactory.getLogger(App.class);
  
  AppPreferences appPreferences = new AppPreferences();

  JFileChooser inputChooser = new JFileChooser(".");

  JFileChooser outputChooser = new JFileChooser(".");

  AppFrame appFrame;

  AllTeamStats allTeamStats;

  TeamStatsExtractor extractor;

  MyTableModel tableModel;
  TableColumnAdjuster tca;

  Dimension size;
  Point location;

  Lock lock = new ReentrantLock();

  Condition windowDone = lock.newCondition();

  boolean windowIsClosed = false;

  public App(String[] args) {
    appFrame = new AppFrame();
    appFrame.setActionAdapter(this);

    extractor = new TeamStatsExtractorTearesa();
    tableModel = new MyTableModel(extractor.getLabels(), 0);
    JTable jTable = appFrame.getTable();

    Class<?>[] columnClasses = extractor.getClasses();
    tableModel.setColumnClasses(columnClasses);
    jTable.setModel(tableModel);

    EnumSet<RenderFlags>[] renderFlags = extractor.getRenderFlags();
    for (int i = 0; i < renderFlags.length; i++) {
      jTable.getColumnModel().getColumn(i).setCellRenderer(new MySuperRenderer(renderFlags[i]));
    }
      
    new ExcelAdapter(appFrame.getTable());
    
    inputChooser.setCurrentDirectory(new File(appPreferences.getInputFilePath()));
    outputChooser.setCurrentDirectory(new File(appPreferences.getOutputFilePath()));
    
    {
      int x = appPreferences.getX();
      int y = appPreferences.getY();
      int w = appPreferences.getW();
      int h = appPreferences.getH();
      appFrame.setBounds(x, y, w, h);
      size = appFrame.getSize();
      location = appFrame.getLocation();
      
      if (appPreferences.getMaximized()) {
        appFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
      }
    }
    
    appFrame.addComponentListener(new ComponentListener() {
      
      @Override
      public void componentShown(ComponentEvent e) {
        recordSize(e);
      }
      
      @Override
      public void componentResized(ComponentEvent e) {
        recordSize(e);
      }
      
      @Override
      public void componentMoved(ComponentEvent e) {
        recordSize(e);
      }
      
      @Override
      public void componentHidden(ComponentEvent e) {
        recordSize(e);
      }
    });
    
    //appFrame.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    tca = new TableColumnAdjuster(appFrame.getTable());
    
    adjustColumns();
  }

  int go() {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          appFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
              logger.info("window closed");
              super.windowClosed(e);
              logger.info("swing getting lock");
              lock.lock();
              logger.info("swing got lock");
              try {
                windowIsClosed = true;
                windowDone.signalAll();
                logger.info("swing signalled");
              } finally {
                logger.info("swing releasing lock");
                lock.unlock();
                logger.info("swing released lock");
              }
            }
          });
          appFrame.setVisible(true);

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    DocumentFrame documentFrame = new DocumentFrame();
    EventQueue.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        documentFrame.getTextPane().setDocument(DocumentAppender.document);
        documentFrame.setVisible(true);
      }
    });

    do {
      logger.info("main waiting for lock");
      lock.lock();
      logger.info("main got lock");
      try {
        logger.info("main waiting for condition");
        windowDone.await();
        logger.info("main got condition");
      } catch (InterruptedException e) {
        logger.error("Interrupted", e);
      } finally {
        logger.info("main releasing lock");
        lock.unlock();
        logger.info("main released lock");
      }
    } while (!windowIsClosed);

    logger.info("main done, saving preferences");
    
    appPreferences.setInputFilePath(inputChooser.getCurrentDirectory().getPath());
    appPreferences.setOutputFilePath(outputChooser.getCurrentDirectory().getPath());
    
    appPreferences.setX((int) location.getX());
    appPreferences.setY((int) location.getY());
    appPreferences.setW((int) size.getWidth());
    appPreferences.setH((int) size.getHeight());
    logger.info("location {} size {} bounds {}", location, size, appFrame.getBounds());
    
    appPreferences.setMaximized(appFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH);
    
    return 0;
  }
  
  void recordSize(ComponentEvent e) {
    if (appFrame.getExtendedState() == JFrame.NORMAL) {
      size = appFrame.getSize();
      location = appFrame.getLocation();
      logger.info("resizing event {} happened; saving size {} location {}", e, size, location);
    }
  }

  public static void main(String[] args) {
    int rv = 0;
    try {
      App app = new App(args);
      rv = app.go();
    } catch (Exception e) {
      rv = 1;
    }
    System.exit(rv);
  }

  @Override
  public boolean fileOpen() {
    int returnValue = inputChooser.showOpenDialog(appFrame);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      File selectedFile = inputChooser.getSelectedFile();

      allTeamStats = AllTeamStats.loadFromCsv(selectedFile.getName());

      List<Integer> teamNumbers = allTeamStats.getTeamNumbers();

      while (tableModel.getRowCount() > 0)
        tableModel.removeRow(0);

      for (int i = 0; i < teamNumbers.size(); i++) {
        Integer teamNumber = teamNumbers.get(i);
        List<Object> values = extractor.getValues(allTeamStats.getTeamStats(teamNumber));
        tableModel.addRow(new Vector<>(values));
      }
      adjustColumns();
      // TODO is this necessary?
      //appFrame.getTable().repaint();
    }
    return true;
  }
  
  void adjustColumns() {
    tca.adjustColumns();
    // TODO set the max size of the frame 
    //appFrame.getContentPane().setMaximumSize(appFrame.getScrollPane().getPreferredSize());
    //appFrame.pack();
  }

  @Override
  public boolean fileSaveCsv() {
    outputChooser.setDialogTitle("Specify a file to save");

    int userSelection = outputChooser.showSaveDialog(appFrame);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File fileToSave = outputChooser.getSelectedFile();
      try {
        allTeamStats.writeCsv(fileToSave.getAbsolutePath(), extractor);
      } catch (Exception ex) {
        new ExceptionDialog("Trouble writing " + fileToSave.getAbsolutePath(), ex);
      }
    }
    return true;
  }

  @Override
  public boolean fileSaveExcel() {
    outputChooser.setDialogTitle("Specify a file to save");

    int userSelection = outputChooser.showSaveDialog(appFrame);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File fileToSave = outputChooser.getSelectedFile();
      try {
        allTeamStats.writeExcel(fileToSave.getAbsolutePath(), extractor);
      } catch (Exception ex) {
        new ExceptionDialog("Trouble writing " + fileToSave.getAbsolutePath(), ex);
      }
    }
    return true;
  }

  @Override
  public boolean fileExit() {
    int confirmed = JOptionPane.showConfirmDialog(appFrame,
        "Are you sure you want to exit the program?", "Exit Program Message Box",
        JOptionPane.YES_NO_OPTION);

    return (confirmed == JOptionPane.YES_OPTION);
  }

  @SuppressWarnings("serial")
  static class MySuperRenderer extends DefaultTableCellRenderer {
    boolean fixedFont = false;
    boolean ratio = false;
    public MySuperRenderer(EnumSet<RenderFlags> renderFlags) {
      super();
      // could control this with RenderFlags
      if (renderFlags.contains(RenderFlags.CENTERJUSTIFY)) {
        setHorizontalAlignment(JLabel.CENTER);
      } else if (renderFlags.contains(RenderFlags.LEFTJUSTIFY)) {
        setHorizontalAlignment(JLabel.LEFT);
      } else {
        setHorizontalAlignment(JLabel.RIGHT);
      }
      fixedFont = renderFlags.contains(RenderFlags.FIXEDWIDTH);
      ratio = renderFlags.contains(RenderFlags.RATIO);
    }

    private static final DecimalFormat formatter = new DecimalFormat("0.000");
    private static final Font font = new Font("monospaced", Font.PLAIN, 12);

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      if (value != null && value instanceof Number && ratio) {
        value = formatter.format((Number) value);
      }
      // And pass it on to parent class
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
          column);
      if (fixedFont) setFont(font);
      return this;
    }
  }

  @SuppressWarnings("serial")
  class MyTableModel extends DefaultTableModel {
    public MyTableModel(Object[] columnNames, int rowCount) {
      super(columnNames, rowCount);
    }

    Class<?>[] classes = null;

    public void setColumnClasses(Class<?>[] _classes) {
      classes = _classes;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (classes == null)
        return String.class;
      return classes[columnIndex];
    }
  }

}
