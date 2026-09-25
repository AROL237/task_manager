pipeline {
    agent none
    environment{
        ACTIVE_ENV="prod"
        TAG="v0.0.${BUILD_NUMBER}"
        PROD_KEY= credentials('prod-server--cred')
        

    }
    stages {
            stage('Build') {
                agent { label 'worker1' }
                
                steps {

                   sh '''
                        docker --version
                   '''
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
