package org.frc3620.scout;

public class EndGameStats {
	private SuccessFail challenge = new SuccessFail(), scale = new SuccessFail();
	
	public void recordDidntSee() {
		challenge.skip();
		scale.skip();
	}
	
	public void recordDidntChallenge() {
		challenge.failure();
		scale.skip();
	}

	public void recordChallenged() {
		challenge.success();
		scale.skip();
	}

	public void recordChallengedThenFailedScale() {
		challenge.success();
		scale.failure();
	}

	public void recordScaled() {
		challenge.success();
		scale.success();
	}
	
	public int getChallenges() {
	  return challenge.getSuccesses();
	}

  public int getChallengeAttempts() {
    return challenge.getAttempts();
  }

  public int getScales() {
    return scale.getSuccesses();
  }

  public int getScaleAttempts() {
    return scale.getAttempts();
  }
  
  public double getScaleRatio() {
    return scale.getRatio();
  }
  
  @Override
  public String toString() {
    return "EndGameStats [challenge=" + challenge + ", cross=" + scale +  "]";
  }

}
