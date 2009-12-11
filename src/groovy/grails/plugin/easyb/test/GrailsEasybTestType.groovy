package grails.plugin.easyb.test

import org.codehaus.groovy.grails.test.support.GrailsTestTypeSupport
import org.codehaus.groovy.grails.test.GrailsTestTypeResult
import org.codehaus.groovy.grails.test.event.GrailsTestEventPublisher
import org.easyb.BehaviorRunner
import grails.plugin.easyb.test.listener.GrailsEasybListener
import grails.plugin.easyb.test.report.EasybReportsFactory
import org.easyb.Configuration

public class GrailsEasybTestType extends GrailsTestTypeSupport {

    protected final List<Class> easybFiles = []

    GrailsEasybTestType(String name, String relativeSourcePath) {
        super(name, relativeSourcePath)
    }

    /**
     * Gather all the tests that easyb should run. *.Story and *.Specification
     * @return the amount of stories + specifications that will be executed.
     */
    protected int doPrepare() {
        testTargetPatterns.each { testTargetPattern ->
            findSource(testTargetPattern).each { sourceResource ->
                def easybSourceFile = sourceResource.file

                //TODO implement a isEasyb() something to avoid conflict with grails-spock *.Specification
                /*if (specFinder.isSpec(easybSourceFile))*/ easybFiles << easybSourceFile
            }
        }

        //TODO should count stories and specifications instead of easybFiles.size()
        return easybFiles.size()
    }

    /**
     * Run the tests through BehaviourRunner from easyb.
     * @return the test results encapsulated by GrailsTestTypeResult
     */
    protected GrailsTestTypeResult doRun(GrailsTestEventPublisher grailsTestEventPublisher) {
        def easybListener = new GrailsEasybListener(grailsTestEventPublisher, createEasybReportsFactory(), createSystemOutAndErrSwapper())
        def easybRunner = new BehaviorRunner(new Configuration(), easybListener)
        easybRunner.runBehaviors(BehaviorRunner.getBehaviors(easybFiles as String[]))
        return new GrailsEasybTestTypeResult(easybListener)
    }

    protected List<String> getTestExtensions() {
        return ["groovy"]
    }

    protected List<String> getTestSuffixes() {
        return ["Story", "Specification"]
    }

    EasybReportsFactory createEasybReportsFactory() {
        EasybReportsFactory.createFromBuildBinding(buildBinding)
    }
}