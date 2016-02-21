package com.markvink.mangooio.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Document {

    @JsonIgnore
    default String getDocumentType() {
        return this.getClass().getSimpleName();
    }
}
