package tests.elasticsearch;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.markvink.mangooio.elasticsearch.Elasticsearch;

import io.mangoo.core.Application;

public class ElasticsearchTest {
    private static Elasticsearch elasticsearch;

    @Before
    public void init() {
        elasticsearch = Application.getInstance(Elasticsearch.class);
    }

    @Test
    public void testInitialisation() {
        assertNotNull(elasticsearch);
        assertNotNull(elasticsearch.getClient());
    }
}
