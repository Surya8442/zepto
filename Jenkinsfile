pipeline {

    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        DOCKER_IMAGE = "surya8442/zepto-app"
    }

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/Surya8442/zepto.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test Coverage') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Scan') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Push Docker Image') {
            steps {
                sh 'docker push $DOCKER_IMAGE'
            }
        }
    }
}
