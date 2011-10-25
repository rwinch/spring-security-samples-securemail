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
public class JdbcMailUserServiceTest {
    @Autowired
    private MailUserService userService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullTemplate() {
        new JdbcMailUserService(null);
    }

    @Test
    public void getUser() {
        MailUser expected = getExistingUser();
        MailUser user = userService.getUser(expected
                .getId());
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(expected.getId());
        assertThat(user.getEmail()).isEqualTo(expected.getEmail());
        assertThat(user.getPassword()).isEqualTo(expected.getPassword());
        assertThat(user.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(user.getLastName()).isEqualTo(expected.getLastName());
    }

    @Test(expected = NotFoundException.class)
    public void getUserNotFound() {
        userService.getUser(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserNull() {
        userService.createUser(null);
    }

    @Test
    public void createUser() {
        MailUser toSave = new MailUser();
        toSave.setEmail("new@nowhere.com");
        toSave.setFirstName("New");
        toSave.setLastName("User");
        toSave.setPassword("New Password");
        int messageCount = countMessageRows();
        int id = userService.createUser(toSave);
        assertThat(countMessageRows()).isEqualTo(messageCount + 1);
        MailUser saved = userService.findUserByEmail(toSave
                .getEmail());
        assertThat(saved.getId()).isNotNull();
        toSave.setId(saved.getId());
        assertEquals(toSave, saved);
        assertEquals(toSave, userService.getUser(id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserIdSpecified() {
        MailUser toSave = new MailUser();
        toSave.setId(100);
        userService.createUser(toSave);
    }

    static void assertEquals(MailUser expected, MailUser actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
    }

    static MailUser getExistingUser() {
        MailUser mailUser = new MailUser();
        mailUser.setId(1);
        mailUser.setEmail("rob@example.org");
        mailUser.setFirstName("Rob");
        mailUser.setLastName("Winch");
        mailUser.setPassword("penguin");
        return mailUser;
    }

    private int countMessageRows() {
        return jdbcTemplate.queryForInt("select count(1) from mail_user");
    }
}
