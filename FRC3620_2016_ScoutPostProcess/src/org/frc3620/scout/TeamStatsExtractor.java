package org.frc3620.scout;

import java.util.*;

abstract public class TeamStatsExtractor {
  
  interface ExtractorLambda {
    public Object x (TeamStats t);
  }
  
  class ExtractorDefinition {
    String s; ExtractorLambda e; Class<?> c;
    ExtractorDefinition (String _s, ExtractorLambda _e, Class<?> _c) {
      s = _s;
      e = _e;
      c = _c;
    }
  }
  
  List<ExtractorDefinition> extractorDefinitions = new ArrayList<>();
  
  public TeamStatsExtractor() {
    super();
    addAllExtractors();
  }
  
  abstract void addAllExtractors();
  
  void addExtractor (String s, ExtractorLambda e, Class<?> c) {
    extractorDefinitions.add(new ExtractorDefinition(s, e, c));
  }
  
  public List<Object> getValues (TeamStats t) {
    List<Object> rv = new ArrayList<>();
    for (ExtractorDefinition extractorDefinition: extractorDefinitions) {
      rv.add(extractorDefinition.e.x(t));
    }
    return rv;
  }
  
  public Class<?>[] getClasses () {
    Class<?>[] rv = new Class<?>[extractorDefinitions.size()];
    for (int i = 0; i < extractorDefinitions.size(); i++) {
      rv[i] = extractorDefinitions.get(i).c;
    }
    return rv;
  }
  
  public String[] getLabels () {
    String[] rv = new String[extractorDefinitions.size()];
    for (int i = 0; i < extractorDefinitions.size(); i++) {
      rv[i] = extractorDefinitions.get(i).s;
    }
    return rv;
  }
  
  public int getFieldCount() {
    return extractorDefinitions.size();
  }
}
