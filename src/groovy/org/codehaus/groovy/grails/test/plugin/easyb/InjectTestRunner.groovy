/*
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Jun 9, 2009
 * Time: 11:34:07 PM
 */
package org.codehaus.groovy.grails.test.plugin.easyb;
public class InjectTestRunner {
    protected def testCase
    protected Binding binding
    String runnerType = "Uninitialized"

    protected void initialize() {

    }

    public void injectMethods(Binding binding) {
//    println "top level inject"
        this.binding = binding

        initialize()
//    println "and out"
    }


    public boolean isConfigured() {
        return this.testCase != null
    }


    public void setUp() {
//    println "settup testcase"
        if (testCase)
            testCase.setUp()
    }

    public void tearDown() {
//    println "teardown testcase"
        if (testCase)
            testCase.tearDown()
    }
}