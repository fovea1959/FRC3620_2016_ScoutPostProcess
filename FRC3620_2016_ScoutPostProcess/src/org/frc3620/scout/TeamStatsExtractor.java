package org.frc3620.scout;

import java.util.*;

abstract public class TeamStatsExtractor {
  interface ExtractorLambda {
    public Object x (TeamStats t);
  }
  
  class ExtractorDefinition {
    String s; ExtractorLambda e; Class<?> c; EnumSet<RenderFlags> rf;
    ExtractorDefinition (String _s, ExtractorLambda _e, Class<?> _c, EnumSet<RenderFlags> _rf) {
      s = _s;
      e = _e;
      c = _c;
      rf = _rf;
    }
  }
  
  List<ExtractorDefinition> extractorDefinitions = new ArrayList<>();
  
  public TeamStatsExtractor() {
    super();
    addAllExtractors();
  }
  
  abstract void addAllExtractors();
  
  void addExtractor (String s, ExtractorLambda e, Class<?> c, RenderFlags... r) {
    EnumSet<RenderFlags> rf = EnumSet.of(RenderFlags.DUMMY, r);
    extractorDefinitions.add(new ExtractorDefinition(s, e, c, rf));
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
  
  public EnumSet<RenderFlags>[] getRenderFlags () {
    @SuppressWarnings("unchecked")
    EnumSet<RenderFlags>[] rv = new EnumSet[extractorDefinitions.size()];
    for (int i = 0; i < extractorDefinitions.size(); i++) {
      rv[i] = extractorDefinitions.get(i).rf;
    }
    return rv;
  }
  
  public int getFieldCount() {
    return extractorDefinitions.size();
  }
}
