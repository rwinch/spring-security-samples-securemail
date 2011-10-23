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
package org.springframework.security.samples.mail.mvc;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.samples.mail.Message;

/**
 * A form object for submitting a {@link Message}.
 *
 * @author Rob Winch
 */
public class MessageForm {
    @NotBlank(message = "Subject is required.")
    private String subject;
    @NotBlank(message = "Message is required.")
    private String message;
    @Email(message = "To must be a valid email address.")
    @NotBlank(message = "To is required.")
    private String toEmail;

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToEmail() {
        return this.toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}