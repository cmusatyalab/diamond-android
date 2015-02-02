#!/bin/bash
#
#  Diamond Android - Diamond filters for the Android platform
#
#  Copyright (c) 2013-2014 Carnegie Mellon University
#  All Rights Reserved.
#
#  This software is distributed under the terms of the Eclipse Public
#  License, Version 1.0 which can be found in the file named LICENSE.
#  ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS SOFTWARE CONSTITUTES
#  RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT

set -x
PKG='edu.cmu.cs.encoding'
ACTIVITY='EncodingBenchmarkActivity'
ant debug &> ant.log || exit 1
adb uninstall $PKG
adb install bin/$ACTIVITY-debug.apk &> adb-install.log || exit 3
adb shell am start -n $PKG/.$ACTIVITY || exit 4
adb logcat | grep 'Gabriel'
