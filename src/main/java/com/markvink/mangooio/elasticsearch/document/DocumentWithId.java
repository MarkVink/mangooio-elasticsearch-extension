package com.markvink.mangooio.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DocumentWithId extends Document {

    /**
     * Gets the document id.
     *
     * @return the document id
     */
    @JsonIgnore
    public String getDocumentId();
}
