package org.frc3620.scout;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

  public List<Integer> getTeamNumbers() {
    return new ArrayList<Integer>(stats.keySet());
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

  static final String DEFENSE_RATING = "Defense Rating";

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
      logger.trace("csv labels = {}", Arrays.asList(csv.getLabels()));
      String[] s = null;
      while ((s = csv.getLine()) != null) {
        lineNumber = csv.getLastLineNumber();
        logger.trace("csv data = {}", Arrays.asList(s));
        int team = i(csv.getValueByLabel(TEAM));

        // if (team != 27) break;

        TeamStats t = rv.getTeamStats(team);

        t.matches++;

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

        String defenseRating = csv.getValueByLabel(DEFENSE_RATING);
        if (defenseRating.equals(NA)) {
          t.defense.recordSkip();
        } else {
          t.defense.recordRank(i(defenseRating));
        }

      }
      csv.close();
    } catch (IOException e) {
      logger.error("IOException at line {}: {}", lineNumber, e);
      e.printStackTrace();
    } catch (Exception e) {
      logger.error("Exception at line {}: {}", lineNumber, e);
      e.printStackTrace();
    }
    return rv;

  }

  public void writeCsv(String path, TeamStatsExtractor teamStatsCsv) throws IOException {
    CSVPrint csvPrint = new CSVPrinter(new FileWriter(path));
    String[] labels = teamStatsCsv.getLabels();
    csvPrint.println(labels);

    Set<Integer> teamNumbers = stats.keySet();
    for (Integer teamNumber : teamNumbers) {
      TeamStats t = stats.get(teamNumber);
      List<Object> vList = teamStatsCsv.getValues(t);
      String[] vArray = new String[vList.size()];
      // TODO need to be careful of strings that only contain digits
      for (int i = 0; i < vList.size(); i++)
        vArray[i] = vList.get(i).toString();
      csvPrint.println(vArray);

    }
    csvPrint.close();
  }

  public void writeExcel(String path, TeamStatsExtractor teamStatsCsv) throws IOException {
    Workbook workBook = new XSSFWorkbook();
    Sheet sheet = workBook.createSheet("Main Sheet");

    Map<String, CellStyle> styles = createStyles(workBook);

    String[] labels = teamStatsCsv.getLabels();

    Row row = sheet.createRow(0);
    for (int col = 0; col < labels.length; col++) {
      Cell headerCell = row.createCell(col);
      headerCell.setCellStyle(styles.get("header"));
      headerCell.setCellValue(labels[col]);
    }

    int rowNumber = 1;

    Set<Integer> teamNumbers = stats.keySet();
    for (Integer teamNumber : teamNumbers) {
      TeamStats t = stats.get(teamNumber);
      List<Object> vList = teamStatsCsv.getValues(t);
      row = sheet.createRow(rowNumber++);
      for (int col = 0; col < vList.size(); col++) {
        Cell cell = row.createCell(col);
        Object o = vList.get(col);
        if (o instanceof Number) {
          cell.setCellValue(((Number) o).doubleValue());
        } else if (o instanceof String) {
          cell.setCellValue((String) o);
        } else {
          cell.setCellValue(o.toString());
        }
      }
    }

    FileOutputStream out = new FileOutputStream(path);
    workBook.write(out);
    out.close();
  }

  static Map<String, CellStyle> createStyles(Workbook wb) {
    Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
    CellStyle style;

    Font headerFont = wb.createFont();
    headerFont.setFontHeightInPoints((short) 14);
    headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
    style = wb.createCellStyle();
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    style.setFont(headerFont);
    style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    styles.put("header", style);

    String[] nfmt = { "#,##0.00", "$#,##0.00", "m/d/yyyy" };
    for (String fmt : nfmt) {
      style = wb.createCellStyle();
      style.setDataFormat(wb.createDataFormat().getFormat(fmt));
      styles.put(fmt, style);
    }

    return styles;
  }

}
