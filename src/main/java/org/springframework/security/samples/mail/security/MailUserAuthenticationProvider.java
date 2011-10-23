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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.MailUserService;
import org.springframework.stereotype.Component;

/**
 * Demonstrates how to provide custom authentication with Spring Security. An
 * advantage to using an {@link AuthenticationProvider} rather than a
 * {@link UserDetailsService} it can authenticate more than just a
 * {@link UsernamePasswordAuthenticationToken} and even more than just a
 * username / password (i.e. it could be a X509 certificate).
 *
 * @author Rob Winch
 */
@Component
public class MailUserAuthenticationProvider implements AuthenticationProvider {
    private final MailUserService userRepository;

    @Autowired
    public MailUserAuthenticationProvider(MailUserService userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("userRepository cannot be null");
        }
        this.userRepository = userRepository;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        MailUser user = this.userRepository.findUserByEmail(token.getName());
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(token.getCredentials())) {
            throw new BadCredentialsException("Username / Password was not found");
        }
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
