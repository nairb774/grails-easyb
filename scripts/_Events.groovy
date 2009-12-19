/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

eventAllTestsStart = {
    def easybTestTypeClass = classLoader.loadClass('grails.plugin.easyb.test.GrailsEasybTestType')
    unitTests << easybTestTypeClass.newInstance('easyb', 'unit')
    integrationTests << easybTestTypeClass.newInstance('easyb', 'integration')
    functionalTests << easybTestTypeClass.newInstance('easyb', 'functional')

    // register Injectors
    classLoader.loadClass("grails.plugin.easyb.test.inject.InjectTestRunnerFactory").getMethod("registerExternalFactory", [classLoader.loadClass("grails.plugin.easyb.test.inject.TestRunnerFactory")] as Class[]).invoke(null, [classLoader.loadClass("grails.plugin.easyb.test.inject.unit.InjectUnitTestRunnerFactory").newInstance()] as Object[])
    classLoader.loadClass("grails.plugin.easyb.test.inject.InjectTestRunnerFactory").getMethod("registerExternalFactory", [classLoader.loadClass("grails.plugin.easyb.test.inject.TestRunnerFactory")] as Class[]).invoke(null, [classLoader.loadClass("grails.plugin.easyb.test.inject.integration.InjectIntegrationTestRunnerFactory").newInstance()] as Object[])
}

// I believe that we do not need this
/*
eventTestPhaseStart = { phaseName ->
    if (phaseName == 'functional') {
        def functionalSpecificationClass = classLoader.loadClass("grails.plugin.easyb.EasybFunctionalSpecification")
        functionalSpecificationClass.baseUrl = argsMap["baseUrl"] ?: "http://localhost:$serverPort$serverContextPath"
    }
}*/

/**
** put this back in as it is critical to get rid of the garbage from the plugin zip otherwise the tests pollute the 
** package-space of the people who use it. Without this, there is a whole lot of junk in the final file
*/
eventPackagePluginEnd = { name ->
  if ( name instanceof String && name == "easyb" ) {
		println "package name is ${name}"
		// TODO: fix the hard coding of this, how to pick up the plugin version?
		def easybzip = 'grails-easyb-1.3.zip'
		def excludeList = ["web-app", "grails-app/conf/**", "grails-app/domain/**", "grails-app/controllers/**", "grails-app/services/**",
		                   "grails-app/views/**", "grails-app/i18n/**", "plugin.xml"]
    ant.delete(dir:'tmp')
    ant.delete(file:'tmp.zip')
    ant.mkdir( dir:'tmp' )
    ant.unzip( dest:'tmp', src:easybzip )
    ant.delete(dir: 'tmp', includes:excludeList)
    ant.delete(dir:'tmp/web-app')
    ant.delete(file:'tmp/plugin.xml')
    ant.copy(file:'plugin-correct.xml', tofile:'tmp/plugin.xml')
		ant.zip(destfile:'tmp.zip', filesonly:true, basedir:'tmp') 
		ant.delete(file:easybzip)
		ant.move(file:'tmp.zip', tofile:easybzip)
    ant.delete(dir:'tmp')
  }
}