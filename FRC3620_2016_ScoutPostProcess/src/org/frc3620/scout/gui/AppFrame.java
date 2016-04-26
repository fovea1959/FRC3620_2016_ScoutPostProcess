package org.frc3620.scout.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class AppFrame extends JFrame {

  private JPanel contentPane;
  private JTable table;
  
  private ActionAdapter actionAdapter;
  private JScrollPane scrollPane;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          AppFrame frame = new AppFrame();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public AppFrame() {
    setTitle("Average Joes 2016 Scouting Data Summarizer");
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent arg0) {
        if (actionAdapter != null) {
          if (actionAdapter.fileExit()) {
            dispose();
          }
        } else {
          dispose();
        }
      }
    });
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setBounds(100, 100, 1117, 786);
    
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);
    
    JMenuItem mntmOpen = new JMenuItem("Open");
    mntmOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (actionAdapter != null) actionAdapter.fileOpen();
      }
    });
    mnFile.add(mntmOpen);
    
    JMenuItem mntmSaveCsv = new JMenuItem("Save CSV");
    mntmSaveCsv.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (actionAdapter != null) actionAdapter.fileSaveCsv();
      }
    });
    mnFile.add(mntmSaveCsv);
    
    JMenuItem mntmSaveExcel = new JMenuItem("Save Excel");
    mntmSaveExcel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (actionAdapter != null) actionAdapter.fileSaveExcel();
      }
    });
    mnFile.add(mntmSaveExcel);
    
    JSeparator separator = new JSeparator();
    mnFile.add(separator);
    
    JMenuItem mntmExit = new JMenuItem("Exit");
    mntmExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (actionAdapter != null) {
          if (actionAdapter.fileExit()) {
            dispose();
          }
        } else {
          dispose();
        }
      }
    });
    mnFile.add(mntmExit);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);
    
    table = new JTable();
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setAutoCreateRowSorter(true);
    scrollPane.setViewportView(table);
  }
  
  public JTable getTable() {
    return table;
  }
  
  public Component getScrollPane() {
    Component c = table.getParent().getParent();
    return c;
  }
  
  static public interface ActionAdapter {
    public boolean fileOpen();
    public boolean fileSaveCsv();
    public boolean fileSaveExcel();
    public boolean fileExit();
  }

  public void setActionAdapter(ActionAdapter actionAdapter) {
    this.actionAdapter = actionAdapter;
  }

}
