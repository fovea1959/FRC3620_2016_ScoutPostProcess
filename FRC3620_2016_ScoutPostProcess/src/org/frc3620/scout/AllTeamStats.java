package org.frc3620.scout;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Ostermiller.util.*;

public class AllTeamStats {
  static Logger logger = LoggerFactory.getLogger(AllTeamStats.class);

  Map<Integer, TeamStats> stats = new TreeMap<>();

  public TeamStats getTeamStats(int team) {
    TeamStats rv = stats.get(team);
    if (rv == null) {
      rv = new TeamStats();
      rv.team = team;
      stats.put(team, rv);
    }
    return rv;
  }

  static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  static final String TEAM = "Team";

  static final String UNKNOWN = "Unknown";

  static final String NO_AUTO = "No Auto";

  static final String MOVED_DIDNT_SCORE = "Moved didn't score";

  static final String REACHED = "Reached";

  static final String CROSSED = "Crossed";

  static final String SPYBOT = "Spy Bot";

  static final String NA = "N/A";

  static final String AUTO_HG_MADE = "Auto HG Made";

  static final String AUTO_HG_MISS = "Auto HG Miss";

  static final String TELE_HG_MADE = "Tele HG Made";

  static final String TELE_HG_MISS = "Tele HG Miss";

  static final String AUTO_LG_MADE = "Auto LG Made";

  static final String AUTO_LG_MISS = "Auto LG Miss";

  static final String TELE_LG_MADE = "Tele LG Made";

  static final String TELE_LG_MISS = "Tele LG Miss";

  static final String ENDGAME_UNKNOWN = "Endgame Unknown";

  static final String CHALLENGE_FAIL = "Challenge Fail";

  static final String CHALLENGE_SUCCESS = "Challenge Success";

  static final String SCALE_FAIL = "Scale Fail";

  static final String SCALE_SUCCESS = "Scale Success";
  
  static final String FOULS = "Fouls";
  static final String T_FOULS = "T Fouls";

  static boolean tf(String s) {
    // TODO thjis is NOT robust
    return !s.equals("0");
  }
  
  static int i(String s) {
    return Integer.parseInt(s);
  }

  public static AllTeamStats loadFromCsv(String path) {
    AllTeamStats rv = new AllTeamStats();
    int lineNumber = 0;
    try {
      LabeledCSVParser csv = new LabeledCSVParser(new CSVParser(new FileReader(path)));
      logger.info("csv labels = {}", Arrays.asList(csv.getLabels()));
      String[] s = null;
      while ((s = csv.getLine()) != null) {
        lineNumber = csv.getLastLineNumber();
        logger.info("csv data = {}", Arrays.asList(s));
        int team = i(csv.getValueByLabel(TEAM));
        
        if (team != 27) {
          break;
        }
        TeamStats t = rv.getTeamStats(team);

        if (tf(csv.getValueByLabel(CROSSED))) {
          t.autoDefense.recordCrossed();
        } else if (tf(csv.getValueByLabel(REACHED))) {
          t.autoDefense.recordReached();
        } else if (tf(csv.getValueByLabel(MOVED_DIDNT_SCORE))) {
          t.autoDefense.recordDidntReach();
        } else if (tf(csv.getValueByLabel(NO_AUTO))) {
          t.autoDefense.recordDidntReach();
        } else if (tf(csv.getValueByLabel(UNKNOWN))) {
          t.autoDefense.recordDidntSee();
        } else if (tf(csv.getValueByLabel(SPYBOT))) {
          t.autoDefense.recordSpied();
        }

        t.autoHighGoals.success(i(csv.getValueByLabel(AUTO_HG_MADE)));
        t.autoHighGoals.failure(i(csv.getValueByLabel(AUTO_HG_MISS)));
        t.autoLowGoals.success(i(csv.getValueByLabel(AUTO_LG_MADE)));
        t.autoLowGoals.failure(i(csv.getValueByLabel(AUTO_LG_MISS)));

        t.teleopHighGoals.success(i(csv.getValueByLabel(TELE_HG_MADE)));
        t.teleopHighGoals.failure(i(csv.getValueByLabel(TELE_HG_MISS)));
        t.teleopLowGoals.success(i(csv.getValueByLabel(TELE_LG_MADE)));
        t.teleopLowGoals.failure(i(csv.getValueByLabel(TELE_LG_MISS)));

        if (tf(csv.getValueByLabel(ENDGAME_UNKNOWN))) {
          t.endGame.recordDidntSee();
        } else if (tf(csv.getValueByLabel(CHALLENGE_FAIL))) {
          t.endGame.recordDidntChallenge();
        } else if (tf(csv.getValueByLabel(CHALLENGE_SUCCESS))) {
          t.endGame.recordChallenged();
        } else if (tf(csv.getValueByLabel(SCALE_FAIL))) {
          t.endGame.recordChallengedThenFailedScale();
        } else if (tf(csv.getValueByLabel(SCALE_SUCCESS))) {
          t.endGame.recordScaled();
        }
        
        t.fouls += i(csv.getValueByLabel(FOULS));
        t.fouls += i(csv.getValueByLabel(T_FOULS));

      }
      csv.close();
    } catch (IOException e) {
      logger.error("IOException at line {}: {}", lineNumber, e);
      e.printStackTrace();
    } catch (Exception e) {
      logger.error("IOException at line {}: {}", lineNumber, e);
      e.printStackTrace();
    }
    return rv;

  }

}
