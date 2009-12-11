package grails.plugin.easyb.test

import org.codehaus.groovy.grails.test.GrailsTestTypeResult
import org.easyb.listener.ResultsCollector

public class GrailsEasybTestTypeResult implements GrailsTestTypeResult {
    final ResultsCollector result

    GrailsEasybTestTypeResult(ResultsCollector result) {
        this.result = result
    }

    public int getPassCount() {
        return (result.behaviorCount - result.failedBehaviorCount - result.pendingBehaviorCount)
    }

    public int getFailCount() {
        return result.failedBehaviorCount
    }
}