/*
* Copyright 2004-2005 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor

/**
 * Gant script that runs the Grails unit tests
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsRun")

// test executioners
testExecutioners = []

// The four test phases that we can run.
unitTests = [ "unit" ]
integrationTests = [ "integration" ]
functionalTests = [ "functional"]
otherTests = []

// The phases that we will run on this execution. Override this in your
// own scripts to control the phases and their order.
phasesToRun = []

// A list of test names. These can be of any of this forms:
//
//   org.example.*
//   org.example.**.*
//   MyService
//   MyService.testSomeMethod
//   org.example.other.MyService.testSomeMethod
//
// The default pattern runs all tests.
testNames = buildConfig.grails.testing.patterns ?: ['**.*']

// Controls which result formats are generated. By default both XML
// and plain text files are created. You can override this in your
// own scripts.
reportFormats = [ "xml", "plain", "html" ]

// If true, only run the tests that failed before.
reRunTests = false

// Where the report files are created.
testReportsDir = grailsSettings.testReportsDir

// Set up an Ant path for the tests.
ant.path(id: "grails.test.classpath", testClasspath)

createTestReports = true
compilationFailures = []

testsFailed = false

target(allTests: "Runs the project's tests.") {
    depends(compile)

    ant.mkdir(dir: testReportsDir)
    ant.mkdir(dir: "${testReportsDir}/html")
    ant.mkdir(dir: "${testReportsDir}/plain")
    ant.mkdir(dir: "${testReportsDir}/xml")

    // If we are to run the tests that failed, replace the list of
    // test names with the failed ones.
    if (reRunTests) testNames = getFailedTests()

    // If no phases are explicitly configured, run them all.
    if (!phasesToRun) phasesToRun = [ "unit", "integration", "functional", "other" ]

    event("TestPhasesStart", [phasesToRun])

    // Process the tests in each phase that is configured to run.
    phasesToRun.each { String phase ->
        // Skip this phase if there are no test types registered for it.
        def testTypes = this."${phase}Tests"
        if (!testTypes) return

        testTypes = testTypes.unique()

        // Add a blank line before the start of this phase so that it
        // is easier to distinguish
        println()

        event("StatusUpdate", ["Starting $phase tests"])
        event("TestPhaseStart", [phase])

        // Do whatever preparation is needed to run the tests in this
        // phase. TestExecutioners shall be added to testExecutioners in the method
        this."${phase}TestsPreparation"()

        println "testing phase ${phase} - test types = ${testTypes}"

        // Now run all the tests registered for this phase.
        testTypes.each (processTests)

        // Perform any clean up required.
        this."${phase}TestsCleanUp"()

        event("TestPhaseEnd", [phase])
    }

    String msg = testsFailed ? "\nTests FAILED" : "\nTests PASSED"

    if (createTestReports) {
        msg += " - view reports in ${testReportsDir}."
    }

    event("StatusFinal", [msg])

    event("TestPhasesEnd", [])

    return testsFailed ? 1 : 0
}

/**
 * Compiles and runs all the tests of the given type and then generates
 * the reports for them.
 * @param type The type of the tests to compile (not the test phase!)
 * For example, "unit", "jsunit", "webtest", etc.
 */
processTests = { String type ->
    println "Running tests of type '$type'"

    // problemm here
    testExecutioners.each { testHelper ->

        // First compile the test classes.
        compileTests(type, testHelper)

        // Run them.
        runTests(type, testHelper)

        // Process the results.
        createReports(type, testHelper)
    }
}

/**
 * Compiles all the test classes for a particular type of test, for
 * example "unit" or "webtest". Assumes that the source files are in
 * the "test/$type" directory. It also compiles the files to distinct
 * directories for each test type: "$testClassesDir/$type".
 * @param type The type of the tests to compile (not the test phase!)
 * For example, "unit", "jsunit", "webtest", etc.
 */
compileTests = { String type, testHelper ->
    event("TestCompileStart", [type])

    def destDir = new File(grailsSettings.testClassesDir, type)
    ant.mkdir(dir: destDir.path)
    try {
        def classpathId = "grails.test.classpath"
        ant.groovyc(destdir: destDir,
                projectName: grailsAppName,
                encoding:"UTF-8",
                classpathref: classpathId) {
            javac(classpathref:classpathId, debug:"yes")
            src(path:"${basedir}/test/${type}")
        }
    }
    catch (Exception e) {
        event("StatusFinal", ["Compilation Error: ${e.message}"])
        return 1
    }

    event("TestCompileEnd", [type])
}

runTests = { String type, testHelper ->
    def prevContextClassLoader = Thread.currentThread().contextClassLoader
    try {
        // Get all the test files to run for this test type.
        testHelper.createTests(testNames, type)
        if (testHelper.testCount() == 0) {
            event("StatusUpdate", ["No tests found in test/$type of framework ${testHelper.getFramework()} to execute"])
            return
        }

        // Set the context class loader to the one used to load the tests.
        Thread.currentThread().contextClassLoader = testHelper.currentClassLoader

        event("TestSuiteStart", [type])
        int testCases = testHelper.countTestCases()
        println "-------------------------------------------------------"
        println "Running ${testCases} $type test${testCases > 1 ? 's' : ''} of framework ${testHelper.getFramework()}..."

        println "before testHelper.runTests()"
        def start = new Date()
        testHelper.runTests()
        def end = new Date()
        println "after testHelper.runTests()"

        event("TestSuiteEnd", [type])
        event("StatusUpdate", ["Tests Completed in ${end.time - start.time}ms"])

        def failedTestCount = testHelper.errorCount() + testHelper.failureCount()
        println "-------------------------------------------------------"
        println "Tests passed: ${testHelper.runCount() - failedTestCount}"
        println "Tests failed: ${failedTestCount}"
        println "-------------------------------------------------------"

        // If any of the tests fail, we register the whole test run as
        // a failure.
        if (failedTestCount > 0) testsFailed = true
    }
    catch (Exception e) {
        event("StatusFinal", ["Error running $type tests: ${e.toString()}"])
        e.printStackTrace()
        testsFailed = true
        return null
    }
    finally {
        Thread.currentThread().contextClassLoader = prevContextClassLoader
    }
}

createReports = { String type, testHelper ->
    if(createTestReports) {
        testHelper.produceReports()
    }
}

/**
 * Prepare for the unit tests. Simply sets up the default test helper.
 */
unitTestsPreparation = {
    testExecutioners = []
    //testExecutioners << new GrailsUnitTestExecutioner(testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources)
    event("UnitTestsPreparation", [testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources])
}

/**
 * Prepare for the integration tests. Packages the tests and then
 * bootstraps the application.
 */
integrationTestsPreparation = {
    packageTests()
    bootstrap()

    // Get the Grails application instance created by the bootstrap
    // process.
    def app = appCtx.getBean(GrailsApplication.APPLICATION_ID)
    if (app.parentContext == null) {
        app.applicationContext = appCtx
    }

    def beanNames = appCtx.getBeanNamesForType(PersistenceContextInterceptor)
    if (beanNames.size() > 0) appCtx.getBean(beanNames[0]).init()

    // We use a specialist test helper for integration tests.
    testExecutioners = []
    //testExecutioners << new GrailsIntegrationTestExecutioner(testReportsDir, reportFormats, grailsSettings, app.classLoader, resolveResources, appCtx)
    event("IntegrationTestsPreparation", [testExecutioners, testReportsDir, reportFormats, grailsSettings, app.classLoader, resolveResources, appCtx]) 
}

/**
 * Prepare for the functional tests. Starts up the test server.
 */
functionalTestsPreparation = {
    packageApp()

    // surely you should run them in a different VM?
    //runApp()

    testExecutioners = []
    event("FunctionalTestsPreparation", [testExecutioners, testReportsDir, reportFormats, grailsSettings, classLoader, resolveResources])
}

/**
 * Prepare for the unit tests. Intentionally does nothing because unit
 * tests require no special preparation.
 */
otherTestsPreparation = {
    testExecutioners = []
    event("OtherTestsPreparation", [testExecutioners])
}

/**
 * Clean up after the unit tests. Nothing to do.
 */
unitTestsCleanUp = {
}

/**
 * Clean up after the integration tests. Shuts down the bootstrapped
 * Grails application.
 */
integrationTestsCleanUp = {
    // Kill any context interceptor we might have.
    def beanNames = appCtx.getBeanNamesForType(PersistenceContextInterceptor)
    if (beanNames.size() > 0) appCtx.getBean(beanNames[0]).destroy()

    shutdownApp()
}

/**
 * Clean up after the functional tests. Shuts down the test server.
 */
functionalTestsCleanUp = {
    stopServer()
}

/**
 * Clean up after the "other" tests. Nothing to do.
 */
otherTestsCleanUp = {
}

target(packageTests: "Puts some useful things on the classpath for integration tests.") {
    ant.copy(todir: new File(grailsSettings.testClassesDir, "integration").path) {
        fileset(dir: "${basedir}", includes: metadataFile.name)
    }
    ant.copy(todir: grailsSettings.testClassesDir.path, failonerror: false) {
        fileset(dir: "${basedir}/grails-app/conf", includes: "**", excludes: "*.groovy, log4j*, hibernate, spring")
        fileset(dir: "${basedir}/grails-app/conf/hibernate", includes: "**/**")
        fileset(dir: "${basedir}/src/java") {
            include(name: "**/**")
            exclude(name: "**/*.java")
        }
        fileset(dir: "${basedir}/test/unit") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy")
        }
        fileset(dir: "${basedir}/test/integration") {
            include(name: "**/**")
            exclude(name: "**/*.java")
            exclude(name: "**/*.groovy")
        }
    }
}

def getFailedTests() {
    File file = new File("${testReportsDir}/TESTS-TestSuites.xml")
    if (file.exists()) {
        def xmlParser = new XmlParser().parse(file)
        def failedTests = xmlParser.testsuite.findAll { it.'@failures' =~ /.*[1-9].*/ || it.'@errors' =~ /.*[1-9].*/}

        return failedTests.collect {
            String testName = it.'@name'
            testName = testName.replace('Tests', '')
            def pkg = it.'@package'
            if (pkg) {
                testName = pkg + '.' + testName
            }
            return testName
        }
    } else {
        return []
    }
}
