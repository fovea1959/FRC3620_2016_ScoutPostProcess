package org.frc3620.scout;

public class TeamStats {
  int team;
  
  int matches;
  
  AutoDefenseStats autoDefense = new AutoDefenseStats();
  
  SuccessFail autoHighGoals = new SuccessFail(), autoLowGoals = new SuccessFail();
  
  SuccessFail teleopHighGoals = new SuccessFail(), teleopLowGoals = new SuccessFail();
  
  int fouls = 0;
  
  EndGameStats endGame = new EndGameStats();
  
  Rating defense = new Rating();

  @Override
  public String toString() {
    return "TeamStats [team=" + team + ", autoDefense=" + autoDefense
        + ", autoHighGoals=" + autoHighGoals + ", autoLowGoals=" + autoLowGoals
        + ", teleopHighGoals=" + teleopHighGoals + ", teleopLowGoals=" + teleopLowGoals
        + ", endGame=" + endGame + ", defense=" + defense + ", fouls=" + fouls + "]";
  }
  
  // machine failures;
}
