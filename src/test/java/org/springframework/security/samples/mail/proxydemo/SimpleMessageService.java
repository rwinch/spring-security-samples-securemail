package org.springframework.security.samples.mail.proxydemo;

public interface SimpleMessageService {

    String getMessage(int id);

    void printMessage(int id);
}
