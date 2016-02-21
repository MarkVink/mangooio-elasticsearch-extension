[![Travis Build Status](https://travis-ci.org/MarkVink/mangooio-elasticsearch-extension.svg?branch=master)](http://travis-ci.org/MarkVink/mangooio-elasticsearch-extension)

Elasticsearch extension for Mangoo I/O
=====================
This is an easly plugable extension for the mangoo I/O framework to work with Elasticsearch.

Setup
-----

1) Add the mangooio-elasticsearch-extension dependency to your pom.xml:

    <dependency>
        <groupId>com.markvink</groupId>
        <artifactId>mangooio-elasticsearch-extension</artifactId>
        <version>x.x.x</version>
    </dependency>
    
2) Configure the Elasticsearch client inapplication.yaml

2.1) Node client

    elasticsearch:
        cluster:
            name: elasticsearch
        path:
            home: /path/to/elasticsearch/home/dir
        index:
            store:
                type: memory|simplefs|niofs|mmapfs|default
        http:
            enabled: true|false

2.1) Transport client

    elasticsearch:
        cluser:
            name: elasticsearch
        node:
            host: 127.0.0.1
            port: 9300
        client:
            transport:
                sniff: true|false
                ignore_cluster_name: true|false
                ping_timeout: 5s
                nodes_sampler_interval: 5s

3) Inject the Elasticsearch client where needed

	@Inject
	Elasticsearch elasticsearch;
	
Basic usage
-----
1) Adding and removing nodes on the transport client

    elasticsearch.addNode(InetAddress address, int port);
    elasticsearch.removeNode(InetAddress address, int port);

2) Wrapper methods

    elasticsearch.getClient();
    elasticsearch.createIndex(String indexName);
    elasticsearch.indexDocument(String indexName, Document document);
    elasticsearch.getDocument(String indexName, String documentType, String documentId);
    elasticsearch.deleteDocument(String indexName, String documentType, String documentId);
    
Indexing documents
-----

1) Indexing fields using jackson annotations

    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.markvink.mangooio.elasticsearch.document.Document;

    public class User implements Document {
        @JsonProperty("username")
        private String username;
        
        public User(String username) {
            this.username = username;
        }
    }
    
    User user = new User("my-username");
    elasticsearch.indexDocument("my-index", user); // Indexed username=my-username with generated ID
    
2) Specifing ID of the document

    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.markvink.mangooio.elasticsearch.document.DocumentWithId;

    public class User implements DocumentWithId {
        @JsonProperty("username")
        private String username;
        
        public User(String username) {
            this.username = username;
        }
        
        @Override
        public String getDocumentId() {
            return username;
        }
    }
    
    User user = new User("my-username");
    elasticsearch.indexDocument("my-index", user); // indexed using ID=my-username
    
3) Specifing source of the document

    import com.markvink.mangooio.elasticsearch.document.DocumentWithSource;

    public class User implements DocumentWithSource {
        private String username;
        
        public User(String username) {
            this.username = username;
        }
        
        @Override
        public Map<String, Object> getDocumentSource() {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", username);
            map.put("key1", "value1");
            map.put("key2", "value2");
            return map;
        }
    }
    
    User user = new User("my-username");
    elasticsearch.indexDocument("my-index", user); // indexed using provided source
