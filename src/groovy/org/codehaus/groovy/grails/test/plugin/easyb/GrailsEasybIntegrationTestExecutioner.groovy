package org.codehaus.groovy.grails.test.plugin.easyb

import grails.util.BuildSettings
import java.io.File
import java.util.ArrayList
import java.util.List
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext

/**
 * User: gcmadruga
 * Date: May 22, 2009
 * Time: 5:04:06 PM
 */

public class GrailsEasybIntegrationTestExecutioner extends GrailsEasybUnitTestExecutioner {
    
    GrailsWebApplicationContext appContext

    public GrailsEasybIntegrationTestExecutioner(File reportsDir, List<String> reportsFormat,
                                     BuildSettings settings, ClassLoader classLoader, Closure resourceResolver,
                                     GrailsWebApplicationContext appContext) {
        super(reportsDir, reportsFormat, settings, classLoader, resourceResolver)
        this.appContext = appContext
    }

    // testName - a list of name pattern or failed test
    // type - the phase (unit, integration, etc)
    public void createTests(ArrayList<String> testNames, String type) {
        super.createTests(testNames, type)
    }
}