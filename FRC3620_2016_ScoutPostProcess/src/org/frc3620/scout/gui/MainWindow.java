package org.frc3620.scout.gui;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JSeparator;

import org.frc3620.scout.AllTeamStats;
import org.frc3620.scout.TeamStats;
import org.frc3620.scout.TeamStatsExtractor;
import org.frc3620.scout.TeamStatsExtractorTearesa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.java2d.Disposer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class MainWindow {
  public static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

  public JFrame frmWildstangScoutingData;

  private JTable table;

  private final Action exitAction = new SwingAction();

  private final Action saveAction = new SwingAction_1();

  private final Action openAction = new SwingAction_2();

  AllTeamStats allTeamStats;

  TeamStatsExtractor extractor;

  String[] sLabels;

  MyTableModel model;

  JFileChooser inputChooser = new JFileChooser(".");

  JFileChooser outputChooser = new JFileChooser(".");

  /**
   * Launch the application.
   */
  private static Object lock = new Object();

  public static void main(String[] args) {
    MainWindow window = new MainWindow();
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          window.setTeamStatsExtractor(new TeamStatsExtractorTearesa());
          window.frmWildstangScoutingData.setVisible(true);

          window.frmWildstangScoutingData.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
              logger.info("window closing");
              super.windowClosing(e);
            }

            @Override
            public void windowClosed(WindowEvent e) {
              logger.info("window closed");
              super.windowClosed(e);
              synchronized (lock) {
                lock.notify();
              }
            }

          });

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    synchronized (lock) {
      do {
        try {
          logger.info("waiting for lock");
          lock.wait();
          logger.info("got lock");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } while (window.frmWildstangScoutingData.isVisible());

      logger.info("done");
    }

  }

  DecimalFormatRenderer decimalFormatRenderer = new DecimalFormatRenderer();

  RightRenderer rightRenderer = new RightRenderer();

  public void setTeamStatsExtractor(TeamStatsExtractor _extractor) {
    extractor = _extractor;

    sLabels = extractor.getLabels();
    model = new MyTableModel(sLabels, 0);
    Class<?>[] columnClasses = extractor.getClasses();
    model.setColumnClasses(columnClasses);
    table.setModel(model);

    for (int i = 0; i < columnClasses.length; i++) {
      if (columnClasses[i].equals(Double.class)) {
        table.getColumnModel().getColumn(i).setCellRenderer(decimalFormatRenderer);
      } else {
        table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
      }
    }
  }

  /**
   * Create the application.
   */
  public MainWindow() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmWildstangScoutingData = new JFrame();
    frmWildstangScoutingData.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent arg0) {
        if (askIfOkToClose()) {
          frmWildstangScoutingData.dispose();
        }
      }
    });
    frmWildstangScoutingData.setTitle("WildStang Scouting Data Summarizer");
    frmWildstangScoutingData.setBounds(100, 100, 1167, 777);
    frmWildstangScoutingData.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    JMenuBar menuBar = new JMenuBar();
    frmWildstangScoutingData.setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);

    JMenuItem mntmOpen = new JMenuItem("Open");
    mntmOpen.setAction(openAction);
    mnFile.add(mntmOpen);

    JMenuItem mntmSave = new JMenuItem("Save");
    mntmSave.setAction(saveAction);
    mnFile.add(mntmSave);

    JSeparator separator = new JSeparator();
    mnFile.add(separator);

    JMenuItem mntmExit = new JMenuItem("Exit");
    mntmExit.setAction(exitAction);
    mnFile.add(mntmExit);

    JScrollPane scrollPane = new JScrollPane();
    frmWildstangScoutingData.getContentPane().add(scrollPane, BorderLayout.CENTER);

    table = new JTable();
    table.setAutoCreateRowSorter(true);
    table.setFillsViewportHeight(true);
    new ExcelAdapter(table);
    scrollPane.setViewportView(table);

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

  public void setData(Object[][] o) {
    while (model.getRowCount() > 0)
      model.removeRow(0);
    for (int i = 0; i < o.length; i++) {
      model.addRow(o[i]);
    }
    table.repaint();
  }

  boolean askIfOkToClose() {
    int confirmed = JOptionPane.showConfirmDialog(frmWildstangScoutingData,
        "Are you sure you want to exit the program?", "Exit Program Message Box",
        JOptionPane.YES_NO_OPTION);

    return (confirmed == JOptionPane.YES_OPTION);
  }

  private class SwingAction extends AbstractAction {
    public SwingAction() {
      putValue(NAME, "Exit");
      putValue(SHORT_DESCRIPTION, "Exit");
    }

    public void actionPerformed(ActionEvent e) {
      if (askIfOkToClose()) {
        frmWildstangScoutingData.dispose();
      }
    }
  }

  private class SwingAction_1 extends AbstractAction {
    public SwingAction_1() {
      putValue(NAME, "Save");
      putValue(SHORT_DESCRIPTION, "Save");
    }

    public void actionPerformed(ActionEvent e) {
      outputChooser = new JFileChooser();
      outputChooser.setDialogTitle("Specify a file to save");

      int userSelection = outputChooser.showSaveDialog(frmWildstangScoutingData);

      if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = outputChooser.getSelectedFile();
        try {
          allTeamStats.writeCsv(fileToSave.getAbsolutePath(), extractor);
        } catch (Exception ex) {
          new ExceptionDialog("Trobule writing " + fileToSave.getAbsolutePath(), ex);
        }
      }
    }
  }

  private class SwingAction_2 extends AbstractAction {
    public SwingAction_2() {
      putValue(ACTION_COMMAND_KEY, "");
      putValue(NAME, "Open");
      putValue(SHORT_DESCRIPTION, "Open");
    }

    public void actionPerformed(ActionEvent e) {
      int returnValue = inputChooser.showOpenDialog(frmWildstangScoutingData);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = inputChooser.getSelectedFile();
        allTeamStats = AllTeamStats.loadFromCsv(selectedFile.getName());
        List<Integer> teamNumbers = allTeamStats.getTeamNumbers();
        Object[][] d = new Object[teamNumbers.size()][sLabels.length];

        for (int i = 0; i < teamNumbers.size(); i++) {
          Integer teamNumber = teamNumbers.get(i);
          List<Object> values = extractor
              .getValues(allTeamStats.getTeamStats(teamNumber));
          for (int j = 0; j < values.size(); j++) {
            Object v = values.get(j);
            System.out.println("\"" + v + "\" " + v.getClass());
            d[i][j] = v;
          }
        }
        setData(d);
      }
    }
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
}
