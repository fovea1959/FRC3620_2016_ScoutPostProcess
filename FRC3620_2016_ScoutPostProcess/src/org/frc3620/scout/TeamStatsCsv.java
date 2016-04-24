package org.frc3620.scout;

import java.io.FileWriter;
import java.util.*;
import java.util.Map.Entry;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

abstract public class TeamStatsCsv {
  
  interface F {
    public String x (TeamStats t);
  }
  
  class SF {
    String s; F f;
    SF (String _s, F _f) {
      s = _s;
      f = _f;
    }
  }
  
  List<SF> fAll = new ArrayList<>();
  
  public TeamStatsCsv() {
    super();
    addAllGetters();
  }
  
  abstract void addAllGetters();
  
  void addGetter (String s, F f) {
    fAll.add(new SF(s, f));
  }
  
  public List<String> values (TeamStats t) {
    List<String> rv = new ArrayList<>();
    for (SF sf: fAll) {
      rv.add(sf.f.x(t));
    }
    return rv;
  }
  
  public List<String> labels () {
    List<String> rv = new ArrayList<>();
    for (SF sf: fAll) {
      rv.add(sf.s);
    }
    return rv;
  }
  
  public String s (double d) {
    return "" + d;
  }

  public String s (int i) {
    return "" + i;
  }
  


}
