package edu.java.bot.domain;

public class Link {
    // todo: добавить нормальную БД с many to many между Link и Chat
    private String domain;
    private String url;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
