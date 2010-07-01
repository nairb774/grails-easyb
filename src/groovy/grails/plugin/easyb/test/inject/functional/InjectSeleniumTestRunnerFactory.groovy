package grails.plugin.easyb.test.inject.functional

import grails.plugin.easyb.test.inject.InjectTestRunner
import grails.plugin.easyb.test.inject.TestRunnerFactory
import org.easyb.domain.Behavior
import grails.plugin.easyb.test.GrailsEasybTestType

class InjectSeleniumTestRunnerFactory implements TestRunnerFactory {
    private static String pathPartMustExist = "test" + File.separator + "selenium"

    boolean willRespond(Behavior currentBehaviour, GrailsEasybTestType gett) {
        return gett.testType == "selenium" && currentBehaviour.file.absolutePath.indexOf(pathPartMustExist) >= 0
    }

    InjectTestRunner findMatchingRunner(String expectedMatchingGrailsClass, Behavior currentBehaviour, GrailsEasybTestType gett) {
        InjectTestRunner matchingRunner = null

        String lowerName = expectedMatchingGrailsClass.toLowerCase();

        if (lowerName.endsWith("selenium")) {
            matchingRunner = new InjectSeleniumTestRunner()
        }

        return matchingRunner
    }

    InjectTestRunner findDynamicRunner(String style, String name, String expectedMatchingGrailsClass, Behavior currentBehavior, GrailsEasybTestType gett) {
        InjectTestRunner testRunner = null

        if (style == "selenium") {
            testRunner = new InjectSeleniumTestRunner()
        }

        return testRunner
    }

    InjectTestRunner getDefaultTestRunner(GrailsEasybTestType gett) {
        return new InjectSeleniumTestRunner()
    }
}
