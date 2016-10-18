package com.glluch.mooclodecf.moocssolr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Simple queries for solr
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class SQuery {

    protected String urlString = "http://localhost:8888/solr/ecf";
    protected HashMap <String,String>options=new HashMap<>();
    protected String term_name="term";
    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public void setCustom(String option, String value){
        //query.set("fl", "category,title,price");
        options.put(option, value);
        
    }
    
    public void delCustom(String option){
        options.remove(option);
    }
    public String build(List<String> terms) {
        String res = "";
        int i = 0;
        for (String term : terms) {
            i++;
            if (i > 1) {
                res += "OR ";
            }
            res += term_name+":\"" + term.trim().toLowerCase() + "\"";
        }
        return res;
    }

    public SolrDocumentList execute(String q) throws IOException, SolrServerException {
        SolrClient solr = new HttpSolrClient(this.urlString);
        SolrQuery query = new SolrQuery();
        Set keys=options.keySet();
        for (Object key0:keys){
            String key=(String)key0;
            query.set(key, options.get(key));
        }
        query.setQuery(q);
        QueryResponse response = solr.query(query);
        return response.getResults();
    }

    

//HashMap<String,String>
    public SolrDocumentList buildAndExecute(List<String> terms) throws IOException, SolrServerException {
        return execute(build(terms));
    }

    
}
