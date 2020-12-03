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
                        catchError {
                            sh 'docker image prune -f'
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
                }
            }
        }
    }

    post {
        always {
            emailext(subject: '$DEFAULT_SUBJECT', body: '$DEFAULT_CONTENT', attachLog: true, compressLog: true, to: '$platform')
            office365ConnectorSend 'https://outlook.office.com/webhook/41e17451-4a57-4a25-b280-60d2d81e3dc9@d70d3a32-a4b8-4ac8-93aa-8f353de411ef/JenkinsCI/e79d56c16a7944329557e6cb29184b32/d0ac2f62-c503-4802-8bf9-f6368d7f39f8'
        }
    }
}