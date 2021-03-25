package tosca;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;

public class Parse {
	
	private static HashMap<String, HashMap<String, Object>> map;
	
	
	
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
 		
 		for(String x : list){

 			if(x.equals("node_types"))
 			{
 			  Nodes node_t = new Nodes();
 			  node_t.nodeTypes();
 		    }
 			
 		}
	  }
	
	public void setMap(HashMap<String, HashMap<String, Object>> map) {
		this.map = map;
	}
	
	public HashMap<String, HashMap<String, Object>> getMap() {
		return map;
	}
	
}
 


//private Node_types node_t = new Node_types();


//ModelBuilder builder = new ModelBuilder();

/*	@SuppressWarnings("unchecked")
public HashMap<String, HashMap<String, Object>> loadyaml() throws IOException
  {
	    InputStream inputStream = new FileInputStream("example.yml");
	    Yaml yaml = new Yaml();
	    HashMap<String, HashMap<String,Object>> data = new HashMap<>();
	    data = yaml.load(inputStream);
	    //System.out.print(data);
	    
	    
	    ArrayList<Object> a = new ArrayList<Object>(data.keySet());
        Object names = null;
		ArrayList<Object> namea = new ArrayList<Object>();
		namea.add(names);
		for (String key : data.keySet()) 
		{
			//System.out.print(data.get(key));
	        data.get(key).forEach((k, v) -> 
	        { 
	        	if(k.equals("derived_from"))
	        	{namea.set(0,v);
	        	}
	        }) ;     	        
	        		
	    }
		ArrayList<String> list = new ArrayList<String>();
		for ( String key : data.keySet() ) {
		    list.add(key);
		}
		
		for(String x : list){

			if(x.equals("node_types")){
				System.out.print(x);
			//    Node_types node_t = new Node_types();
		    }
			else if(x.equals("data_types")){
				System.out.print(x);
		    }
			else if(x.equals("node_templates")){
				System.out.print(x);
		    }
		}
		
		
       HashMap<String, HashMap<String, Object>> map = new HashMap<String, HashMap<String,Object>>();
	    map.put("key", new HashMap<String, Object>());
	    map.get("key").put("key2", "val2");

	    //System.out.println(data.get("data_types").get("sodalite.datatypes.OpenStack.SecurityRule"));
	//	Model model = builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/")
		//      .subject("ex:"+o)
		  //    .add(RDF.TYPE, "owl:Class")
		    //  .add(RDFS.SUBCLASSOF, RDFS.RESOURCE, "https://intelligence.csd.auth.gr/ontologies/tosca/"+ namea.get(0))
		      //.add(RDFS.SUBCLASSOF, OWL.RESTRICTION, RDF.PROPERTY)
		      
		    // .build();
		//model.forEach(System.out::println);



        
		
	 return map;
}



 ArrayList<Object> a = new ArrayList<Object>(data.keySet());
	        Object names = null;
			ArrayList<Object> namea = new ArrayList<Object>();
			namea.add(names);
			for (String key : data.keySet()) 
			{
				//System.out.print(data.get(key));
		        data.get(key).forEach((k, v) -> 
		        { 
		        	if(k.equals("derived_from"))
		        	{namea.set(0,v);
		        	}
		        }) ;     	        
		        		
		    }
			ArrayList<String> list = new ArrayList<String>();
			for ( String key : data.keySet() ) {
			    list.add(key);
			}
			
			for(String x : list){

				if(x.equals("node_types")){
					System.out.print(x);
				//    Node_types node_t = new Node_types();
			    }
				else if(x.equals("data_types")){
					System.out.print(x);
			    }
				else if(x.equals("node_templates")){
					System.out.print(x);
			    }
			}
			
			
	       HashMap<String, HashMap<String, Object>> map = new HashMap<String, HashMap<String,Object>>();
		    map.put("key", new HashMap<String, Object>());
		    map.get("key").put("key2", "val2");

		    //System.out.println(data.get("data_types").get("sodalite.datatypes.OpenStack.SecurityRule"));
		//	Model model = builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/")
			//      .subject("ex:"+o)
			  //    .add(RDF.TYPE, "owl:Class")
			    //  .add(RDFS.SUBCLASSOF, RDFS.RESOURCE, "https://intelligence.csd.auth.gr/ontologies/tosca/"+ namea.get(0))
			      //.add(RDFS.SUBCLASSOF, OWL.RESTRICTION, RDF.PROPERTY)
			      
			    // .build();
			//model.forEach(System.out::println);


*/
