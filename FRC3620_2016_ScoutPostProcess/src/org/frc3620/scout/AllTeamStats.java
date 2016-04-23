package org.frc3620.scout;

import java.io.*;
import java.util.*;

import com.Ostermiller.util.*;

public class AllTeamStats {
	Map<Integer,TeamStats> stats = new TreeMap<>();
		
	public TeamStats getTeamStats (int team) {
		TeamStats rv = stats.get(team);
		if (rv == null) {
			rv = new TeamStats();
			rv.team = team;
		}
		return rv;
	}
	
	public static AllTeamStats loadFromCsv (File file) {
		AllTeamStats rv = new AllTeamStats();
		try {
			LabeledCSVParser csv = new LabeledCSVParser(new CSVParser(new FileReader(file)));
			while (csv.getLine() != null) {
			  int team = Integer.parseInt(csv.getValueByLabel("team"));
			  TeamStats t = rv.getTeamStats(team);
			  
			  t.autoDefense;
			t.autoHighGoals;
			t.autoLowGoals;
			t.teleopHighGoals;
			t.teleopLowGoals;
			t.challengeScales;
			t.defense;
			}
			csv.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rv;
		
	}
	
	public static String dumpTeamStats()

}
