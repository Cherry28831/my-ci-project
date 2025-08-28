pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building the project...'
                sh 'javac ci.java'  // Compile Java code
            }
        }
        stage('Test') {
            steps {
                echo 'Testing the project...'
                sh 'java ci'  // Run Java program
            }
        }
    }
}