pipeline {
    agent any

    environment {
        ANDROID_HOME="${HOME}/Library/Android/sdk"
        PATH="${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:$PATH"
    }
    stages {
        stage('Build') {
            steps { sh './gradlew assembleDebug' }
        }
        stage('Archive') {
            steps { 
                dir('app/build/outputs/') { archiveArtifacts 'apk/' }
            }
        }
    }
}
