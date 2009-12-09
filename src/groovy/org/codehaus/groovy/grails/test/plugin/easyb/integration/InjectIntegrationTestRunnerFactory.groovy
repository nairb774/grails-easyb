package org.codehaus.groovy.grails.test.plugin.easyb.integration

import org.codehaus.groovy.grails.test.plugin.easyb.GrailsEasybTestHelper
import org.codehaus.groovy.grails.test.plugin.easyb.InjectTestRunner
import org.codehaus.groovy.grails.test.plugin.easyb.TestRunnerFactory
import org.codehaus.groovy.grails.test.plugin.easyb.unit.*
import org.easyb.domain.Behavior

/**
 * this class is identical to UnitTestRunner at the moment, 
 */
public class InjectIntegrationTestRunnerFactory extends InjectUnitTestRunnerFactory {
	private static String pathPartMustExist = "test" + File.separator + "integration"
	
	public boolean willRespond( Behavior currentBehaviour, String type ) {
		return type == "integration" && currentBehaviour.file.absolutePath.indexOf( pathPartMustExist ) >= 0
	}
}
