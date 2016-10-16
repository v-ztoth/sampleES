import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by Zoli on 10/15/2016.
 */
public class Sample {
    public String indexDocument() throws IOException, URISyntaxException {

        TransportClient client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        Path path = Paths.get(getClass().getResource("katica_sajat.xls").toURI());
        byte[] content = Files.readAllBytes(path);



        IndexResponse response = null;
        try {


            String mapping = "{\"pdf\":{\"properties\":{\"file\":{\"type\":\"attachment\",\"fields\":{\"content\":{\"type\":\"string\",\"store\":true,\"term_vector\":\"with_positions_offsets\"},\"author\":{\"type\":\"string\"},\"title\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"},\"date\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"},\"keywords\":{\"type\":\"string\"},\"content_type\":{\"type\":\"string\"},\"content_length\":{\"type\":\"integer\"},\"language\":{\"type\":\"string\"}}}}}}";

            client.admin().indices().prepareCreate("doc").get();



            client.admin().indices()
                    .preparePutMapping("doc")
                    .setType("pdf")
                    .setSource(mapping)
                    .execute().actionGet();

            response = client.prepareIndex("doc", "pdf", "1")
                    .setSource(jsonBuilder().startObject()
                            .field("file", Base64.encodeBytes(content))
                            .endObject()).setRefresh(true).get();

                    } catch (ElasticsearchException e) {
            //
        } catch (Exception e) {
            String d = "d";
        }
        return response.getId();
    }


   /* GET /doc/pdf/_search
    {
        "fields": [],
        "query": {
        "match": {
            "file.content": "Lóki"
        }
    },
        "highlight": {
        "fields": {
            "file.content": {
            }
        }
    }
    }

    http://localhost:9200/doc/pdf/_mapping

    GET /doc/pdf/_mapping*/
}
