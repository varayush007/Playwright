pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Playwright Browsers') {
            steps {
                sh 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/surefire-reports/**',
                             allowEmptyArchive: true

            junit 'target/surefire-reports/*.xml'
        }
    }
}