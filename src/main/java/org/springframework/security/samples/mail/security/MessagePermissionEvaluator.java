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

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.Message;
import org.springframework.stereotype.Component;

/**
 * Demonstrates how to create a custom {@link PermissionEvaluator}. This can be
 * used to evaluate expressions that use hasPermission in a the Spring Security
 * annotations (i.e. @PostAuthorize). By using a {@link PermissionEvaluator}
 * logic can be more centralized that embedding the checks in annotations
 * themselves.
 *
 * @author Rob Winch
 */
@Component
public class MessagePermissionEvaluator implements PermissionEvaluator {

    @SuppressWarnings("unchecked")
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof MailUser)) {
            return false;
        }
        if (targetDomainObject == null) {
            return true;
        }
        if (!(targetDomainObject instanceof Message)) {
            return false;
        }
        if (targetDomainObject instanceof Iterable) {
            Iterable<Object> iTargets = (Iterable<Object>) targetDomainObject;
            for (Object target : iTargets) {
                if (!hasPermission(authentication, target, permission)) {
                    return false;
                }
            }
            return true;
        }
        Message message = (Message) targetDomainObject;
        MailUser currentUser = (MailUser) authentication.getPrincipal();
        return currentUser.equals(message.getFromUser()) || currentUser.equals(message.getToUser());
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        throw new UnsupportedOperationException();
    }
}
