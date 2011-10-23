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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Demonstrates how to manage currently logged in users. In order for the
 * {@link SessionRegistry} to be populated the concurrency-control element
 * should be specified. For example:
 *
 * <pre>
 * &lt;http ...>
 *   &lt;session-management>
 *        &lt;concurrency-control error-if-maximum-exceeded="true" expired-url="/login?expired" max-sessions="1"/>
 *      &lt;/session-management>
 * &lt;/http>
 * </pre>
 * <p>
 * Note that this method is not ideal in a real world application since Spring
 * Security's interface only exposes minimal methods that lack things like
 * paging support. This is so that if these things are not needed, users do not
 * need to implement them in order to get concurrency support. In a real world
 * situation it would be better to:
 * <ul>
 * <li>Write your own interface with the appropriate methods (i.e. with paging
 * logic)</li>
 * <li>Write a class that implements both SessionRegistry and the custom
 * interface</li>
 * <li>Interact with the class using the custom interface in your controller</li>
 * </ul>
 *
 * @author Rob Winch
 */
@Controller
public class UserSessionController {

    private final SessionRegistry sessionRegistry;

    /**
     * Creates a new {@link UserSessionController}
     *
     * @param sessionRegistry the {@link SessionRegistry} to use. Cannot be null.
     * @throws IllegalArgumentException if sessionRegistry is null.
     */
    @Autowired
    public UserSessionController(SessionRegistry sessionRegistry) {
        if (sessionRegistry == null) {
            throw new IllegalArgumentException("sessionRegistry cannot be null");
        }
        this.sessionRegistry = sessionRegistry;
    }

    @RequestMapping("/users/sessions")
    public ModelAndView list() {
        return new ModelAndView("user/session/list", "principals", this.sessionRegistry.getAllPrincipals());
    }

    @RequestMapping("/users/{userId}/sessions")
    public ModelAndView listForUser(@PathVariable int userId) {
        for (Object principal : this.sessionRegistry.getAllPrincipals()) {
            MailUser user = (MailUser) principal;
            if (user.getId().equals(userId)) {
                List<SessionInformation> sessionDetails = this.sessionRegistry.getAllSessions(principal, false);
                return new ModelAndView("user/session/details", "sessionDetails", sessionDetails).addObject(
                        "messageUser", user);
            }
        }
        throw new NotFoundException("Couldn't find sessions for user "+userId);
    }

    @RequestMapping(value = "/sessions/{sessionId}", method = RequestMethod.DELETE)
    public String expireForm(@PathVariable String sessionId) {
        SessionInformation sessionInfo = this.sessionRegistry.getSessionInformation(sessionId);
        sessionInfo.expireNow();
        return "redirect:/users/sessions?expired";
    }
}
