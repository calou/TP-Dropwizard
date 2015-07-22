package com.acme.kanban.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.apache.log4j.Logger;

abstract class BaseModel {
    private final static Logger LOGGER = Logger.getLogger(Story.class);

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        LOGGER.warn("Unknown json property \"" + key + "\" with value \"" + value + "\" for class \"" + this.getClass().getSimpleName() + "\"" );
    }
}
