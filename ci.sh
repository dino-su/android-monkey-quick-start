#!/usr/bin/env bash

## init
mkdir -p output/

## install monkey recorder
cd monkey-recorder && ./gradlew -q installDebug && cd -

## install monkey test
cd monkey-test && ./gradlew -q installDebug installDebugAndroidTest && cd -

## start recording
adb shell am start -n com.kkbox.sqa.recorder/.MainActivity -a android.intent.action.RUN -d START

## app precondition
adb shell am instrument -w -e class com.kkbox.sqa.monkey.CalculatorTest#start com.kkbox.sqa.monkey.test/android.support.test.runner.AndroidJUnitRunner

## run monkey
adb shell monkey -p com.android.calculator2 -v 2000 > output/monkey.log
sleep 10

## stop recording
adb shell am start -n com.kkbox.sqa.recorder/.MainActivity -a android.intent.action.RUN -d STOP

## screenshot
adb shell screencap /sdcard/monkey.png

## acquiring logs
adb bugreport > output/bugreport.log
adb pull /sdcard/monkey.png output/monkey.png
adb pull /sdcard/recorder.mp4 output/monkey.mp4

## export JUnit xml
python monkey-to-junit.py output/monkey.log > output/monkey.xml
