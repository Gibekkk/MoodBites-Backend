pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
    }

    environment {
        IMAGE_NAME = 'moodbites-api'
        IMAGE_TAG  = '0.0.1'
        DEPLOY_DIR = '/opt/moodbites'
        HOST_IP    = '10.1.49.196'  // ganti IP host
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                withCredentials([file(credentialsId: 'moodbites-env-file', variable: 'ENV_FILE')]) {
                    sh 'cp $ENV_FILE .env'
                }
                sh 'docker compose up -d mariadb'
                sh 'sleep 30'
                sh 'mvn test'
            }
            post {
                always {
                    sh 'docker compose down'
                    sh 'rm -f .env'
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                sh 'docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .'
            }
        }

        stage('Deploy') {
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
                        # Kirim image ke host
                        docker save ${IMAGE_NAME}:${IMAGE_TAG} | \
                        ssh -i $SSH_KEY -o StrictHostKeyChecking=no \
                            $SSH_USER@${HOST_IP} \
                            "docker load"

                        # Kirim env file ke host
                        scp -i $SSH_KEY -o StrictHostKeyChecking=no \
                            $ENV_FILE $SSH_USER@${HOST_IP}:${DEPLOY_DIR}/.env

                        # Deploy di host
                        ssh -i $SSH_KEY -o StrictHostKeyChecking=no \
                            $SSH_USER@${HOST_IP} "
                                docker compose -f ${DEPLOY_DIR}/docker-compose.yml \
                                    --env-file ${DEPLOY_DIR}/.env \
                                    down || true

                                docker compose -f ${DEPLOY_DIR}/docker-compose.yml \
                                    --env-file ${DEPLOY_DIR}/.env \
                                    up -d

                                rm -f ${DEPLOY_DIR}/.env
                            "
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline berhasil! Moodbites API jalan di port 8080.'
        }
        failure {
            echo 'Pipeline gagal! Periksa log di atas.'
        }
    }
}