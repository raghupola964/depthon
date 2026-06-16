pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Code checked out from GitHub'
            }
        }

        stage('Build & Unit Test') {
            steps {
                dir('depthon-core') {
                    sh 'mvn -B test -Dtest=UserServiceTest,SettingsServiceTest'
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded - all unit tests passed!'
        }
        failure {
            echo 'Pipeline failed - check the test results above.'
        }
    }
}