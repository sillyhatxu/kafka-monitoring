package com.sillyhat.kafkamonitoring;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class DeleteIndex {

    public static void main(String[] args) {

        String[] indexArray = {"kafka-monitoring-2018.11.27", "kafka-monitoring-2018.11.28"};
        for (String index : indexArray) {
            RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("10.60.12.111", 9200, "http")));
            try {
                DeleteIndexRequest request = new DeleteIndexRequest(index);
                client.indices().delete(request, RequestOptions.DEFAULT);

            } catch (IOException e) {

            }finally {
                try {
                    client.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
