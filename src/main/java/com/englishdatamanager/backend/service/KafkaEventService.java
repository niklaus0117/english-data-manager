package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.config.AppProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public void publishVideoChanged(Long videoId, String action) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("videoId", videoId);
        event.put("action", action);
        event.put("eventTime", LocalDateTime.now().toString());
        try {
            kafkaTemplate.send(appProperties.getKafka().getTopicVideoChanged(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            log.warn("serialize video event failed, videoId={}", videoId, exception);
        } catch (Exception exception) {
            log.warn("publish video event failed, videoId={}", videoId, exception);
        }
    }
}
