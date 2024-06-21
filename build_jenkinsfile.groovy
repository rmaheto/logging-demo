pipeline {
    agent any
    environment {
        GIT_URL = 'https://github.com/rmaheto/logging-demo.git'
        BRANCH = 'main' // Change to your desired branch
        BUILD_DIR = 'target' // Directory where the built files are located
        ARTIFACT_NAME = 'logging-demo.war' // Change to your WAR or JAR file name
        PUBLISH_DIR = '/path/to/publish/location' // Local or remote directory
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", url: "${GIT_URL}", credentialsId: '61f8848c-29c3-448f-9e37-1f87a4512fd5'
            }
        }
        stage('Build') {
            steps {
                // Example for a Maven build
                sh 'mvn clean install'
            }
        }
//        stage('Publish') {
//            steps {
//                script {
//                    // Example for local publishing
//                    sh "cp ${BUILD_DIR}/${ARTIFACT_NAME} ${PUBLISH_DIR}/"
//
//                    // Example for remote publishing via SSH
//                    // sh "scp ${BUILD_DIR}/${ARTIFACT_NAME} user@remote-server:${PUBLISH_DIR}/"
//                }
//            }
//        }
    }
    post {
        success {
            // Actions to take on successful build
            echo 'Build and publish completed successfully!'
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