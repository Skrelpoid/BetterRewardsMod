# BetterRewardsMod

A Mod for Slay The Spire

It adds a choice at Neow not to get a reward but instead get to a special shop with gold equal to the score of your last run (with this character).

This Mod needs BaseMod: https://github.com/daviscook477/BaseMod/releases

Like all mods, my Mod needs Mod the Spire: https://github.com/kiooeht/ModTheSpire

Download: 
https://github.com/Skrelpoid/BetterRewardsMod/releases

## Building (only important for Modders)
This Mod uses Gradle for building. After making changes, you can double click buildMod.cmd 
(or run ./gradlew build or gradlew build in a console) to 
compile it and build the jar. The Mod will be in the /build/libs folder. Copy it over to 
your mods directory and test it out.

If the task fails and there is no jar in /build/libs make sure you have a folder named 
lib next to the folder of this Project (not in it) where you have the jars of ModTheSpire, 
BaseMod and the Game's jar (desktop-1.0.jar)

Your directory structure should look like this:
```
git (or wherever you store your git projects)
   \_The-Sage
             \_src...
   \_lib
        \_ModTheSpire.jar
          BaseMod.jar
          desktop1.0.jar
```
You can run "./gradlew listJars" (without the ") to see which jars are found in your 
libs folder. If there are no jars listed, you probably haven't configured it correctly

If you want to import your mod into an IDE, run:
 - For Eclipse: ./gradlew eclipse OR gradlew eclipse
 - For IntelliJ Idea: ./gradlew idea OR gradlew idea

And then import the project using your IDE's Wizard