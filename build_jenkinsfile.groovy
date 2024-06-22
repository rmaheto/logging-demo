pipeline {
    agent any
    environment {
        GIT_URL = 'https://github.com/rmaheto/logging-demo.git'
        BRANCH = 'main' // Change to your desired branch
        BUILD_DIR = 'target' // Directory where the built files are located
        ARTIFACT_NAME = 'logging-demo.war'
        CREDENTIAL_ID = '61f8848c-29c3-448f-9e37-1f87a4512fd5'
        REMOTE_CREDENTIAL_ID = 'remote-server-ssh' // SSH credentials ID
        REMOTE_USER = 'ec2-user'
        REMOTE_HOST = '52.23.198.34'
        PUBLISH_DIR = '/home/ec2-user/artifacts'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", url: "${GIT_URL}", credentialsId: CREDENTIAL_ID
            }
        }
        stage('Build') {
            steps {
                // Example for a Maven build
                sh 'mvn clean install'
            }
        }
        stage('Publish') {
            steps {
                script {
                    // Use SSH credentials for secure copy
                    withCredentials([sshUserPrivateKey(credentialsId: REMOTE_CREDENTIAL_ID, keyFileVariable: 'SSH_KEY')]) {
                        sh """
                            scp -i $SSH_KEY ${BUILD_DIR}/${ARTIFACT_NAME} ${REMOTE_USER}@${REMOTE_HOST}:${PUBLISH_DIR}/
                        """
                    }
                }
            }
        }
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