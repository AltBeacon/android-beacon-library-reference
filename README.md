## Overview

This is an Eclipse reference app for the Android Beacon Library supporting AltBeacon compatible devices

If you are looking for an AndroidStudio reference project, see the android-studio branch.

## Project Setup

1. Install Eclipse with the ADT plugin
2. Install Google Android SDKs 
3. Import the Android Beacon Library (2.0-beta3 +) as an existing project in the workspace
4. Import this project as an existing project into the workspace
5. Under Project -> Properties -> Android, add a library reference to the Android Beacon Library above

## Installing the `AndroidBeaconLibrary`

This project is dependent on a 2.0-beta3 or higher version of the open source Android Beacon Library (from http://altbeacon.github.io/android-beacon-library/download.html).  It must be
imported into Eclipse before this project can be imported.  This library open source library requires a 
gradle build system (normally used with Android Studio not Eclipse), so if you want to build it from
source you need to set up that build environment.  Alternately, you may download a binary copy of the
AndroidBeaconLibrary-2.x.tar.gz file, and extract it for import into Eclipse.
