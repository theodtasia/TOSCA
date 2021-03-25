package tosca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.Model;



public class Nodes 
{
	

    Parse map = new Parse();
    void nodeTypes()
    {
    	String node_name = null;
        Object derived_from= null;
    	final int i[]= {0};
    	HashMap<String, Object> map2 = new HashMap<>();
    	ArrayList<String> attribute_names= new ArrayList<>();
    	map2=map.getMap().get("node_types");
    	HashMap<String, Object> second_level = map2;
    	HashMap<String, Object> third_level = map2;
    	HashMap<String, Object> fourth_level = map2;

    	for (String key : map2.keySet()) 
		{
             node_name=key;
			second_level=(HashMap<String, Object>) map2.get(node_name);
            for(String key2 : second_level.keySet())
            {

            	//System.out.println(key2);
            	if(key2.equals("derived_from"))
        	    {  
	        		derived_from=map2.get("derived_from");
        	    }
            	else if(key2.equals("properties"))
                {
                	System.out.println("d");
                	
                }
            	else if(key2.equals("attributes"))
            	{
            	    third_level= (HashMap<String, Object>) second_level.get("attributes");
                	for(String key3: third_level.keySet())
                	{
                	    //fourth_level= (HashMap<String, Object>) third_level.get(key3);
                		attribute_names.add(key3);
                    	//System.out.println("F" + fourth_level);


                	}
                	System.out.println("Third" + third_level);
            	}
            	else if(key2.equals("capabilities"))
            	{
            		
            	}
            	
     


    
     }
		}
    
    	ModelBuilder builder = new ModelBuilder();
		builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/")
		      .subject("ex:" + node_name)
		      .add(RDF.TYPE, "owl:Class")
		      .add(RDFS.SUBCLASSOF, RDFS.RESOURCE, "https://intelligence.csd.auth.gr/ontologies/tosca/" + derived_from);
		      for(int j=0;j<attribute_names.size();j++)
		      {
			  //System.out.println(third_level.get(third_level.get(attribute_names.get(j))));

		       builder.add(OWL.DATATYPEPROPERTY, "https://intelligence.csd.auth.gr/ontologies/tosca/"+ attribute_names.get(j));
		       if(third_level.get(attribute_names.get(j))!=null)
		       {
		    	   fourth_level= (HashMap<String, Object>) third_level.get(attribute_names.get(j));
		    	   if(fourth_level.get("type")!=null)
		    	   {
		           builder.add(RDFS.RANGE,RDFS.RESOURCE,fourth_level.get("type"));
		    	   }
		       }
		      }
        Model m = builder.build();
		m.forEach(System.out::println);
    
}
}