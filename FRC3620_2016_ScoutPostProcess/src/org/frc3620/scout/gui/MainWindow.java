package org.frc3620.scout.gui;

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
import org.frc3620.scout.TeamStatsCsv;
import org.frc3620.scout.TeamStatsCsvTearesa;

import sun.java2d.Disposer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class MainWindow {

  public JFrame frmWildstangScoutingData;

  private JTable table;
  private final Action exitAction = new SwingAction();
  private final Action saveAction = new SwingAction_1();
  private final Action openAction = new SwingAction_2();

  AllTeamStats allTeamStats;
  TeamStatsCsv tsc = new TeamStatsCsvTearesa();

      String[] sLabels;


  /**
   * Launch the application.
   */
  public static void main (String[] args) {
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MainWindow window = new MainWindow();
          
          window.frmWildstangScoutingData.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
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
    
    // my stuff
    List<String> labels = tsc.labels();
    sLabels = new String[labels.size()];
    sLabels = labels.toArray(sLabels);
    
    String[][] d = new String[0][labels.size()];
    
    setData(d);
  }

  public void setData(Object[][] o) {
    table.setModel(new DefaultTableModel(o, sLabels));
    
    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
    for (int i = 0; i < sLabels.length; i++) {
      table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
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
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Specify a file to save");    

      int userSelection = fileChooser.showSaveDialog(frmWildstangScoutingData);

      if (userSelection == JFileChooser.APPROVE_OPTION) {
          File fileToSave = fileChooser.getSelectedFile();
          try {
          allTeamStats.writeCsv(fileToSave.getName(), tsc);
          } catch (Exception ex) {
            
          }
      }
    }
  }
  DecimalFormat f3Formatter = new DecimalFormat("0.000");
  private class SwingAction_2 extends AbstractAction {
    public SwingAction_2() {
      putValue(ACTION_COMMAND_KEY, "");
      putValue(NAME, "Open");
      putValue(SHORT_DESCRIPTION, "Open");
    }
    public void actionPerformed(ActionEvent e) {
      JFileChooser fileChooser = new JFileChooser();
      int returnValue = fileChooser.showOpenDialog(frmWildstangScoutingData);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        allTeamStats = AllTeamStats.loadFromCsv(selectedFile.getName());
        List<Integer> teamNumbers = allTeamStats.getTeamNumbers();
        Object[][] d = new Object[teamNumbers.size()][sLabels.length];
        
        for (int i = 0; i < teamNumbers.size(); i++) {
          Integer teamNumber = teamNumbers.get(i);
          List<Object> values = tsc.values(allTeamStats.getTeamStats(teamNumber));
          for (int j = 0; j < values.size(); j++) {
            Object v = values.get(j);
            System.out.println ("\"" + v + "\" " + v.getClass());
            if (v instanceof Double) {
              v = f3Formatter.format(v);
              System.out.println ("** \"" + v + "\" " + v.getClass());
            }
            d[i][j] = v;
          }
        }
        setData(d);
      }
    }
  }
}
