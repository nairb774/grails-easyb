package org.codehaus.groovy.grails.test.plugin.easyb

import grails.util.BuildSettings
import org.springframework.core.io.Resource

/**
 * User: gcmadruga
 * Date: May 22, 2009
 * Time: 5:39:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class GrailsEasybTestHelper {
    String[] testSuffixes = ["Story", "Specification"]

    protected final File baseDir
    protected final File testClassesDir
    protected final ClassLoader parentLoader
    protected final Closure resourceResolver

    private ClassLoader currentClassLoader

    GrailsEasybTestHelper(BuildSettings settings, ClassLoader classLoader, Closure resourceResolver) {
        this.baseDir = settings.baseDir
        this.testClassesDir = settings.testClassesDir
        this.parentLoader = classLoader
        this.resourceResolver = resourceResolver

    }

    List<String> createTests(List<String> testNames, String type) {
        def testSrcDir = "${baseDir.absolutePath}/test/$type"
        def testSuite = []

        currentClassLoader = createClassLoader(type)

        def resources = []

        if (testNames.size() == 1 && testNames[0] == "**.*") {
            testNames = ["**"]
        }

        if (testNames) {
            testSuffixes.each {testSuffix ->
                testNames.each {filePattern ->
                    resources += resourceResolver("file:${testSrcDir}/${filePattern}${testSuffix}.groovy") as List
                    resources += resourceResolver("file:${testSrcDir}/${filePattern}.${testSuffix.toLowerCase()}") as List
                }
            }
        }

        resources.findAll { it.exists() }.each {Resource resource ->
            println "adding test ${resource.file.path} for processing"
            testSuite.add(resource.file.path)
        }
/*        def nonMethodTests = potentialTests.findAll { !it.hasMethodName() }

        testSuffixes.each { testSuffix ->
            def nmTestResources = nonMethodTests*.filePattern.inject([]) { resources, String filePattern ->
                resources += resourceResolver("file:${testSrcDir}/${filePattern}${testSuffix}.groovy") as List
                resources += resourceResolver("file:${testSrcDir}/${filePattern}.${testSuffix.toLowerCase()}") as List
            }

            nmTestResources.findAll { it.exists() }.each { Resource resource ->
                testSuite.add(resource.file.path)
            }
        }
*/
        return testSuite as List<String>
    }

    String fileToClassName(File file, String type, String replaceFilename) {
        String classpath = fileToClassName(file, new File(baseDir, "test/${type}"))

        if (Character.isLowerCase(replaceFilename.charAt(0)))
        replaceFilename = replaceFilename.substring(0, 1).toUpperCase() + replaceFilename.substring(1)

        int lastIndexOf = classpath.lastIndexOf('.')

        if (lastIndexOf >= 0) {
            classpath = classpath.substring(0, lastIndexOf) + replaceFilename
        } else {
            classpath = replaceFilename
        }

        return classpath
    }

    String fileToClassName(File file, String type) {
        return fileToClassName(file, new File(baseDir, "test/${type}"))
    }

    /**
     * Given the location of a test file, and the directory that it is
     * relative to, this method returns the fully qualifed class name
     * of that tests. So for example, if you have "test/unit/org/example/MyTest.groovy"
     * with a base directory of "test/unit", the method returns the
     * string "org.example.MyTest".
     */
    String fileToClassName(File file, File baseDir) {
        def filePath = file.canonicalFile.absolutePath
        def basePath = baseDir.canonicalFile.absolutePath
        if (!filePath.startsWith(basePath)) {
            throw new IllegalArgumentException("File path (${filePath}) is not descendent of base path (${basePath}).")
        }

        filePath = filePath.substring(basePath.size() + 1)
        def suffixPos = filePath.lastIndexOf(".")
        return filePath[0..(suffixPos - 1)].replace(File.separatorChar, '.' as char)
    }

    ClassLoader getCurrentClassLoader() {
        return currentClassLoader
    }

    /**
     * Creates a class loader that the tests of the given type can be
     * loaded from.
     */
    protected ClassLoader createClassLoader(String type) {
        return new URLClassLoader([
                new File("test/$type").toURI().toURL(),
                new File(testClassesDir, type).toURI().toURL()
        ] as URL[], parentLoader)
    }

}