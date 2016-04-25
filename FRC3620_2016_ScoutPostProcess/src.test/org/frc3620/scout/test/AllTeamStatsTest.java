package org.frc3620.scout.test;

import java.io.IOException;

import org.frc3620.scout.AllTeamStats;
import org.frc3620.scout.TeamStats;
import org.frc3620.scout.TeamStatsExtractor;
import org.frc3620.scout.TeamStatsExtractorTearesa;
import org.junit.Test;

public class AllTeamStatsTest {

  @Test
  public void test() throws IOException {
    AllTeamStats allTeamStats = AllTeamStats.loadFromCsv("WildRankEveningFixed.csv");
    TeamStats t = allTeamStats.getTeamStats(27);
    System.out.println(t);
    
    TeamStatsExtractor tsc = new TeamStatsExtractorTearesa();
    System.out.println (tsc.getLabels());
    System.out.println (tsc.getValues(t));
    
    allTeamStats.writeCsv("output.csv", tsc);
  }

}
