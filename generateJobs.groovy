import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

// Load config from workspace
def configText = readFileFromWorkspace('config.json')
def config = jsonSlurper.parseText(configText)

def orgs = ["SrehaS-protean", "org2", "org3"]

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

    def repoList
    try {
        repoList = jsonSlurper.parseText(repoJsonText)
    } catch (Exception e) {
        println "[ERROR] Failed to parse '${jsonFileName}': ${e.message}"
        return
    }

    repoList.each { repoName ->
        def jobName = "${orgName}/${repoName}"
        println "[INFO] Processing job: ${jobName}"

        if (Jenkins.instance.getItemByFullName(jobName) == null) {
            println "[CREATE] Creating job: ${jobName}"

            multibranchPipelineJob(jobName) {
                branchSources {
                    branchSource {
                        source {
                            git {
                                id("${orgName}-${repoName}")
                                remote("${config.baseGitUrl}/${orgName}/${repoName}.git")
                                credentialsId(config.credentialsId)
                            }
                        }
                    }
                }
                factory {
                    workflowBranchProjectFactory {
                        scriptPath(config.scriptPath)
                    }
                }
                orphanedItemStrategy {
                    discardOldItems {
                        numToKeep(config.maxBranchesToKeep)
                    }
                }
            }
        } else {
            println "[SKIP] Job '${jobName}' already exists."
        }
    }
}
