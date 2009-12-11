package grails.plugin.easyb.test.inject.unit
/*
 * User: richard
 * Date: Jun 7, 2009
 * Time: 9:51:13 PM
 */

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.plugin.easyb.test.inject.InjectTestRunner

public class InjectGrailsTestRunner extends InjectTestRunner {

    protected void initialize() {
        runnerType = "Grails Unit Test"
        this.testCase = new GrailsUnitTestCase()
    }

    public void injectMethods(Binding binding) {
        super.injectMethods(binding)
        //println "second level inject"

        if (ApplicationHolder.application) {
            binding.inject = {beanName ->
                // time to get *really* meta
                binding."${beanName}" = ApplicationHolder.application.mainContext.getBean(beanName)
            }
        }

        binding.registerMetaClass = {Class clazz ->
            if (testCase) {
                testCase.registerMetaClass clazz
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.mockFor = {Class clazz, boolean loose = false ->
            if (testCase) {
                return testCase.mockFor(clazz, loose)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.mockForConstraintsTests = {Class clazz, instance = [] ->
            if (testCase) {
                if(instance instanceof List) {
                    testCase.mockForConstraintsTests(clazz, [instance])
                } else {
                    testCase.mockForConstraintsTests(clazz, instance)
                }
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.mockDomain = {Class domainClass, List instances = [] ->
            if (testCase) {
                testCase.mockDomain(domainClass, instances)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.enableCascadingValidation = {->
            if (testCase) {
                testCase.enableCascadingValidation()
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.mockController = {Class controllerClass ->
            if (testCase) {
                testCase.mockController(controllerClass)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.mockTagLib = {Class tagLibClass ->
            if (testCase) {
                testCase.mockTagLib(tagLibClass)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.mockLogging = {Class clazz, boolean enableDebug = false ->
            if (testCase) {
                testCase.mockLogging(clazz, enableDebug)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.mockConfig = {String config ->
            if (testCase) {
                testCase.mockConfig(config)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.loadCodec = {Class codecClass ->
            if (testCase) {
                testCase.loadCodec(codecClass)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }

        binding.addConverters = {Class clazz ->
            if (testCase) {
                testCase.addConverters(clazz)
            } else {
                throw new RuntimeException("no test case associated with story/scenario")
            }
        }
    }
}