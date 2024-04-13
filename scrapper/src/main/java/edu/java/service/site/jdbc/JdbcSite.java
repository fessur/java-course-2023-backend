package edu.java.service.site.jdbc;

import edu.java.service.model.jdbc.JdbcLink;
import edu.java.service.site.Site;

public interface JdbcSite extends Site {
    void update(JdbcLink link);
}
