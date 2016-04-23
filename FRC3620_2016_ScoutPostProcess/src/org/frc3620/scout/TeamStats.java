package org.frc3620.scout;

public class TeamStats {
  int team;
  
  ThreeState autoDefense = new ThreeState();
  
  SuccessFail autoHighGoals = new SuccessFail(), autoLowGoals = new SuccessFail();
  
  SuccessFail teleopHighGoals = new SuccessFail(), teleopLowGoals = new SuccessFail();
  
  ThreeState challengeScales = new ThreeState();
  
  Rating defense = new Rating();
  
  // machine failures;
}
