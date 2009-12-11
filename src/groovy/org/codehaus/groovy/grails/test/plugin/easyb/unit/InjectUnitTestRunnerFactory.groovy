/*
 * User: richard
 * Date: Jun 18, 2009
 * Time: 9:20:50 PM
 */
package org.codehaus.groovy.grails.test.plugin.easyb.unit

import org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybTestHelper
import org.codehaus.groovy.grails.test.plugin.easyb.InjectTestRunner
import org.codehaus.groovy.grails.test.plugin.easyb.TestRunnerFactory
import org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectControllerTestRunner
import org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectGSPTestRunner
import org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectGrailsTestRunner
import org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectTaglibTestRunner
import org.easyb.domain.Behavior

public class InjectUnitTestRunnerFactory implements TestRunnerFactory {
    private static String pathPartMustExist = "test" + File.separator + "unit"

    public boolean willRespond(Behavior currentBehaviour, String type) {
        return type == "unit" && currentBehaviour.file.absolutePath.indexOf(pathPartMustExist) >= 0
    }

    public InjectTestRunner findMatchingRunner(String expectedMatchingGrailsClass, Behavior currentBehaviour, GrailsEasybTestHelper testHelper, String type) {
        InjectTestRunner matchingRunner = null

        String lowerName = expectedMatchingGrailsClass.toLowerCase();

        if (lowerName.endsWith("controller"))
            matchingRunner = new InjectControllerTestRunner(expectedMatchingGrailsClass)
        else if (lowerName.endsWith("taglib"))
            matchingRunner = new InjectTaglibTestRunner(expectedMatchingGrailsClass)
        else if (lowerName.endsWith("gsp"))
                matchingRunner = new InjectGSPTestRunner()

        return matchingRunner
    }

    public InjectTestRunner getDefaultTestRunner(String type) {
        return new InjectGrailsTestRunner()
    }

    public InjectTestRunner findDynamicRunner(String style, String name, String expectedMatchingGrailsClass, Behavior currentBehavior, String testType, GrailsEasybTestHelper testHelper) {
        InjectTestRunner testRunner = null

        if (!style || style == "domain") {
            testRunner = new InjectGrailsTestRunner()
        }
        else if (style == "gsp") {
            testRunner = new InjectGSPTestRunner()
        }
        else {

            switch (style.toString().toLowerCase()) {
                case "controller":
                    if (!name.endsWith("Controller"))
                        testRunner = new InjectControllerTestRunner(name + "Controller")
                    else
                        testRunner = new InjectControllerTestRunner(name)
                    break
                case "taglib":
                    if (!name.endsWith("TagLib"))
                        testRunner = new InjectTaglibTestRunner(name + "TagLib")
                    else
                        testRunner = new InjectTaglibTestRunner(name)
                    break
                default:
                    break
            }
        }

        return testRunner
    }
}