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
package org.springframework.security.samples.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * A jdbc implementation of the {@link MailUserService}.
 *
 * @author rw012795
 */
@Repository
public class JdbcMailUserService implements MailUserService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMailUserService(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("jdbcTemplate cannot be null");
        }
        this.jdbcTemplate = jdbcTemplate;
    }

    public MailUser getUser(int id) {
        try {
            return this.jdbcTemplate.queryForObject(
                    "select id, email, password, firstName, lastName from mail_user where id = ?",
                    USER_ROWMAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Could not find MailUser with id " + id, e);
        }
    }

    public int createUser(final MailUser mailUser) {
        if (mailUser == null) {
            throw new IllegalArgumentException("mailUser cannot be null");
        }
        if (mailUser.getId() != null) {
            throw new IllegalArgumentException("mailUser.getId() must be null when creating a message");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "insert into mail_user (email, password, firstName, lastName) values (?, ?, ?, ?)",
                        new String[] { "id" });
                ps.setString(1, mailUser.getEmail());
                ps.setString(2, mailUser.getPassword());
                ps.setString(3, mailUser.getFirstName());
                ps.setString(4, mailUser.getLastName());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public MailUser findUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email cannot be null");
        }
        try {
            return this.jdbcTemplate.queryForObject(
                    "select id, email, password, firstName, lastName from mail_user where email = ?",
                    USER_ROWMAPPER, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * A RowMapper for {@link MailUser}'s that can accept a prefix for the jdbc
     * column label. This allows the RowMapper to be used in different contexts.
     *
     * @author Rob Winch
     *
     */
    static final class MailUserRowMapper implements RowMapper<MailUser> {
        private final String prefix;

        public MailUserRowMapper(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("prefix cannot be null");
            }
            this.prefix = prefix;
        }

        public MailUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            MailUser mailUser = new MailUser();
            mailUser.setId(rs.getInt(this.prefix + "id"));
            mailUser.setEmail(rs.getString(this.prefix + "email"));
            mailUser.setPassword(rs.getString(this.prefix + "password"));
            mailUser.setFirstName(rs.getString(this.prefix + "firstName"));
            mailUser.setLastName(rs.getString(this.prefix + "lastName"));
            return mailUser;
        }
    }

    /**
     * Maps {@link MailUser}'s for the {@link JdbcMailUserService}.
     */
    private static final RowMapper<MailUser> USER_ROWMAPPER = new MailUserRowMapper("mail_user.");
}
