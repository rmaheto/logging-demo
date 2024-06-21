pipeline {
    agent any
    environment {
        GIT_URL = 'https://github.com/rmaheto/logging-demo.git'
        BRANCH = 'main'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", url: "${GIT_URL}"
            }
        }
        stage('Build') {
            steps {
                // Example for a Maven build
                sh 'mvn clean install'
            }
        }
    }
    post {
        success {
            // Actions to take on successful build
            echo 'Build completed successfully!'
        }
        failure {
            // Actions to take on failed build
            echo 'Build failed!'
        }
        always {
            // Actions to take regardless of build status
            cleanWs()
        }
    }
}