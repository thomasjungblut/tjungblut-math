This is my own Math package. It features some daily math operations on sparse/dense vectors and matrices.

Parts of this library are also featured in Apache Hama's ML module and power several algorithms there.

I have worked on the test coverage and received 100% instruction coverage on the core math classes in the latest 1.1 version.
Note that test coverage is not a very good metric for bug-freeness as many tests state implicit assertions.

Chain Calling
-------

My library was intentionally build to easily translate octave/matlab code from MOOC's to Java.
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

To avoid the performance problem with large matrices/vectors, I have added JBLAS for dense matrix multiplications. 
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

Benchmarks
-------

You may want to see a Caliper benchmark result of comparing matrix multiplications of square matrices using GPU (JCUDA)/ JBLAS (lapack lite)/ Java implementations on windows:

```
   n    type          us linear runtime
  10     GPU      238,18 =
  10   JBLAS        5,14 =
  10 TJ_MATH        4,88 =
  20     GPU      694,48 =
  20   JBLAS       15,70 =
  20 TJ_MATH       15,74 =
  40     GPU      257,27 =
  40   JBLAS       74,38 =
  40 TJ_MATH       86,26 =
  60     GPU      650,83 =
  60   JBLAS      224,14 =
  60 TJ_MATH      277,91 =
  80     GPU      684,96 =
  80   JBLAS      542,27 =
  80 TJ_MATH      645,70 =
 100     GPU      858,59 =
 100   JBLAS     1019,58 =
 100 TJ_MATH     1245,27 =
 500     GPU    10996,48 =
 500   JBLAS   116263,36 =
 500 TJ_MATH   152830,94 =
1000     GPU    71822,50 =
1000   JBLAS   953681,51 ==
1000 TJ_MATH  1358882,88 ===
2000     GPU   395683,94 =
2000   JBLAS  9465452,45 ======================
2000 TJ_MATH 12386527,91 ==============================

Hardware: Intel i7 920, Nvidia GTX580, Java7
```

Until ~50x50 there is no benefit in using JBLAS and even later on there is little benefit. This is largely due to the overhead of copying the matrix representations 
from heap to native memory and the slowness of lapack lite. 
The GPU gives great improvements when matrices get very large > 500x500 and copy costs (main to device memory) start to amortize itself.
 
On Linux this looks a bit more in favor of JBLAS:

```
   n    type          us linear runtime
  10     GPU       82,86 =
  10   JBLAS        4,09 =
  10 TJ_MATH        3,27 =
  20     GPU      204,50 =
  20   JBLAS        7,91 =
  20 TJ_MATH        9,55 =
  40     GPU      103,20 =
  40   JBLAS       34,71 =
  40 TJ_MATH       53,18 =
  60     GPU      267,69 =
  60   JBLAS       63,40 =
  60 TJ_MATH      163,84 =
  80     GPU      324,13 =
  80   JBLAS      143,65 =
  80 TJ_MATH      358,85 =
 100     GPU      475,00 =
 100   JBLAS      295,71 =
 100 TJ_MATH      675,14 =
 500     GPU    18507,11 =
 500   JBLAS    24072,70 =
 500 TJ_MATH    84433,83 =
1000     GPU   139407,39 =
1000   JBLAS   183385,59 =
1000 TJ_MATH   842254,72 ===
2000     GPU   961625,41 ===
2000   JBLAS  1320723,49 =====
2000 TJ_MATH  7436553,82 ==============================

Hardware: Intel i7-3740QM, Nvidia GeForce GT 650M (mobile)
```

ATLAS does in fact better from 10x10 matrices. The GPU outperfoms the Java code earlier on 100x100 matrices, and outperforms ATLAS from 500x500.
The difference isn't extreme between ATLAS and the GPU, because I benchmarked on a laptop. 
The memory interface width is much more narrow than on the desktop (128 bit vs. at least 384 bit). Also the desktop has more CUDA Cores (512 vs. 384).
If you would run this on a desktop (e.g. with a GTX780) the result would be desaterous for JBLAS from 100x100 matrices. (Will bench this in the future!)

So even if you have a cool library at hand, you still need to benchmark the parameters on your target system. 
Always remember the smart sentence in relation to technology: If you have a hammer, everything looks like a nail.

The benchmark code can be found [here.](https://gist.github.com/thomasjungblut/5652037 "here")

License
-------

Since I am Apache committer, I consider everything inside of this repository 
licensed by Apache 2.0 license, although I haven't put the usual header into the source files.

If something is not licensed via Apache 2.0, there is a reference or an additional licence header included in the specific source file.


Build
-----

You will need Java 8 to build this library.

You can simply build with:
 
> mvn clean package install

The created jar contains debuggable code + sources.

If you want to skip testcases you can use:

> mvn clean package install -DskipTests
