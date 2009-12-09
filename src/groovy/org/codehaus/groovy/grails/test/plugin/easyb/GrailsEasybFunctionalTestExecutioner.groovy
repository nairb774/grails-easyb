package org.codehaus.groovy.grails.test.plugin.easyb

import grails.util.BuildSettings
import java.io.File
import java.util.ArrayList
import java.util.List
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext

/**
 * Created by IntelliJ IDEA.
 * User: gcmadruga
 * Date: May 22, 2009
 * Time: 5:04:06 PM
 * To change this template use File | Settings | File Templates.
 */

public class GrailsEasybFunctionalTestExecutioner extends GrailsEasybUnitTestExecutioner {
    
    public GrailsEasybFunctionalTestExecutioner(File reportsDir, List<String> reportsFormat,
                                     BuildSettings settings, ClassLoader classLoader, Closure resourceResolver/*,
                                     GrailsWebApplicationContext appContext*/) {
        super(reportsDir, reportsFormat, settings, classLoader, resourceResolver)
    }

    // testName - a list of name pattern or failed test
    // type - the phase (unit, integration, etc)
    public void createTests(ArrayList<String> testNames, String type) {
        super.createTests(testNames, type)
    }
}