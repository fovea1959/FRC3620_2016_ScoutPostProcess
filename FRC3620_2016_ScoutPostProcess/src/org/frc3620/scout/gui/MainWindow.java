package org.frc3620.scout.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MainWindow {

  public JFrame frame;

  private JTable table;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MainWindow window = new MainWindow();
          window.frame.setVisible(true);
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
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);

    JMenuItem mntmOpen = new JMenuItem("Open");
    mnFile.add(mntmOpen);

    JMenuItem mntmSave = new JMenuItem("Save");
    mnFile.add(mntmSave);

    JMenuItem mntmExit = new JMenuItem("Exit");
    mnFile.add(mntmExit);

    JScrollPane scrollPane = new JScrollPane();
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

    table = new JTable();
    table.setFillsViewportHeight(true);
    new ExcelAdapter(table);
    scrollPane.setViewportView(table);
  }

  public void setData(String[] headers, Object[][] o) {
    table.setModel(new DefaultTableModel(o, headers));
    table.repaint();
  }

}
