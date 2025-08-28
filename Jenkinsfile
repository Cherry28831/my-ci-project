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
                echo 'Installing Python...'
                sh 'sudo apt-get update && sudo apt-get install -y python3'
                echo 'Testing the project...'
                sh 'python3 ci.py || python ci.py'  // Try python3, fallback to python
            }
        }
    }
}
