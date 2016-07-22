package com.glluch.moocssolr;

import com.glluch.utils.CsvWriter;
import com.glluch.utils.Out;
import com.glluch.utils.Table;
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

    public static void main(String[] args) throws IOException, ParseException, SolrServerException, Exception {
        //map()   ;
        //simpleMap();
        //logtest();
        //solr();
  
        //testcsv();
       //testgetCertis ();
       //testClassifiers();
       profiles();
       

    }

    
    private static void profiles() throws IOException, ParseException, SolrServerException {
        Certs certs=new Certs(); 
        certs.solr2Profiles();
        //Moocs moocs=new Moocs();
        // moocs.solr2Profiles();
    }
    
    
    private static void map() throws IOException, ParseException, SolrServerException {
        //HashMap<String, HashMap<String, Double>> map = MoocsReader.readDir();
        //Out.smsd(map);
        Moocs moocs=new Moocs();
        moocs.buildTables();
        //moocs.solr2Ecfs();
        
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
      
        HashMap<String, Table> ts=certs.buildTables();
        Table t1=ts.get("A.3");
        Out.p(t1.getHeaders());
         
        
        
        
    }

    private static void testClassifiers() throws IOException, Exception {
        //Classifiers cls=new Classifiers();
        //cls.init();
        //cls.linear();
        //cls.gauss();
        //cls.evaluateLinear();
        //cls.evaluateNeigh();
        Trainer t=new Trainer();
        Models ms=t.buildModels();
        HashMap<String, HashMap<String, Double>> previsions = ms.getPrevisions();
        Out.smsd(previsions);
        HashMap<String, ArrayList<String>> chosePrevisions = ms.chosePrevisions();
        Out.msas(chosePrevisions);
    }

    

    

}
