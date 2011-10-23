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

import java.util.List;


/**
 * <p>
 * Used to manage {@link Message} objects.
 * </p>
 *
 * @author Rob Winch
 */
public interface MessageService {

    /**
     * Given an id gets a {@link Message}.
     * @param messageId the {@link Message#getId()}
     * @return the {@link Message}. Cannot be null.
     * @throws RuntimeException if the {@link Message} cannot be found.
     */
    Message getMessage(int messageId);

    /**
     * Creates a {@link Message} and returns the new id for that {@link Message}.
     * @param message the {@link Message} to create. Note that the {@link Message#getId()} should be null.
     * @return the new id for the {@link Message}
     * @throws RuntimeException if {@link Message#getId()} is non-null.
     */
    int createMessage(Message message);

    /**
     * Finds the {@link Message}'s that were sent by a specific {@link MailUser}.
     * @param userId the {@link MailUser#getId()} to search for sent {@link Message}'s for.
     * @return a non-null {@link List} of {@link Message}'s sent by the specified {@link MailUser}
     */
    List<Message> findSentForUser(int userId);

    /**
     * Finds the {@link Message}'s that were received by a specific {@link MailUser}.
     * @param userId the {@link MailUser#getId()} to search for {@link Message}'s received by.
     * @return a non-null {@link List} of {@link Message}'s received by the specified {@link MailUser}
     */
    List<Message> findInboxMessageForUser(int userId);
}
