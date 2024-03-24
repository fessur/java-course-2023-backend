package edu.java.service.site.jpa;

import edu.java.service.model.jpa.JpaLink;
import edu.java.service.site.Site;

public interface JpaSite extends Site {
    void update(JpaLink link);
}
