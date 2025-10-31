import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
def orgs = ["SrehaS-protean", "org2", "org3"]
def baseGitUrl = 'https://github.com'
def credentialsId = 'github-token'
def scriptPath = 'Jenkinsfile'
def maxBranchesToKeep = 20

orgs.each { orgName ->
    folder(orgName) {
        displayName(orgName)
        description("Folder for ${orgName} repositories")
    }

    def repoFile = new File("${orgName}.json")
    if (!repoFile.exists()) {
        println "[WARN] JSON file for ${orgName} not found. Skipping..."
        return
    }

    def repoList
    try {
        repoList = jsonSlurper.parse(repoFile)
    } catch (Exception e) {
        println "[ERROR] Failed to parse ${orgName}.json: ${e.message}"
        return
    }

    repoList.each { repoName ->
        def jobName = "${orgName}/${repoName}"
        println "[INFO] Checking if job '${jobName}' already exists..."

        // Only create job if it doesn't exist
        if (Jenkins.instance.getItemByFullName(jobName) == null) {
            println "[CREATE] Creating new job: ${jobName}"

            multibranchPipelineJob(jobName) {
                branchSources {
                    branchSource {
                        source {
                            git {
                                id("${orgName}-${repoName}")
                                remote("${baseGitUrl}/${orgName}/${repoName}.git")
                                credentialsId(credentialsId)
                            }
                        }
                    }
                }
                factory {
                    workflowBranchProjectFactory {
                        scriptPath(scriptPath)
                    }
                }
                orphanedItemStrategy {
                    discardOldItems {
                        numToKeep(maxBranchesToKeep)
                    }
                }
            }
        } else {
            println "[SKIP] Job '${jobName}' already exists. Skipping creation."
        }
    }
}