/*
 * Copyright (C) 2016 Guillem LLuch Moll guillem72@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.glluch.moocssolr;

import com.glluch.utils.CsvWriter;
import com.glluch.utils.JMap;
import com.glluch.utils.Out;
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
public class Moocs extends IEEE2ecfs {

    protected HashMap<String, HashMap<String, Double>> map;

    private HashMap<String, HashMap<String, Double>> toIEEE() throws IOException, ParseException {
        map = MoocsReader.readDir();
        //Out.smsd(map);
        return map;
    }

    void solr2Profiles() throws IOException, ParseException, SolrServerException{
         SolrDocumentList docs;
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", "type:ICT_profile");
        sq.setCustom("rows", PROFILES_TO_RETRIEVE);
        if (map == null) {
            toIEEE();
        }
        Set names = map.keySet();
        for (Object name0 : names) {
            String name = (String) name0;
            Out.p(name);
           ArrayList <String> profiles=new ArrayList<>();
            docs = sq.buildAndExecute(map.get(name));

            
            for (SolrDocument docu : docs) {
                //get the competences included in docs
                
               profiles.add((String) docu.get("id"));
               
                //Out.p(attrib);
            }
           
            Out.p(profiles);
           
        }//for (Object name0:names)
        //Out.p(refs);
    
    }


    @Override
    void solr2Ecfs() throws IOException, SolrServerException, ParseException, NullPointerException {
        SolrDocumentList docs;
        SolrQ sq = new SolrQ();
        sq.setCustom("fq", "type:ICT_profile OR type:competence");
        sq.setCustom("rows", DOCS_TO_RETRIEVE);
        if (map == null) {
            toIEEE();
        }
        Set names = map.keySet();
        for (Object name0 : names) {
            String name = (String) name0;
            Out.p(name);
           
            docs = sq.buildAndExecute(map.get(name));

            HashMap<String, Double> prevision = new HashMap<>();
            for (SolrDocument docu : docs) {
                //get the competences included in docs
                HashMap<String, Double> attrib = attributes(docu);
                prevision = JMap.intersectAndSum(prevision, attrib);
                //Out.p(attrib);
            }
            previsions.addRow(name, prevision);
            Out.p(prevision);
        }//for (Object name0:names)
        //Out.p(refs);
    }
    
    
   
    
    protected void writeTable() throws IOException{
    CsvWriter cw=new CsvWriter();
            
            cw.setFileName(FILE_MATRIX_PREFIX+"previsions.csv");
            if (previsions!=null)
            cw.writeTableWithNames(previsions);
            
    }

}
