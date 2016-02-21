package com.markvink.mangooio.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Document {

    /**
     * Gets the document type.
     *
     * @return the document type
     */
    @JsonIgnore
    default String getDocumentType() {
        return this.getClass().getSimpleName();
    }
}
