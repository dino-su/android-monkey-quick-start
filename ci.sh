#!/usr/bin/env bash

## Init
mkdir -p output/

## Install Monkey Recorder
cd monkey-recorder && ./gradlew -q installDebug && cd -

## Install Monkey Test
cd monkey-test && ./gradlew -q installDebug installDebugAndroidTest && cd -

##  START Recording
adb shell am start -n com.kkbox.sqa.recorder/.MainActivity -a android.intent.action.RUN -d START

## App Precondition
adb shell am instrument -w -e class com.kkbox.sqa.monkey.CalculatorTest#start com.kkbox.sqa.monkey.test/android.support.test.runner.AndroidJUnitRunner

## Run Monkey
adb shell monkey -p com.android.calculator2 -v 2000 > output/monkey.log
sleep 10

## STOP Recording
adb shell am start -n com.kkbox.sqa.recorder/.MainActivity -a android.intent.action.RUN -d STOP

## Screenshot
adb shell screencap /sdcard/monkey.png

## Acquiring Logs
adb bugreport > output/bugreport.log
adb pull /sdcard/monkey.png output/monkey.png
adb pull /sdcard/recorder.mp4 output/monkey.mp4
