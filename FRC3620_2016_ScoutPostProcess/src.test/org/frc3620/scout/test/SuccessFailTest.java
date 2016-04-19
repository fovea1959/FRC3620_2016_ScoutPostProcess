package org.frc3620.scout.test;

import static org.junit.Assert.*;

import org.frc3620.scout.SuccessFail;
import org.junit.Test;

public class SuccessFailTest {

  @Test
  public void test() {
    SuccessFail sf = new SuccessFail();
    sf.skip();
    sf.success();
    sf.failure();
    System.out.println (sf.getRatio());
    System.out.println (sf.getSparkLine());
  }

}
