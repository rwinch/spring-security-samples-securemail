/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.security.samples.mail.mvc

import geb.spock.GebReportingSpec

import org.springframework.security.samples.mail.mvc.page.*

import spock.lang.Stepwise

@Stepwise
class SignupSpec extends GebReportingSpec {

    def firstName = 'Amanda'
    def lastName = 'Winch'
    def email = 'amanda'+System.currentTimeMillis()+'@example.com'
    def password = 'panda'

    def 'missing fields displays errors'() {
        when:
        to SignupPage
        submit.click(SignupPage)
        then:
        errors.contains('First Name is required.')
        errors.contains('Last Name is required.')
        errors.contains('Email is required.')
    }

    def 'invalid email displays error'() {
        when:
        form.firstName = firstName
        form.lastName = lastName
        form.email = 'invalid'
        form.password = '1234'
        submit.click(SignupPage)
        then:
        errors.contains('Email must be a valid email address.')
        errors.contains('Password must be at least 5 characters.')
    }

    def 'duplicate user displays error'() {
        def existingEmail = 'rob@example.org'
        when:
        form.password = password
        form.email = existingEmail
        submit.click(ComposePage)
        then:
        errors.contains('A user with email '+existingEmail+' already exists.')
    }

    def 'signup'() {
        setup:
        def subject = 'does '+email+' exist?'
        when:
        form.email = email
        form.password = password
        submit.click(InboxPage)
        to ComposePage
        form.toEmail = email
        form.subject = subject
        form.message = 'this is a test this is only a test'
        submit.click(SentPage)
        then:
        messages(0).user == 'Winch, Amanda'
        messages(0).subject == subject
    }

    def 'cannot access others messages'() {
        when:
        to MessagePage, '1'
        then:
        at AccessDeniedPage
    }

    def afterSpec() {
        to LogoutPage
    }
}
