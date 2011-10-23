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
class MessagesSpec extends GebReportingSpec {

    def subjectToSend = 'testing 1, 2, 3'
    def toEmail = 'luke@example.com'
    def messageToSend = 'this is a test...this is only a test'

    def inboxSubject = 'Vulnerabilities Found?'
    def inboxFrom = 'Taylor, Luke'

    def 'missing fields displays errors'() {
        setup:
        to ComposePage
        at LoginPage
        login()
        when:
        submit.click(ComposePage)
        then:
        errors.contains('Subject is required.')
        errors.contains('Message is required.')
        errors.contains('To is required.')
    }

    def 'invalid email displays error'() {
        when:
        form.toEmail = 'notvalid'
        form.subject = subjectToSend
        form.message = messageToSend
        submit.click(ComposePage)
        then:
        errors.contains('To must be a valid email address.')
    }

    def 'missing email displays error'() {
        when:
        form.toEmail = 'nobody@example.org'
        submit.click(ComposePage)
        then:
        errors.contains('A user with email nobody@example.org was not found.')
    }

    def 'send message'() {
        when:
        to ComposePage
        form.toEmail = toEmail
        form.subject = subjectToSend
        form.message = messageToSend
        submit.click(SentPage)
        then:
        messages(0).subject == subjectToSend
        messages(0).user == 'Taylor, Luke'
    }

    def 'view sent'() {
        when:
        messages(0).link.click(MessagePage)
        then:
        at MessagePage
        toUser == toEmail
        fromUser == 'rob@example.org'
        messageToSend == this.messageToSend
    }

    def 'view inbox'() {
        when:
        to InboxPage
        then:
        messages(0).subject == inboxSubject
        messages(0).user == inboxFrom
    }

    def 'view message'() {
        when:
        messages(0).link.click(MessagePage)
        then:
        at MessagePage
        toUser == 'rob@example.org'
        fromUser == 'luke@example.com'
        message == 'I believe I found some vulnerabilities in the message application. It may be good to ensure that you secure the application.'
    }

    def cleanupSpec() {
        to LogoutPage
    }
}
