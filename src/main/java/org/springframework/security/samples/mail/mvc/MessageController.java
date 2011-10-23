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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.MailUserContext;
import org.springframework.security.samples.mail.MailUserService;
import org.springframework.security.samples.mail.Message;
import org.springframework.security.samples.mail.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageRespository;
    @Autowired
    private MailUserService userRepository;
    @Autowired
    private MailUserContext userContext;

    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "redirect:/messages/inbox";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid MessageForm messageForm, BindingResult result) {
        if (result.hasErrors()) {
            return "messages/compose";
        }
        MailUser toUser = this.userRepository.findUserByEmail(messageForm
                .getToEmail());
        if (toUser == null) {
            result.rejectValue("toEmail", "errors.toEmail.notfound",
                    "A user with email " + messageForm.getToEmail()
                    + " was not found.");
            return "messages/compose";
        }
        Message message = new Message();
        message.setFromUser(this.userContext.getCurrentUser());
        message.setSubject(messageForm.getSubject());
        message.setMessage(messageForm.getMessage());
        message.setToUser(toUser);
        this.messageRespository.createMessage(message);
        return "redirect:/messages/sent";
    }

    @RequestMapping(value = "", params = "form")
    public String compose(ModelMap model) {
        model.addAttribute(new MessageForm());
        return "messages/compose";
    }

    @RequestMapping(value = "/inbox")
    public String inbox(ModelMap model) {
        Integer userId = this.userContext.getCurrentUser().getId();
        List<Message> messages = this.messageRespository
                .findInboxMessageForUser(userId);
        model.addAttribute("messages", messages);
        return "messages/inbox";
    }

    @RequestMapping("/sent")
    public String sent(ModelMap model) {
        Integer userId = this.userContext.getCurrentUser().getId();
        List<Message> messages = this.messageRespository
                .findSentForUser(userId);
        model.addAttribute("messages", messages);
        return "messages/sent";
    }

    @RequestMapping("/{id}")
    public String show(@PathVariable int id, ModelMap model) {
        Message message = this.messageRespository.getMessage(id);
        model.addAttribute("message", message);
        return "messages/show";
    }
}
