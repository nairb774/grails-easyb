package com.bluetrainsoftware.easyb.grails.test.controllers;

import com.bluetrainsoftware.easyb.grails.test.domain.Author;

public class AuthorController {
	def list = {
			[Author.list()]
	}
}