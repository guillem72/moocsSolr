package com.glluch.moocssolr;

import com.glluch.utils.Out;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class SolrQ extends SQuery {

    public SolrQ() {
        super.term_name = "term";
        super.urlString = "http://localhost:8888/solr/ecf3";
    }

    
    
    public String build(HashMap<String, Double> terms_value) {
        //Out.p("SolrQuery terms="+terms_value.size());
        boolean showed=false;
        String res = "";
        int i = 0;
        Set terms = terms_value.keySet();
        for (Object t0 : terms) {
            String t=(String) t0;
            i++;
            if (res.length()<MAX_QUERY_LENGH){
            if (i > 1) {
                
                res += " OR ";
            }
            res += term_name+":\"" + t.trim().toLowerCase() 
                    
                    + "\"" +"^"+terms_value.get(t);
        }
            else {
            
               // if (!showed) {Out.p("Mega query "+res); showed=true;}
            
            }
        }
        return res;
    }
    
    public SolrDocumentList buildAndExecute(HashMap<String, Double> terms_value) throws IOException, SolrServerException{
        return execute(build(terms_value));
    }

}