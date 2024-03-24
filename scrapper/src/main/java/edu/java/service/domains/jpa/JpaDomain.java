package edu.java.service.domains.jpa;

import edu.java.service.domains.Domain;
import edu.java.service.model.jpa.JpaLink;

public interface JpaDomain extends Domain {
    void update(JpaLink link);
}
