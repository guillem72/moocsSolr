# moocsSolr-
moocs  to e-cf mapping

## Training

From certifications. A json file for each certification is placed in *resources/cert/* //TODO especify the certs project.
The method **buildModels** in the **Trainer** class is responbible to build a list of weka classifiers.
This is done following this work flow:

1. Read each file in the directori, using *org.apache.commons.lang3.StringUtils*.
2. For each file build and inicialize a *Classifiers*.
3. Add this classifier in a models. This models, internally, 
consist of a HashMap NameOfTheCompetence -> ClassifierForThatCompetence 



## Prediccions

One the models are built, guessing the competences are possible. For that,  these steps are made:

        Trainer t=new Trainer();
        Models ms=t.buildModels();
        HashMap<String, ArrayList<String>> chosePrevisions = ms.chosePrevisions();
        
The map *chosePrevisions* contains mooc -&gt; ( Guess_competence1, Guess_competence2,... )
It is possible to obtain the prevision for each competence with:

    HashMap<String, HashMap<String, Double>> previsions = ms.getPrevisions();
