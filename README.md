# G-Tric
Three-dimensional dataset generator with triclustering solutions.

**Contents:**:
- [G-Tric](https://github.com/jplobo1313/G-Tric/tree/master/G-Tric) source project.
- [Runnable JAR](https://github.com/jplobo1313/G-Tric/tree/master/Executable%20JAR) to run the G-Tric's GUI version.
- [Demo project](https://github.com/jplobo1313/G-Tric/tree/master/Demo/G-Tric-Demo) to generate the synthetic datasets defined on G-Tric's paper (configured on external files [here](https://github.com/jplobo1313/G-Tric/tree/master/Demo/G-Tric-Demo/config_files), and to exemplify how G-Tric can be imported into another project and used programaticly.

**How to run:**
1. Download the runnable jar file.
2. Execute command `java -jar G-Tric-X.X.X.jar`.

**Requirements:**
- Java 11 or above

**Note:** The generation of large datasets (>10^8 elements) can fail due to memory restrictions (Java Heap Space). One way to overcome the problem is to increase the memory available by setting the JVM argument `-Xmx<amount>g`. For example: `java -jar -Xmx6g G-Tric-X.X.X.jar`.
