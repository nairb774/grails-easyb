/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

eventAllTestsStart = {
    def easybTestTypeClass = classLoader.loadClass('grails.plugin.easyb.test.GrailsEasybTestType')
    unitTests << easybTestTypeClass.newInstance('easyb', 'unit')
    integrationTests << easybTestTypeClass.newInstance('easyb', 'integration')
    functionalTests << easybTestTypeClass.newInstance('easyb', 'functional')
}

// I believe that we do not need this
/*
eventTestPhaseStart = { phaseName ->
    if (phaseName == 'functional') {
        def functionalSpecificationClass = classLoader.loadClass("grails.plugin.easyb.EasybFunctionalSpecification")
        functionalSpecificationClass.baseUrl = argsMap["baseUrl"] ?: "http://localhost:$serverPort$serverContextPath"
    }
}*/