#/bin/bash
set -x
PKG='edu.cmu.cs.encoding'
ACTIVITY='EncodingBenchmarkActivity'
ant debug &> ant.log || exit 1
adb uninstall $PKG
adb install bin/$ACTIVITY-debug.apk &> adb-install.log || exit 3
adb shell am start -n $PKG/.$ACTIVITY || exit 4
adb logcat | grep 'Gabriel'
