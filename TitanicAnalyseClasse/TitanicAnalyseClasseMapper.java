package TitanicAnalyseClasse;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


//public class TitanicClassAgeMapper extends Mapper<LongWritable, Text, Text, IntWritable>  {
public class TitanicAnalyseClasseMapper extends Mapper<LongWritable, Text, Text, Text>  {
    private NcdcRecordParser parser = new NcdcRecordParser();
    private Text outputkey = new Text();
    //private IntWritable outputvalue = new IntWritable();
    private Text outputvalue = new Text();
    
    
    private Text cleSegmente = new Text();
    private Text InfoPassenger = new Text(); 
    private Text typeFamille = new Text();
    private Text SegmentEconomique = new Text();
    
     
    @Override
    protected void map(LongWritable key, Text value,
        Context context) throws IOException, InterruptedException {
String line = value.toString().trim();
        
        // Ignorer la ligne d'en-tête
        if (line.startsWith("PassengerId") || line.isEmpty()) {
            return;
        }
        
        try {
            // Parser le CSV (attention aux virgules dans les guillemets)
            String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            if (fields.length >= 11) {
        
           
      
      //parser.parse(value);
      //if (parser.isValidTemperature()) {
    	  
        ///*[*/context.write(new IntPair(parser.getYearInt(),
            //parser.getAirTemperature()), NullWritable.get());/*]*/ 
        
        ///*[*/context.write(new Text(parser.getYearInt()), 
        		//new IntWritable(parser.getAirTemperature()));/*]*/
    	  
    	  /*
    	 String year = parser.getYear();
    	 int temperature = parser.getAirTemperature();
    	 
    	  
    	 String sexe = parser.getSexe();
    	     	 
      
    	 int survie = parser.getSurvie();

    	 int classe = parser.getClasse();
 
    	 int age = parser.getAge();
    	 
    	 int sibSp = parser.getSisp();
    	 
    	 int parch = parser.getParch();
    	 
    	 String sexe = parser.getSexe();
    	 
    	 String nom = parser.getNom();

    	 String ticket = parser.getTicket();
    	 
    	 String fareStr = parser.getFare();
    	 
    	 String cabin = parser.getCabin();
    	 
    	 String embarked = parser.getEmbarked();
 
    	 */
            	
    	 //Text outputkey = new Text();
    	 
    	     
    	 
     
    	    


          String sexe = fields[4].replace("\"", "").trim(); // Colonne Sex
          int survie = Integer.parseInt(fields[1].trim());   // Colonne Survived
     	  int classe = Integer.parseInt(fields[2].trim());    
     	  int age = Integer.parseInt(fields[5].trim());
     	  int sibSp = Integer.parseInt(fields[6].trim());
     	  int parch = Integer.parseInt(fields[7].trim());
     	  String nom = fields[3].trim();
     	  String ticket = fields[8].trim();
     	  String fareStr = fields[9].trim();
     	  String cabin = fields[10].trim();
     	  String embarked = fields[11].trim();
     	  
    	 /*
    	 outputkey.set(String.valueOf(classe));
    	 outputvalue.set(String.valueOf(age));
    	 */
    	 
         String ageIntervalle;
         ageIntervalle = getAgeGroup(age);
         
         /*
         if (ageIntervalle == null ||ageIntervalle ) {
             ageGroup = "Unknown";
         } else {
             double age = Double.parseDouble(ageStr);
             ageIntervalle = getAgeGroup(age);
         }
         
         */
         
    	 
    	 /*
         String ageIntervalle;
         if (age.isEmpty()) {
             ageGroup = "Unknown";
         } else {
             double age = Double.parseDouble(ageStr);
             ageIntervalle = getAgeGroup(age);
         }
         
         */
         
         
         /*
        
         // Créer la clé composite Classe-Âge
         String cle_composite = "Class" + classe + "_" + ageIntervalle;
         cleSegmente.set(cle_composite);
         
         // Valeur: survived status pour calculer les statistiques
         InfoPassenger.set(survie + "");
         
         
         */
         
         
         // Traiter le tarif
         double fare = 0.0;
         if (!fareStr.isEmpty()) {
             fare = Double.parseDouble(fareStr);
         }
         
         

         /*
         // Calculer la taille de la famille
         int grandeurFamille = sibSp + parch + 1; // +1 pour le passager lui-même
         
         // Déterminer le type de famille
         String categorieFamille = getCategoryFamille(grandeurFamille, sibSp, parch);
         
         // Déterminer le rôle dans la famille basé sur le nom et l'âge
         String roleFamille = getRoleFamille(nom, sexe, ageIntervalle, sibSp, parch);
         
         // Créer la clé composite
         String cle_composite = categorieFamille + "_" + roleFamille;
         typeFamille.set(cle_composite);
         
         // Données du passager: survived,pclass,sex,age,familySize
         String info = String.format("%d,%d,%s,%s,%d", 
                                    survie, classe, sexe, 
                                    ageIntervalle.isEmpty() ? "Unknown" : age,  //cherche l'explication
                                    		grandeurFamille);
         
         InfoPassenger.set(info);
         context.write(typeFamille, InfoPassenger);
         
         */
        
         
         // Déterminer la catégorie économique
         String categorieEconomie  = getCategorieEconomie(classe, fare);
         
         // Analyser le type de cabine
         String typeCabine = getTypeCabine(cabin);
         
         // Analyser le port d'embarquement (indicateur économique)
         String categoriePort = getCategoriePort(embarked);
         
         // Créer la clé composite
         String cle_composite  = categorieEconomie + "_" + typeCabine + "_" + categoriePort;
         //economicSegment.set(cle_composite);
         SegmentEconomique.set(cle_composite);
         
         // Données: survived,pclass,sex,age,fare,familySize,ticketType
         int grandeurFamille = sibSp + parch + 1;
         String typeTicket = getTypeTicket(ticket);
         
         String info = String.format("%d,%d,%s,%s,%.2f,%d,%s", 
                                    survie, classe, sexe, 
                                    ageIntervalle.isEmpty() ? "Unknown" : age, 
                                    fare, grandeurFamille, typeTicket);
         
         InfoPassenger.set(info);
         context.write(typeFamille, InfoPassenger);
          }
            } catch (Exception e) {
                System.err.println("Erreur parsing ligne: " + line + " - " + e.getMessage());
            }

         
          
         
         //context.write(cleSegmente, InfoPassenger);

    	 
    	 
    	 
    	 /*
    	  * 
 		 outputkey.set(sexe);
    	 outputvalue.set(survie);
    	 
    	 outputkey.set(year);
    	 outputvalue.set(temperature);
    	 context.write(outputkey, outputvalue);
    	 */
    	 
    	 
    	 
        
      //}
    }
    
    private String getAgeGroup(double age) {
        if (age < 13) {
            return "Enfant";
        } else if (age < 20) {
            return "Ado";
        } else if (age < 35) {
            return "Jeune";
        } else if (age < 55) {
            return "MoyenAge";
        } else if(age >= 55){
            return "Viellard";
        } else {
            return "Age manquant ou non connu";
        }
    }
    
    
    
    
      
    
    private String getCategoryFamille(int grandeurFamille, int sibSp, int parch) {
        if (grandeurFamille == 1) {
            return "Seul";
        } else if (grandeurFamille == 2) {
            return "Petit";
        } else if (grandeurFamille <= 4) {
            return "Moyen";
        } else {
            return "Grande";
        }
    }
    
    
    
    private String getRoleFamille(String nom, String sexe, String ageIntervalle, int sibSp, int parch) {
        // Extraire le titre du nom
        String title = extraireTitre(nom);
        
        if (!ageIntervalle.isEmpty()) {
            try {
                double age = Double.parseDouble(ageIntervalle);
                
                // Enfant
                if (age < 18) {
                    return "Enfant";
                }
                
                // Adulte avec enfants
                if (parch > 0 && age >= 25) {
                    return "Parent";
                }
                
                // Marié sans enfants ou avec conjoint
                if (sibSp > 0) {
                    return "Epoux";
                }
                
                // Adulte seul
                return "Adulte";
                
            } catch (NumberFormatException e) {
                // Si l'âge n'est pas parsable, utiliser le titre
            }
        }
        
        // Classification basée sur le titre si l'âge n'est pas disponible
        if (title.contains("Master") || title.contains("Miss") && sibSp == 0) {
            return "Enfant";
        } else if (title.contains("Mrs") || title.contains("Mr") && sibSp > 0) {
            return "Epoux";
        } else {
            return "Adulte";
        }
    }
    
    /**
     * Extraire le titre du nom
     */
    private String extraireTitre(String name) {
        String[] parts = name.split(",");
        if (parts.length > 1) {
            String secondPart = parts[1].trim();
            if (secondPart.contains(".")) {
                return secondPart.substring(0, secondPart.indexOf(".")).trim();
            }
        }
        return "";
    }
    
    
          
    /**
     * Déterminer la catégorie économique basée sur classe et tarif
     */
    private String getCategorieEconomie(int classe, double fare) {
        if (classe == 1) {
            if (fare > 100) return "Luxury";
            else if (fare > 50) return "FirstClass";
            else return "UpperMiddle";
        } else if (classe == 2) {
            if (fare > 25) return "MiddleClass";
            else return "LowerMiddle";
        } else {
            if (fare > 15) return "WorkingClass";
            else if (fare > 7) return "LowIncome";
            else return "VeryLowIncome";
        }
    }
    
    /**
     * Analyser le type de cabine
     */
    private String getTypeCabine(String cabine) {
        if (cabine == null || cabine.trim().isEmpty()) {
            return "NoCabin";
        }
        
        char deck = cabine.charAt(0);
        switch (deck) {
            case 'A': return "ToppestDeck";
            case 'B': return "TopDeck";
            case 'C': return "LowTopDeck";
            case 'D': return "MiddleDeck";
            case 'E': return "LowMiddleDeck";
            case 'F': return "LowerDeck";
            case 'G': return "LowestDeck";
            default: return "UnknownDeck";
        }
    }
    
    /**
     * Catégoriser le port d'embarquement
     */
    private String getCategoriePort(String embarked) {
        switch (embarked.trim().toUpperCase()) {
            case "C": return "Cherbourg"; // Port français, souvent plus cher
            case "Q": return "Queenstown"; // Port irlandais, souvent moins cher
            case "S": return "Southampton"; // Port principal anglais
            default: return "Unknown";
        }
    }
    
    /**
     * Analyser le type de billet
     */
    private String getTypeTicket(String ticket) {
        if (ticket == null || ticket.isEmpty()) {
            return "Unknown";
        }
        
        ticket = ticket.trim().toUpperCase();
        
        if (ticket.contains("PC")) return "FirstClass";
        else if (ticket.contains("SOTON") || ticket.contains("S.O.C.")) return "Southampton";
        else if (ticket.contains("CA")) return "California";
        else if (ticket.matches("\\d+")) return "Numeric";
        else return "Other";
    }
    
    
    
    
}


 

 


 
	
	




