package org.frc3620.scout;

public class AutoDefenseStats {
	private SuccessFail reach = new SuccessFail(), cross = new SuccessFail();
	private int spied = 0;
	
	public void recordDidntSee() {
		reach.skip();
		cross.skip();
	}
	
	public void recordSpied() {
    reach.skip();
    cross.skip();
    spied++;
	}
	
	public void recordDidntReach() {
		reach.failure();
		cross.skip();
	}

	public void recordReached() {
		reach.success();
		cross.failure();
	}

	public void recordCrossed() {
		reach.success();
		cross.success();
	}
	
	public int getReaches() {
	  return reach.getSuccesses();
	}
	
	public int getCrosses() {
	  return cross.getSuccesses();
	}
	
	public int getReachAttempts() {
	  return reach.getAttempts();
	}
	
	public int getCrossAttempts() {
	  return cross.getAttempts();
	}
	
	public double getReachRatio() {
	  return reach.getRatio();
	}
	
	public double getCrossRatio() {
	  return cross.getRatio();
	}
	
	public int getSpied() {
	  return spied;
	}

  @Override
  public String toString() {
    return "AutoDefenseStats [reach=" + reach + ", cross=" + cross + ", spied=" + spied + "]";
  }

}
