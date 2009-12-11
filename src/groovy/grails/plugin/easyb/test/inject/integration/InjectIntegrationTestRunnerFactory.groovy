package grails.plugin.easyb.test.inject.integration

import grails.plugin.easyb.test.inject.unit.InjectUnitTestRunnerFactory
import org.easyb.domain.Behavior
import grails.plugin.easyb.test.GrailsEasybTestType

/**
 * this class is identical to UnitTestRunner at the moment, 
 */
public class InjectIntegrationTestRunnerFactory extends InjectUnitTestRunnerFactory {
    private static String pathPartMustExist = "test" + File.separator + "integration"

    @Override
    public boolean willRespond(Behavior currentBehaviour, GrailsEasybTestType gett) {
        return gett.testType == "integration" && currentBehaviour.file.absolutePath.indexOf(pathPartMustExist) >= 0
    }
}
