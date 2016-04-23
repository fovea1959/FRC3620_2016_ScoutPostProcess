package org.frc3620.scout;

public class Rating {
	int sum;
	int attempts;
	StringBuilder sparkLine = new StringBuilder();
	
	public void skip() {
		sparkLine.append("\u00b7");
	}
	
	public void addRank(int i) {
		// TODO fix this (single digit)
		sparkLine.append("" + i);
		sum += i;
		attempts ++;
	}

}
