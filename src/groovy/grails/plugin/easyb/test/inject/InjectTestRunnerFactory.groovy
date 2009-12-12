package grails.plugin.easyb.test.inject

import org.easyb.domain.Behavior
import grails.plugin.easyb.test.GrailsEasybTestType

/*
 * User: richard
 * This is used to determine what injection factory should be used
 * Date: Jun 9, 2009
 * Time: 10:38:54 PM
 */
public class InjectTestRunnerFactory {
    static List<TestRunnerFactory> externalFactories = new ArrayList<TestRunnerFactory>()

    public static registerExternalFactory(TestRunnerFactory factory) {
        externalFactories << factory
    }

    private static String getExpectedClasspath(GrailsEasybTestType gett, Behavior currentBehaviour, String name = null) {
        String expectedMatchingGrailsClass

        expectedMatchingGrailsClass = gett.sourceFileToClassName(currentBehaviour.file)

        gett.getTestSuffixes().each {String ending ->
            if (expectedMatchingGrailsClass.endsWith(ending)) {
                expectedMatchingGrailsClass = expectedMatchingGrailsClass.substring(0, expectedMatchingGrailsClass.length() - ending.length())
            }
        }

        return expectedMatchingGrailsClass
    }

    /**
     * can return null, no matching runner for test type
     */
    public static InjectTestRunner findMatchingRunner(Behavior currentBehaviour, GrailsEasybTestType gett) {
        String classpath = getExpectedClasspath(gett, currentBehaviour, gett.testType)

        InjectTestRunner matchingRunner = null

        for (TestRunnerFactory fact: externalFactories) {
            if (fact.willRespond(currentBehaviour, gett)) {
                matchingRunner = fact.findMatchingRunner(classpath, currentBehaviour, gett)
                if (matchingRunner) {
                    break
                }
            }
        }

        if (!matchingRunner) {
            for (TestRunnerFactory fact: externalFactories) {
                if (fact.willRespond(currentBehaviour, gett)) {
                    matchingRunner = fact.getDefaultTestRunner(gett)
                    if (matchingRunner) {
                        break
                    }
                }
            }
        }

        return matchingRunner
    }

    public static InjectTestRunner getDefault(Behavior currentBehaviour, GrailsEasybTestType gett) {
        InjectTestRunner matchingRunner = null

        for (TestRunnerFactory fact: externalFactories) {
            if (fact.willRespond(currentBehaviour, gett)) {
                matchingRunner = fact.getDefaultTestRunner(gett)
                if (matchingRunner) {
                    break
                }
            }
        }

        return matchingRunner
    }

    public static InjectTestRunner findDynamicRunner(String style, String name, Behavior currentBehaviour, GrailsEasybTestType gett) {
        InjectTestRunner matchingRunner = null

        String expectedMatchingGrailsClass = getExpectedClasspath(gett, currentBehaviour, name)

        // use external iterator as can break from it
        for (TestRunnerFactory fact: externalFactories) {
            if (fact.willRespond(currentBehaviour, gett)) {
                matchingRunner = fact.findDynamicRunner(style, name, expectedMatchingGrailsClass, currentBehaviour, gett)
                if (matchingRunner) {
                    break
                }
            }
        }

        return matchingRunner
    }
}