package grails.plugin.easyb.test.listener

import org.easyb.listener.ResultsCollector
import org.codehaus.groovy.grails.test.event.GrailsTestEventPublisher
import org.codehaus.groovy.grails.test.io.SystemOutAndErrSwapper
import org.easyb.result.Result
import org.easyb.domain.Behavior
import grails.plugin.easyb.test.report.EasybReportsFactory
import org.easyb.BehaviorStep

public class GrailsEasybListener extends ResultsCollector {
    final protected GrailsTestEventPublisher eventPublisher
    final protected EasybReportsFactory reportsFactory
    final protected SystemOutAndErrSwapper outAndErrSwapper

    protected currentStep

    GrailsEasybListener(GrailsTestEventPublisher eventPublisher, EasybReportsFactory reportsFactory, SystemOutAndErrSwapper outAndErrSwapper) {
        super()
        this.eventPublisher = eventPublisher
        this.reportsFactory = reportsFactory
        this.outAndErrSwapper = outAndErrSwapper
    }

    /**
     * Called when a Story or Specification file before being executed (comparable as a TestCase).
     * Publishes the event testCaseStart.
     */
    public void startBehavior(Behavior behavior) {
        super.startBehavior(behavior)
        eventPublisher.testCaseStart(trucateEventName(behavior.phrase))
    }

    /**
     * Called when a Story or Specification file after is executed.
     * Publishes the event testCaseEnd.
     */
    public void stopBehavior(BehaviorStep behaviorStep, Behavior behavior) {
        super.stopBehavior(behaviorStep, behavior)
        eventPublisher.testCaseEnd(trucateEventName(behavior.phrase))
    }

    /**
     * Called when a Step is about to be executed. A Step is a closure in a easyb file.
     * Publishes the event testStart.
     */
    public synchronized void startStep(BehaviorStep behaviorStep) {
        super.startStep(behaviorStep)
        currentStep = behaviorStep
        eventPublisher.testStart(trucateEventName(behaviorStep.name))
    }

    /**
     * Called when a Step has been executed and has a result.
     * Publishes the event testEnd, and also the event testFailure if the result is marked as failed.
     */
    public void gotResult(Result result) {
        super.gotResult(result)

        if(result.failed()) {
            if(result.cause) {
                eventPublisher.testFailure(trucateEventName(currentStep.name), result.cause, true)
            } else {
                eventPublisher.testFailure(trucateEventName(currentStep.name), (String)null, true)
            }
        }

        eventPublisher.testEnd(trucateEventName(currentStep.name))
    }

    /**
     * Called when all easyb files have been executed.
     * Generates the easyb reports.
     */
    void completeTesting() {
        super.completeTesting()
        reportsFactory.produceReports(this)
    }

    /*
     * Truncate the event name to a maximum, because sometimes easyb story descriptions are too long, and make the
     * command line very difficult to read.
     * @param name the name o the event
     * @return a truncated event name if the event lenght is longer than the max allowed (70 chars default)
     */
    private String trucateEventName(String name) {
        int delimitator = 70
        if(name.length() <= delimitator) {
            return name
        } else {
            return name.substring(0, delimitator)
        }
    }
}