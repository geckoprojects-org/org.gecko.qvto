pipeline  {
    agent any

    tools {
        jdk 'OpenJDK17'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    stages {
        stage('Main branch release') {
            when { 
                branch 'main' 
            }
            steps {
                script {
                    echo "I am building on ${env.BRANCH_NAME}"
                    try {
                        sh "./gradlew clean build release -Drelease.dir=$JENKINS_HOME/repo.gecko/release/org.gecko.qvto --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
                    } finally {
                        junit testResults: '**/generated/test-reports/**/TEST-*.xml', skipPublishingChecks: true
                    }
                }
            }
        }
        stage('Snapshot branch release') {
            when { 
                branch 'snapshot'
            }
            steps  {
                echo "I am building on ${env.JOB_NAME}"
                sh "./gradlew clean release --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
                sh "mkdir -p $JENKINS_HOME/repo.gecko/snapshot/org.gecko.qvto"
                sh "rm -rf $JENKINS_HOME/repo.gecko/snapshot/org.gecko.qvto/*"
                sh "cp -r cnf/release/* $JENKINS_HOME/repo.gecko/snapshot/org.gecko.qvto"
            }
        }
    }

}
