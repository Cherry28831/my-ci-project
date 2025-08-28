pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building the project...'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing the project...'
                sh 'python3 hello.py'  // Runs the Python script as a "test"
            }
        }
    }
}