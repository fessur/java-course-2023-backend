package edu.java.service.domains.jdbc;

import edu.java.service.domains.Domain;
import edu.java.service.model.jdbc.JdbcLink;

public interface JdbcDomain extends Domain {
    void update(JdbcLink link);
}
