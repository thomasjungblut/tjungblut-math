package de.jungblut.math.function;

/**
 * A function that applies a running average on two vectors. It must be
 * initialized with a number k, which defines how often it was averaged with
 * this function.
 * 
 */
public final class RunningAverageFunction implements DoubleDoubleVectorFunction {

  private final double newk;

  public RunningAverageFunction(double newk) {
    super();
    this.newk = newk;
  }

  @Override
  public final double calculate(int index, double left, double right) {
    return left + (right / newk) - (left / newk);
  }

}
