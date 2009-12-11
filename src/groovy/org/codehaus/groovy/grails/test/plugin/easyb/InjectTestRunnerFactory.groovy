/*
 * User: richard
 * This is used to determine what injection factory should be used
 * Date: Jun 9, 2009
 * Time: 10:38:54 PM
 */
package org.codehaus.groovy.grails.test.plugin.easyb

import org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybTestHelper
import org.codehaus.groovy.grails.test.plugin.easyb.InjectTestRunner
import org.codehaus.groovy.grails.test.plugin.easyb.TestRunnerFactory
import org.easyb.domain.Behavior

public class InjectTestRunnerFactory {
    static List<TestRunnerFactory> externalFactories = new ArrayList<TestRunnerFactory>()

    public static registerExternalFactory(TestRunnerFactory factory) {
        externalFactories << factory
    }


    private static String getExpectedClasspath(GrailsEasybTestHelper testHelper, Behavior currentBehaviour, String type, String name = null) {
        String expectedMatchingGrailsClass

        if (!name)
            expectedMatchingGrailsClass = testHelper.fileToClassName(currentBehaviour.file, type)
        else if (name.toString().indexOf('.') == -1)
            expectedMatchingGrailsClass = testHelper.fileToClassName(currentBehaviour.file, type, name)

        def stripOffEnd = ["Story", "Specification"]

/*    println "fq name = ${expectedMatchingGrailsClass}"*/

        stripOffEnd.each {String ending ->
            if (expectedMatchingGrailsClass.endsWith(ending)) {
                expectedMatchingGrailsClass = expectedMatchingGrailsClass.substring(0, expectedMatchingGrailsClass.length() - ending.length())
            }
        }

        return expectedMatchingGrailsClass
    }

    /**
     * can return null, no matching runner for test type
     */
    public static InjectTestRunner findMatchingRunner(Behavior currentBehaviour, GrailsEasybTestHelper testHelper, String type) {

        String classpath = getExpectedClasspath(testHelper, currentBehaviour, type)

        InjectTestRunner matchingRunner = null

        for (TestRunnerFactory fact: externalFactories) {
            if (fact.willRespond(currentBehaviour, type)) {
                matchingRunner = fact.findMatchingRunner(classpath, currentBehaviour, testHelper, type)
                if (matchingRunner) break
            }
        }

        if (!matchingRunner) {
            for (TestRunnerFactory fact: externalFactories) {
                if (fact.willRespond(currentBehaviour, type)) {
                    matchingRunner = fact.getDefaultTestRunner(type)
                    if (matchingRunner) break
                }
            }
        }


        return matchingRunner
    }

    public static InjectTestRunner getDefault(Behavior currentBehaviour, String testType) {
        InjectTestRunner matchingRunner = null

        for (TestRunnerFactory fact: externalFactories) {
            if (fact.willRespond(currentBehaviour, testType)) {
                matchingRunner = fact.getDefaultTestRunner(testType)
                if (matchingRunner) break
            }
        }


        return matchingRunner
    }

    public static InjectTestRunner findDynamicRunner(String style, String name, Behavior currentBehaviour, String testType, GrailsEasybTestHelper testHelper) {
        InjectTestRunner matchingRunner = null

        String expectedMatchingGrailsClass = getExpectedClasspath(testHelper, currentBehaviour, testType, name)

        // use external iterator as can break from it
        for (TestRunnerFactory fact: externalFactories) {
            if (fact.willRespond(currentBehaviour, testType)) {
                matchingRunner = fact.findDynamicRunner(style, name, expectedMatchingGrailsClass, currentBehaviour, testType, testHelper)
                if (matchingRunner) break
            }
        }


        return matchingRunner
    }
}