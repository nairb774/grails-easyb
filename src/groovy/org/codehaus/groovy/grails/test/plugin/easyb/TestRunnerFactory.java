package org.codehaus.groovy.grails.test.plugin.easyb;

import org.easyb.domain.Behavior;

/**
 * User: richard
 */
public interface TestRunnerFactory {

    /**
     * always called before attempting to call any other functions
     *
     * @param currentBehaviour
     * @param type
     * @return
     */
    boolean willRespond(Behavior currentBehaviour, String type);

    /**
     * this method is used on initial load (startBehavior) of the behavior - and determines automatically which test case type to
     * associate with this behavior.
     *
     * @param expectedMatchingGrailsClass
     * @param currentBehaviour
     * @param testHelper
     * @param type
     * @return
     */
    InjectTestRunner findMatchingRunner(String expectedMatchingGrailsClass, Behavior currentBehaviour, GrailsEasybTestHelper testHelper, String type);

    /**
     * this is used by grailsTest style, name - such as grailsTest "controller", "Person" - would cause this to try and load a PersonController using the same package
     * as this behavior file. grailsTest "taglib", "com.bluetrainsoftware.testapp.taglibs.MyTagLib" would find the taglib wherever it was however.
     *
     * @param style
     * @param name
     * @param expectedMatchingGrailsClass
     * @param currentBehavior
     * @param testType
     * @param testHelper
     * @return
     */
    InjectTestRunner findDynamicRunner(String style, String name, String expectedMatchingGrailsClass, Behavior currentBehavior, String testType, GrailsEasybTestHelper testHelper);

    /**
     * if we don't find a specific matching runner or if the initialize fails, then we need to go back and ask for a default runner for this type. For unit it will be InjectGrailsTestRunner
     *
     * @param type
     * @return
     */
    InjectTestRunner getDefaultTestRunner(String type);
}
