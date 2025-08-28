pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building the project...'
                sh 'javac ci.java'  
            }
        }
        stage('Test') {
            steps {
                echo 'Testing the project...'
                sh 'java ci'  
            }
        }
    }
}

