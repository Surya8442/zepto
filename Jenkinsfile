pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'sq'
        DOCKER_IMAGE = "surya8442/zepto"
        AWS_CREDS = credentials('aws-creds')
        AWS_DEFAULT_REGION = 'us-east-1'
        RECIPIENTS = 'surya8442@gmail.com'
    }

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/Surya8442/zepto.git'
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Jenkins to Nexus') {
            steps {
                withMaven(jdk: 'jdk17', maven: 'maven3', traceability: true) {
                    sh 'mvn deploy'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:latest .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-cred',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {
                    sh '''
                    echo $PASS | docker login -u $USER --password-stdin
                    docker push $DOCKER_IMAGE:latest
                    docker logout
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                export AWS_ACCESS_KEY_ID=$AWS_CREDS_USR
                export AWS_SECRET_ACCESS_KEY=$AWS_CREDS_PSW

                aws eks update-kubeconfig --region us-east-1 --name mycluster
                kubectl apply -f deployment.yml
                kubectl apply -f service.yml
                '''
            }
        }
    }

    post {

        success {
            emailext(
                subject: "Jenkins Job '${env.JOB_NAME}' SUCCESS",
                body: "Good news! Job '${env.JOB_NAME}' (#${env.BUILD_NUMBER}) succeeded.\n\nCheck console: ${env.BUILD_URL}",
                to: "${RECIPIENTS}"
            )
        }

        failure {
            emailext(
                subject: "Jenkins Job '${env.JOB_NAME}' FAILED",
                body: "Alert! Job '${env.JOB_NAME}' (#${env.BUILD_NUMBER}) failed.\n\nCheck console: ${env.BUILD_URL}",
                to: "${RECIPIENTS}"
            )
        }
    }
}
