
package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.ExecutionBundle;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.NumberFormat;

public class Formatters {

  private static final Logger log = LogManager.getLogger(Formatters.class);


  public static String printTest(final SubstepsTestProxy test) {


      if (test == null || test.getName() == null) {

        return "null test name";
      }

    return test.getName();
  }


  public static String printTime(final long milliseconds) {
    if (milliseconds == 0) {
      return ExecutionBundle.message("junit.runing.info.time.sec.message", "0.0");
    }
    long seconds = milliseconds / 1000;
    if (seconds == 0) {
      return ExecutionBundle.message("junit.runing.info.time.sec.message", NumberFormat.getInstance().format((double)milliseconds/1000.0));
    }

    final StringBuilder sb = new StringBuilder();
    if (seconds >= 3600) {
      sb.append(seconds / 3600).append("h ");
      seconds %= 3600;
    }
    
    if (seconds >= 60) {
      sb.append(seconds / 60).append("m ");
      seconds %= 60;
    }

    if (seconds > 0 || sb.length() > 0) {
      sb.append(seconds).append("s");
    }
    return sb.toString();
  }


}
