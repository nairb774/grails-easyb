package org.codehaus.groovy.grails.test.plugin.easyb;

import java.util.ArrayList;

/**
 * User: gcmadruga
 * Date: May 22, 2009
 * Time: 9:02:31 PM
 */
public interface GrailsTestExecutioner {
    /**
     * @return the framework used in the test (junit, easyb, etc)
     */
    String getFramework();

    /**
     * Find the tests sources. (junit also create the test suite/case)
     */
    void createTests(ArrayList<String> testNames, String type);

    int testCount();

    int countTestCases();

    void runTests();

    int failureCount();

    int errorCount();

    int runCount();

    void produceReports();

    ClassLoader getCurrentClassLoader();
}
