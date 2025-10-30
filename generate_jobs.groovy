import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
def orgsFile = new File('orgs.json')
def orgs = jsonSlurper.parse(orgsFile)

def orgs = ["org1", "org2", "org3"]

orgs.each { orgName ->
  folder(orgName) {
    displayName(orgName)
    description("Folder for " + orgName + " repositories")
  }

  def repoFile = new File("${orgName}.json")
  def repoList = new groovy.json.JsonSlurper().parse(repoFile)

  repoList.each { repoName ->
    multibranchPipelineJob("${orgName}/${repoName}") {
      branchSources {
        branchSource {
          source {
            git {
              id("${orgName}-${repoName}")
              remote("https://github.com/${orgName}/${repoName}.git")
              credentialsId('github-token')
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
