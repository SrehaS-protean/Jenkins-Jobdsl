import groovy.json.JsonSlurper

def orgs = ['org1', 'org2', 'org3'] // Define orgs here

orgs.each { orgName ->
  folder(orgName) {
    displayName(orgName)
    description("Folder for ${orgName} repositories")
  }

  def repoFile = new File("${orgName}.json")
  if (!repoFile.exists()) {
    println "Repo file for ${orgName} not found: ${repoFile}"
    return
  }

  def repoList = new JsonSlurper().parse(repoFile)

  repoList.each { repoName ->
    multibranchPipelineJob("${orgName}/${repoName}") {
      branchSources {
        branchSource {
          source {
            git {
              id("${orgName}-${repoName}")
              remote("https://github.com/${orgName}/${repoName}.git")
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
