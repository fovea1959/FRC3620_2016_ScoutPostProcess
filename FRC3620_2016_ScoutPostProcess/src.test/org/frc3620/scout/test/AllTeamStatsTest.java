package org.frc3620.scout.test;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.frc3620.scout.AllTeamStats;
import org.frc3620.scout.TeamStats;
import org.frc3620.scout.TeamStatsExtractor;
import org.frc3620.scout.TeamStatsExtractorTearesa;
import org.frc3620.scout.gui.MainWindow;
import org.junit.Test;

public class AllTeamStatsTest {

  @Test
  public void test() throws IOException {
    AllTeamStats allTeamStats = AllTeamStats.loadFromCsv("WildRankEveningFixed.csv");
    TeamStats t = allTeamStats.getTeamStats(27);
    System.out.println(t);
    
    TeamStatsExtractor tsc = new TeamStatsExtractorTearesa();
    String[] labels = tsc.getLabels();
    System.out.println (tsc.getLabels());
    System.out.println (tsc.getValues(t));
    
    allTeamStats.writeCsv("output.csv", tsc);
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MainWindow window = new MainWindow();
          
          List<Integer> teamNumbers = allTeamStats.getTeamNumbers();
          Object[][] d = new String[teamNumbers.size()][labels.length];
          
          for (int i = 0; i < teamNumbers.size(); i++) {
            Integer teamNumber = teamNumbers.get(i);
            List<Object> values = tsc.getValues(allTeamStats.getTeamStats(teamNumber));
            for (int j = 0; j < values.size(); j++) {
              d[i][j] = values.get(j);
            }
          }
          window.setTeamStatsExtractor(tsc);
          window.setData(d);
          
          window.frmWildstangScoutingData.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

}
