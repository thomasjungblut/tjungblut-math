package de.jungblut.math.util;

public abstract class OSUtils {

  /**
   * @return true if the current OS is windows and the architecture is 64bit.
   */
  public static boolean isWindows64Bit() {
    return System.getProperty("os.name").contains("Windows")
        && System.getenv("ProgramFiles(x86)") != null;
  }

}
