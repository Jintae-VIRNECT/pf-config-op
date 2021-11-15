pipeline {
    agent any

    environment {
        GIT_TAG = sh(returnStdout: true, script: 'git for-each-ref refs/tags --sort=-creatordate --format="%(refname)" --count=1 | cut -d/  -f3').trim()
        REPO_NAME = sh(returnStdout: true, script: 'git config --get remote.origin.url | sed "s/.*:\\/\\/github.com\\///;s/.git$//"').trim()
    }

    stages {
        stage('Build') {
            parallel {
                stage('Develop Branch') {
                    when {
                        branch 'develop'
                    }
                    steps {
                        sh 'docker build -t pf-config -f docker/Dockerfile .'
                    }
                }

                stage('Staging Branch') {
                    when {
                        branch 'staging'
                    }
                    steps {
                        sh 'git checkout ${GIT_TAG}'
                        sh 'docker build -t pf-config:${GIT_TAG} -f docker/Dockerfile .'
                    }
                }
            }
            post {
                always {
                    jiraSendBuildInfo site: 'virtualconnection.atlassian.net'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Test Stage'
            }
        }

        stage('Deploy') {
            parallel {
                stage('Develop Branch') {
                    when {
                        branch 'develop'
                    }
                    steps {
                        sh 'count=`docker ps -a | grep pf-config | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-config && docker rm pf-config; else echo "Not Running STOP&DELETE"; fi;'
                        sh 'docker run -p 6383:6383 -e "CONFIG_ENV=git" -e "VIRNECT_ENV=develop,onpremise" --restart=always -d --name=pf-config pf-config'
                        sh 'docker tag pf-login $NEXUS_REGISTRY/pf-config'
                        sh 'docker push $NEXUS_REGISTRY/pf-config'
                        sshCommand(remote: [allowAnyHosts: true, name:"PF-Renewal", host:"192.168.6.7", user:"vntuser", password:"virnect0!"], command: "docker login ${NEXUS_REGISTRY} && docker pull $NEXUS_REGISTRY/pf-config", failOnError: true)
                        sshCommand(remote: [allowAnyHosts: true, name:"PF-Renewal", host:"192.168.6.7", user:"vntuser", password:"virnect0!"], command: "docker stop pf-config && docker rm pf-config || true", failOnError: true)
                        sshCommand(remote: [allowAnyHosts: true, name:"PF-Renewal", host:"192.168.6.7", user:"vntuser", password:"virnect0!"], command: "docker run -p 6383:6383 -e 'CONFIG_ENV=git' -e 'VIRNECT_ENV=develop,onpremise' --restart=always -d --name=pf-config $NEXUS_REGISTRY/pf-config", failOnError: true)
                        catchError {
                            sh 'docker image prune -f'
                        }
                    }
                    post {
                        always {
                            jiraSendDeploymentInfo site: 'virtualconnection.atlassian.net', environmentId: 'develop-server', environmentName: 'develop-server', environmentType: 'development'
                        }
                    }                    
                }

                stage('Staging Branch') {
                    when {
                        branch 'staging'
                    }
                    steps {

                        script {
                            docker.withRegistry("https://$aws_ecr_address", 'ecr:ap-northeast-2:aws-ecr-credentials') {
                                docker.image("pf-config:${GIT_TAG}").push("${GIT_TAG}")
                                docker.image("pf-config:${GIT_TAG}").push("latest")
                            }
                        }

                        script {
                            sshPublisher(
                                continueOnError: false, failOnError: true,
                                publishers: [
                                    sshPublisherDesc(
                                        configName: 'aws-bastion-deploy-qa',
                                        verbose: true,
                                        transfers: [
                                            sshTransfer(
                                                execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker pull $aws_ecr_address/pf-config:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-config | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-config && docker rm pf-config; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 6383:6383 -e CONFIG_ENV=git -e VIRNECT_ENV=staging --restart=always -d --name=pf-config $aws_ecr_address/pf-config:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'docker image prune -f'
                                            )
                                        ]
                                    )
                                ]
                            )
                        }
                        script {
                            def GIT_TAG_CONTENT = sh(returnStdout: true, script: 'git for-each-ref refs/tags/$GIT_TAG --format=\'%(contents)\' | sed -z \'s/\\\n/\\\\n/g\'')
                            def payload = """
                            {"tag_name": "$GIT_TAG", "name": "$GIT_TAG", "body": "$GIT_TAG_CONTENT", "target_commitish": "master", "draft": false, "prerelease": true}
                            """                             

                            sh "curl -d '$payload' -X POST 'https://api.github.com/repos/$REPO_NAME/releases?access_token=$securitykey'"
                        }
                    }
                    post {
                        always {
                            jiraSendDeploymentInfo site: 'virtualconnection.atlassian.net', environmentId: 'aws-staging-server', environmentName: 'aws-staging-server', environmentType: 'testing'
                        }
                    }                    
                }


                stage('Master Branch') {
                    when {
                        branch 'master'
                    }
                    steps {
                        script {
                            sshPublisher(
                                continueOnError: false, failOnError: true,
                                publishers: [
                                    sshPublisherDesc(
                                        configName: 'aws-bastion-deploy-prod',
                                        verbose: true,
                                        transfers: [
                                            sshTransfer(
                                                execCommand: 'aws ecr get-login --region ap-northeast-2 --no-include-email | bash'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker pull $aws_ecr_address/pf-config:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'count=`docker ps -a | grep pf-config | wc -l`; if [ ${count} -gt 0 ]; then echo "Running STOP&DELETE"; docker stop pf-config && docker rm pf-config; else echo "Not Running STOP&DELETE"; fi;'
                                            ),
                                            sshTransfer(
                                                execCommand: "docker run -p 6383:6383 -e CONFIG_ENV=git -e VIRNECT_ENV=production --restart=always -d --name=pf-config $aws_ecr_address/pf-config:\\${GIT_TAG}"
                                            ),
                                            sshTransfer(
                                                execCommand: 'docker image prune -f'
                                            )
                                        ]
                                    )
                                ]
                            )
                        }

                        script {
                            def GIT_RELEASE_INFO = sh(returnStdout: true, script: 'curl -X GET https:/api.github.com/repos/$REPO_NAME/releases/tags/$GIT_TAG?access_token=$securitykey')
                            def RELEASE = readJSON text: "$GIT_RELEASE_INFO"
                            def RELEASE_ID = RELEASE.id
                            def payload = """
                            {"prerelease": false}
                            """

                            sh "echo '$RELEASE'"

                            sh "curl -d '$payload' -X PATCH 'https://api.github.com/repos/$REPO_NAME/releases/$RELEASE_ID?access_token=$securitykey'"
                        }
                    }
                    post {
                        always {
                            jiraSendDeploymentInfo site: 'virtualconnection.atlassian.net', environmentId: 'aws-prod-server', environmentName: 'aws-prod-server', environmentType: 'production'        
                        }
                    }                    
                }
            }
        }
    }

    post {
        always {
            office365ConnectorSend webhookUrl:'https://virtualconnect.webhook.office.com/webhookb2/9b126938-3d1f-4493-98bb-33f25285af65@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/IncomingWebhook/72710a45ecce45e4bf72663717e7f323/d5a8ebb7-7fe2-4cd2-817c-1884fd25e7b0'
        }
    }
}
