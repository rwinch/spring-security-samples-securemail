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
 * Used for managing users for the secure mail aplication.
 * @author rw012795
 *
 */
public interface MailUserService {

    /**
     * Gets a {@link MailUser} for a specific {@link MailUser#getId()}.
     *
     * @param id the {@link MailUser#getId()} of the {@link MailUser} to find.
     * @return a {@link MailUser} for the given id. Cannot be null.
     * @throws NotFoundException
     *             if the {@link MailUser} cannot be found
     */
    MailUser getUser(int id);

    /**
     * Finds a given {@link MailUser} by email address.
     * @param email the email address to use to find a {@link MailUser}. Cannot be null.
     * @return a {@link MailUser} for the given email or null if one could not be found.
     * @throws IllegalArgumentException if email is null.
     */
    MailUser findUserByEmail(String email);

    /**
     * Creates a new {@link MailUser}.
     * @param mailUser the new {@link MailUser} to create. The {@link MailUser#getId()} must be null.
     * @return the new {@link MailUser#getId()}.
     * @throws IllegalArgumentException if {@link MailUser#getId()} is non-null.
     */
    int createUser(MailUser mailUser);
}
