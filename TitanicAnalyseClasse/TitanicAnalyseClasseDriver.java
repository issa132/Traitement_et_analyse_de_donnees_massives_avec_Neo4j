package TitanicAnalyseClasse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TitanicAnalyseClasseDriver extends Configured implements Tool {
	@Override
	  public int run(String[] args) throws Exception {
	    if (args.length != 2) {
	      System.err.printf("Usage: %s [generic options] <input> <output>\n",
	          getClass().getSimpleName());
	      ToolRunner.printGenericCommandUsage(System.err);
	      return -1;
	    }
	    
	    Configuration conf = new Configuration();
	    
	    //Job job = new Job(getConf(), "Titanic taux de survivant par sexe");
	    Job job = Job.getInstance(conf, "Analyse des membres de la famille du Titanic");
	    
	    /*
	//Indique à Hadoop quel fichier JAR exécuter.
	    job.setJarByClass(getClass());

	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    
	    job.setMapperClass(MaxTemperatureMapper.class);
	//permet de réduire les données localement avant de les envoyer au réducteur.
	    job.setCombinerClass(MaxTemperatureReducer.class);
	    job.setReducerClass(MaxTemperatureReducer.class);

	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    
	    */
	    
	    
	     
	      job.setJarByClass(getClass());
	      //job.setJarByClass(TitanicSurvivalByGender.class);
	      job.setMapperClass(TitanicAnalyseClasseMapper.class);
	      //job.setCombinerClass(TitanicClassAgeReducer.class);
	      job.setReducerClass(TitanicAnalyseClasseReducer.class);
	      
	      job.setOutputKeyClass(Text.class);
	      job.setOutputValueClass(Text.class);
	      
	      FileInputFormat.addInputPath(job, new Path(args[0]));
	      FileOutputFormat.setOutputPath(job, new Path(args[1]));
	      
	      System.exit(job.waitForCompletion(true) ? 0 : 1);
		  
 
	        
	        // Si vous voulez seulement la distribution sans les taux de survie,
	        // remplacez ClassAgeReducer par DistributionReducer et changez le type de sortie
	        // job.setReducerClass(DistributionReducer.class);
	        // job.setOutputValueClass(IntWritable.class);
	        
	        FileInputFormat.addInputPath(job, new Path(args[0]));
	        FileOutputFormat.setOutputPath(job, new Path(args[1]));
	        
	        //System.exit(job.waitForCompletion(true) ? 0 : 1);

	        
	    
	    
	    
	    return job.waitForCompletion(true) ? 0 : 1;
	  }
	  
	  public static void main(String[] args) throws Exception {
	    int exitCode = ToolRunner.run(new TitanicAnalyseClasseDriver(), args);
	    System.exit(exitCode);
	  }
}


 