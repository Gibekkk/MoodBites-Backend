pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
    }

    environment {
        IMAGE_NAME = 'moodbites-api'
        IMAGE_TAG  = '0.0.1'
        DEPLOY_DIR = '/home/moodbites/moodbites/moodbites-backend'
        HOST_IP    = '103.185.52.161'
        HOST_USER  = 'moodbites'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build & Deploy') {
            steps {
                withCredentials([
                    file(credentialsId: 'moodbites-env-file', variable: 'ENV_FILE'),
                    sshUserPrivateKey(
                        credentialsId: 'moodbites-host-ssh',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    sh '''
                        # Kirim env ke host
                        scp -i $SSH_KEY -o StrictHostKeyChecking=no \
                            $ENV_FILE $SSH_USER@${HOST_IP}:${DEPLOY_DIR}/.env

                        # Kirim compose file ke host
                        scp -i $SSH_KEY -o StrictHostKeyChecking=no \
                            docker-compose.deploy.yml \
                            $SSH_USER@${HOST_IP}:${DEPLOY_DIR}/docker-compose.yml

                        # Build dan deploy di host
                        ssh -i $SSH_KEY -o StrictHostKeyChecking=no \
                            $SSH_USER@${HOST_IP} "
                                cd ${DEPLOY_DIR} &&
                                git pull &&
                                docker build -t ${IMAGE_NAME}:${IMAGE_TAG} . &&
                                docker compose -f ${DEPLOY_DIR}/docker-compose.yml \
                                    --env-file ${DEPLOY_DIR}/.env \
                                    down || true &&
                                docker compose -f ${DEPLOY_DIR}/docker-compose.yml \
                                    --env-file ${DEPLOY_DIR}/.env \
                                    up -d &&
                                rm -f ${DEPLOY_DIR}/.env
                            "
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline berhasil! Moodbites API jalan di port 8000.'
        }
        failure {
            echo 'Pipeline gagal! Periksa log di atas.'
        }
    }
}