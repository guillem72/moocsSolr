/*
 * The MIT License
 *
 * Copyright 2016 Guillem LLuch Moll guillem72@gmail.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.glluch.mooclodecf.moocssolr;

import com.glluch.certisparser.Certification;
import com.glluch.certisparser.JReader;
import com.glluch.utils.Obj;
import com.glluch.utils.Out;
import com.glluch.utils.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class Certs {

    private final String CERTIS = "type:Certification";
    private Table certs = new Table();

     
    
    public ArrayList<String> buildHeaders() throws IOException, SolrServerException {
        SolrDocumentList heads;
        ArrayList<String> hs = new ArrayList<>();
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", "type:competence OR type:ICT_profile");
        sq.setCustom("fl", "id");
        sq.setCustom("rows", "1000");
        String query = "*:*";
        heads = sq.execute(query);
        
        //Labels for attributes
        for (SolrDocument title : heads) {
            String id = (String) title.get("id");
            if (!hs.contains(id)) {
                hs.add((String) title.get("id"));
            }
        }
        
        //labels for tags
        hs.addAll(makeCompLabels("A",9));
        hs.addAll(makeCompLabels("B",6));
        hs.addAll(makeCompLabels("C",4));
        hs.addAll(makeCompLabels("D",12));
        hs.addAll(makeCompLabels("E",9));
        
        certs.setHeaders(hs);
        return hs;

    }

    
    
    private ArrayList<String> makeCompLabels(String letter, int max) {
        ArrayList<String> res = new ArrayList<>();
       
        for (int i = 1; i <= max; i++) {
            boolean add = res.add(letter + "." + i);
        }
        
        return res;
    }

    public void solr2certis() throws IOException, SolrServerException, ParseException {
        ArrayList<Certification> certss=JReader.readDir();
        SolrDocumentList certis;
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", CERTIS);
        sq.setCustom("rows", "10");
        for(Certification cer:certss){
         HashMap<String, Double> hm=cer.getTerms();
        certis = sq.buildAndExecute(hm);
         for (SolrDocument certi : certis) {
             HashMap<String, Double> attrib= attributes(certi);
             
         }
        }
        /*
         SolrDocumentList certis;
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", "type:ICT_profile OR type:competence");
        sq.setCustom("rows", "1000");

        String query = "*:*";
        certis = sq.execute(query);
        for (SolrDocument certi : certis) {
            if (certi.get("type").equals("ICT_profile")){
            
            }
            if (certi.get("type").equals("competence")){
                Out.p(certi.get("id"));
            }
            
            
        }
         */
    }

    public HashMap<String, Double> attributes(SolrDocument cert) {
   
        HashMap<String, Double> res = new HashMap<>();
        String id = (String) cert.get("id");
        String type = (String) cert.get("type");
        //Obj.showTypeOf(cert.get("term"));
        ArrayList <String> terms=(ArrayList <String>) cert.get("term");
        //Out.p(terms);
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", "type:ICT_profile OR type:competence");
        sq.setCustom("rows", "1000");

        
        return res;
    }

    public HashMap<String, Double> compentences(SolrDocument certi) throws IOException, SolrServerException {
        HashMap<String, Double> res = new HashMap<>();

       
            ArrayList<String> comps = new ArrayList<>();
            Object id0 = certi.get("id");
            String id = (String) id0;
            ArrayList<Object> comps1 = (ArrayList<Object>) certi.get("competence_");
            ArrayList<Object> comps2 = (ArrayList<Object>) certi.get("competence_g");

            if (comps1 != null) {
                comps.addAll(comps(comps1));
            }
            if (comps2 != null) {
                comps.addAll(comps(comps2));
            }
            for(String comp:comps){
                
            res.put(comp, 1.0);
            }
        
        return res;
    }

    private ArrayList<String> comps(ArrayList<Object> comps0) {
        ArrayList<String> comps = new ArrayList<>();
        for (Object comp0 : comps0) {
            String comp = (String) comp0;
            String[] cs = comp.split(" ");
            if (!StringUtils.isEmpty(cs[0])) {
                comps.add(cs[0].trim());
            }

        }
        return comps;
    }

}
