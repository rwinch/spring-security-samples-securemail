package org.springframework.security.samples.mail.proxydemo;

public class MessageServiceBean implements SimpleMessageService {
    private String message = "Hello World";

    public String getMessage(int id) {
        return message;
    }

    public void printMessage(int id) {
        System.out.println(getMessage(id));
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
