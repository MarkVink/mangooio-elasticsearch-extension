package com.markvink.mangooio.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DocumentWithId extends Document {

    @JsonIgnore
    public String getDocumentId();
}
