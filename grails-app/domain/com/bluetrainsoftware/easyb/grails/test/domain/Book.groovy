package com.bluetrainsoftware.easyb.grails.test.domain;

public class Book {
	String title
	static hasMany = [authors:Author]

    static constraints = {
		title(blank:false, size:5..30)
	}
}
