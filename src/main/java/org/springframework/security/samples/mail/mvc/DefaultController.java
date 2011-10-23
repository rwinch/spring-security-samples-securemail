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

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * A controller used to demonstrate how to display a different page depending on
 * the user's role after login. The idea is to tell Spring Security to use
 * /default as the default-target-url and then programmatically determine where
 * to send the user afterwards.
 * </p>
 * <p>
 * This has advantages over other approaches in that it is in no way tied to
 * Spring Security. Despite using Spring MVC for this, we could just have easily
 * processed the url /default with another type of controller like a Servlet or
 * Struts Action so it is not required to use Spring MVC to use this approach.
 * </p>
 * <p>
 * Below is a snippet showing how to utilize this if the user went directly to
 * the login page. If the user went first to a secured page, then Spring
 * Security will send the user to the secured page after login instead of
 * /default.
 * </p>
 *
 * <pre>
 * &lt;http use-expressions="true" auto-config="true">
 *     &lt;intercept-url pattern="/login*" access="permitAll"/>
 *     &lt;form-login login-page="/login" authentication-failure-url="/login?error" default-target-url="/default"/>
 * &lt;/http>
 * </pre>
 * <p>
 * Using the always-use-default-target=true, Spring Security will always send
 * the user to this page and thus the first page after login will always differ
 * by role.
 * </p>
 *
 * <pre>
 * &lt;http use-expressions="true" auto-config="true">
 *     &lt;intercept-url pattern="/login*" access="permitAll"/>
 *     &lt;form-login login-page="/login" authentication-failure-url="/login?error" default-target-url="/default" always-use-default-target="true"/>
 * &lt;/http>
 * </pre>
 *
 * @author Rob Winch
 *
 */
@Controller
public class DefaultController {

    @RequestMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/users/sessions";
        }
        return "redirect:/messages/inbox";
    }
}
