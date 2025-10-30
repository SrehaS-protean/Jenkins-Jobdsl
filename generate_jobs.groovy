import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
def orgsFile = new File('orgs.json')
def orgs = jsonSlurper.parse(orgsFile)

orgs.each { orgName ->
    folder(orgName) {
        displayName(orgName)
        description("Folder for ${orgName} repositories")
    }

    def reposFile = new File("${orgName}_repos.json")
    def repos = jsonSlurper.parse(reposFile)

    repos.each { fullRepo ->
        def repoName = fullRepo.split('/')[1]

        multibranchPipelineJob("${orgName}/${repoName}") {
            branchSources {
                branchSource {
                    source {
                        git {
                            id("${orgName}-${repoName}")
                            remote("https://github.com/${fullRepo}.git")
                            credentialsId('github-token') // Replace with your actual credentials ID
                        }
                    }
                }
            }
            factory {
                workflowBranchProjectFactory {
                    scriptPath('Jenkinsfile')
                }
            }
            orphanedItemStrategy {
                discardOldItems {
                    numToKeep(20)
                }
            }
        }
    }
}
