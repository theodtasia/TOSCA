package tosca;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;




public class Parse 
{
	
	private static HashMap<String, HashMap<String, Object>> map;
	public static Model m;
	
	
	public static void main(String[] args) throws IOException

   {

	Parse p = new Parse();
		//p.loadyaml();
		 InputStream inputStream = new FileInputStream("example.yml");
		    Yaml yaml = new Yaml();
		    HashMap<String, HashMap<String,Object>> data = new HashMap<>();
		    data = yaml.load(inputStream);
		    //System.out.print(data)
	        p.setMap(data);
         //System.out.print(p.getMap());
         ArrayList<String> list = new ArrayList<String>();
 		for ( String key : data.keySet() ) {
 		    list.add(key);
 		}
 		
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
 			if(x.equals("node_templates"))
 			{
 			  NodeTemplates templates = new NodeTemplates();
 			  templates.nodeTemplates();
 		    }
 			
 		}
		Rio.write(Parse.m, System.out, RDFFormat.TURTLE);

 		}
	  
	
	public void setMap(HashMap<String, HashMap<String, Object>> map) {
		this.map = map;
	}
	
	public HashMap<String, HashMap<String, Object>> getMap() {
		return map;
	}
	
}
 
