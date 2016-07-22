
package com.glluch.moocssolr;

import com.glluch.certisparser.Certification;
import com.glluch.certisparser.EcfCert;
import com.glluch.certisparser.JReader;
import com.glluch.utils.CsvWriter;
import com.glluch.utils.JMap;
import com.glluch.utils.Numbers;
import com.glluch.utils.Out;
import com.glluch.utils.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class Certs {

    //private final String CERTIS = "type:Certification";
    private final String DOCS_TO_RETRIEVE = "10"; //To send to solr a string is nedeed.
    //private final String CERTS_LIMIT="5";//for testing next to 5, a big number otherwise(10000);
    protected final String PROFILES_TO_RETRIEVE = "3";
    private final String PREVISION_SUFIX = "_prevision";
    private final Double GENERAL_SUPPORT = 1.0;
    private final Double PARTIAL_SUPPORT = 0.5;
    private final Double UNKNOWN_SUPPORT = 1.0;
    private final Double SUPERFICIAL_SUPPORT = 0.15;
    private HashMap<String, Table> certs;
    private HashMap<String, Double> suport_values = new HashMap<>();
    private String FILE_MATRIX_PREFIX="resources/ml/";
    private ArrayList <String> refs=new ArrayList<>();

    public Certs() {
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
        
        certs = makeTables(hs, "A", 9);
        certs.putAll(makeTables(hs, "B", 6));
        certs.putAll(makeTables(hs, "C", 4));
        certs.putAll(makeTables(hs, "D", 12));
        certs.putAll(makeTables(hs, "E", 9));
        solr2certis();
        writeTablesCsv();
        
        
        return certs;

    }

        private void writeTablesCsv() throws IOException {
             CsvWriter cw=new CsvWriter();
            for (String ref:refs){
            cw.setFileName(FILE_MATRIX_PREFIX+ref+".csv");
            Table t=certs.get(ref);
            if (t!=null){
            cw.writeTableWithNames(t);
            }
            
            
            }
    }
    
    
    private HashMap<String, Table> makeTables(final ArrayList<String> headers0, String letter, int max) throws IOException {
        HashMap<String, Table> res = new HashMap<>();
       
        for (int i = 1; i <= max; i++) {
            //res.add(letter + "." + i);
            //res.add();
            ArrayList<String> headers = (ArrayList<String>) headers0.clone();
            headers.add(letter + "." + i);
            Table t = new Table();
            t.setHeaders(headers);
            res.put(letter + "." + i, t);
            refs.add(letter + "." + i);
            //Out.p(letter + "." + i);
            //cw.setFileName(FILE_MATRIX_PREFIX+letter+"."+i);
            //cw.writeTableWithNames(t);
            
        }
        
        return res;
    }

    public void solr2Profiles() throws IOException, ParseException, SolrServerException{
    ArrayList<Certification> certss = JReader.readDir(); //com.glluch.certisparser.JReader;
        SolrDocumentList docs;
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", "type:ICT_profile");
        sq.setCustom("rows", PROFILES_TO_RETRIEVE);
        for (Certification cer : certss) {
            Out.p();
            Out.p(cer.getTitle());
            
            HashMap<String, Double> targets=tags(cer);
            Out.p(targets);
            HashMap<String, Double> hm = cer.getTerms();
            docs = sq.buildAndExecute(hm);//list of related (with cer) docs 
            ArrayList <String> profiles=new ArrayList<>();
            for (SolrDocument docu : docs) {
                
                 profiles.add((String) docu.get("id"));
               
            }
            Out.p(profiles);                           
       
        }
    }
    
    
    public void solr2certis() throws IOException, SolrServerException, ParseException {
        ArrayList<Certification> certss = JReader.readDir(); //com.glluch.certisparser.JReader;
        SolrDocumentList docs;
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", "type:ICT_profile OR type:competence");
        sq.setCustom("rows", DOCS_TO_RETRIEVE);
        for (Certification cer : certss) {
            Out.p();
            Out.p(cer.getTitle());
            
            HashMap<String, Double> targets=tags(cer);
            Out.p(targets);
            HashMap<String, Double> hm = cer.getTerms();
            docs = sq.buildAndExecute(hm);//list of related (with cer) docs 
            HashMap<String, Double> prevision = new HashMap<>();
            for (SolrDocument docu : docs) {
                //get the competences included in docs
                HashMap<String, Double> attrib = attributes(docu);
                prevision = JMap.intersectAndSum(prevision, attrib);
                //Out.p(attrib);
            }
            Out.p(prevision);
           Set targetss=targets.keySet();
           for (String targ:refs){
               Table co=certs.get(targ);
               if (co!=null){
                   HashMap<String, Double>  prev=(HashMap<String, Double>)prevision.clone();
                   if (targetss.contains(co)){
                       prev.put(targ, targets.get(targ));
                   }
                   else {prev.put(targ, 0.0);}
                   co.addRow(cer.getTitle(),prev);
               }
           }
           
       
        }

    }

    private HashMap<String, Double> tags(Certification cert) {
        HashMap<String, Double> res = new HashMap<>();
        ArrayList<EcfCert> ecfs = cert.getEcfs();
        for (EcfCert ecf : ecfs) {
            String suport = ecf.getSupport();
            if (suport_values.containsKey(suport)) {
                res.put(ecf.getCompetenceCode(), suport_values.get(suport));
            }
        }
        return res;
    }

    public HashMap<String, Double> attributes(SolrDocument document) {

        HashMap<String, Double> res = new HashMap<>();
        String id = (String) document.get("id");
        String type = (String) document.get("type");
        //Obj.showTypeOf(cert.get("term"));
        ArrayList<String> terms = (ArrayList<String>) document.get("term");
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
   

 private ArrayList<String> makeCompLabelsPrevision(String letter, int max) {
        ArrayList<String> res = new ArrayList<>();

        for (int i = 1; i <= max; i++) {
            res.add(letter + "." + i + PREVISION_SUFIX);
            //res.add();
        }

        return res;
    }


}
