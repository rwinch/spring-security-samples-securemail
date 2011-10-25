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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A jdbc implementation of {@link MessageService}.
 * @author Rob Winch
 *
 */
@Repository
public class JdbcMessageService implements MessageService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new instance of {@link JdbcMailUserService}
     * @param jdbcTemplate the {@link JdbcTemplate} to use. Cannot be null.
     * @throws IllegalArgumentException if {@code jdbcTemplate} is null.
     */
    @Autowired
    public JdbcMessageService(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("jdbcTemplate cannot be null");
        }
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostAuthorize("returnObject.fromUser.id == principal.id or returnObject.toUser.id == principal.id")
    public Message getMessage(int id) {
        String messageQuery = MESSAGE_QUERY + " where m.id = ? and m.fromUser = fromUser.id and m.toUser = toUser.id";
        try {
            return this.jdbcTemplate.queryForObject(messageQuery, this.MESSAGE_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Could not find a Message by id "+id, e);
        }
    }

    @Transactional
    public int createMessage(final Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }
        if (message.getId() != null) {
            throw new IllegalArgumentException("Message.getId() must be null when creating a new Message");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "insert into message (fromUser,toUser,subject,message) values (?, ?, ?, ?)",
                        new String[] { "id" });
                ps.setInt(1, message.getFromUser().getId());
                ps.setInt(2, message.getToUser().getId());
                ps.setString(3, message.getSubject());
                ps.setString(4, message.getMessage());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public List<Message> findSentForUser(int userId) {
        String messageQuery = MESSAGE_QUERY
                + " where m.fromUser = ? and m.fromUser = fromUser.id and m.toUser = toUser.id order by id desc";
        return this.jdbcTemplate.query(messageQuery, this.MESSAGE_ROW_MAPPER, userId);
    }

    public List<Message> findInboxMessageForUser(int userId) {
        String messageQuery = MESSAGE_QUERY
                + " where m.toUser = ? and m.fromUser = fromUser.id and m.toUser = toUser.id order by id desc";
        return this.jdbcTemplate.query(messageQuery, this.MESSAGE_ROW_MAPPER, userId);
    }

    /**
     * A RowMapper for mapping a {@link Message}
     */
    private final RowMapper<Message> MESSAGE_ROW_MAPPER = new RowMapper<Message>() {
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            Message message = new Message();
            message.setId(rs.getInt("message.id"));
            message.setMessage(rs.getString("message.message"));
            message.setSubject(rs.getString("message.subject"));
            message.setFromUser(FROM_USER_ROWMAPPER.mapRow(rs, rowNum));
            message.setToUser(TO_USER_ROWMAPPER.mapRow(rs, rowNum));
            return message;
        }
    };

    /**
     * The query for a message to be reused with different where clauses.
     */
    private static final String MESSAGE_QUERY = "select m.id, m.subject, m.message, fromUser.id as from_id, fromUser.email as from_email, fromUser.password as from_password, fromUser.firstName as from_firstName, fromUser.lastName as from_lastName, toUser.id as to_id, toUser.email as to_email, toUser.password as to_password, toUser.firstName as to_firstName, toUser.lastName as to_lastName from message as m, mail_user as fromUser, mail_user as toUser";
    private static RowMapper<MailUser> FROM_USER_ROWMAPPER = new JdbcMailUserService.MailUserRowMapper("from_");
    private static RowMapper<MailUser> TO_USER_ROWMAPPER = new JdbcMailUserService.MailUserRowMapper("to_");
}
