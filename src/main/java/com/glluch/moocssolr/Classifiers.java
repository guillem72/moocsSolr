
package com.glluch.moocssolr;

import com.glluch.utils.Out;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class Classifiers {
   
    private Instances data=null;
   
    private Classifier classifier;
   
    
    private int folds=10;

    public Instances getData() {
        return data;
    }

    public Classifier getClassifier() {
        return classifier;
    }
    
    
   public void debugData(){
       //Out.p(data.toSummaryString());
     
       for (int i=0; i<data.size();i++){
             String miss="";
           Instance ins=data.get(i);
           for (int j=0;j<ins.numAttributes();j++){
           Attribute at=ins.attribute(j);
           miss+=" "+at.name()+" "+ins.toString(at)+System.lineSeparator();
           }
            Out.p(miss);
       }
       
    
       
       
   }
    
    public void init(File fileName) throws IOException, Exception{
        //Out.p(fileName.getName());
        readCsvFile( fileName);
           
       data.deleteAttributeAt(0);
       //standarize();
       normalize();
       guessTargetIndex();    
       //data.attribute(2).
       //Out.p(data.toSummaryString());
    }
    
    public void init(String fileName) throws IOException, Exception{
        
        readCsvFile( fileName);
        //Out.p(fileName);
        
        data.deleteAttributeAt(0);
        //standarize();
       normalize();
       guessTargetIndex();
    }
    
    public void init() throws IOException, Exception{
        
        readCsvFile("resources/instances.csv");
       
        data.deleteAttributeAt(0);
       //standarize();
       normalize();
       guessTargetIndex();
    }
    
    public double evaluateNeigh() throws Exception{
        Classifier ibk = new IBk();	
        
         
        Evaluation eval = new Evaluation(data);
    eval.crossValidateModel(ibk, data, folds, new Random(1));
   //Out.p(data.get(guessTargetIndex()));
   
    //Out.p(eval.toSummaryString());
        //ibk.buildClassifier(data);
       return eval.correlationCoefficient();
    }
    
    public void evaluateLinear() throws Exception{
        Out.p(data.numInstances());
        int folds=2;
        
     LinearRegression lr=new LinearRegression();
     Evaluation eval = new Evaluation(data);
    eval.crossValidateModel(lr, data, folds, new Random(1));
    Out.p(eval.toSummaryString());
    }
    
    public Classifier Neigh() throws Exception{
    Classifier ibk = new IBk();
    ibk.buildClassifier(data);
    return ibk;
    }
    
    public Classifier linear() throws Exception  {
       
       LinearRegression lr=new LinearRegression();
       
       lr.buildClassifier(data);
       return lr;
    }
    
    public Classifier gauss() throws Exception{
        GaussianProcesses gp=new GaussianProcesses();
        gp.buildClassifier(data);
        return gp;
    }
    
    private void readCsvFile(String fileName) throws IOException{
         CSVLoader loader = new CSVLoader();
    loader.setSource(new File(fileName));
    data = loader.getDataSet();
    }
    
    private void readCsvFile(File fileName) throws IOException{
         CSVLoader loader = new CSVLoader();
    loader.setSource(fileName);
    data = loader.getDataSet();
    }
    
    private int guessTargetIndex() {
        int pos=0;
        if (data==null)
        {
          return -1;
        }
        boolean found=false;
        while (!found && pos<data.numAttributes()-1){
        
        String[] parts=data.attribute(pos).name().split("_");
        if (parts.length==1){
           //Out.p("Index found at "+pos+" = "+parts[0]);
           found=true;
        }
        pos++;
        }
        data.setClassIndex(pos);
        return pos;
    }

    protected void standarize(){
        
         int datasize=data.size();
        for (int i=0;i<datasize;i++){
           Instance element=data.remove(0);//always 0 because the instances move to the left
           
        }
    }
    
    private void normalize() throws Exception {
        
        //Standardize st=new Standardize();
        Normalize st=new Normalize();
        st.setInputFormat(data);
        int datasize=data.size();
        for (int i=0;i<datasize;i++){
           Instance element=data.remove(0);//always 0 because the instances move to the left
            st.input(element);
        }
      
        while (!st.batchFinished()){;}
       
        while(st.numPendingOutput()>0)
         {
            data.add(st.output());
        }
        
    }
}
