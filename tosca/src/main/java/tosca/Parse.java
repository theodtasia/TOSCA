package tosca;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.yaml.snakeyaml.Yaml;
import java.io.BufferedReader;
import java.io.FileInputStream;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

//class for parsing users yaml file

public class Parse 
{
	
	private static HashMap<String, HashMap<String, Object>> map;	
	public static Model m;
	
	public static void main(String[] args) throws IOException

   {

		
		   //reading file from user
		
	       Parse p = new Parse();
	       String file = null; 
	       
	       try 
	       {
	           BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));          
	 	       System.out.print("Enter your yaml file: ");
	            file = reader.readLine();
	            
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
	    	   
	    	   ArrayList<String> list = new ArrayList<String>();
	    	   for ( String key : data.keySet() ) 
	    	   {
	    		   list.add(key);
	    	   }
 		
	    	   //depends on key_type, calling the corresponding class
	    	   for(String x : list)
	    	   {

	    		   if(x.equals("node_types"))
	    		   {
	    			   NodeType node_t = new NodeType();
	    			   node_t.nodeTypes();
	    		   }
	    		   if(x.equals("data_types"))
	    		   {
	    			   DataType data_t = new DataType();
	    			   data_t.dataTypes();
	    		   }
	    		   if(x.equals("capability_types"))
	    		   {
	    			   CapabilityType capability_t = new CapabilityType();
	    			   capability_t.capabilityTypes();
	    		   }
	    		   if(x.equals("node_templates"))
	    		   {
	    			   NodeTemplates templates = new NodeTemplates();
	    			   templates.nodeTemplates();
	    		   }
	    	   }
	    	   
	    	   Rio.write(Parse.m, System.out, RDFFormat.TURTLE);
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
	
}
 
