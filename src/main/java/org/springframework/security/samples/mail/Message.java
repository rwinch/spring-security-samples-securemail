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

import java.io.Serializable;


/**
 * <p>
 * Represents a message sent from a {@link MailUser} to another {@link MailUser}.
 * </p>
 * <p>
 * In order to keep the example simple only a single {@link MailUser} can receive a {@link Message}.
 * </p>
 * @author rw012795
 *
 */
public class Message implements Serializable {
    private String message;
    private String subject;
    private MailUser toUser;
    private MailUser fromUser;
    private Integer id;

    /**
     * Gets the id. This may be null in the event that
     * the {@link Message} has not been persisted yet.
     *
     * @return
     */
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the message.
     * @return
     */
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the message subject line or a short summary about the message.
     * @return
     */
    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the {@link MailUser} that this {@link Message} is sent to. The {@link Message} will appear in this {@link MailUser}'s inbox.
     * @return
     */
    public MailUser getToUser() {
        return this.toUser;
    }

    public void setToUser(MailUser toUser) {
        this.toUser = toUser;
    }

    /**
     * Gets the {@link MailUser} that this {@link Message} is from. The {@link Message} will appear in this {@link MailUser}'s sent mail.
     * @return the fromUser
     */
    public MailUser getFromUser() {
        return this.fromUser;
    }

    public void setFromUser(MailUser fromUser) {
        this.fromUser = fromUser;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Message other = (Message) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName()+" [id=" + this.id + "]";
    }

    private static final long serialVersionUID = 6618767246921777380L;
}
