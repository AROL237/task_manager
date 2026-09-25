pipeline {
    agent none
    environment{
        ACTIVE_ENV="prod"
        TAG="v0.0.${BUILD_NUMBER}"
        PROD_KEY= credentials('prod-server--cred')
        

    }
    stages {
            stage('Build') {
                agent { label 'worker1'                }
                
                steps {

                   sh '''
                        docker --version
                   '''
                        // docker build -t tm_ui:${TAG} ./frontend
                        // docker build -t tm_api:${TAG} ./backend
                }
            }
            stage("Deploy"){
                agent{ label "worker1"}
                steps{
                    sh '''
                    
                        ssh -i ${PROD_KEY} jenkins@prod-server-1
                        docker --version

                        
                    '''
                }
            }
        }
}
