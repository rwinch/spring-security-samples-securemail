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
package org.springframework.security.samples.mail.config

import java.util.Arrays
import scala.collection.JavaConversions.seqAsJavaList
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.ExpressionBasedAnnotationAttributeFactory
import org.springframework.security.access.expression.method.ExpressionBasedPostInvocationAdvice
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor
import org.springframework.security.access.intercept.AfterInvocationProviderManager
import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource
import org.springframework.security.access.prepost.PostInvocationAdviceProvider
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource
import org.springframework.security.access.vote.AffirmativeBased
import org.springframework.security.access.vote.AuthenticatedVoter
import org.springframework.security.access.vote.RoleVoter
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.samples.mail.security.MessagePermissionEvaluator
import org.springframework.security.web.access.DefaultWebInvocationPrivilegeEvaluator
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.util.RequestMatcher
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.SecurityFilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.Filter
import scalasec.web.WebAccessRules.hasRole
import scalasec.web.WebAccessRules.permitAll
import scalasec.web.BasicAuthentication
import scalasec.web.ConcurrentSessionControl
import scalasec.web.FilterChain
import scalasec.web.FormLogin
import scalasec.web.Logout
import scalasec.web.RememberMe
import scalasec.web.WebAccessControl
import scalasec.Conversions
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.security.web.access.AccessDeniedHandlerImpl

/**
 * An @Configuration using Luke Taylor's implementation of <a href="https://github.com/tekul/scalasec/">scalasec</a>. To get a better understanding, also look at
 * <a href="http://blog.springsource.com/2011/08/01/spring-security-configuration-with-scala/">Spring Security Configuration with Scala</a>.
 */
@Configuration
class ScalaSecurityConfiguration {
    @Autowired val messageUserDetailsService: UserDetailsService = null

    /**
     * The FilterChainProxy bean which is delegated to from web.xml
     */
    @Bean def springSecurityFilterChain = new FilterChainProxy(List(resourcesSecurityFilterChain, securityFilterChain))

    @Bean
    def messageUserAuthenticationManager = {
        val provider = new DaoAuthenticationProvider
        provider.setUserDetailsService(messageUserDetailsService)
        val am = new ProviderManager(Arrays.asList(provider))
        am.setAuthenticationEventPublisher(authenticationEventPublisher)
        am
    }

    @Bean
    def resourcesSecurityFilterChain = {
        new SecurityFilterChain with Conversions {
            val requestMatcher: RequestMatcher = "/resources/**"
            val getFilters: java.util.List[Filter] = Nil
            def matches(request: HttpServletRequest) = requestMatcher.matches(request)
        }
    }

    @Bean
    def securityFilterChain = {
        new FilterChain with BasicAuthentication with FormLogin with Logout with RememberMe with ConcurrentSessionControl with WebAccessControl {
            val userDetailsService = messageUserDetailsService
            val authenticationManager = messageUserAuthenticationManager

            override val logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler()
            logoutSuccessHandler.setDefaultTargetUrl("/login?logout")
            logoutFilter.setFilterProcessesUrl("/logout")

            val loginPage = "/login"
            val failureHandler = new SimpleUrlAuthenticationFailureHandler
            failureHandler.setDefaultFailureUrl("/login?error");
            formLoginFilter.setAuthenticationFailureHandler(failureHandler)

            val accessDeniedHandler = new AccessDeniedHandlerImpl()
            accessDeniedHandler.setErrorPage("/errors/403")
            exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler)

            override val sessionExpiredUrl = "/login?expired"
            sessionAuthenticationStrategy.setMaximumSessions(1)
            sessionAuthenticationStrategy.setExceptionIfMaximumExceeded(true)
            sessionAuthenticationStrategy.setExceptionIfMaximumExceeded(true)

            interceptUrl("/users/**", hasRole("ROLE_ADMIN"))
            interceptUrl("/sessions/**", hasRole("ROLE_ADMIN"))
            interceptUrl("/login*", permitAll)
            interceptUrl("/logout*", permitAll)
            interceptUrl("/errors/*", permitAll)
            interceptUrl("/signup*", permitAll)
            interceptUrl("/**", hasRole("ROLE_USER"))
        }
    }

    @Bean
    def expressionHandler = {
        val result = new DefaultMethodSecurityExpressionHandler
        result.setPermissionEvaluator(new MessagePermissionEvaluator)
        result
    }

    @Bean
    def preInvocationVoterBldr = {
        val advice = new ExpressionBasedPreInvocationAdvice
        advice.setExpressionHandler(expressionHandler)
        new PreInvocationAuthorizationAdviceVoter(advice)
    }

    @Bean
    def metaDataSource = {
        val invocationFactory = new ExpressionBasedAnnotationAttributeFactory(expressionHandler)
        val source = new PrePostAnnotationSecurityMetadataSource(invocationFactory)
        new DelegatingMethodSecurityMetadataSource(List(source))
    }

    @Bean
    def methodSecurityInterceptor = {
        val result = new MethodSecurityInterceptor
        result.setAccessDecisionManager(accessManager)
        result.setAuthenticationManager(messageUserAuthenticationManager)
        result.setSecurityMetadataSource(metaDataSource)
        val afterInvocationMgr = new AfterInvocationProviderManager
        afterInvocationMgr.setProviders(List(afterInvocationBldr))
        result.setAfterInvocationManager(afterInvocationMgr)
        result
    }

    @Bean
    def afterInvocationBldr = {
        val advice = new ExpressionBasedPostInvocationAdvice(expressionHandler)
        new PostInvocationAdviceProvider(advice)
    }

    @Bean
    def accessManager = {
        new AffirmativeBased(List(preInvocationVoterBldr, new RoleVoter, new AuthenticatedVoter))
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    def metaDataSourceAdvisor = {
        new MethodSecurityMetadataSourceAdvisor("methodSecurityInterceptor", metaDataSource, "metaDataSource")
    }

    @Bean
    def internalAutoProxyCreator = {
        new InfrastructureAdvisorAutoProxyCreator
    }

    @Bean def authenticationEventPublisher = new DefaultAuthenticationEventPublisher

    @Bean def accessLogger = new org.springframework.security.access.event.LoggerListener

    @Bean def authenticationLogger = new org.springframework.security.authentication.event.LoggerListener

    @Bean def webSecurityExpressionHandler = new org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler

    @Bean def webInvocationPrivEval = new DefaultWebInvocationPrivilegeEvaluator(securityFilterChain.filterSecurityInterceptor)

    @Bean def sessionRegistry = securityFilterChain.sessionRegistry
}