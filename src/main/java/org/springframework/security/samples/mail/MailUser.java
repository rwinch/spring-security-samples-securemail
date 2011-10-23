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

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * A {@link MailUser} represents a user in the secure mail application.
 * @author Rob Winch
 *
 */
public class MailUser implements Serializable {
    @Email(message = "Email must be a valid email address.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "First Name is required.")
    private String firstName;

    @NotBlank(message = "Last Name is required.")
    private String lastName;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, message = "Password must be at least 5 characters.")
    private String password;
    private Integer id;

    /**
     * Gets the id. This may be null in the event that
     * the {@link MailUser} has not been persisted yet.
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
     * Gets the username for the {@link MailUser}.
     * <p>
     * This is an alias for {@link #getEmail()}.
     * </p>
     *
     * @return
     */
    public String getUsername() {
        return getEmail();
    }

    /**
     * Gets the email address for a {@link MailUser}. This field is unique across all {@link MailUser}'s.
     * @return
     */
    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    /**
     * Gets the password for this {@link MailUser}. Note that the password is
     * not hashed, but in a real application should be.
     *
     * @return
     */
    public String getPassword() {
        return this.password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
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
        if (!(obj instanceof MailUser)) {
            return false;
        }
        MailUser other = (MailUser) obj;
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

    private static final long serialVersionUID = -8114411764534562374L;
}
