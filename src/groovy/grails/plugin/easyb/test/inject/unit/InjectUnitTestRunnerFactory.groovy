/*
 * User: richard
 * Date: Jun 18, 2009
 * Time: 9:20:50 PM
 */
package grails.plugin.easyb.test.inject.unit

import grails.plugin.easyb.test.inject.unit.InjectControllerTestRunner
import grails.plugin.easyb.test.inject.unit.InjectGSPTestRunner
import grails.plugin.easyb.test.inject.unit.InjectGrailsTestRunner
import grails.plugin.easyb.test.inject.unit.InjectTaglibTestRunner
import org.easyb.domain.Behavior
import grails.plugin.easyb.test.inject.TestRunnerFactory
import grails.plugin.easyb.test.GrailsEasybTestType
import grails.plugin.easyb.test.inject.InjectTestRunner

public class InjectUnitTestRunnerFactory implements TestRunnerFactory {
    private static String pathPartMustExist = "test" + File.separator + "unit"

    @Override
    public boolean willRespond(Behavior currentBehaviour, GrailsEasybTestType gett) {
        return gett.testType == "unit" && currentBehaviour.file.absolutePath.indexOf(pathPartMustExist) >= 0
    }

    @Override
    public InjectTestRunner findMatchingRunner(String expectedMatchingGrailsClass, Behavior currentBehaviour, GrailsEasybTestType gett) {
        InjectTestRunner matchingRunner = null

        String lowerName = expectedMatchingGrailsClass.toLowerCase();

        if (lowerName.endsWith("controller")) {
            matchingRunner = new InjectControllerTestRunner(expectedMatchingGrailsClass)
        } else if (lowerName.endsWith("taglib")) {
            matchingRunner = new InjectTaglibTestRunner(expectedMatchingGrailsClass)
        } else if (lowerName.endsWith("gsp")) {
            matchingRunner = new InjectGSPTestRunner()
        }

        return matchingRunner
    }

    @Override
    public InjectTestRunner getDefaultTestRunner(GrailsEasybTestType gett) {
        return new InjectGrailsTestRunner()
    }

    @Override
    public InjectTestRunner findDynamicRunner(String style, String name, String expectedMatchingGrailsClass, Behavior currentBehavior, GrailsEasybTestType gett) {
        InjectTestRunner testRunner = null

        if (!style || style == "domain") {
            testRunner = new InjectGrailsTestRunner()
        } else if (style == "gsp") {
            testRunner = new InjectGSPTestRunner()
        } else {
            switch (style.toString().toLowerCase()) {
                case "controller":
                    if (!name.endsWith("Controller")) {
                        testRunner = new InjectControllerTestRunner(name + "Controller")
                    } else {
                        testRunner = new InjectControllerTestRunner(name)
                    }
                    break
                case "taglib":
                    if (!name.endsWith("TagLib")) {
                        testRunner = new InjectTaglibTestRunner(name + "TagLib")
                    } else {
                        testRunner = new InjectTaglibTestRunner(name)
                    }
                    break
            }
        }

        return testRunner
    }
}