package com.acme.kanban.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StoryTest {
    public static final TypeReference<HashMap<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<HashMap<String,Object>>() {
    };

    final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void serialization() throws IOException {
        final Story story = Story.builder().id(14l).title("This is the new story")
                .project(Project.builder().id(1234l).build())
                .step(Step.builder().id(10l).build())
                .build();
        String json = mapper.writeValueAsString(story);

        Map<String,Object> map = mapper.readValue(json, MAP_TYPE_REFERENCE);
        assertThat(map.get("id")).isEqualTo(14);
        assertThat(((Map<String,Object>)map.get("project")).get("id")).isEqualTo(1234);
        assertThat(((Map<String,Object>)map.get("step")).get("id")).isEqualTo(10);
    }
}