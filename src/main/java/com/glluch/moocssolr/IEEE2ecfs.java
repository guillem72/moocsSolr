
package com.glluch.moocssolr;

import com.glluch.utils.CsvWriter;
import com.glluch.utils.Numbers;
import com.glluch.utils.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public abstract class IEEE2ecfs {

    //private final String CERTIS = "type:Certification";
    protected final String DOCS_TO_RETRIEVE = "10"; //To send to solr a string is nedeed.
    //private final String CERTS_LIMIT="5";//for testing next to 5, a big number otherwise(10000);
      protected final String PROFILES_TO_RETRIEVE = "3";
    protected final String PREVISION_SUFIX = "_prevision";
    protected final Double GENERAL_SUPPORT = 1.0;
   protected final Double PARTIAL_SUPPORT = 0.5;
    protected final Double UNKNOWN_SUPPORT = 1.0;
    protected final Double SUPERFICIAL_SUPPORT = 0.15;
    protected HashMap<String, Table> data0;
    protected Table previsions;
    protected HashMap<String, Double> suport_values = new HashMap<>();
    protected String FILE_MATRIX_PREFIX="resources/ml/";
    protected ArrayList <String> refs=new ArrayList<>();

    public IEEE2ecfs() {
        suport_values.put("", UNKNOWN_SUPPORT);
        suport_values.put("G", GENERAL_SUPPORT);
        suport_values.put("P", PARTIAL_SUPPORT);
        suport_values.put("S", SUPERFICIAL_SUPPORT);
    }

    public HashMap<String, Table> buildTables() throws IOException, SolrServerException, ParseException {

        ArrayList<String> hs = new ArrayList<>();
       

        hs.addAll(makeCompLabelsPrevision("A", 9));
        hs.addAll(makeCompLabelsPrevision("B", 6));
        hs.addAll(makeCompLabelsPrevision("C", 4));
        hs.addAll(makeCompLabelsPrevision("D", 12));
        hs.addAll(makeCompLabelsPrevision("E", 9));
        previsions=new Table();
        previsions.setHeaders(hs);
        solr2Ecfs();
        writeTable("resources/previsions.csv");
        
        
        return data0;

    }

     protected void writeTable(String filename) throws IOException{
    CsvWriter cw=new CsvWriter();
            
            cw.setFileName(filename);
            if (previsions!=null)
            cw.writeTableWithNames(previsions);
            
    }
    
        protected void writeTablesCsv() throws IOException {
             CsvWriter cw=new CsvWriter();
            for (String ref:refs){
            cw.setFileName(FILE_MATRIX_PREFIX+ref+".csv");
            Table t=data0.get(ref);
            if (t!=null){
            cw.writeTableWithNames(t);
            }
            
            
            }
    }
    
    

   
 abstract void solr2Ecfs()  throws IOException, SolrServerException, ParseException;

    

    public HashMap<String, Double> attributes(SolrDocument document) {

        HashMap<String, Double> res = new HashMap<>();
        String id = (String) document.get("id");
        String type = (String) document.get("type");
        //Obj.showTypeOf(cert.get("term"));
        //ArrayList<String> terms = (ArrayList<String>) document.get("term");
        //ArrayList <EcfCert> ecfs=(ArrayList <EcfCert>)cert.get("EcfCert");
        ArrayList<String> comps = (ArrayList<String>) document.get("competence_");

        if (type.equals("ICT_profile") && comps != null) {
            //Out.p(comps + " " + comps.size());
            int n = comps.size();
            double nd = Numbers.int2double(n);
            double val = 1 / nd;
            for (String comp : comps) {

                String[] parts = comp.split(" ");
                if (parts.length > 0) {
                    String id0 = parts[0].trim() + PREVISION_SUFIX;
                    //There are some repited competences (the same but with diferent level)
                    if (res.containsKey(id0)) {
                        val += res.get(id0);
                    }
                    res.put(id0, val);

                    //Out.p(parts[0]+"-->"+val);
                }
            }
        } else if (type.equals("competence")) {
            res.put(id.substring(0, 3) + PREVISION_SUFIX, 1.0);
        }
        
        return res;
    }
   

 protected ArrayList<String> makeCompLabelsPrevision(String letter, int max) {
        ArrayList<String> res = new ArrayList<>();

        for (int i = 1; i <= max; i++) {
            res.add(letter + "." + i + PREVISION_SUFIX);
            //res.add();
        }

        return res;
    }

 

}
