package TitanicAnalyseClasse;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.io.NullWritable;
//import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
//import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;



import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.mapreduce.Mapper;






public class TitanicAnalyseClasseReducer extends Reducer<Text, Text, Text, Text> {
	
	private Text resultat = new Text();
	
	//La classe étend la classe Reducer de Hadoop. Cela signifie qu'elle doit implémenter la méthode 
	//reduce qui est utilisée pour traiter les paires clé/valeur issues de la phase de mappage.

	@Override
	//public void reduce(Text key, Iterable<IntWritable> values,
	public void reduce(Text key, Iterable<Text> values,
	    Context context)
	    throws IOException, InterruptedException {
	// Cette ligne initialise maxValue avec la plus petite valeur possible d'un entier (Integer.MIN_VALUE). 
	//Cela sert de base pour comparer les températures et trouver la température maximale.   
	  //int maxValue = Integer.MIN_VALUE;
	  // essayer aussi avec IntWritable , essayer de remplacer avec text aussi 
	  // IntWritable statistique = new statistique();
	  int statistique ;
	  
	//Une boucle qui parcourt toutes les températures (IntWritable) associées à l'année donnée
	//À chaque itération, on compare la température actuelle (value.get()) à la température maximale 
	//enregistrée jusqu'à présent (maxValue). La méthode Math.max() renvoie la plus grande des deux valeurs,
	//et on met à jour maxValue avec cette température maximale.

	  int nombreHomme = 0; 
	  int nombreFemme = 0; 
	  int nombreTotalVoyageur = 0;
	  int survivant = 0; 
      int nombreClass1  = 0;
      int nombreClass2 = 0;
      int nombreClass3 = 0;
      double totalAge = 0;
      int nombreAge = 0;
      int totalGrandeurFamille = 0;
      
      double totalFare = 0.0;
      int fareCount = 0;
      double minFare = Double.MAX_VALUE;
      double maxFare = Double.MIN_VALUE;
      int totalFamilySize = 0;
	  
	  /*
	  for (IntWritable value : values) {
	    maxValue = Math.max(maxValue, value.get());
	  }
	  */
	  
	  
	  
	  //for (IntWritable value : values) {
	  for (Text value : values) {
		  //for (Text key : keys) {
		  
		   
          String[] data = value.toString().split(",");
          if (data.length >= 7) {
        	  nombreTotalVoyageur++;
        	  
        	  
              
              // Survie
              if (data[0].equals("1")) {
            	  survivant++;
              }
              
              // Classe
              int pclass = Integer.parseInt(data[1]);
              if (pclass == 1) nombreClass1++;
              else if (pclass == 2) nombreClass2++;
              else if (pclass == 3) nombreClass2++;
              
              // Sexe
              if (data[2].equals("male")) {
            	  nombreHomme++;
              } else {
            	  nombreFemme++;
              }
              
 
 
              
              // Âge
              if (!data[3].equals("Unknown")) {
                  try {
                      totalAge += Double.parseDouble(data[3]);
                      nombreAge++;
                  } catch (NumberFormatException e) {
                      // Ignorer
                  }
              }
              
           // Tarif
              try {
                  double fare = Double.parseDouble(data[4]);
                  if (fare > 0) {
                      totalFare += fare;
                      fareCount++;
                      minFare = Math.min(minFare, fare);
                      maxFare = Math.max(maxFare, fare);
                  }
              } catch (NumberFormatException e) {
                  // Ignorer
              }
              
              // Taille de famille
              try {
            	  totalGrandeurFamille += Integer.parseInt(data[5]);
              } catch (NumberFormatException e) {
                  // Ignorer
              }
              
 
          }
      
	  
               
          /*    
		  nombreTotalVoyageur ++;
		  
          int survecu = Integer.parseInt(value.toString());
          if (survecu == 1) {
        	  survivant++;
          }
          
          */
        	  
        	  
		  
		  /*
     	  if(value.get() == 1){
			  
			  survivant ++;
		  }
		  if(value.get() == 1 && key.get() == male){
			  survieFemme = +1;
		  }else
		  {
			  survieHomme = +1;
		  } 
		  */
	  }
	  
	  
      // Calculer le taux de survie
      double tauxDeSurvie = nombreTotalVoyageur > 0 ? 
          (double) survivant / nombreTotalVoyageur * 100 : 0;
      
          
          /*
      // Formater le résultat
      String resultString = String.format("Passagers: %d, Survivant: %d, Taux de Survie: %.1f%%", 
    		  nombreTotalVoyageur, survivant, tauxDeSurvie);
      
      resultat.set(resultString);
      context.write(key, resultat);
      */
      
    
      
      
      // Calculer les statistiques
      double tauxSurvie = nombreTotalVoyageur > 0 ? (double) survivant / nombreTotalVoyageur * 100 : 0;
      double moyenneAge = nombreAge > 0 ? totalAge / nombreAge : 0;
      double moyenneTailleFamille = nombreTotalVoyageur > 0 ? (double) totalGrandeurFamille / nombreTotalVoyageur : 0;
      double avgFare = fareCount > 0 ? totalFare / fareCount : 0;
     
      
      if (minFare == Double.MAX_VALUE) minFare = 0;
      if (maxFare == Double.MIN_VALUE) maxFare = 0;
   
      
      /*
      // Formater le résultat
      String resultString = String.format(
          "Count: %d | Survecu: %d (%.1f%%) | M/F: %d/%d | Class1/2/3: %d/%d/%d | AvgAge: %.1f | AvgFamSize: %.1f",
          nombreTotalVoyageur, survivant, tauxSurvie, 
          nombreHomme, nombreFemme, 
          nombreClass1, nombreClass2, nombreClass3,
          moyenneAge, moyenneTailleFamille
      );
      */
      
      String resultString = String.format(
              "Count: %d | Survived: %d (%.1f%%) | M/F: %d/%d | AvgFare: £%.2f | FareRange: £%.2f-£%.2f | AvgFamSize: %.1f",
              nombreTotalVoyageur, survivant, tauxSurvie,
              nombreHomme, nombreFemme,
              avgFare, minFare, maxFare, moyenneTailleFamille
          );
      
      resultat.set(resultString);
      context.write(key, resultat);      
      
 
	  
	  
	  /*
	  // taux de survie	  
	  double tauxSurvie = nombreTotalVoyageur > 0 ? (double) survivant / nombreTotalVoyageur * 100 : 0;
	  
	  // resultat
	  String resulatPrint = String.format("Total: %d, Survivant: %d, Pourcentage: %.2f%%", nombreTotalVoyageur, survivant,tauxSurvie );
	  resultat.set(resulatPrint);
	  
	  //en effet key represente soit femme ou homme
	  context.write(key,resultat);
	  
	  */
	  
	  
	  /*
	  context.write(key, new IntWritable(survieFemme));
	  #context.write(key, new IntWritable(survieHomme));
	  
	  #context.write(key, new IntWritable(maxValue));
	  */
	  
	  
	  


	  
	}
	

}

 

 


 
 


