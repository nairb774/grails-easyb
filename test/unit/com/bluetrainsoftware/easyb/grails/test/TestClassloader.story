package com.bluetrainsoftware.easyb.grails.test

scenario "A story makes use of a test class", {
    /*
     * Now this is not a perfect test, but we assume that if we can load the class dynamically at runtime, we would be
     * able to import the class at compile time. The other option is to write a test that would fail to compile,
     * resulting in a halted build and a bad exception.
     */
    when "a class in the test classpath that is loaded, it loads successfully", {
        def classloader = getClass().classLoader
        clazz = Class.forName("com.bluetrainsoftware.easyb.grails.test.TestClassloaderTarget", true, classloader)
    }
    then "the loaded without exception and is not null", {
        clazz.shouldNotBe null
    }
}
