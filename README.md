Jenkins Job DSL Setup for Multibranch Pipelines

This repository automates the creation of multibranch pipeline jobs in Jenkins using the Job DSL plugin. It supports hundreds of services across multiple organizations, with a modular and scalable structure.

DSL Scripts: 
generate_jobs.groovy: Uses individual JSON files per organization.
generate_jobs_nested.groovy: Uses a nested JSON structure (optional).

JSON Files: 
orgs.json: List of all organizations.
org1.json, org2.json, etc.: Each contains a list of repositories for that organization.
nested_orgs.json: (Optional) A single file mapping orgs to their repo lists.
