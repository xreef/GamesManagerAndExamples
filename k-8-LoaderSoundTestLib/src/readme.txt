
Chapter 8. Loading and Playing Sounds

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


============================
Compilation:

> javac *.java 
    // if you get "Warning" messages, please see the note below

============================
JAR file Creation:

> makeJar
    // execute the makeJar.bat batch file
    // creates LoadersTests.jar

The Images/ and Sounds/ directories must be present.

============================
Execution:

> java LoadersTests
or
> java -jar LoadersTests.jar
or
  double click on the JAR file


-------------
Note on "unchecked or unsafe operation" Warnings

As explained in chapter 3, I have not used J2SE 5.0's type-safe 
collections, so that this code will compile in early versions of
J2SE (e.g. version 1.4).

The warning messages are always related to my use of collections
(e.g. ArrayList) without specifying a type for the objects they will
contain at run time.

No. of Warnings generated in J2SE 5.0 for the examples:
/LoadersTests: 16

-----
Last updated: 14th April 2005

