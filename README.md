This is my own Math package. It features some daily math operations on sparse/dense vectors and matrices.

Parts of this library are also featured in Apache Hama's ML module and power several algorithms there.

I have worked on the test coverage and received 100% instruction coverage on the core math classes in the latest 1.1 version.
Note that test coverage is not a very good metric for bug-freeness as many tests state implicit assertions.

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
JBLAS is only activated on matrices that are bigger than 100 elements on every dimension. 
On Linux (I use Ubuntu) it uses ATLAS routines and needs libgfortran3. 
On Windows 64bit it uses lapack-lite and needs mingw64-x86_64-gcc-core and mingw64-x86_64-gfortran in the path.

You can read more about the datails on the [JBLAS project page.](http://jblas.org/ "JBLAS project page.")

To check if it is available, it checks the system libraries at class loading time. 
If you want to debug or have error messages if the libraries were loaded correctly you can set the VERBOSE flag in the MyNativeBlasLibraryLoader class like that:

```java
MyNativeBlasLibraryLoader.VERBOSE = true;
```

This will print error messages and help you tracing down problems with missing libraries.


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

If you want to skip testcases you can use:

> mvn clean package install -DskipTests
