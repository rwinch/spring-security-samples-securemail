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
package org.springframework.security.samples.mail;


/**
 * A stub that always returns the same {@link MailUser}. This is used for
 * testing before we have a valid user logged in.
 *
 * @author Rob Winch
 */
public class MailUserContextStub implements MailUserContext {

    public MailUser getCurrentUser() {
        MailUser current = new MailUser();
        current.setId(1);
        current.setFirstName("Rob");
        current.setLastName("Winch");
        current.setEmail("rob@example.org");
        current.setPassword("penguin");
        return current;
    }
}
