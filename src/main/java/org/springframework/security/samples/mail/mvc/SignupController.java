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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.MailUserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Demonstrates how to automatically login a user after they signup using the
 * {@link SecurityContextHolder}. This is the easiest way to indicate a user is
 * authenticated.
 *
 * <p>
 * Note that in order for this to work use intercept-url@access=permitAll. Do
 * NOT use http@security=none or intercept-url@filters=none.
 * </p>
 *
 * @author Rob Winch
 *
 */
@Controller
@RequestMapping("/signup")
public class SignupController {
    private final MailUserService userService;

    /**
     * Creates a new {@link SignupController}
     * @param userService the {@link MailUserService} to use for signing someone up. Cannot be null.
     * @throws IllegalArgumentException if userService is null
     */
    @Autowired
    public SignupController(MailUserService userService) {
        if (userService == null) {
            throw new IllegalArgumentException("userService cannot be null");
        }
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView signupForm() {
        return new ModelAndView("signup/form", "mailUser", new MailUser());
    }

    @RequestMapping(method = RequestMethod.POST)
    public String signup(@Valid MailUser mailUser, BindingResult result) {
        if (result.hasErrors()) {
            return "signup/form";
        }
        if (this.userService.findUserByEmail(mailUser.getEmail()) != null) {
            result.rejectValue("email", "errors.signup.email.existingUser",
                    "A user with email " + mailUser.getEmail() + " already exists.");
            return "signup/form";
        }
        int id = this.userService.createUser(mailUser);
        mailUser.setId(id);
        Authentication authentication = new UsernamePasswordAuthenticationToken(mailUser, mailUser.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/messages/inbox";
    }
}
