class EasybGrailsPlugin {
    // the plugin version
    def version = "1.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/conf/*",
            "grails-app/domain/**",
            "grails-app/controllers/**",
            "test/*",
            "web-app/**",
            "grails-app/views/**"
    ]
    def environments = ['test']
    def scopes = [excludes:'war']
    def jarExcludes = [ "default", "com\\.bluetrainsoftware\\..*" ]
    def jarGroupId = "org.easyb"
    def jarArtifactId = "grails-easyb"

	
    // inject code by Jeffrey Erikson

    def author = "Richard Vowles, Gustavo Madruga"
    def authorEmail = "richard@bluetrainsoftware.com"
    def title = "Allows Grails applications to write unit, integration and functional tests. Integrates Grails unit testing features"
    def description = '''\\
Groovy testing implementation of easyb - exposes all of the easyb functionality and an infrastructure to expose more testing functionality.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/Easyb+Plugin"

    def doWithSpring = {
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def doWithWebDescriptor = { xml ->
    }

    def doWithDynamicMethods = { ctx ->
    }

    def onChange = { event ->
    }

    def onConfigChange = { event ->
    }
}
