package com.bluetrainsoftware.easyb.grails.test.controllers;

import com.bluetrainsoftware.easyb.grails.test.domain.*;

// this should pick up the controller automatically as it is named the same and in the same package

scenario "We should be able to mock the controller", {
    given "a mocked domain of authors", {
        mockDomain(Author, [new Author(name: 'Terry'), new Author(name: 'Ernest')])
    }
    when "when we make a request of the controller", {
        controller = new AuthorController()
        authors = controller.list()
    }
    then "the controller should return a list of authors", {
        authors.shouldNotBe null
        authors.size().shouldBe 1
        authors[0].size().shouldBe 2
        def titles = authors[0].collect {author -> author.name }
        ensure(titles) {
            contains("Terry")
            and
            contains("Ernest")
        }
    }
}