package org.springframework.security.samples.mail.proxydemo;

import static junit.framework.Assert.fail;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

public class ProxyBasedAopDemoTest {

    @Test
    public void interfaceBaseProxy() {
        final MessageServiceBean unsecuredStub = new MessageServiceBean();
        SimpleMessageService securedProxy = new SimpleMessageService() {
            public String getMessage(int id) {
                if (id != 1) {
                    throw new AccessDeniedException(
                            "We only allow access to messages with id of 1");
                }
                return unsecuredStub.getMessage(id);
            }

            public void printMessage(int id) {
                unsecuredStub.printMessage(id);
            }
        };

        // notice if we try and cast the proxy to MessageRepositoryStub we would
        // get a ClassCastException
        // notice that the method getFromUser(int id) cannot be used since it is
        // not declared on the interface
        try {
            @SuppressWarnings("unused")
            MessageServiceBean securedStub = (MessageServiceBean) securedProxy;
            fail("Expected Exception");
        } catch (ClassCastException success) {
        }

        // everything is secured
        try {
            securedProxy.getMessage(2);
            fail("Expected Exception");
        } catch (AccessDeniedException success) {
        }

        // notice that printMessage is not secured even though MessageServiceBean calls
        // getMessage internally and getMessage is secured
        securedProxy.printMessage(2);
    }

    @Test
    public void classBaseProxy() {
        final MessageServiceBean unsecuredStub = new MessageServiceBean();
        SimpleMessageService securedProxy = new MessageServiceBean() {
            public String getMessage(int id) {
                if (id != 1) {
                    throw new AccessDeniedException(
                            "We only allow access to messages with id of 1");
                }
                return unsecuredStub.getMessage(id);
            }
        };
        // notice if we try and cast the proxy to MessageRepositoryStub it works
        MessageServiceBean securedStub = (MessageServiceBean) securedProxy;
        // notice that the method getFromUser(int id) can be called since we can cast to MessageServiceBean
        securedStub.setMessage("Hello AOP");

        // everything is secured
        try {
            securedProxy.getMessage(2);
            fail("Expected Exception");
        } catch (AccessDeniedException success) {
        }
    }

    @Test
    public void messageRepsitoryStub() {
        MessageServiceBean unsecuredStub = new MessageServiceBean();
        assertThat(unsecuredStub.getMessage(2)).isNotNull();
    }
}
