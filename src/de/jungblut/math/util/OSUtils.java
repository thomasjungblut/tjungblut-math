package de.jungblut.math.util;

public abstract class OSUtils {

  /**
   * @return true if the current OS is windows and the architecture is 64bit.
   */
  public static boolean isWindows64Bit() {
    boolean is64bit = false;
    if (System.getProperty("os.name").contains("Windows")) {
      is64bit = (System.getenv("ProgramFiles(x86)") != null);
    } else {
      is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
    }
    return is64bit;
  }

}
