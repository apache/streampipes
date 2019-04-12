/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl.datalake;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.rest.impl.datalake.model.DataResult;
import org.streampipes.rest.impl.datalake.model.InfoResult;
import org.streampipes.rest.impl.datalake.model.PageResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataLakeManagement {

    public DataResult getEvents(String index) throws IOException {
        List<Map<String, Object>> events = getDataLakeData(index);
        DataResult dataResult = new DataResult(events.size(), events);
        return dataResult;
    }

    public DataResult getEvents(String index, String timestampRuntimeName, Long from, Long to) throws IOException {
        List<Map<String, Object>> events = getDataLakeData(index, timestampRuntimeName, from, to);
        DataResult dataResult = new DataResult(events.size(), events);
        return dataResult;
    }

    public PageResult getEvents(String index, int itemsPerPage) throws IOException {
        int page = getBiggestPageNumber(index, itemsPerPage);
        List<Map<String, Object>> events = getDataLakeData(index, itemsPerPage, page);
        PageResult dataResult = new PageResult(events.size(), events, page);
        return dataResult;
    }

    public PageResult getEvents(String index, int itemsPerPage, int page) throws IOException {
        List<Map<String, Object>> events = getDataLakeData(index, itemsPerPage, page);
        PageResult dataResult = new PageResult(events.size(), events, page);
        return dataResult;
    }

    public InfoResult getInfo(String index) {
        return null;
    }

    public List<InfoResult> getAllInfos() {
        return null;
    }


    public List<Map<String, Object>> getDataLakeData(String index) throws IOException {
        return getDataLakeData(index, null, -1, -1);
    }

    public List<Map<String, Object>> getDataLakeData(String index, String timestampRuntimeName, long from, long to) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();

        RestHighLevelClient client = getRestHighLevelClient();

        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        if (timestampRuntimeName != null) {
            searchSourceBuilder.query(QueryBuilders.rangeQuery(timestampRuntimeName).from(from).to(to));
        }

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        String scrollId = searchResponse.getScrollId();


        SearchHit[] searchHits = searchResponse.getHits().getHits();

        for (SearchHit hit : searchHits) {
            result.add(hit.getSourceAsMap());
        }

        while (searchHits != null && searchHits.length > 0) {

            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                result.add(hit.getSourceAsMap());
            }
        }


        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);

        return result;
    }



    public List<Map<String, Object>> getDataLakeData(String index, int itemsPerPage, int page) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        if(page < 0)
            return result;

        RestHighLevelClient client = getRestHighLevelClient();

        //TODO remove?
        //Count
        CountRequest countRequest = new CountRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        countRequest.source(searchSourceBuilder);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        Long numOfElements = countResponse.getCount();

        if (numOfElements < page * itemsPerPage)
            return result;


        //Get num of elements
        //check if page want new data
        //if new data, get

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(page * itemsPerPage);
        sourceBuilder.size(itemsPerPage);
        sourceBuilder.sort(new FieldSortBuilder("date").order(SortOrder.DESC));
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            result.add(hit.getSourceAsMap());
        }

        return result;
    }

    private RestHighLevelClient getRestHighLevelClient() {
        String host = BackendConfig.INSTANCE.getDatalakeHost();
        int port = BackendConfig.INSTANCE.getDatalakePort();

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, "http")));
    }

    public String deleteIndex(String index) throws IOException {
        RestHighLevelClient client = getRestHighLevelClient();

        DeleteIndexRequest request = new DeleteIndexRequest(index);

        try {
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);
            if (deleteIndexResponse.isAcknowledged()) {
                return "";
            } else {
                return "Index: " + index + " did not exist!";
            }

        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.NOT_FOUND) {
                return "Index: " + index + " did not exist!";
            }
        }

        return "Index: " + index + " did not exist!";
    }

    private int getBiggestPageNumber(String index, int itemsPerPage) throws IOException {
        RestHighLevelClient client = getRestHighLevelClient();

        CountRequest countRequest = new CountRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        countRequest.source(searchSourceBuilder);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        Long numOfElements = countResponse.getCount();

        int page = (int) (numOfElements /  (long)itemsPerPage);

        return page;
    }


}
