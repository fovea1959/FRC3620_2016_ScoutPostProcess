package org.frc3620.scout;

public class TeamStatsCsvTearesa extends TeamStatsCsv {

  @Override
  void addAllGetters() {
    addGetter("team", (t) -> t.team);

    addGetter("auto.reach", (t) -> t.autoDefense.getReaches());
    addGetter("auto.attempts", (t) -> t.autoDefense.getReachAttempts());
    addGetter("auto.cross", (t) -> t.autoDefense.getCrosses());
    addGetter("auto.reach.ratio", (t) -> t.autoDefense.getReachRatio());
    addGetter("auto.cross.ratio", (t) -> t.autoDefense.getCrossRatio());

    addGetter("auto.high", (t) -> t.autoHighGoals.getSuccesses());
    addGetter("auto.low", (t) -> t.autoLowGoals.getSuccesses());

    addGetter("tele.high", (t) -> t.teleopHighGoals.getSuccesses());
    addGetter("tele.high.ratio", (t) -> t.teleopHighGoals.getRatio());
    addGetter("tele.low", (t) -> t.teleopLowGoals.getSuccesses());
    addGetter("tele.low.ratio", (t) -> t.teleopLowGoals.getRatio());
    addGetter("tele.boulderpts", (t) -> 5 * t.teleopHighGoals.getSuccesses() + 2
        * t.teleopLowGoals.getSuccesses());

    addGetter("end.challenges", (t) -> t.endGame.getChallenges());
    addGetter("end.scales", (t) -> t.endGame.getScales());
    addGetter("end.scale.ratio", (t) -> t.endGame.getScaleRatio());

    addGetter("fouls", (t) -> t.fouls);

    addGetter("team", (t) -> t.team);
  }

}
