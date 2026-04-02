pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        IMAGE_NAME = "surya8442/zepto"
        TAG = "latest"
        CONTAINER_NAME = "zepto-container"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Surya8442/zepto.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('SonarQube') {
            steps {
                withSonarQubeEnv('sq') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$TAG .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([string(credentialsId: 'Docker_cred', variable: 'TOKEN')]) {
                    sh '''
                    docker login -u surya8442 -p $TOKEN
                    docker push $IMAGE_NAME:$TAG
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                docker stop $CONTAINER_NAME || true
                docker rm $CONTAINER_NAME || true
                docker run -d -p 8082:8080 --name $CONTAINER_NAME $IMAGE_NAME:$TAG
                '''
            }
        }
    }
}
