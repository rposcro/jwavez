package com.rposcro.zstick;

import com.rposcro.jwavez.serial.utils.TimeoutKeeper;

public class TimeoutKeeperTest {

  public static void alarm() {
    System.out.println("Set Off!!!");
  }

  public static void main(String[] args) throws Exception {
    TimeoutKeeper keeper = TimeoutKeeper.setTimeout(2000, TimeoutKeeperTest::alarm);

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(1000);
          keeper.cancel();
        }catch(Exception e) {
          e.printStackTrace();
        }
      }
    }).start();

        keeper.park();
//    Thread.sleep(1000);
  //  keeper.cancel();
    //Thread.sleep(2000);
  }
}
