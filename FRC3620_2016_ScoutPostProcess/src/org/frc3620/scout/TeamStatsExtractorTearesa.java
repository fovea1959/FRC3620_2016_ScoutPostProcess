package org.frc3620.scout;

public class TeamStatsExtractorTearesa extends TeamStatsExtractor {

  @Override
  void addAllExtractors() {
    addExtractor("team", (t) -> t.team, Integer.class);

    addExtractor("auto.reach", (t) -> t.autoDefense.getReaches(), Integer.class);
    addExtractor("auto.attempts", (t) -> t.autoDefense.getReachAttempts(), Integer.class);
    addExtractor("auto.cross", (t) -> t.autoDefense.getCrosses(), Integer.class);
    addExtractor("auto.reach.ratio", (t) -> t.autoDefense.getReachRatio(), Double.class);
    addExtractor("auto.cross.ratio", (t) -> t.autoDefense.getCrossRatio(), Double.class);
    addExtractor("auto.spied", (t) -> t.autoDefense.getSpied(), Integer.class);

    addExtractor("auto.high", (t) -> t.autoHighGoals.getSuccesses(), Integer.class);
    addExtractor("auto.low", (t) -> t.autoLowGoals.getSuccesses(), Integer.class);

    addExtractor("tele.high", (t) -> t.teleopHighGoals.getSuccesses(), Integer.class);
    addExtractor("tele.high.ratio", (t) -> t.teleopHighGoals.getRatio(), Double.class);
    addExtractor("tele.low", (t) -> t.teleopLowGoals.getSuccesses(), Integer.class);
    addExtractor("tele.low.ratio", (t) -> t.teleopLowGoals.getRatio(), Double.class);
    addExtractor("tele.boulderpts", (t) -> 5 * t.teleopHighGoals.getSuccesses() + 2
        * t.teleopLowGoals.getSuccesses(), Integer.class);

    addExtractor("end.challenges", (t) -> t.endGame.getChallenges(), Integer.class);
    addExtractor("end.scales", (t) -> t.endGame.getScales(), Integer.class);
    addExtractor("end.scale.ratio", (t) -> t.endGame.getScaleRatio(), Double.class);

    addExtractor("fouls", (t) -> t.fouls, Integer.class);

    addExtractor("team", (t) -> t.team, Integer.class);
  }

}
