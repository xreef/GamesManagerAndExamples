
Chapter 7. Introducing Java Sound 
and
Chapter 9. Audio Effects

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

> makeSPJar
    // execute the makeSPJar.bat batch file
    // creates SoundPlayer.jar

The Sounds/ directories must be present.

============================
Execution:

// the Sounds/ directory must be located here

------------
> java SoundPlayer
or
> java -jar SoundPlayer.jar
or
  double click on the SoundPlayer.jar

------------
> java PlayClip <a sampled audio file in Sounds/>
e.g. 
> java PlayClip dog.wav
> java PlayClip dogLong.wav
> java PlayClip spacemusic.au
> java playClip tiger.aiff


------------
> java BufferedPlayer <a sampled audio file in Sounds/>
e.g. 
> java BufferedPlayer dog.wav
> java PlayClip dogLong.wav
> java BufferedPlayer spacemusic.au
> java BufferedPlayer tiger.aiff


------------
> java PlayMidi <a MIDI sequence in Sounds/>
e.g. 
> java PlayMidi farmerinthedell.mid


------------
> java EchoSamplesPlayer <a sampled audio file in Sounds/>
e.g. 
> java EchoSamplesPlayer dog.wav
> java EchoSamplesPlayer tiger.aiff


------------
> java PlaceClip <a sampled audio file in Sounds/> [ <volume> [ <pan> ] ]
e.g.
> java PlaceClip dog.wav 0.8 -1
> java PlaceClip dogLongStereo.wav 0.8 -1
> java PlaceClip tiger.aiff 0.9 1
> java PlaceClip clank.wav 0.9


------------
> java FadeMidi <a MIDI sequence in Sounds/>
e.g. 
> java FadeMidi farmerinthedell.mid

------------
> java PanMidi <a MIDI sequence in Sounds/>
e.g. 
> java PanMidi baabaablacksheep.mid


-----------
Note on "unchecked or unsafe operation" Warnings

As explained in chapter 3, I have not used J2SE 5.0's type-safe 
collections, so that this code will compile in early versions of
J2SE (e.g. version 1.4).

The warning messages are always related to my use of collections
(e.g. ArrayList) without specifying a type for the objects they will
contain at run time.

No. of Warnings generated in J2SE 5.0 for the examples:
/SoundPlayer: 2 (in the SoundPlayer class)

-----
Last updated: 14th April 2005

