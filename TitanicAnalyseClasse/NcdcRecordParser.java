// cc NcdcRecordParserV2 A class for parsing weather records in NCDC format
package TitanicAnalyseClasse;
import org.apache.hadoop.io.Text;

// vv NcdcRecordParserV2
public class NcdcRecordParser {
  //Cette constante est utilisée pour identifier les enregistrements de température manquants. 
  //Si la température est égale à 9999, cela signifie qu'il n'y a pas de donnée valide.
  //private static final int MISSING_TEMPERATURE = 9999;
  
  private String year;
  private int airTemperature;
  private String quality;
  
  
  private String nom; 
  private String sexe;
  private String ticket;
  private String fareStr;
  private String cabin;
  private String embarked;
  
  
  private int survie;
  private int classe;
  private int age;
  private int sibsp;
  private int parch;
  
  
  
  
  
  public void parse(String record) {
    //year = record.substring(15, 19);
   // String airTemperatureString;
    

    String survieString; 
    String classeString;
    String classeAge;
    String sibspString;
    String parchString;
    
    
    /*
    // Remove leading plus sign as parseInt doesn't like them (pre-Java 7)
// Si la température contient un signe plus (+), on doit l'ignorer car la méthode 
// Integer.parseInt() ne gère pas bien les signes au début des nombres dans certaines versions de Java
    if (record.charAt(87) == '+') { 
      airTemperatureString = record.substring(88, 92);
    } else {
      airTemperatureString = record.substring(87, 92);
    }
//La température est ensuite convertie en un entier (int) après avoir été extraite.
    airTemperature = Integer.parseInt(airTemperatureString);
    quality = record.substring(92, 93);
    
    */
    
    
    classeAge = record.substring(5);
    
    survieString = record.substring(1); 
    classeString = record.substring(2);
    sibspString = record.substring(6);
    parchString = record.substring(7);
    ticket =  record.substring(8);
    fareStr =  record.substring(9);
    cabin =  record.substring(10);
    embarked =  record.substring(11);
    
 
    classe = Integer.parseInt(classeString);
    age = Integer.parseInt(classeAge);  //chercher comment supprimer ceci pour qu on puisse recuper lage en string    
    sibsp = Integer.parseInt(sibspString);
    parch = Integer.parseInt(parchString);
    nom = record.substring(3);
    sexe = record.substring(4);    
    survie = Integer.parseInt(survieString);
    

 
    
  }
  //Cette méthode est une surcharge de la méthode précédente
//La méthode convertit simplement l'objet Text en une chaîne de caractères et appelle 
//la méthode parse(String record) pour analyser l'enregistrement.
  public void parse(Text record) {
    parse(record.toString());
  }
  
  /*
//La qualité de l'enregistrement doit correspondre à un caractère dans le jeu de caractères [01459]. 
//Cela garantit que l'enregistrement est valide (les valeurs possibles pour la qualité sont 0, 1, 4, 5, ou 9, selon le format NCDC).
  public boolean isValidTemperature() {
    return airTemperature != MISSING_TEMPERATURE && quality.matches("[01459]");
  }
  
  */ 
  
  
  
  public String getYear() {
    return year;
  }

  public int getAirTemperature() {
    return airTemperature;
  }
  
  
 
  public String getSexe() {
	    return sexe;
	  }

  
  public int getSurvie() {
	    return survie;
	  }
	  
  
  public int getClasse() {
	    return classe;
	  }

  
  public int getAge() {
	    return age;
	  }

  public String getNom() {
	    return nom;
	  }

  public int getSisp() {
	    return sibsp;
	  }
  
  public int getParch() {
	    return parch;
	  }

  
  public String getTicket() {
	    return ticket;
	  }
  
  public String getFare() {
	    return fareStr;
	  }
  
  public String getCabin() {
	    return cabin;
	  }
  
  public String getEmbarked() {
	    return embarked;
	  }
 
 
  
}
// ^^ NcdcRecordParserV2
