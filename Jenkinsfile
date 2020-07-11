final String CI_NAME = "PretronicCI"
final String CI_EMAIL = "ci@pretronic.net"
final String COMMIT_MESSAGE = "Version change %version%"
final String RESOURCE_ID = "ea6d0f4d-1a8f-4f9b-835a-c17b9169df17"

final String BRANCH_DEVELOPMENT = "origin/development"
final String BRANCH_BETA = "origin/beta"
final String BRANCH_MASTER = "origin/master"
final String PROJECT_SSH = "git@github.com:DKProject/DKBans.git"

String PROJECT_NAME = "DKBans"
String VERSION = "UNDEFINED"
String BRANCH = "UNDEFINED"
boolean SKIP = false

String QUALIFIER = "UNDEFINDED"
int BUILD_NUMBER = -1;

pipeline {
    agent any
    tools {
        maven 'Maven3'
        jdk 'Java9'
    }
    options {
        buildDiscarder logRotator(numToKeepStr: '10')
    }
    stages {
        stage('CI Check') {
            steps {
                script {
                    String name = sh script: 'git log -1 --pretty=format:\"%an\"', returnStdout: true
                    String email = sh script: 'git log -1 --pretty=format:\"%ae\"', returnStdout: true
                    if (name == CI_NAME && email == CI_EMAIL) {
                        SKIP = true;
                    }
                }
            }
        }
        stage('Read information') {
            when { equals expected: false, actual: SKIP }
            steps {
                script {
                    VERSION = readMavenPom().getVersion()
                    BRANCH = env.GIT_BRANCH
                    BUILD_NUMBER = env.BUILD_NUMBER.toInteger()
                    if(BRANCH == BRANCH_MASTER) QUALIFIER = "RELEASE"
                    else if(BRANCH == BRANCH_BETA) QUALIFIER = "BETA"
                    else if(BRANCH == BRANCH_DEVELOPMENT) QUALIFIER = "SNAPSHOT"
                }
            }
        }
        stage('Version change') {
            when { equals expected: false, actual: SKIP }
            steps {
                script {
                    String[] versionSplit = VERSION.split("[-.]")

                    String major = versionSplit[0]
                    int minorVersion = versionSplit[1].toInteger()
                    int patchVersion = versionSplit[2].toInteger()

                    VERSION = major + "." + minorVersion + "." + patchVersion + "." + BUILD_NUMBER

                    if (BRANCH.equalsIgnoreCase(BRANCH_DEVELOPMENT)) {
                        if (!VERSION.endsWith("-SNAPSHOT")) {
                            VERSION = VERSION + '-SNAPSHOT'
                        }
                    } else if(BRANCH.equalsIgnoreCase(BRANCH_BETA)) {
                        if (!VERSION.endsWith("-BETA")) {
                            VERSION = VERSION + '-BETA'
                        }
                    }
                    sh "mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$VERSION"
                }
            }
        }

        stage('Build & Deploy') {
            when { equals expected: false, actual: SKIP }
            steps {
                configFileProvider([configFile(fileId: 'afe25550-309e-40c1-80ad-59da7989fb4e', variable: 'MAVEN_GLOBAL_SETTINGS')]) {
                    sh 'mvn -B -gs $MAVEN_GLOBAL_SETTINGS clean deploy -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true'
                }
            }
        }
        stage('Publish javadoc') {
            when { equals expected: false, actual: SKIP }
            steps {
                script {
                    if(BRANCH == BRANCH_MASTER || BRANCH == BRANCH_BETA) {
                        sh 'mvn javadoc:aggregate-jar -pl :DKBans,:dkbans-api'
                        withCredentials([string(credentialsId: '120a9a64-81a7-4557-80bf-161e3ab8b976', variable: 'SECRET')]) {
                            String name = env.JOB_NAME

                            httpRequest(acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_OCTETSTREAM',
                                    httpMode: 'POST', ignoreSslErrors: true, timeout: 3000,
                                    multipartName: 'file',
                                    responseHandle: 'NONE',
                                    uploadFile: "target/${name}-${VERSION}-javadoc.jar",
                                    customHeaders:[[name:'token', value:"${SECRET}", maskValue:true]],
                                    url: "https://pretronic.net/javadoc/${name}/${VERSION}/create")
                        }
                    }
                }
            }
        }
        stage('Archive') {
            when { equals expected: false, actual: SKIP }
            steps {
                archiveArtifacts artifacts: '**/target/*.jar'
            }
        }
        stage('Publish on MirrorServer') {
            when { equals expected: false, actual: SKIP }
            steps {
                script {
                    withCredentials([string(credentialsId: '120a9a64-81a7-4557-80bf-161e3ab8b976', variable: 'SECRET')]) {

                        //Temporary because project is in beta state

                        String qualifier = QUALIFIER;
                        if(qualifier == "BETA") qualifier = "RELEASE"

                        httpRequest(acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON',
                                httpMode: 'POST', ignoreSslErrors: true,timeout: 3000,
                                responseHandle: 'NONE',
                                customHeaders:[[name:'token', value:"${SECRET}", maskValue:true]],
                                url: "https://mirror.pretronic.net/v1/$RESOURCE_ID/versions/create?name=$VERSION" +
                                        "&qualifier=$qualifier&buildNumber=$BUILD_NUMBER")

                        httpRequest(acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_OCTETSTREAM',
                                httpMode: 'POST', ignoreSslErrors: true, timeout: 3000,
                                multipartName: 'file',
                                responseHandle: 'NONE',
                                uploadFile: "dkbans-minecraft/target/dkbans-minecraft-${VERSION}.jar",
                                customHeaders:[[name:'token', value:"${SECRET}", maskValue:true]],
                                url: "https://mirror.pretronic.net/v1/$RESOURCE_ID/versions/$BUILD_NUMBER/publish?edition=default")

                        httpRequest(acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_OCTETSTREAM',
                                httpMode: 'POST', ignoreSslErrors: true, timeout: 3000,
                                multipartName: 'file',
                                responseHandle: 'NONE',
                                uploadFile: "dkbans-minecraft/target/dkbans-minecraft-${VERSION}-loader.jar",
                                customHeaders:[[name:'token', value:"${SECRET}", maskValue:true]],
                                url: "https://mirror.pretronic.net/v1/$RESOURCE_ID/versions/$BUILD_NUMBER/publish?edition=loader")
                    }
                }
            }
        }
    }
    post {
        success {
            script {
                if(!SKIP) {
                    BUILD_NUMBER++

                    sh """
                    git config --global user.name '$CI_NAME' -v
                    git config --global user.email '$CI_EMAIL' -v
                    """

                    String[] versionSplit = VERSION.split("[-.]")

                    String major = versionSplit[0]
                    int minorVersion = versionSplit[1].toInteger()
                    int patchVersion = versionSplit[2].toInteger()

                    if (BRANCH == BRANCH_DEVELOPMENT) {
                        patchVersion++

                        String version = major + "." + minorVersion + "." + patchVersion+ "." + BUILD_NUMBER + "-SNAPSHOT"
                        String commitMessage = COMMIT_MESSAGE.replace("%version%", version)
                        sh """
                        mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version
                        git add . -v
                        git commit -m '$commitMessage' -v
                        """

                        sshagent(['1c1bd183-26c9-48aa-94ab-3fe4f0bb39ae']) {
                            sh "git push origin HEAD:development -v"
                        }
                    } else if(BRANCH == BRANCH_BETA) {
                        String version = major + "." + minorVersion + "." + patchVersion+ "." + BUILD_NUMBER + "-BETA"

                        String commitMessage = COMMIT_MESSAGE.replace("%version%", version)

                        sshagent(['1c1bd183-26c9-48aa-94ab-3fe4f0bb39ae']) {

                            sh """
                            mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version
                            git add . -v
                            git commit -m '$commitMessage' -v
                            git push origin HEAD:beta -v
                            """
                            minorVersion++
                            patchVersion = 0

                            version = major + "." + minorVersion + "." + patchVersion+ "." + BUILD_NUMBER + "-SNAPSHOT"

                            commitMessage = COMMIT_MESSAGE.replace("%version%", version)

                            sh """
                            if [ -d "tempDevelopment" ]; then rm -Rf tempDevelopment; fi
                            mkdir tempDevelopment
                            cd tempDevelopment/
                            git clone --single-branch --branch development $PROJECT_SSH

                            cd $PROJECT_NAME/
                            mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version

                            git add . -v
                            git commit -m '$commitMessage' -v
                            git push origin HEAD:development -v
                            cd ..
                            cd ..
                            if [ -d "tempDevelopment" ]; then rm -Rf tempDevelopment; fi

                            """
                        }
                    } else if (BRANCH == BRANCH_MASTER) {
                        String version = major + "." + minorVersion + "." + patchVersion + "." + BUILD_NUMBER

                        String commitMessage = COMMIT_MESSAGE.replace("%version%", version)

                        sshagent(['1c1bd183-26c9-48aa-94ab-3fe4f0bb39ae']) {
                            sh """
                            mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version
                            git add . -v
                            git commit -m '$commitMessage' -v
                            git push origin HEAD:master -v
                            """
                        }
                    }
                }
            }
        }
    }
}
