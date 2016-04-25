package org.frc3620.scout.gui;

import java.awt.BorderLayout;
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
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Rectangle;
import java.awt.Frame;

public class AppFrame extends JFrame {

  private JPanel contentPane;
  private JTable table;
  
  private ActionAdapter actionAdapter;

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
    setExtendedState(Frame.MAXIMIZED_BOTH);
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
    
    JMenuItem mntmSave = new JMenuItem("Save");
    mntmSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (actionAdapter != null) actionAdapter.fileSave();
      }
    });
    mnFile.add(mntmSave);
    
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
    
    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);
    
    table = new JTable();
    table.setAutoCreateRowSorter(true);
    scrollPane.setViewportView(table);
  }
  
  public JTable getTable() {
    return table;
  }
  
  static public interface ActionAdapter {
    public boolean fileOpen();
    public boolean fileSave();
    public boolean fileExit();
  }

  public void setActionAdapter(ActionAdapter actionAdapter) {
    this.actionAdapter = actionAdapter;
  }

}
