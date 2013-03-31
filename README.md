[![Build Status](https://travis-ci.org/thomasjungblut/tjungblut-math.png)](https://travis-ci.org/thomasjungblut/tjungblut-math.png)

This is my own Math package. It features some daily math operations on sparse/dense vectors and matrices.

Chain Calling
-------

My library as intentionally build to easily translate octave/matlab code from MOOC's to Java.
For example the following octave code (simple gradient descent):

> theta = theta - alpha * gradient;

Is written in Java as: 

```java
double alpha = 0.1;
DoubleVector theta = new DenseDoubleVector(new double[] {x, y});
DoubleVector gradient = new DenseDoubleVector(new double[] {1, 1});
theta = theta.subtract(gradient.multiply(alpha));
```
You see that this is a bit more expressive and verbose, but since you can't overload operators this is the only way to solve it.

### Also note:

Since this method chaining requires multiple iterations on the internal datastructure, this might be slower than hand-optimized code.
Take the simple euclidian distance for example:

> Math.sqrt(vec2.subtract(vec1).pow(2).sum());

This will need to iterate three times over your whole vector size of size N (subtract, pow and sum). So you have a quite high constant opposed to a single loop from 0 to N:

```java
double sum = 0;
for (int i = 0; i < vec1.getLength(); i++) {
  double diff = vec2[i] - vec1[i];
  sum += (diff * diff);
}
```

Currently I have an idea to write a bytecode optimizer that will unchain such calls to minimize the performance penalty.
This optimizer would be hooked into a classloader and optimize the bytecode at load-time or at build time as part of maven.

JBLAS
-------

To neglect the performance problem with large matrices/vectors, I have added JBLAS for dense matrix multiplications. 
JBLAS is only activated when not on Windows 64bit, as there is no compiled ATLAS implementation available. 
You can read more about the datails on the [JBLAS project page.](http://jblas.org/ "JBLAS project page.")


License
-------

Since I am Apache committer, I consider everything inside of this repository 
licensed by Apache 2.0 license, although I haven't put the usual header into the source files.

If something is not licensed via Apache 2.0, there is a reference or an additional licence header included in the specific source file.


Build
-----

You can simply build with:
 
> mvn clean package install

The created jar contains debuggable code + sources.