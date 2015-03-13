https://sourceforge.net/projects/entrainer
http://entrainer.sourceforge.net

EntrainerFX is an open source program that uses light and sound to entrain brain waves.  
To run the program, double click the 'EntrainerFX.jar' file in the install directory.

EntrainerFX generates binaural beat frequencies using sine waves, one at a base frequency, and
the other at a slightly greater frequency.  The difference between these frequencies is the 
entrainment frequency.  This entrainment frequency can also be used to flash the application 
in sync with the generated binaural beats if desired.

EntrainerFX is programmable via xml files.  They are easily read & edited, and EntrainerFX
includes its own editor to create and edit EntrainerFX program files.  Sample files included.

The sound output of EntrainerFX can also be saved as a .wav file.  Using your favourite 
audio conversion tool (ie. iTunes) you can create mp3's of the EntrainerFX programs, for use 
on your personal media device.

EntrainerFX will run on Windows, OS X and Linux and was built using Java 8 (1.8.0_40).

5.1.0 - March 14, 2015
  - Last feature release prior to full JavaFX conversion
  - Minimum Java version - 1.8.0_40
  - Added configurable background w/ 3 modes - random images from the chosen directory,
    static image or background colour
  - Deprecated flash panel (functionality merged into EntrainerBackground)
  - Moved build system to Gradle, source now on Github (https://github.com/mrstampy/EntrainerFX.git)
  - EntrainerFX settings saved on each change (was on program exit)
  - Splash screen can be enabled/disabled via help menu item
  - Merged EntrainerCommon source into the project
  - updated libraries, small code improvements

5.0.0 Final - June 7, 2014
  - Java 8 minimum required
  - added ESP 2.1 (http://mrstampy.github.io/ESP/) framework support (see html documentation for details)
  - improved program editor look
  - renamed executable jar to 'EntrainerFX.jar'
  - small bug fixes, library upgrades
  - retired the use of the excellent SteelSeries library (https://github.com/HanSolo/SteelSeries-Swing)

4.5.0 Final - April 19, 2014
  - added Neuralizer titled pane for power signal analysis using the Neuroph neural network library.
  - added 'Random background' option in file menu
  - updated doco

4.3.0 Final - April 12, 2014
  - using migrated esp library (http://mrstampy.github.io/ESP/)
  - migrated from log4j to slf4j/logback for logging
  - library updates
  - bugfix - recording didn't work on Java 8

4.3.0 RC1 - April 8, 2014
  - using Poison Ivy (http://mrstampy.github.io/PoisonIvy/) to resolve dependencies on first startup
  - cleanup

4.2.5 Final - March 27, 2014
  - Will now run on Java 7
  - Added 'flash background' to network messages
  
4.2.4 Final - March 24, 2014
  - Java 8 bugfix for the Entrainer program editor
  - Added 'flash background' option to change the background opacity via the entrainment frequency
  
4.2.3 Final - March 22, 2014
  - Refactorings to allow Entrainer to run on Java 8
  - JSyn and Netty library updates

4.2.2 Final - August 17, 2013
  - Replaced Apache MINA with Netty (http://netty.io) for socket creation and handling
  - Allow both XML and JSON for socket communication
  - Introduced WebSocket communication
  - Created EntrainerFX Lite (http://entrainer.sourceforge.net/entrainerfx-lite.html)

4.2.1 Final - June 9, 2013
  - Integrated EntrainerESP v. 1.0.1; now using raw signal for EEG displays
  - EEG displays based on SteelSeries show entrainment frequency
  - fixed system tray bug, starting & stopping EntrainerFX via the system tray icon
  - upgraded JTattoo to v. 1.6.9

4.2.0 Final - April 20, 2013
  - New splash screen
  - Reorg of UI
  - Upgrade of Look and Feel libraries
  - New shimmers and animations
  - Fixed scaling bug in background picture
  - Added error dialog for spaces in install directory names
  - The program editor and player were broken during the conversion to JavaFX
    and the pure Java JSyn sound library.  The editor has been fixed (and enhanced)
    and the levels are now managed externally to the sound library.
  - Updated doco to reflect the latest changes

4.1.1 Final - February 19, 2013
  - The latest Java 7 releases from Oracle prevent EntrainerFX from starting if any spaces exist
    in the directory path with the error: java.awt.IllegalComponentStateException: The dialog is decorated.  
    Noting in the documentation, and thanks to Rich for bringing it to my attention.
  - updated JTattoo library
  - fixed UI bug where pause button would enable on recording
  - fixed threading bug where recording would not finalize
  - improved messaging for recording
  - small cleanup & improvements

4.1.0 Final - December 26, 2012
  - bugfix, Entrainer programs that don't specify an animation resulted in an NPE when editing
  - bugfix, Neurosky graphs were not initialized with the entrainment frequency (necessary for flashing)
  - conversion to use Apache Ivy ( http://ant.apache.org/ivy/ ) and 
    IvyDE ( http://ant.apache.org/ivy/ivyde/index.html ) for dependency management
  - upgraded to latest libraries
  - changes to support multi-device support changes in Entrainer ESP (formerly ThinkGear) library
  - known issue: the Swing colour chooser dialog is broken when using any Substance look and feel
    work around: switch to another look and feel to use the colour chooser. 

4.0.2 Final - October 13, 2012
  - (Hopefully) fixed cosmetic issues on Windows
  - added dynamic detection and loading of Entrainer animation jars

4.0.1 Final - September 29, 2012
  - removed need to include jfxrt.jar explicitly in the classpath & distribution
  - reworked animation api

4.0.0 Final - September 25, 2012
  - JavaFX rewrite of the main gui components and animation subsystem
  - Now using pure Java JSyn library only
  - Bugfix, validation prevented a zero pink noise program from being saved (thanks to elel18980!)
  - Implemented 3 different shimmer effects
  - Upgraded SteelSeries library

3.5.0 Final - April 27, 2012
  - Fixed nanosecond bug with flashing times
  - Added EntrainerThinkGear to the project, allowing display of EEG signals & EEG control of volume
  - Added Entrainer TrayIcon to SystemTray
  - Upgraded JFreeChart libs
  - Improved processing for Entrainer sockets, use of Executors to keep message send & receive threads clear
  - Made the flash/message panel flash-only
  - Added NotificationWindow for transient informational messages
  - Small UI improvements
  - Added SteelSeries library to display Neurosky EEG output, now 3 different representations.

3.0.1 Final - March 19, 2012
	- Windows bugfix, opening local documentation does not work when spaces exist in the path.

3.0.0 Final - March 18, 2012

  - Entrainer Sockets, Entrainer can now communicate its state to and have its state changed by external
    programs and hardware.
  - Library upgrades.
  - Small fixes and enhancements.

2.0.0 Final - December 12, 2010

	- Entrainer now needs a minimum of Java 6 to run
	- Will now run on any platform which supports Java 6 using the pure Java JSyn sound library
	- Linux, Mac and Windows users have the option of using either the native or the pure Java JSyn library
		via a new menu
	- Multi monitor support added for animations
	- Organized Look and Feel menu
	- Trident 1.3 upgrade fixes shimmer effect hang
	- Now using spinners for Entrainer program editor fields
	- Upgraded look and feels to latest versions
	- fade in / fade out for splash screen
	- now using JAXB API for xml 2 Java
	- other various small improvements

1.1.5 Final - December 24, 2009

	- Bug fix, 'Flash' and 'Psychelelic' check box settings were not being saved between sessions
	- Added 'Shimmer' effect, colours and transparency are gently shifted over top of the application, oil-on-water effect
	- Upgraded Trident library to v. 1.1

1.1.4 Final - July 29, 2009

	- Bug fix, MessagePanel was not being notified of entrainment frequency changes during
		Entrainer program execution
	- 'Cleared Entrainer Program' message now displayed appropriately
	- Menu items now enabled/disabled appropriately for Entrainer programs 
	- Now using EntrainerCommon project (shared with EntrainerEEG)
	- Implementing Trident animations in preparation for game creation

1.1.3 Final - June 29, 2009

	- Added unit test suite to build
	- Upgraded MiG layout manager libraries
	- Minor bug fixes

1.1.2 Final - May 25, 2009

	- 1.1.1 Final could not switch to native look and feels.  Fixed.
	- 1.1.1 Final could not add custom intervals.  Fixed.
	- Deprecated images removed from distribution
	- A more exact ratio used for the Golden Ratios
	- Fixed strange bug with UIManager.getInstalledLookAndFeels(); Entrainer would not start under Windows, 
		sometimes would not start on OS X 10.4 - the call to this method would not return (but it would work in the debugger).  
		Fixed by moving this line of code to later in the startup.

1.1.1 Final - March 24, 2009

	- Cleaned up html documentation.
	- Added JTattoo (http://www.jtattoo.net) look and feels to the distribution.
	- Small reorg & code refactoring in prep for future development.
	- Fixed bug with 'About' dialog, will now display if Entrainer cannot start.
	- Fixed bug with AnimationRegister, will now allow abstract superclasses subclassing EntrainerAnimation 
	- Added 'Coming Soon...' dialog...

1.1.0 Final - March 10, 2009

	- Added animation architecture + 3 animations for the distribution.
		- can be used in Entrainer programs.
		- animations loaded dynamically via reflection; anyone can write animations for Entrainer.
		- instructions included in the Javadoc for the EntrainerAnimation superclass.
	- updated doco, small clean up & small bug fixes.
	
1.0.1 Final (codename: Peacekeeper) - September 22, 2008

	- Added the ability to add and remove intervals to the base frequency.
		- can be used in Entrainer programs.
		- custom intervals can be created and deleted, and are saved between sessions.
	- Better use of the JSyn library classes, using the SynthMixer class for output.
	- Use of a variation of the mediator pattern to decouple classes in preparation for future development.
	- General cleanup, reorganization of code and packages.
	
1.0 Final - August 3, 2008

	- Small documentation and organization improvements
	- Pause/resume functionality now in sound control class, pause disabled from gui recording until artifacts can be eliminated.
	- One small message bug fix from 1.0 RC1
	
1.0 RC1 - July 31, 2008

	- Now using icons in place of many buttons
	- Added pause functionality for Entrainer programs
	- Many small bug fixes, UI improvements, message clarification
		
Release 11

	- Thoroughly implemented MigHelper, removed usage of GridBagLayout in system
	- Added dynamic look and feel architecture, now defaulting to 'Substance Mango' as the 
		Entrainer default l&f.
	- Now saving all settings between sessions.
	
Release 10

	- Now using MiGLayout (http://www.migcomponents.com) to position enterable fields in
		the XML editor.  Fixes GridBagLayout bug on Windows.
	- Flashing now limited to the message panel on the main application.  Will now work on
		Windows.

Release 9 (codename: Independence) - July 4, 2008

	- Objects can now listen for property changes to unit values.
	- XML Editor now allows values to be changed while testing.
	- Added JFreeChart to the project, will display a chart of all the unit settings
	
Release 8 (codename: Taygeta) - June 17, 2008

	- On Mac, repainting (for flashing) happens automatically; must be called 
	  explicitly for other platforms.  Will now flash under Windows.
		
Release 7 - June 11, 2008

	- Unit Sleeper event model flawed; refactored.
	- Linux install bug fixed.
	- Including Entrainer documentation in the install.
	- Small bug fixes, doco update etc.
		
Release 6 - June 10, 2008

	- JSyn redistribution license acquired 
		- all files necessary to run Entrainer are included in distribution
	- Now using IzPack (http://www.izforge.com) to create installer
	- Small bug fixes, doco update etc.
  	
Release 5 - June 9, 2008

	Second beta release:
		- fixed bug with hour display in xml unit tab titles
		- added 'Test' buttons to test unit xml settings from the editor
		- added the ability to save programs to WAV files
		- various trivial improvements, doco, etc.
  
Release 4 - June 8, 2008

  First beta release:
  	- splash screen on startup
  	- added intro, xml editor overview and license dialogs
  	- included 'Save As' button on xml editor
  	- added GPL license information to source Javadoc
  	- added Ant build file to Eclipse project
  	- improved About dialog information
  	- added logging via Log4j
  	- improved exception handling
  	- xml editor tabs reflect total time and time interval
  	- added message panel to xml editor
  	- various small bug fixes, xml file fixes
	
Release 3 - June 6, 2008

  Added gui XML editor, small UI improvements, package reorg
	
Release 2 - June 5, 2008

	Added pink noise control plus panning options.

Release 1 - June 4, 2008

	Initial release
