
Chapter 6. Image Loading, Visual Effects, and Animation

From:
  Killer Game Programming in Java
  Andrew Davison
  O'Reilly, May 2005
  ISBN: 0-596-00730-2
  http://www.oreilly.com/catalog/killergame/
  Web Site for the book: http://fivedots.coe.psu.ac.th/~ad/jg

Contact Address:
  Dr. Andrew Davison
  Dept. of Computer Engineering
  Prince of Songkla University
  Hat yai, Songkhla 90112, Thailand
  E-mail: ad@fivedots.coe.psu.ac.th


If you use this code, please mention my name, and include a link
to the book's Web site.

Thanks,
  Andrew

---------
Contents:

ImagesTest.java:   the top-level of an application that illustrates a variety of 
                   Java 2D visual effects and animations; 

ImagesLoader.java  contains a class to load images which are assumed to be in 
                   the subdirectory Images/, together with a configuration
                   file called imsInfo.txt;

ImagesSFXs.java:  contains the code for the visual effects;

ImagesPlayer.java:  support code for animating a sequence of images;

ImagesPlayerWatcher.java:  interface for sequence callbacks;

Images/           the images directory -- don't change the name.
                  The images in these files are used by ImagesTests.
                  Images/ should contain a configuration file called imsInfo.txt;


mainClass.txt:    manifest information for the JAR;

makeJar.bat:      a batch file for creating a JAR file from the ImagesTest application
                  (I'm too lazy to keep typing this stuff :)).
                  Only create the JAR after compiling the Java code.
                  The resulting JAR is called ImagesTests.jar

runIT.bat:        a batch file for running the JAR file. 
                  When runIT.bat finishes, it automatically closes the DOS window.
                  Alternatively, you can double-click on the JAR, but then you don't 
                  see the output sent to stdout by the application.

--------
Compilation:

$ javac *.java
    // if you get "Warning" messages, please see the note below

--------
Execution:

$ java ImagesTests

Or create the ImagesTests.jar file by executing makeJar.bat, 
   and run it with runIT.bat. 
   (You can also double-click on the JAR.)

-----------
Note on "unchecked or unsafe operation" Warnings

As explained in chapter 3, I have not used J2SE 5.0's type-safe 
collections, so that this code will compile in early versions of
J2SE (e.g. version 1.4).

The warning messages are always related to my use of collections
(e.g. ArrayList) without specifying a type for the objects they will
contain at run time.

No. of Warnings generated in J2SE 5.0 for the examples:
/ImagesTests: 14

---------
Last updated: 14th April 2005
