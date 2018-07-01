
Chapter 11. Sprites

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
Execution:

> java BugRunner

The images must be located in the Images/ subdirectory, the 
sounds in Sounds/.

The objective of the game is to move the ant to stop the dropping 
ball from reaching the floor.

The ant is controlled by the arrow keys or by clicking the mouse
in the direction you want the ant to walk.

-----------
Note on "unchecked or unsafe operation" Warnings

As explained in chapter 3, I have not used J2SE 5.0's type-safe 
collections, so that this code will compile in early versions of
J2SE (e.g. version 1.4).

The warning messages are always related to my use of collections
(e.g. ArrayList) without specifying a type for the objects they will
contain at run time.

No. of Warnings generated in J2SE 5.0 for the examples:
/BugRunner: 16

-----
Last updated: 14th April 2005

