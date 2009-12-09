package com.bluetrainsoftware.easyb.grails.test.domain;

public class Author {
	String name

	static constraints = {
		name(nullable:false)
	}

	def fakeLogging() {
		log.debug( "some logging" )
	}
}