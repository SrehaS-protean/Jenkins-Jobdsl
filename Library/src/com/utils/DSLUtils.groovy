package com.utils

import groovy.json.JsonSlurper

class DSLUtils {
    static Map createRepoBranchMap(String jsonText) {
        def jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(jsonText)
    }
}
