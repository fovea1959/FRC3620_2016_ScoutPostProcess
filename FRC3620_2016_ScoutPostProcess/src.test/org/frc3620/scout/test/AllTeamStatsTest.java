package org.frc3620.scout.test;

import org.frc3620.scout.AllTeamStats;
import org.frc3620.scout.TeamStats;
import org.frc3620.scout.TeamStatsCsv;
import org.frc3620.scout.TeamStatsCsvTearesa;
import org.junit.Test;

public class AllTeamStatsTest {

  @Test
  public void test() {
    AllTeamStats allTeamStats = AllTeamStats.loadFromCsv("WildRankEveningFixed.csv");
    TeamStats t = allTeamStats.getTeamStats(27);
    System.out.println(t);
    
    TeamStatsCsv tsc = new TeamStatsCsvTearesa();
    System.out.println (tsc.labels());
    System.out.println (tsc.values(t));
  }

}
