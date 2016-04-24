package org.frc3620.scout;

public class TeamStatsCsvTearesa extends TeamStatsCsv {

  @Override
  void addAllGetters() {
    addGetter("team", (t) -> "" + t.team);

    addGetter("auto.reach", (t) -> s(t.autoDefense.getReaches()));
    addGetter("auto.attempts", (t) -> s(t.autoDefense.getReachAttempts()));
    addGetter("auto.cross", (t) -> s(t.autoDefense.getCrosses()));
    addGetter("auto.reach.ratio", (t) -> s(t.autoDefense.getReachRatio()));
    addGetter("auto.cross.ratio", (t) -> s(t.autoDefense.getCrossRatio()));

    addGetter("auto.high", (t) -> s(t.autoHighGoals.getSuccesses()));
    addGetter("auto.low", (t) -> s(t.autoLowGoals.getSuccesses()));

    addGetter("tele.high", (t) -> s(t.teleopHighGoals.getSuccesses()));
    addGetter("tele.high.ratio", (t) -> s(t.teleopHighGoals.getRatio()));
    addGetter("tele.low", (t) -> s(t.teleopLowGoals.getSuccesses()));
    addGetter("tele.low.ratio", (t) -> s(t.teleopLowGoals.getRatio()));
    addGetter("tele.boulderpts", (t) -> s(5 * t.teleopHighGoals.getSuccesses() + 2
        * t.teleopLowGoals.getSuccesses()));

    addGetter("end.challenges", (t) -> s(t.endGame.getChallenges()));
    addGetter("end.scales", (t) -> s(t.endGame.getScales()));
    addGetter("end.scale.ratio", (t) -> s(t.endGame.getScaleRatio()));

    addGetter("fouls", (t) -> s(t.fouls));

    addGetter("team", (t) -> "" + t.team);
  }

}
