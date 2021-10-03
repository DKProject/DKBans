#!groovy

//Configuration
//Set BRANCH_BETA to null, if you don't have it
final String CI_NAME = "PretronicCI"
final String CI_EMAIL = "ci@pretronic.net"
final String COMMIT_MESSAGE = "Version change %version%"

final String RESOURCE_ID = "ea6d0f4d-1a8f-4f9b-835a-c17b9169df17"

final String BRANCH_DEVELOPMENT = "development"
final String BRANCH_BETA = "beta"
final String BRANCH_MASTER = "master"

String PROJECT_NAME = "DKBans"

boolean JAVADOCS_ENABLED = true
String JAVADOCS_NAME = "dkbans"
String JAVADOCS_MODULES = ":DKBans,:dkbans-api"


def MIRROR_SERVER_PUBLISHING = [
        "dkbans-minecraft/target/dkbans-minecraft-%version%.jar": "default",
        "dkbans-minecraft/target/dkbans-minecraft-%version%-loader.jar": "loader"
]

String MAVEN_SETTINGS_FILE_ID = "afe25550-309e-40c1-80ad-59da7989fb4e"
String MIRROR_SERVER_TOKEN_CREDENTIAL_ID = "120a9a64-81a7-4557-80bf-161e3ab8b976"
String JAVADOCS_TOKEN_CREDENTIAL_ID = "120a9a64-81a7-4557-80bf-161e3ab8b976"
String PRETRONIC_CI_SSH_KEY_CREDENTIAL_ID = "1c1bd183-26c9-48aa-94ab-3fe4f0bb39ae"

String MINECRAFT_MESSAGES_DIRECTORY = "dkbans-minecraft/src/main/resources/messages/"

//Internal
String PROJECT_SSH = "UNDEFINED"
String VERSION = "UNDEFINED"
String BRANCH = "UNDEFINED"
boolean SKIP = false
String QUALIFIER = "UNDEFINDED"
int BUILD_NUMBER = -1;
boolean BETA_PATCH_NUMBER_UPDATE = false;

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
                    String change = sh script: 'git diff --quiet ' + MINECRAFT_MESSAGES_DIRECTORY + '/default.yml', returnStdout: true
                    println "Changes:"+change
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
                    PROJECT_SSH = env.GIT_URL
                    if(BRANCH == "origin/$BRANCH_MASTER") QUALIFIER = "RELEASE"
                    else if(BRANCH_BETA != null && BRANCH == "origin/$BRANCH_BETA") QUALIFIER = "BETA"
                    else if(BRANCH == "origin/$BRANCH_DEVELOPMENT") QUALIFIER = "SNAPSHOT"
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

                    if(versionSplit.length == 4) {
                        String oldQualifier = versionSplit[3];
                        if(oldQualifier == "BETA") {
                            BETA_PATCH_NUMBER_UPDATE = true;
                        }
                    }

                    VERSION = major + "." + minorVersion + "." + patchVersion + "." + BUILD_NUMBER

                    if (BRANCH.equalsIgnoreCase("origin/$BRANCH_DEVELOPMENT")) {
                        if (!VERSION.endsWith("-SNAPSHOT")) {
                            VERSION = VERSION + '-SNAPSHOT'
                        }
                    } else if(BRANCH_BETA != null && BRANCH.equalsIgnoreCase("origin/$BRANCH_BETA")) {
                        if (!VERSION.endsWith("-BETA")) {
                            VERSION = VERSION + '-BETA'
                        }
                    }
                    sh "mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$VERSION"
                }
            }
        }
        stage('Checkout Translations') {
            when { equals expected: false, actual: SKIP }
            steps {
                sshagent([PRETRONIC_CI_SSH_KEY_CREDENTIAL_ID]) {
                    sh """
                    if [ -d "translations" ]; then rm -Rf translations; fi
                    mkdir translations

                    cd translations/
                    git clone --single-branch --branch main git@github.com:DKProject/Translations.git
                    """
                }
               script {

                    sh """
                    cp translations/Translations/${PROJECT_NAME}/* ${MINECRAFT_MESSAGES_DIRECTORY} -n
                    rm -Rf translations/
                    """
               }
            }
        }
        stage('Build & Deploy') {
            when { equals expected: false, actual: SKIP }
            steps {
                configFileProvider([configFile(fileId: MAVEN_SETTINGS_FILE_ID, variable: 'MAVEN_GLOBAL_SETTINGS')]) {
                    sh 'mvn -B -gs $MAVEN_GLOBAL_SETTINGS clean deploy'
                }
            }
        }
        stage('Clean Translations') {
            steps {
                script {
                    dir(MINECRAFT_MESSAGES_DIRECTORY) {
                       def files = findFiles()

                       files.each{ file ->
                          if(!file.name.equalsIgnoreCase("default.yml")) {
                            sh "rm ${file.name}"
                          }
                       }
                    }
                }
            }
        }
        stage('Publish javadoc') {
            when { equals expected: false, actual: SKIP }
            steps {
                script {
                    if(!JAVADOCS_ENABLED) return
                    if(BRANCH == "origin/$BRANCH_MASTER" || (BRANCH_BETA != null && BRANCH == "origin/$BRANCH_BETA")) {
                        sh 'mvn javadoc:aggregate-jar -Dadditionalparam=-Xdoclint:none -DadditionalJOption=-Xdoclint:none -pl '+ JAVADOCS_MODULES
                        withCredentials([string(credentialsId: JAVADOCS_TOKEN_CREDENTIAL_ID, variable: 'SECRET')]) {
                            String name = env.JOB_NAME
                            httpRequest(acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_OCTETSTREAM',
                                    httpMode: 'POST', ignoreSslErrors: true, timeout: 3000,
                                    multipartName: 'file',
                                    responseHandle: 'NONE',
                                    uploadFile: "target/${name}-${VERSION}-javadoc.jar",
                                    customHeaders:[[name:'token', value:"${SECRET}", maskValue:true]],
                                    url: "https://pretronic.net/javadoc/${JAVADOCS_NAME}/${VERSION}/create")
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
                    if(MIRROR_SERVER_PUBLISHING.isEmpty()) return
                    withCredentials([string(credentialsId: MIRROR_SERVER_TOKEN_CREDENTIAL_ID, variable: 'SECRET')]) {
                        httpRequest(acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON',
                                httpMode: 'POST', ignoreSslErrors: true,timeout: 3000,
                                responseHandle: 'NONE',
                                customHeaders:[[name:'token', value:SECRET, maskValue:true]],
                                url: "https://mirror.mcnative.org/v1/$RESOURCE_ID/versions/create?name=$VERSION&qualifier=$QUALIFIER&buildNumber=$BUILD_NUMBER")

                        MIRROR_SERVER_PUBLISHING.each { entry ->
                            sleep 1
                            httpRequest(acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_OCTETSTREAM',
                                    httpMode: 'POST', ignoreSslErrors: true, timeout: 3000,
                                    multipartName: 'file',
                                    responseHandle: 'NONE',
                                    uploadFile: entry.key.replace("%version%", VERSION),
                                    customHeaders:[[name:'token', value:SECRET, maskValue:true]],
                                    url: "https://mirror.mcnative.org/v1/$RESOURCE_ID/versions/$BUILD_NUMBER/publish?edition=${entry.value}")
                        }
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

                    if (BRANCH == "origin/$BRANCH_DEVELOPMENT") {
                        patchVersion++

                        String version = major + "." + minorVersion + "." + patchVersion+ "." + BUILD_NUMBER + "-SNAPSHOT"
                        String commitMessage = COMMIT_MESSAGE.replace("%version%", version)

                        sh """
                        mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version
                        git add . -v
                        git commit -m '$commitMessage' -v
                        """

                        sshagent([PRETRONIC_CI_SSH_KEY_CREDENTIAL_ID]) {
                            sh "git push origin HEAD:$BRANCH_DEVELOPMENT -v"
                        }
                    } else if(BRANCH_BETA != null && BRANCH == "origin/$BRANCH_BETA") {
                        if(BETA_PATCH_NUMBER_UPDATE) {
                            patchVersion++;
                        }
                        String version = major + "." + minorVersion + "." + patchVersion+ "." + BUILD_NUMBER + "-BETA"

                        String commitMessage = COMMIT_MESSAGE.replace("%version%", version)

                        sshagent([PRETRONIC_CI_SSH_KEY_CREDENTIAL_ID]) {

                            sh """
                            mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version
                            git add . -v
                            git commit -m '$commitMessage' -v
                            git push origin HEAD:$BRANCH_BETA -v
                            """
                            minorVersion++
                            patchVersion = 0

                            version = major + "." + minorVersion + "." + patchVersion+ "." + BUILD_NUMBER + "-SNAPSHOT"

                            commitMessage = COMMIT_MESSAGE.replace("%version%", version)

                            sh """
                            if [ -d "tempDevelopment" ]; then rm -Rf tempDevelopment; fi
                            mkdir tempDevelopment
                            cd tempDevelopment/
                            git clone --single-branch --branch $BRANCH_DEVELOPMENT $PROJECT_SSH

                            cd $PROJECT_NAME/
                            mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version

                            git add . -v
                            git commit -m '$commitMessage' -v
                            git push origin HEAD:$BRANCH_DEVELOPMENT -v
                            cd ..
                            cd ..
                            if [ -d "tempDevelopment" ]; then rm -Rf tempDevelopment; fi
                            """
                        }
                    } else if (BRANCH == "origin/$BRANCH_MASTER") {
                        String version = major + "." + minorVersion + "." + patchVersion + "." + BUILD_NUMBER

                        String commitMessage = COMMIT_MESSAGE.replace("%version%", version)

                        sshagent([PRETRONIC_CI_SSH_KEY_CREDENTIAL_ID]) {
                            sh """
                            mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version
                            git add . -v
                            git commit -m '$commitMessage' -v
                            git push origin HEAD:$BRANCH_MASTER -v
                            """

                            if(BRANCH_BETA == null) {
                                sh """
                                if [ -d "tempDevelopment" ]; then rm -Rf tempDevelopment; fi
                                mkdir tempDevelopment
                                cd tempDevelopment/
                                git clone --single-branch --branch $BRANCH_DEVELOPMENT $PROJECT_SSH
                                
                                cd $PROJECT_NAME/
                                mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version
                                git add . -v
                                git commit -m '$commitMessage' -v
                                git push origin HEAD:$BRANCH_DEVELOPMENT -v
                                cd ..
                                cd ..
                                if [ -d "tempDevelopment" ]; then rm -Rf tempDevelopment; fi
                                """
                            }
                        }
                    }
                }
            }
        }
    }
}