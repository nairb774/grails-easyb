package org.codehaus.groovy.grails.test.plugin.easyb.integration

import org.codehaus.groovy.grails.test.plugin.easyb.unit.InjectUnitTestRunnerFactory
import org.easyb.domain.Behavior

/**
 * this class is identical to UnitTestRunner at the moment, 
 */
public class InjectIntegrationTestRunnerFactory extends InjectUnitTestRunnerFactory {
    private static String pathPartMustExist = "test" + File.separator + "integration"

    public boolean willRespond(Behavior currentBehaviour, String type) {
        return type == "integration" && currentBehaviour.file.absolutePath.indexOf(pathPartMustExist) >= 0
    }
}
