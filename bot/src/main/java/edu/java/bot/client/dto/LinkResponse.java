package edu.java.bot.client.dto;

import lombok.Getter;
import lombok.Setter;
import java.net.URL;

//@Getter
//@Setter
//public class LinkResponse {
//    private long id;
//    private URL url;
//}

public record LinkResponse(long id, URL url) {
}
