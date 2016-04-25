package org.frc3620.scout.gui;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppPreferences {
  Preferences preferences = Preferences.userNodeForPackage(getClass());
  Logger logger = LoggerFactory.getLogger(getClass());
  
  private static final String INPUTFILEPATH = "inputFilePath";
  private static final String OUTPUTFILEPATH = "outputFilePath";
  private static final String WINDOWX = "windowX";
  private static final String WINDOWY = "windowY";
  private static final String WINDOWW = "windowW";
  private static final String WINDOWH = "windowH";
  private static final String WINDOWMAXIMIZED = "windowMaximized";

  public String getInputFilePath() {
    return g(INPUTFILEPATH, ".");
  }
  public void setInputFilePath(String v) {
    s(INPUTFILEPATH, v);
  }
  public String getOutputFilePath() {
    return g(OUTPUTFILEPATH, ".");
  }
  public void setOutputFilePath(String v) {
    s(OUTPUTFILEPATH, v);
  }
  public int getX() {
    return gInt(WINDOWX, 0);
  }
  public void setX (int v) {
    s(WINDOWX, v);
  }
  public int getY() {
    return gInt(WINDOWY, 0);
  }
  public void setY (int v) {
    s(WINDOWY, v);
  }
  public int getW() {
    return gInt(WINDOWW, 1200);
  }
  public void setW (int v) {
    s(WINDOWW, v);
  }
  public int getH() {
    return gInt(WINDOWH, 800);
  }
  public void setH (int v) {
    s(WINDOWH, v);
  }
  public boolean getMaximized() {
    return gBoolean(WINDOWMAXIMIZED, false);
  }
  public void setMaximized (boolean v) {
    s(WINDOWMAXIMIZED, v);
  }
  private String g (String l, String d) {
    String rv = preferences.get(l,  d);
    logger.info("Fetching preference '{}' = '{}'", l, rv);
    return rv;
  }
  private int gInt (String l, int d) {
    int rv = preferences.getInt(l,  d);
    logger.info("Fetching preference '{}' = '{}'", l, rv);
    return rv;
  }
  private boolean gBoolean (String l, boolean d) {
    boolean rv = preferences.getBoolean(l,  d);
    logger.info("Fetching preference '{}' = '{}'", l, rv);
    return rv;
  }
  private void s (String l, String v) {
    logger.info("Saving preference '{}' = '{}'", l, v);
    preferences.put(l, v);
  }
  private void s (String l, int v) {
    logger.info("Saving preference '{}' = '{}'", l, v);
    preferences.putInt(l, v);
  }
  private void s (String l, boolean v) {
    logger.info("Saving preference '{}' = '{}'", l, v);
    preferences.putBoolean(l, v);
  }
}
