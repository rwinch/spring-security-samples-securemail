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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.samples.mail.security.MailUserAuthenticationProvider;
import org.springframework.security.samples.mail.security.MailUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Obtains the currently logged in {@link MailUser} by extracting the principal
 * from the Spring Security {@link Authentication}. This demonstrates how to
 * obtain the principal without being tightly coupled to Spring Security.
 * </p>
 * <p>
 * The {@link SecurityContextHolder} can be used to obtain the Spring Security
 * {@link Authentication} which contains a principal of {@link MailUser}. The
 * principal is {@link MailUser} because that is what populated as the principal
 * by the {@link MailUserDetailsService} and
 * {@link MailUserAuthenticationProvider}.
 * </p>
 *
 * @author Rob Winch
 *
 */
@Component
public class SpringSecurityUserContext implements MailUserContext {

    public MailUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : (MailUser) authentication.getPrincipal();
    }
}