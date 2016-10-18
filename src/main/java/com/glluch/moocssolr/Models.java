/*
 * Copyright (C) 2016 Guillem LLuch Moll guillem72@gmail.com
 *
 * This program iss free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program iss distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.glluch.moocssolr;

import com.glluch.utils.Out;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class Models {

    private HashMap<String, Classifier> classifiers = new HashMap<>();
    private String instancesFileName = "resources/previsions.csv";
    private double threshold = 0.9;

    public HashMap<String, Classifier> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(HashMap<String, Classifier> classifiers) {
        this.classifiers = classifiers;
    }

    public void put(String id, Classifier c) {
        classifiers.put(id, c);
    }

    public Classifier get(String id) {
        return classifiers.get(id);
    }

    /**
     * When classifiers and instances are set, guess the classes for each instance (a MOOC)
     * @return mooc -&gt; ( Guess_competence1, Guess_competence2,... )
     * @throws Exception
     */
    public HashMap<String, ArrayList<String>> chosePrevisions() throws Exception {
        HashMap<String, ArrayList<String>> res = new HashMap<>();
        HashMap<String, HashMap<String, Double>> previsions = getPrevisions();
        Set moocs = previsions.keySet();
        for (Object mooc0 : moocs) {
            String mooc = (String) mooc0;
            ArrayList<String> cho = chosePrevision(previsions.get(mooc));
            res.put(mooc, cho);
        }

        return res;
    }

    /**
     * Given a map competence -&gt; guess_value, return the best competences.
     * It return a list of competences which value is above a threshold or the list of the competences
     * with the best value found.
     * @param p a map competence -&gt; guess_value
     * @return a list with the guessed competences
     */
    protected ArrayList<String> chosePrevision(HashMap<String, Double> p) {
        ArrayList<String> res = new ArrayList<>();
        //Out.p("chosePrevison:");
        //Out.stringdoublemap(p);
        ArrayList<String> maxs = new ArrayList<>();
        double max = 0.0;
        Set comps = p.keySet();
        for (Object comp0 : comps) {
            String comp = (String) comp0;
            double val = p.get(comp);
            if (val > max) {
                max = val;
                maxs = new ArrayList<>();
                maxs.add(comp);
            }
            else {if (val==max){
                maxs.add(comp);
            }}
            if (val>=threshold){
                res.add(comp);
            }
        }
        if (res.isEmpty()) res.addAll(maxs);
         if (res.isEmpty()) res=null;
         //Out.p("Return = "+res.toString());
        return res;
    }

    public HashMap<String, HashMap<String, Double>> getPrevisions() throws IOException, Exception {
        HashMap<String, HashMap<String, Double>> previsions = new HashMap<>();
       Instances iss1 = readCsvFile();//only used to get the name of the instance
       Instances iss = readCsvFile();

      
        iss.deleteAttributeAt(0);
        Attribute att = new Attribute("Classe", 0);
        iss.insertAttributeAt(att, 0);
        // iss.renameAttribute(0, "Maybe");

        iss.setClassIndex(0);
        for (int i = 0; i < iss1.size(); i++) {
            Instance is1 = iss1.get(i);
            Instance is0 = iss.get(i);
            String name;
            name = is1.toString(0);
            Out.p(name);
            HashMap<String, Double> probas = getProbabilities(is0);
            previsions.put(name, probas);
            Out.p(probas);
        }

        return previsions;
    }

    protected void debugInstance(Instance is) {
        Out.p(is.toString(0) + ", atribs: " + is.numAttributes() + ", classes=" + is.numClasses());
        String mes = "";
        Instances dataset = is.dataset();
        Out.p(dataset.toSummaryString());
        for (int i = 1; i < is.numAttributes(); i++) {
            mes += ", " + is.toString(i);

        }
        Out.p(mes);
    }

    
    
    protected HashMap<String, Double> getProbabilities(Instance is) throws Exception {
        HashMap<String, Double> probas = new HashMap<>();
        Set cnames = classifiers.keySet();
        for (Object cname0 : cnames) {
            String cname = (String) cname0;
            Classifier c = classifiers.get(cname);
            //double[] c.distributionForInstance(iss);
            double prob = c.classifyInstance(is);
            probas.put(cname, prob);
        }
        return probas;
    }

    private Instances readCsvFile() throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(instancesFileName));
        Instances data = null;
       // loader.
       try{
        
            data = loader.getDataSet();
       }
       catch (Error e){
           
       }

        //Out.p("Instances retrieved: "+System.lineSeparator()+data.toSummaryString());
        return data;
    }
    
    private Instances readArffFile() throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(
                              new FileReader("resources/previsions.arff"));
        Instances data = new Instances(reader);
        reader.close();
        return data;
    }

    private Instances readCsvFile(File fileName) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(fileName);
        Instances data = loader.getDataSet();
        return data;
    }

}
