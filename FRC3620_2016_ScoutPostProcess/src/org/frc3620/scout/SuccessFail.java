package org.frc3620.scout;

public class SuccessFail {
  private int successes, attempts;
  
  StringBuilder sparkLine = new StringBuilder();
  
  public void success() {
    attempts++;
    successes++;
    sparkLine.append("*");
  }
  
  public void failure() {
    attempts++;
    sparkLine.append("-");
  }
  
  public void success(int n) {
    attempts += n;
    successes += n;
    for (int i = 0; i < n; i++ ) sparkLine.append("*");
  }
  
  public void failure(int n) {
    attempts += n;
    for (int i = 0; i < n; i++ ) sparkLine.append("-");
  }
  
  public void skip() {
    sparkLine.append("\u00b7");
  }
  
  public int getSuccesses() {
    return successes;
  }
  
  public int getAttempts() {
    return attempts;
  }
  
  public String getSparkLine() {
    return sparkLine.toString();
  }
  
  public double getRatio() {
    return Util.ratio(successes,  attempts);
  }

  @Override
  public String toString() {
    return "[" + successes + "/" + attempts + "]";
  }

}
