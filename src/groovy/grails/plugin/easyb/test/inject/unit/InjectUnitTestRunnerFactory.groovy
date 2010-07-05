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
        InjectTestRunner matchingRunner = detectRunner(expectedMatchingGrailsClass)

        return matchingRunner
    }

    @Override
    public InjectTestRunner getDefaultTestRunner(GrailsEasybTestType gett) {
        return new InjectGrailsTestRunner()
    }

    @Override
    public InjectTestRunner findDynamicRunner(String style, String name, String expectedMatchingGrailsClass, Behavior currentBehavior, GrailsEasybTestType gett) {
        InjectTestRunner testRunner = null
        println "style: $style"

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

    private def detectRunner(className) {
        if(className.endsWith("Controller")) {
            return new InjectControllerTestRunner(className)
        } else if(className.endsWith("Taglib")) {
            return new InjectTaglibTestRunner(className)
        } else if(className.endsWith("Gsp")) {
            return new InjectGSPTestRunner(className)
        } else if(className.endsWith("ControllerStory")) {
            return new InjectControllerTestRunner(className.substring(0, className.indexOf("Story")))
        } else if(className.endsWith("TaglibStory")) {
            return new InjectTaglibTestRunner(className.substring(0, className.indexOf("Story")))
        } else if(className.endsWith("GspStory")) {
            return new InjectGSPTestRunner(className.substring(0, className.indexOf("Story")))
        } else if(className.endsWith("ControllerSpecification")) {
            return new InjectControllerTestRunner(className.substring(0, className.indexOf("Specification")))
        } else if(className.endsWith("TaglibSpecification")) {
            return new InjectTaglibTestRunner(className.substring(0, className.indexOf("Specification")))
        } else if(className.endsWith("GspSpecification")) {
            return new InjectGSPTestRunner(className.substring(0, className.indexOf("Specification")))
        }
        return null
    }
}