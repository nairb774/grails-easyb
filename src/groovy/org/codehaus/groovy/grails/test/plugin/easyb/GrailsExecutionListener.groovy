/*
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Jun 8, 2009
 * Time: 7:13:28 PM
 */
package org.codehaus.groovy.grails.test.plugin.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.domain.Behavior
import org.easyb.BehaviorStep
import org.slf4j.LoggerFactory;
import org.easyb.result.Result
import org.easyb.util.BehaviorStepType
import grails.test.GrailsUnitTestCase

import org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectGrailsTestRunner

/**
*8 The GrailsExecutionListener is intended to listen to easyb for events about what is going on in the execution space. As scenarios
* start and stop, it creates and destroys the appropriate test cases. It also adds the grailsTest method to allow you to override the test
* case that is being used.
*/

public class GrailsExecutionListener implements ExecutionListener {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(GrailsExecutionListener)
  Behavior currentBehaviour
  Stack<BehaviorStep> steps = new Stack<BehaviorStep>();
  BehaviorStep currentStep
  InjectTestRunner testRunner
  GrailsEasybTestHelper testHelper
  String type

  public GrailsExecutionListener(GrailsEasybTestHelper testHelper, String type) {
    this.testHelper = testHelper
    this.type = type
  }

  public void startBehavior(Behavior behavior) {
    this.currentBehaviour = behavior
    // determine what type of test case is appropriate and initialize it

    testRunner = InjectTestRunnerFactory.findMatchingRunner( currentBehaviour, testHelper, type )

    if ( !testRunner )
    	testRunner = InjectTestRunnerFactory.getDefault( currentBehaviour, type )
    
    if ( testRunner ) {
      testRunner.initialize()

	    if ( testRunner.testCase == null ) {
	    	log.warn "Unable to create expected test runner ${testRunner.runnerType}, using default instead"
	    	testRunner = InjectTestRunnerFactory.getDefault( currentBehaviour, type )
	    	if ( testRunner ) testRunner.initialize()
	    }
    }

  }


  public void stopBehavior(BehaviorStep step, Behavior behavior) {
    // shut down the test case completely
    
    this.currentBehaviour = null
    this.testRunner = null
  }

  /**
   * we can have situations where we want to replace the test case attached. This happens in the middle of a scenario or specification
   * so we need to make sure we tell the new test runner about the binding
   */
  private void dynamicallyInjectGrailsTest( String name, String style ) {
    testRunner = InjectTestRunnerFactory.findDynamicRunner( style, name, currentBehaviour, type, testHelper )
    
    if ( testRunner ) {
      testRunner.initialize()
      testRunner.injectMethods( currentBehaviour.binding )
      
      // we have missed the start of the scenario or specification as well, so we need to inject the setup
      if ( currentStep.stepType == BehaviorStepType.IT || BehaviorStepType.SCENARIO )
        testRunner.setUp()
    }
  }

  public void startStep(BehaviorStep step) {
    steps.push( step )

    currentStep = step

    switch ( step.getStepType() ) {
      case BehaviorStepType.EXECUTE:
        currentBehaviour.binding.grailsTest = { String name, style = null ->
           dynamicallyInjectGrailsTest( style, name );
        }
        
        if ( testRunner ) {
          testRunner.injectMethods( currentBehaviour.binding )
        }
        break
      case BehaviorStepType.IT:
      case BehaviorStepType.SCENARIO:
        if ( testRunner )
          testRunner.setUp()
        break
    }
  }

  public void describeStep(String description) {
  }


  public void completeTesting() {
  }

  public void stopStep() {
    BehaviorStep step = steps.pop()
    
    switch ( step.getStepType() ) {
      case BehaviorStepType.EXECUTE:
        break
      case BehaviorStepType.IT:
      case BehaviorStepType.SCENARIO:
        if ( testRunner != null )
          testRunner.tearDown()
        break
    }
  }

  public void gotResult(Result result) {
  }

}