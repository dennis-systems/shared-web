#!/usr/bin/env groovy
@Library('jenkins-pipeline-library@v2') _

pipeline {
    agent {
        label 'maven-3.6-jdk-11'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
    }



    stages {
        stage('increment version') {
            when {
                anyOf {
                    branch '^release'
                    branch 'master'
                }
            }
            steps {
                script {
                    def pomVersion = getProjectVersionFromPom()
                    def nextVersion = gitc.nextVersionFromRemoteTag(pomVersion, 'github-enterprise')
                    info.map.nextVersion = nextVersion
                    echo "changing version from '${pomVersion}' to '${nextVersion}'."
                    configFileProvider([configFile(fileId: 'global-maven-settings', variable: 'globalsettings')]) {
                        sh "mvn -V -B -gs ${globalsettings} versions:set -DskipTests -DgenerateBackupPoms=false -DnewVersion=${nextVersion}"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                configFileProvider([configFile(fileId: 'global-maven-settings', variable: 'globalsettings')]) {
                    sh "mvn -V -B -gs ${globalsettings} -DskipTests clean install -Pdependency "
                }
            }
        }


        stage('Deploy to Artifactory') {
            when {
                anyOf {
                    branch '^release'
                    branch 'master'
                }
            }
            steps {
                withCredentials([
                        usernamePassword(
                                credentialsId   : "github-enterprise",
                                usernameVariable: "GHE_CREDENTIALS_USR",
                                passwordVariable: "GHE_CREDENTIALS_PSW"
                        )
                ]) {
                    sh "git tag v${info.map.nextVersion}"
                    sh "git push --tags"
                }

                echo "Execute maven deploy task"
                configFileProvider([configFile(fileId: 'global-maven-settings', variable: 'globalsettings')]) {
                    sh "mvn -V -B -gs ${globalsettings} -DskipTests -DskipITs=true -Pdependency deploy "
                }
            }
        }
    }
    post {
        always {
            script {
                if (currentBuild.result == null) {
                    currentBuild.result = 'SUCCESS'
                }
            }
        }
    }
}
