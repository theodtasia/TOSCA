package tosca;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.yaml.snakeyaml.Yaml;
import java.io.BufferedReader;
import java.io.FileInputStream;
import org.eclipse.rdf4j.model.Model;

//class for reading users input, parsing and handling the yaml file 

public class Parse 
{
	
	private static HashMap<String, HashMap<String, Object>> map;	
	public static Model m;
	public static String folder;
	public static String repo;

	public static void main(String[] args) throws IOException

   {

		   //reading file from user
		
	       Parse p = new Parse();
	       String file=null,input = null; 
	       folder=null;
	       try 
	       {
	           BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));          
	 	       System.out.print("Give your yaml file: ");
	           file = reader.readLine();
	           System.out.print("Do you want to save the output files to a specific folder? y/n ");
	           input= reader.readLine(); 
	           if(input.equals("y"))
	           {
		           System.out.print("Give the folder path, where you want to save the files:");   
		           folder = reader.readLine(); 
	           }
	           
	   			//http://192.168.1.4:7200/repositories/tosca
	           System.out.print("Give the graphdb url repository");
	           repo = reader.readLine(); 
	           
	        } 
	       catch (IOException ioe) 
	       {
	            ioe.printStackTrace();
	       }
		    
	       if(file!=null)
	       {
	    	   // parse yaml file to hashmap
	    	   InputStream inputStream = new FileInputStream(file);
	    	   Yaml yaml = new Yaml();
	    	   HashMap<String, HashMap<String,Object>> data = new HashMap<>();
	    	   data = yaml.load(inputStream);
	    	   p.setMap(data);
	    	   
	    	   //depends on key type, call the corresponding class
	    	   for(String key : data.keySet())
	    	   {

	    		   if(key.equals("node_types"))
	    		   {
	    			   NodeType node_t = new NodeType();
	    			   node_t.nodeTypes();
	    		   }
	    		   if(key.equals("data_types"))
	    		   {
	    			   DataType data_t = new DataType();
	    			   data_t.dataTypes();
	    		   }
	    		   if(key.equals("capability_types"))
	    		   {
	    			   CapabilityType capability_t = new CapabilityType();
	    			   capability_t.capabilityTypes();
	    		   }
	    		   if(key.equals("node_templates"))
	    		   {
	    			   NodeTemplates templates = new NodeTemplates();
	    			   templates.nodeTemplates();
	    		   }
	    	   }
	    	   
	       }
	       else
	       {
	    	   System.out.println("File is empty");
	       }

 		}
	  
	
	public void setMap(HashMap<String, HashMap<String, Object>> map) 
	{
		Parse.map = map;
	}
	
	public HashMap<String, HashMap<String, Object>> getMap() 
	{
		return map;
	}

	public void setfolder(String folder) 
	{
		Parse.folder = folder;
	}
	
	public String getfolder() 
	{
		return folder;
	}
	
	
}
 
