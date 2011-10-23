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
package org.springframework.security.samples.mail.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.MailUserService;
import org.springframework.stereotype.Component;

/**
 * This demonstrates how provide a custom authentication mechanism.
 * UserDetailsService plugs into a {@link DaoAuthenticationProvider} which plugs
 * into the {@link ProviderManager}. An few advantages to using a custom
 * {@link UserDetailsService} instead of writing a custom
 * {@link AuthenticationProvider} are
 * <ul>
 * <li>For username/password based authentication a lot of logic is already
 * provided in {@link DaoAuthenticationProvider}. Examples include comparing the
 * username/password, salting the password, etc</li>
 * <li>Since other portions of Spring Security (like the provided
 * RememberMeServices) utilize UserDetailsService this will also allow for using
 * them</li>
 * </ul>
 *
 * @author Rob Winch
 * @see MailUser
 */
@Component
public class MailUserDetailsService implements UserDetailsService {
    private final MailUserService userRepository;

    @Autowired
    public MailUserDetailsService(MailUserService userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("userRepository cannot be null");
        }
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MailUser user = this.userRepository.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with username '" + username + "'");
        }
        return new MessageUserDetails(user);
    }

    /**
     * {@link MessageUserDetails} implements both {@link UserDetails} (so that
     * Spring Security can treat it as a {@link UserDetails}) and
     * {@link MailUser} (so that the {@link Authentication}'s principal can be
     * cast to a {@link MailUser} which provides the application access to
     * additional custom attributes and decouples it from Spring Security).
     *
     * <p>
     * The implementation always returns true for the boolean methods on
     * {@link UserDetails} and always returns ROLE_ADMIN and ROLE_USER for users
     * with username of "luke@example.com" else returns ROLE_USER.
     * </p>
     *
     * @author Rob Winch
     *
     */
    private static class MessageUserDetails extends MailUser implements UserDetails {
        private final List<GrantedAuthority> USER_ROLES = Collections.unmodifiableList(AuthorityUtils
                .createAuthorityList("ROLE_USER"));
        private final List<GrantedAuthority> ADMIN_ROLES = Collections.unmodifiableList(AuthorityUtils
                .createAuthorityList("ROLE_USER", "ROLE_ADMIN"));

        /**
         * Creates a new {@link MessageUserDetails}
         *
         * @param user
         *            the {@link MailUser} to use. Cannot be null.
         * @throws IllegalArgumentException
         *             if user is null.
         */
        public MessageUserDetails(MailUser user) {
            if (user == null) {
                throw new IllegalArgumentException("user cannot be null");
            }
            setId(user.getId());
            setEmail(user.getEmail());
            setPassword(user.getPassword());
            setFirstName(user.getFirstName());
            setLastName(user.getLastName());
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return "luke@example.com".equals(getUsername()) ? this.ADMIN_ROLES : this.USER_ROLES;
        }

        public boolean isAccountNonExpired() {
            return true;
        }

        public boolean isAccountNonLocked() {
            return true;
        }

        public boolean isCredentialsNonExpired() {
            return true;
        }

        public boolean isEnabled() {
            return true;
        }

        private static final long serialVersionUID = 6657212226407069272L;
    }
}
