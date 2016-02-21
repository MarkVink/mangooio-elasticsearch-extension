package com.markvink.mangooio.elasticsearch.document;

import java.util.Map;

public interface DocumentWithoutId {

    default String getDocumentType() {
        return this.getClass().getSimpleName();
    }

    public Map<String, Object> getDocumentContent();
}
