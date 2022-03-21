#!groovy
/*
#===================================================================================
# THIS IS A TEMPLATED FILE; NEVER EDIT MANUALLY
#
# These configurations should not be modified here, they are handled by the Copier
# tool and the DICE template. If you need to change any implementation in this file,
# please consider if they are generic enough to be included in the template, or
# use a project specific file for your needs. Instructions can be found in:
# https://tools.adidas-group.com/bitbucket/projects/CG/repos/daa_eds_python_template
#===================================================================================
*/

@Library(['MyJenkinsLibrary@0.1']) _

// Load project's commonLib
library identifier: "commonLib@${tools.git.getBranchName()}", retriever: modernSCM([
    $class: 'GitSCMSource',
    remote: 'https://github.com/bugra-yilmaz/copier-project-example.git',
    credentialsId: '54b31b56-a912-11ec-b909-0242ac120002'
])

// Project definitions
String PIPELINE_NAME = 'CI/CD - copier-project-example'

// Add defaults for log retention
meta.pipelines.withDefaults([
    // Pipeline parameters
    parameters([
        booleanParam(
            name: 'Run E2E test',
            defaultValue: false,
            description: 'Run the application pipeline with the triggered branch' +
                        ' (normally E2E test is done only when merging to staging and master)'),
    ])
])

// -------------------------------
// Main

// Read pipeline parameters
String BRANCH_NAME = tools.git.getBranchName()
String DTAP = (BRANCH_NAME == 'master') ? 'production' : (BRANCH_NAME == 'staging' ? 'staging' : 'development')

echo "dtap: $DTAP\nbranch: $BRANCH_NAME"
commonLib.setDisplayName(BRANCH_NAME)

// An e2e test is expensive (time & cost), so only run it in a meaningful step of the CI/CD flow.
Boolean runE2EAutomatically = env.CHANGE_TARGET in ['master', 'staging']

// Define global variables to be updated afterwards
String VERSION_TAG = null

try {
    node(incubation-lite) {
        stage('Checkout') {
            dir (copier-project-example) {
                deleteDir()
                checkout scm
                VERSION_TAG = commonLib.readLocalVersionTag()
                commonLib.setDisplayName(VERSION_TAG)
            }
        }

        stage('Unit tests') {
            // Run unit tests
        }

        stage('Deploy') {
            // Deploy new version
        }
    }

    stage('E2E test') {
        if(['Run E2E test'] || runE2EAutomatically) {
            // Run E2E test
        }
    }

    currentBuild.result = 'SUCCESS'
}
catch (Exception e) {
    currentBuild.result = 'FAILURE'
    println """
    ------------------------------------------
    $e
    ------------------------------------------"""
    throw e
}
finally {
    sendTeamsNotification(PIPELINE_NAME, DTAP, BRANCH_NAME, VERSION_TAG)  // a function imported from MyJenkinsLibrary
}
