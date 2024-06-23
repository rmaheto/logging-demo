pipeline {
    agent any
    parameters {
        string(name: 'VERSION', defaultValue: '0.0.1-SNAPSHOT', description: 'Version of the artifact to deploy')
    }
    environment {
        BUILD_ARTIFACT_PATH = '/home/ec2-user/artifacts/builds'
        DEPLOY_DIR = '/home/ec2-user/artifacts/deploys'
        REMOTE_USER = 'ec2-user'
        REMOTE_HOST = '3.87.243.80'
        REMOTE_CREDENTIAL_ID = 'remote-server-ssh'
        ARTIFACT_NAME = "logging-demo-${params.VERSION}.war"
    }
    stages {
        stage('Copy Artifact') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: REMOTE_CREDENTIAL_ID, keyFileVariable: 'SSH_KEY')]) {
                        sh '''
                            mkdir -p ~/.ssh
                            echo "Host ${REMOTE_HOST}" > ~/.ssh/config
                            echo "  StrictHostKeyChecking no" >> ~/.ssh/config
                            ssh -v -i $SSH_KEY ${REMOTE_USER}@${REMOTE_HOST} "pkill -f \\"java -jar\\" || true"
                            ssh -i $SSH_KEY ${REMOTE_USER}@${REMOTE_HOST} "rm ${DEPLOY_DIR}/*.war"
                            ssh -i $SSH_KEY ${REMOTE_USER}@${REMOTE_HOST} "cp ${BUILD_ARTIFACT_PATH}/${ARTIFACT_NAME} ${DEPLOY_DIR}"
                        '''
                    }
                }
            }
        }
        stage('Deploy Application') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: REMOTE_CREDENTIAL_ID, keyFileVariable: 'SSH_KEY')]) {
                        sh '''
                            
                            ssh -i $SSH_KEY ${REMOTE_USER}@${REMOTE_HOST} "nohup java -jar ${DEPLOY_DIR}/${ARTIFACT_NAME} > /dev/null 2>&1 &"
                        '''
                    }
                }
            }
        }
        stage('Test Application') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: REMOTE_CREDENTIAL_ID, keyFileVariable: 'SSH_KEY')]) {
                        sh '''
                            sleep 30
                            curl -f http://${REMOTE_HOST}:8084/actuator/health || (echo 'Application not running!' && exit 1)
                        '''
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Deploy and test completed successfully!'
        }
        failure {
            echo 'Deploy failed!'
        }
        always {
            cleanWs()
        }
    }
}