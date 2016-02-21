package com.markvink.mangooio.elasticsearch.document;

public interface DocumentWithId extends DocumentWithoutId {

    public String getDocumentId();
}
