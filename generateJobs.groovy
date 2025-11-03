import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

// Load global config
def configText = readFileFromWorkspace('config.json')
def config = jsonSlurper.parseText(configText)

def orgs = ["org1", "org2", "org3"]

orgs.each { orgName ->
    folder(orgName) {
        displayName(orgName)
        description("Folder for ${orgName} repositories")
    }

    def jsonFileName = "${orgName}.json"
    def repoJsonText

    try {
        repoJsonText = readFileFromWorkspace(jsonFileName)
    } catch (Exception e) {
        println "[WARN] JSON file '${jsonFileName}' not found in workspace. Skipping ${orgName}..."
        return
    }

    def repoBranchMap
    try {
        repoBranchMap = jsonSlurper.parseText(repoJsonText)
    } catch (Exception e) {
        println "[ERROR] Failed to parse '${jsonFileName}': ${e.message}"
        return
    }

    repoBranchMap.each { repoName, branches ->
        def jobName = "${orgName}/${repoName}"
        println "[INFO] Creating job: ${jobName}"

        pipelineJob(jobName) {
            definition {
                cpsScm {
                    scm {
                        git {
                            remote {
                                url("${config.baseGitUrl}/${orgName}/${repoName}.git")
                                credentials(config.credentialsId)
                            }
                            branch('${BRANCH}')
                        }
                    }
                    scriptPath(config.scriptPath)
                }
            }

            parameters {
                choiceParam('BRANCH', branches, 'Select the branch to build')
            }

            logRotator {
                numToKeep(config.maxBuildsToKeep ?: 20)
            }
        }
    }
}
