pipeline {
    agent any
    environment {
        GIT_URL = 'https://github.com/rmaheto/logging-demo.git'
        BRANCH = 'main' // Change to your desired branch
        BUILD_DIR = 'target' // Directory where the built files are located
        ARTIFACT_NAME = 'logging-demo-0.0.1-SNAPSHOT.war'
        CREDENTIAL_ID = '61f8848c-29c3-448f-9e37-1f87a4512fd5'
        REMOTE_CREDENTIAL_ID = 'remote-server-ssh' // SSH credentials ID
        REMOTE_USER = 'ec2-user'
        REMOTE_HOST = '52.23.198.34'
        PUBLISH_DIR = '/home/ec2-user/artifacts'
    }
    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: 'Select the branch to build')
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: "${params.BRANCH}", url: "${GIT_URL}", credentialsId: CREDENTIAL_ID
            }
        }
        stage('Build') {
            steps {
                // Example for a Maven build
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Publish') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: REMOTE_CREDENTIAL_ID, keyFileVariable: 'SSH_KEY')]) {
                        // Manually disable host key checking for the scp command
                        sh '''
                            mkdir -p ~/.ssh
                            echo "Host ${REMOTE_HOST}" > ~/.ssh/config
                            echo "  StrictHostKeyChecking no" >> ~/.ssh/config
                            scp -i $SSH_KEY ${BUILD_DIR}/${ARTIFACT_NAME} ${REMOTE_USER}@${REMOTE_HOST}:${PUBLISH_DIR}/
                        '''
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