pipeline {
    agent none
    environment{
        ACTIVE_ENV="prod"

    }
    stages {
            stage('Frontend -- Build') {
                agent {
                    docker {
                        image "node:20-alpine"
                        label "worker1"
                    }
                 }
                
                steps {
                   sh '''
                   echo 'running in node: $(node --version)'
                   ls -al

                   ls -al ./frontend
                   '''
                }
            }
        }
}
