package com.glluch.mooclodecf.moocssolr;

import com.glluch.utils.CsvWriter;
import com.glluch.utils.Out;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class Main {

    public static void main(String[] args) throws IOException, ParseException, SolrServerException {
        //map()   ;
        //simpleMap();
        //logtest();
        //solr();
  
        //testcsv();
        testgetCertis                ();

    }

    private static void map() throws IOException, ParseException {
        HashMap<String, HashMap<String, Double>> map = MoocsReader.readDir();
        Out.smsd(map);
    }

    private static void simpleMap() throws IOException, ParseException {
        HashMap<String, HashMap<String, Double>> map = MoocsReader.readDir();
        Set keys = map.keySet();
        for (Object key0 : keys) {
            String title = (String) key0;
            Out.p(title + " " + map.get(title).size());

        }
    }

    private static void logtest() {
        System.out.println(System.getProperty("user.dir"));
        PropertyConfigurator.configure("logging.conf");
        Logger logger = LoggerFactory.getLogger("superLogger");
        logger.info("Logging for the first Time!");
        logger.warn("A warning to be had");
        logger.error("This is an error!");
    }

    private static void solr() throws IOException, ParseException, SolrServerException {
        HashMap<String, HashMap<String, Double>> map = MoocsReader.readDir();
        Set keys = map.keySet();
        SolrQ sq = new SolrQ();

        for (Object key0 : keys) {
            String title = (String) key0;
            SolrDocumentList comps;
            comps = sq.buildAndExecute(map.get(title));
            //Out.p(title+" "+map.get(title).size());
            for (SolrDocument comp : comps) {
                //Set compskeys=comp.entrySet();
                Collection <String> compskeys=comp.getFieldNames();
                Out.p(comp.get("id")+" "+comp.get("type"));
            }
        }

    }

    private static void testcsv() throws IOException {
              CsvWriter cw=new CsvWriter();
        ArrayList<String> h=new ArrayList <>();
        h.add("A1");
        h.add("A2");
        cw.writeHeader(h);
        ArrayList<Double> n=new ArrayList<>();
        n.add(.4);
        n.add(1.0);
        cw.writeRow(n);
        n=new ArrayList<>();
         n.add(.46);
        n.add(0.0);
        cw.writeRow(n);
    }

    private static void testgetCertis() throws IOException, SolrServerException, ParseException {
        Certs certs=new Certs();
        //ArrayList<String> heads=certs.buildHeaders();
        //Out.p(heads);
        certs.solr2certis();
         //Real compentences:
        //HashMap<String, ArrayList<String>> comps=cert.compentences();
        //Out.p(comps);
        
        //Attributes:
        
        
        
    }

}
