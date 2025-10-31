pipeline {
    agent any

    environment {
        DSL_SCRIPT = 'generateJobs.groovy'
    }

    stages {
        stage('Checkout DSL Repo') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], userRemoteConfigs: [[url: 'https://github.com/your-org/jenkins-dsl-configs.git']]])
            }
        }

        stage('Generate Jobs from DSL') {
            steps {
                jobDsl targets: "${DSL_SCRIPT}",
                       removedJobAction: 'IGNORE',
                       removedViewAction: 'IGNORE',
                       lookupStrategy: 'SEED_JOB'
            }
        }
    }

    post {
        success {
            echo '[SUCCESS] DSL jobs generated successfully.'
        }
        failure {
            echo '[FAILURE] DSL job generation failed.'
        }
    }
}