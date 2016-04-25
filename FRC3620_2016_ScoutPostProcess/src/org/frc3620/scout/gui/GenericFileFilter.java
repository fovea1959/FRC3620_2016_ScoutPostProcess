package org.frc3620.scout.gui;

import java.io.File;

public class GenericFileFilter extends javax.swing.filechooser.FileFilter {
  private String[] fileExts;
  private String description;

  /**
   * This is the constructor - it takes in the following:-<br>
   * filesExtsIn - this is the array of file extensions that you wish to create
   * a file filter for. <br>
   * description - this is the description that will be displayed in the file
   * chooser dialog box.
   */
  public GenericFileFilter(String[] filesExtsIn, String description) {
    fileExts = filesExtsIn;
    this.description = description;
  }

  /**
   * This is the constructor - it takes in the following:-<br>
   * filesExtIn - this is the file extension that you wish to create a file filter for.<br>
   * description - this is the description that will be displayed in the file
   * chooser dialog box.
   */
  public GenericFileFilter(String filesExtIn, String description) {
    fileExts = new String[] { filesExtIn };
    this.description = description;
  }

  /**
   * This is the method to allow a file to bee added to the displayed list or
   * not. This method is called by the model that handles the FileChooser dialog
   */
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    String extension = getExtension(f);
    if (extension != null) {
      for (int i = 0; i < fileExts.length; i++) {
        if (extension.equalsIgnoreCase(fileExts[i]))
          return true;
      }
    }
    return false;
  }

  /**
   * This is the method defined by the model
   */
  public String getDescription() {
    return description;
  }

  /**
   * This is the method to get the file extension from the file name
   */
  private String getExtension(File file) {
    String filename = file.getName();
    int i = filename.lastIndexOf('.');
    if (i >= 0)
      return filename.substring(i).toLowerCase();
    return null;
  }
}
