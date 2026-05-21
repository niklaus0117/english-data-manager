package com.englishdatamanager.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Auth auth = new Auth();
    private Storage storage = new Storage();
    private Kafka kafka = new Kafka();
    private Sms sms = new Sms();

    @Data
    public static class Auth {
        private long tokenExpireSeconds = 86400L;
        private String passwordSalt = "english-data-manager";
    }

    @Data
    public static class Storage {
        private String localPath = "storage";
        private String publicPrefix = "/static/";
    }

    @Data
    public static class Kafka {
        private String topicVideoChanged = "video-changed-topic";
    }

    @Data
    public static class Sms {
        private long codeExpireSeconds = 300L;
        private boolean mockEnabled = true;
        private String mockPrefix = "EDM";
    }
}
