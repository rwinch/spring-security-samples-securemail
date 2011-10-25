package org.springframework.security.samples.mail;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.Message;
import org.springframework.security.samples.mail.MessageService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/*.xml")
public class JdbcMessageServiceTest {
    @Autowired
    private MessageService messageRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        // alternatively you can not load the security.xml and then you can test the Services in isolation
        Authentication authentication = new UsernamePasswordAuthenticationToken(getExistingMessage().getFromUser(),
                "notused", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getMessage() {
        Message expected = getExistingMessage();
        Message actual = this.messageRepository.getMessage(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void createMessage() {
        int messageCount = countMessageRows();
        Message existing = getExistingMessage();
        Message message = new Message();
        message.setFromUser(existing.getToUser());
        message.setToUser(existing.getFromUser());
        message.setMessage("replied to your message");
        message.setSubject("RE: subject");

        int id = this.messageRepository.createMessage(message);
        assertThat(countMessageRows()).isEqualTo(messageCount + 1);
        message.setId(id);
        assertEquals(message, this.messageRepository.getMessage(id));
    }

    public static Message getExistingMessage() {
        Message message = new Message();
        message.setId(1);
        message.setSubject("Vulnerabilities Found?");
        message.setMessage("I believe I found some vulnerabilities in the message application. It may be good to ensure that you secure the application.");
        MailUser fromUser = new MailUser();
        fromUser.setId(2);
        fromUser.setEmail("luke@example.com");
        fromUser.setPassword("lion");
        fromUser.setFirstName("Luke");
        fromUser.setLastName("Taylor");
        message.setFromUser(fromUser);
        message.setToUser(JdbcMailUserServiceTest.getExistingUser());
        return message;
    }

    private int countMessageRows() {
        return this.jdbcTemplate.queryForInt("select count(1) from message");
    }

    private static void assertEquals(Message expected, Message actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getSubject()).isEqualTo(expected.getSubject());
        assertThat(actual.getMessage()).isEqualTo(expected.getMessage());
        MailUser fromUser = actual.getFromUser();
        JdbcMailUserServiceTest.assertEquals(expected.getFromUser(),
                fromUser);
        JdbcMailUserServiceTest.assertEquals(expected.getToUser(),
                actual.getToUser());
    }
}
