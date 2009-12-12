target('default': "Show message about test-app command") { 
    depends()

    println "> Grails 1.2 supports all testing frameworks to use the core test infrastructure"
    println "> To run all of your tests use the command \$> grails test-app"
    println "> To run only  easyb tests use the command \$> grails test-app :easyb"
}