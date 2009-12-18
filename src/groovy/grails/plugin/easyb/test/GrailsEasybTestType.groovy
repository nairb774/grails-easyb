package grails.plugin.easyb.test

import org.codehaus.groovy.grails.test.support.GrailsTestTypeSupport
import org.codehaus.groovy.grails.test.GrailsTestTypeResult
import org.codehaus.groovy.grails.test.event.GrailsTestEventPublisher
import org.easyb.BehaviorRunner
import grails.plugin.easyb.test.listener.GrailsEasybListener
import grails.plugin.easyb.test.report.EasybReportsFactory
import org.easyb.Configuration
import org.easyb.domain.BehaviorFactory

public class GrailsEasybTestType extends GrailsTestTypeSupport {

    protected final List<Class> easybFiles = []
    public final String testType

    GrailsEasybTestType(String name, String relativeSourcePath) {
        super(name, relativeSourcePath)
        testType = relativeSourcePath
    }

    public String sourceFileToClassName(File source) {
        return super.sourceFileToClassName(source)
    }

    /**
     * Gather all the tests that easyb should run. *.Story and *.Specification
     * @return the amount of stories + specifications that will be executed.
     */
    protected int doPrepare() {
        testTargetPatterns.each { testTargetPattern ->
            findSourceFiles(testTargetPattern).each { file ->
                if (isEasybSourceFile(file)) {
                    easybFiles << file
                }
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
        def easybListener = new GrailsEasybListener(grailsTestEventPublisher, createEasybReportsFactory(), createSystemOutAndErrSwapper(), this)
        def easybRunner = new BehaviorRunner(new Configuration(), easybListener)
        easybRunner.runBehaviors(BehaviorRunner.getBehaviors(easybFiles as String[]))
        return new GrailsEasybTestTypeResult(easybListener)
    }

    public List<String> getTestExtensions() {
        return ["groovy", "story", "specification"]
    }

    public List<String> getTestSuffixes() {
        return ["*"]
    }

    EasybReportsFactory createEasybReportsFactory() {
        EasybReportsFactory.createFromBuildBinding(buildBinding)
    }

    /**
     * Verifies if the given file is an easyb test file.
     * @param file the file to test if is an easyb source file
     * @return true if is easyb source file, false otherwise
     */
    private boolean isEasybSourceFile(File file) {
        def boolean isValidExtension = file.absolutePath.endsWith(".specification") ||
                file.absolutePath.endsWith(".story") ||
                file.absolutePath.endsWith("Story.groovy") ||
                file.absolutePath.endsWith("Specification.groovy")

        if(!isValidExtension) {
            return false
        }

        try {
            BehaviorFactory.createBehavior(file)
            return true
        } catch (e) {
            return false
        }
    }
}