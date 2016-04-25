package org.frc3620.scout.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
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
import org.frc3620.scout.TeamStatsExtractor;
import org.frc3620.scout.TeamStatsExtractorTearesa;
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

  RightRenderer rightRenderer = new RightRenderer();

  DecimalFormatRenderer decimalFormatRenderer = new DecimalFormatRenderer();
  
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

    for (int i = 0; i < columnClasses.length; i++) {
      if (columnClasses[i].equals(Double.class)) {
        jTable.getColumnModel().getColumn(i).setCellRenderer(decimalFormatRenderer);
      } else {
        jTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
      }
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
  }

  int go() {
    EventQueue.invokeLater(new Runnable() {
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
      appFrame.getTable().repaint();
    }
    return true;
  }

  @Override
  public boolean fileSave() {
    outputChooser.setDialogTitle("Specify a file to save");

    int userSelection = outputChooser.showSaveDialog(appFrame);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File fileToSave = outputChooser.getSelectedFile();
      try {
        allTeamStats.writeCsv(fileToSave.getAbsolutePath(), extractor);
      } catch (Exception ex) {
        new ExceptionDialog("Trobule writing " + fileToSave.getAbsolutePath(), ex);
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
  static class RightRenderer extends DefaultTableCellRenderer {
    public RightRenderer() {
      super();
      setHorizontalAlignment(JLabel.RIGHT);
    }
  }

  @SuppressWarnings("serial")
  static class DecimalFormatRenderer extends DefaultTableCellRenderer {
    public DecimalFormatRenderer() {
      super();
      setHorizontalAlignment(JLabel.RIGHT);
    }

    private static final DecimalFormat formatter = new DecimalFormat("0.000");

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      // First format the cell value as required

      value = formatter.format((Number) value);

      // And pass it on to parent class

      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
          column);
    }
  }

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
