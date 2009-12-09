package org.codehaus.groovy.grails.test.plugin.easyb

import org.codehaus.groovy.grails.test.plugin.easyb.GrailsTestExecutioner
import org.easyb.listener.ResultsCollector
import org.easyb.BehaviorRunner
import grails.util.BuildSettings
import org.easyb.listener.ExecutionListener
import org.easyb.report.TxtSpecificationReportWriter
import org.easyb.report.TxtStoryReportWriter
import org.easyb.report.HtmlReportWriter
import org.easyb.report.XmlReportWriter
import org.easyb.report.ReportWriter
import java.io.File
import java.util.ArrayList
import java.util.List
import org.codehaus.groovy.grails.test.plugin.easyb.GrailsTestExecutioner
import org.easyb.Configuration
import org.easyb.domain.Behavior

/**
 * User: gcmadruga
 * Date: May 22, 2009
 * Time: 5:04:06 PM
 * To change this template use File | Settings | File Templates.
 */

public class GrailsEasybUnitTestExecutioner implements GrailsTestExecutioner {
    ResultsCollector testResult
    List<String> testSuite
    BehaviorRunner testRunner
    GrailsEasybTestHelper testHelper

    private File reportsDir
    private List<String> reportsFormat 
    private reports = []

    public GrailsEasybUnitTestExecutioner(File reportsDir, List<String> reportsFormat,
                                     BuildSettings settings, ClassLoader classLoader, Closure resourceResolver) {
        this.testHelper = new GrailsEasybTestHelper(settings, classLoader, resourceResolver)
        this.testResult = new ResultsCollector()

        this.reportsFormat = reportsFormat
        this.reportsDir = reportsDir
    }

    public String getFramework() {
        return "easyb"
    }

    // testName - a list of name pattern or failed test
    // type - the phase (unit, integration, etc)
    public void createTests(ArrayList<String> testNames, String type) {
        testSuite = testHelper.createTests(testNames, type)

        def reportsDirAbsolute = reportsDir.absolutePath

        if(reportsFormat.contains('xml')) {
            this.reports.add(new XmlReportWriter(location: "${reportsDirAbsolute}/xml/easyb-${type}.xml"))
        }
        if(reportsFormat.contains('html')) {
            this.reports.add(new HtmlReportWriter(location: "${reportsDirAbsolute}/html/easyb-${type}.html"))
        }
        if(reportsFormat.contains('plain')) {
            this.reports.add(new TxtStoryReportWriter(location: "${reportsDirAbsolute}/plain/stories-${type}.txt"))
            this.reports.add(new TxtSpecificationReportWriter(location: "${reportsDirAbsolute}/plain/specifications-${type}.txt"))
        }

      Configuration cfg = new Configuration([] as String[], reports, true, false )

//      println "testhelper1 ${testHelper}"
//        this.testRunner = new BehaviorRunner(cfg, [this.testResult] as ExecutionListener[])
        this.testRunner = new BehaviorRunner(cfg, [this.testResult, new GrailsExecutionListener(testHelper, type)] as ExecutionListener[])
//        this.testRunner = new BehaviorRunner(cfg, [new GrailsExecutionListener()] as ExecutionListener[])
//        this.testRunner = new BehaviorRunner(this.reports, [this.testResult, new GrailsExecutionListener()].toArray(new ExecutionListener[0]))
    }

    public int testCount() {
        return testSuite.size()
    }

    public int countTestCases() {
        return testSuite.size()
    }

    public void runTests() {
        def behaviors = BehaviorRunner.getBehaviors(this.testSuite as String[])
        this.testRunner.runBehaviors(behaviors)
    }

    public int failureCount() {
      def failedCount = this.testResult.failedBehaviorCount
      return failedCount
    }

    public int errorCount() {
        return 0 // included in failed count
    }

    public int runCount() {
        return this.testResult.behaviorCount
    }

    public void produceReports() {
        this.reports.each { ReportWriter report ->
            report.writeReport(this.testResult)
        }
    }

    public ClassLoader getCurrentClassLoader() {
        return this.testHelper.currentClassLoader
    }
}