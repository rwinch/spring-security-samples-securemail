package org.springframework.security.samples.mail;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.samples.mail.JdbcMailUserService;
import org.springframework.security.samples.mail.MailUser;
import org.springframework.security.samples.mail.MailUserService;
import org.springframework.security.samples.mail.NotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/*.xml")
public class JdbcMessageUserServiceTest {
    @Autowired
    private MailUserService messageUserRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullTemplate() {
        new JdbcMailUserService(null);
    }

    @Test
    public void getMessageUser() {
        MailUser expected = getExistingUser();
        MailUser user = messageUserRepository.getUser(expected
                .getId());
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(expected.getId());
        assertThat(user.getEmail()).isEqualTo(expected.getEmail());
        assertThat(user.getPassword()).isEqualTo(expected.getPassword());
        assertThat(user.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(user.getLastName()).isEqualTo(expected.getLastName());
    }

    @Test(expected = NotFoundException.class)
    public void getMessageUserNotFound() {
        messageUserRepository.getUser(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createMessageUserNull() {
        messageUserRepository.createUser(null);
    }

    @Test
    public void createMessageUser() {
        MailUser toSave = new MailUser();
        toSave.setEmail("new@nowhere.com");
        toSave.setFirstName("New");
        toSave.setLastName("User");
        toSave.setPassword("New Password");
        int messageCount = countMessageRows();
        int id = messageUserRepository.createUser(toSave);
        assertThat(countMessageRows()).isEqualTo(messageCount + 1);
        MailUser saved = messageUserRepository.findUserByEmail(toSave
                .getEmail());
        assertThat(saved.getId()).isNotNull();
        toSave.setId(saved.getId());
        assertEquals(toSave, saved);
        assertEquals(toSave, messageUserRepository.getUser(id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createMessageUserIdSpecified() {
        MailUser toSave = new MailUser();
        toSave.setId(100);
        messageUserRepository.createUser(toSave);
    }

    static void assertEquals(MailUser expected, MailUser actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
    }

    static MailUser getExistingUser() {
        MailUser messageUser = new MailUser();
        messageUser.setId(1);
        messageUser.setEmail("rob@example.org");
        messageUser.setFirstName("Rob");
        messageUser.setLastName("Winch");
        messageUser.setPassword("penguin");
        return messageUser;
    }

    private int countMessageRows() {
        return jdbcTemplate.queryForInt("select count(1) from message_user");
    }
}
