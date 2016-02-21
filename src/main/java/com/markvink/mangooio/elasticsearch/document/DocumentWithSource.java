package com.markvink.mangooio.elasticsearch.document;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DocumentWithSource extends Document {

    /**
     * Gets the document source.
     *
     * @return the document source
     */
    @JsonIgnore
    public Map<String, Object> getDocumentSource();
}
