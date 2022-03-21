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

/**
 * Read the version from a setup.py present in current working directory.
 *
 * @return Tag form of the version (vX.Y.Z).
 */
String readLocalVersionTag() {
    sh(script: 'python3 -m pip show setuptools || python3 -m pip install setuptools')
    String versionTag = 'v' + sh(script: 'python3 setup.py --version', returnStdout: true).trim()
    echo "Current local version: $versionTag"
    return versionTag
}

/**
 * Changes the build name on Jenkins (instead of just "#123").
 *
 * @param reference    Either a branch name or a version tag.
 * @param extraSuffix  Any extra info to display, e.g. some parameter (default empty).
 */
void setDisplayName(String reference, String extraSuffix = '') {
    String abbreviated = reference.split('/')[0].take(12)
    String displayName = "#${currentBuild.number} $abbreviated"
    if (extraSuffix && !extraSuffix.isEmpty()) {
        displayName += " - ${extraSuffix.take(10)}"
    }
    currentBuild.displayName = displayName
}
