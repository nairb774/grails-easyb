package org.codehaus.groovy.grails.test;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import grails.util.BuildSettings;

import java.io.File;
import java.util.ArrayList ;

import groovy.lang.Closure;
import org.codehaus.groovy.grails.test.plugin.easyb.GrailsTestExecutioner;

public class GrailsUnitTestExecutioner implements GrailsTestExecutioner {
    private DefaultGrailsTestRunner testRunner;
    private TestSuite testSuite;
    private TestResult testResult;
    private DefaultGrailsTestHelper testHelper;

    private File reportsDir;
    private ArrayList<String> reportsFormat;

    public GrailsUnitTestExecutioner(File reportsDir, ArrayList<String> reportsFormat,
                                     BuildSettings settings, ClassLoader classLoader, Closure resourceResolver) {
        this.reportsDir = reportsDir;
        this.reportsFormat = reportsFormat;

        this.testRunner = new DefaultGrailsTestRunner(reportsDir, reportsFormat);
        this.testHelper = new DefaultGrailsTestHelper(settings, classLoader, resourceResolver);
    }


    public String getFramework() {
        return "junit";
    }

    public void createTests(ArrayList<String> testNames, String type) {
        this.testSuite = this.testHelper.createTests(testNames, type);
    }

    public int testCount() {
        return this.testSuite.testCount();
    }

    public int countTestCases() {
        return this.testSuite.countTestCases();
    }

    public void runTests() {
        this.testResult = this.testRunner.runTests(this.testSuite);
    }

    public int failureCount() {
        return this.testResult.failureCount();
    }

    public int errorCount() {
        return this.testResult.errorCount();
    }

    public int runCount() {
        return this.testResult.runCount();
    }

    public void produceReports() {
        /*
        ant.junitreport(todir: "${this.reportsDir}") {
            fileset(dir: this.reportsDir) {
                include(name: "TEST-*.xml")
            }
            report(format: "frames", todir: "${this.reportsDir}/html")
        }
        */
    }

    public ClassLoader getCurrentClassLoader() {
        return this.testHelper.getCurrentClassLoader();
    }
}
