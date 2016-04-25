package org.frc3620.scout.test;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.frc3620.scout.AllTeamStats;
import org.frc3620.scout.TeamStats;
import org.frc3620.scout.TeamStatsCsv;
import org.frc3620.scout.TeamStatsCsvTearesa;
import org.frc3620.scout.gui.MainWindow;
import org.junit.Test;

public class AllTeamStatsTest {

  @Test
  public void test() throws IOException {
    AllTeamStats allTeamStats = AllTeamStats.loadFromCsv("WildRankEveningFixed.csv");
    TeamStats t = allTeamStats.getTeamStats(27);
    System.out.println(t);
    
    TeamStatsCsv tsc = new TeamStatsCsvTearesa();
    List<String> labels = tsc.labels();
    System.out.println (tsc.labels());
    System.out.println (tsc.values(t));
    
    allTeamStats.writeCsv("output.csv", tsc);
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MainWindow window = new MainWindow();
          
          String[] s = new String[labels.size()];
          s = labels.toArray(s);
          
          List<Integer> teamNumbers = allTeamStats.getTeamNumbers();
          String[][] d = new String[teamNumbers.size()][labels.size()];
          
          for (int i = 0; i < teamNumbers.size(); i++) {
            Integer teamNumber = teamNumbers.get(i);
            List<String> values = tsc.values(allTeamStats.getTeamStats(teamNumber));
            for (int j = 0; j < values.size(); j++) {
              d[i][j] = values.get(j);
            }
          }
          
          window.setData(s, d);
          
          window.frmWildstangScoutingData.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

}
