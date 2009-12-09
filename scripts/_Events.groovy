
def addExecutioner( String className,
   testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources, appCtx = null ) {
  // this is hideously messy because we can't have the initial compile fail (which it will because _Events is loaded before the compile target)

    def testExecutionerArgs = [testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources]
    if ( appCtx ) testExecutionerArgs << appCtx
    def testExecutionClassArgs = [File.class, List.class, grails.util.BuildSettings.class, ClassLoader.class, Closure.class]
    if ( appCtx ) testExecutionClassArgs << org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext.class
    def testGrailsUnit = classLoader.loadClass( className ).getConstructor( testExecutionClassArgs as Class[] ).newInstance(testExecutionerArgs as Object[])

    testExecutioners << testGrailsUnit

}

// adds easyb test executioner to tests
eventUnitTestsPreparation = { testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources ->

    addExecutioner( "org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybUnitTestExecutioner",
        testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources )
//    testExecutioners << new org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybUnitTestExecutioner(testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources)
//

    classLoader.loadClass( "org.codehaus.groovy.grails.test.plugin.easyb.InjectTestRunnerFactory" ).getMethod( "registerExternalFactory",
          [classLoader.loadClass("org.codehaus.groovy.grails.test.plugin.easyb.TestRunnerFactory")] as Class[] ).invoke( null,
               [ classLoader.loadClass("org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectUnitTestRunnerFactory").newInstance() ] as Object[])
//    org.codehaus.groovy.grails.test.plugin.easyb.InjectTestRunnerFactory.registerExternalFactory( new org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectUnitTestRunnerFactory() )
}

eventIntegrationTestsPreparation = { testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources, appCtx ->
  addExecutioner( "org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybIntegrationTestExecutioner",
      testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources, appCtx )

	classLoader.loadClass( "org.codehaus.groovy.grails.test.plugin.easyb.InjectTestRunnerFactory" ).getMethod( "registerExternalFactory",
			[classLoader.loadClass("org.codehaus.groovy.grails.test.plugin.easyb.TestRunnerFactory")] as Class[] ).invoke( null,
			[ classLoader.loadClass("org.codehaus.groovy.grails.test.plugin.easyb.integration.InjectIntegrationTestRunnerFactory").newInstance() ] as Object[])
	//    testExecutioners << new org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybIntegrationTestExecutioner(testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources, appCtx)
}

eventFunctionalTestsPreparation = { testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources ->
  addExecutioner( "org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybFunctionalTestExecutioner",
      testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources )
//    testExecutioners << new org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybFunctionalTestExecutioner(testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources)
}

eventPackagePluginEnd = { name ->
  if ( name instanceof String && name == "easyb" ) {
		println "package name is ${name}"
		def easybzip = 'grails-easyb-1.2.zip'
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