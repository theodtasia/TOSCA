package tosca;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

public class Nodes {

	Parse map = new Parse();

	void nodeTypes() 
	{
		String node_name = null;
		Object derived_from = null;
		HashMap<String, Object> map2 = new HashMap<>();
		ArrayList<String> attribute_names = new ArrayList<>();
		ArrayList<String> properties_names = new ArrayList<>();
		ArrayList<String> capabilities_names = new ArrayList<>();
		HashMap<String, Object> second_level = map2;
		HashMap<String, Object> third_level = map2;
		HashMap<String, Object> fourth_level = map2;
		map2 = map.getMap().get("node_types");
		for (String key : map2.keySet()) 
		{
			node_name = key;
			//System.out.println(key);
			second_level = (HashMap<String, Object>) map2.get(node_name);
			for (String key2 : second_level.keySet()) 
			{

				//System.out.println(key2);
				if (key2.equals("derived_from")) 
				{
					derived_from = second_level.get("derived_from"); // this is not map2 but second_level
					//System.out.println(derived_from);
				} 
				else if (key2.equals("properties"))
				{
					third_level = (HashMap<String, Object>) second_level.get("properties");
					for (String key3 : third_level.keySet()) 
					{
						properties_names.add(key3);
				    }
				
				}
				else if (key2.equals("attributes")) 
				{
					third_level = (HashMap<String, Object>) second_level.get("attributes");
					for (String key3 : third_level.keySet()) 
					{
						// fourth_level= (HashMap<String, Object>) third_level.get(key3);
						attribute_names.add(key3);
						// System.out.println("F" + fourth_level);

				    }
				} 
				else if (key2.equals("capabilities")) 
				{
					third_level = (HashMap<String, Object>) second_level.get("attributes");
					for (String key3 : third_level.keySet()) 
					{
						
						capabilities_names.add(key3);

				    }
				}

		    }
		
		
		int counter = 0;
		String ex = null;
		IRI tosca_description = null;
		IRI toscaDefault = null;
		IRI toscaType = null;
		IRI toscaProperty= null;
		
		if(counter==0)
		{
		 ex = "https://intelligence.csd.auth.gr/ontologies/tosca/";
		 tosca_description = Values.iri(ex,"description");
		 toscaDefault=Values.iri(ex,"toscaDefault");
		 toscaProperty = Values.iri(ex,"toscaProperty");
   		 toscaType = Values.iri(ex,"toscaType");

		}
		counter++;
		
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/").subject("ex:" + node_name)
				.add(RDF.TYPE, "owl:Class")
				.add(RDFS.SUBCLASSOF, "https://intelligence.csd.auth.gr/ontologies/tosca/" + derived_from)
		        .add(tosca_description,RDF.TYPE,"owl:AnnotationProperty")
		        .add(tosca_description,RDFS.RANGE,"string")
		        .add(toscaDefault,RDF.TYPE,"owl:AnnotationProperty")
		        .add(toscaDefault,RDFS.RANGE,"string")
		        .add(toscaProperty,RDF.TYPE,"owl:AnnotationProperty")
		        .add(toscaProperty,RDFS.RANGE,"boolean");
    

		/**
		 * Above, the same "subject" is used for all "add" definitions. So, the two "add" have as subject the ex:node_name:
		 * 
		 *  <ex:node_name type owl:Class>
		 *  <ex:node_name rdfs:subClassOf <drivedFrom>
		 *  
		 */
		
		for (int j = 0; j < properties_names.size(); j++) 
		{
			IRI properties = Values.iri(ex,properties_names.get(j));
			builder.add(properties,RDF.TYPE,"owl:DatatypeProperty");
			if (third_level.get(properties_names.get(j)) != null) 
			{
				fourth_level = (HashMap<String, Object>) third_level.get(properties_names.get(j));

				// check if the type is "normal" datatype or toscaType
				if ((fourth_level.get("type").equals("string")) || (fourth_level.get("type").equals("integer")) || (fourth_level.get("type").equals("float")) || (fourth_level.get("type").equals("boolean"))) 
				{

					builder.add(properties,RDFS.RANGE,fourth_level.get("type"));
					
				}
				else
				{
					builder.add(properties,toscaType,fourth_level.get("type"));

				}
				if (fourth_level.get("description") != null) 
				{
				 builder.add(properties,tosca_description,Values.literal(fourth_level.get("description")));
				}
				if (fourth_level.get("default") != null) 
				{
				  builder.add(properties,toscaDefault,Values.literal(fourth_level.get("default")));

				}
				builder.add(properties,toscaProperty,"true");
			}
			
		}
		
		
		for (int j = 0; j < attribute_names.size(); j++) 
		{
			// System.out.println(third_level.get(third_level.get(attribute_names.get(j))));
			/**
			 * Properties are defined as objects as well. So for example, the property public_address should be defined as:
			 * <public_address, type, owl:DatatypeProperty>. So, we can not simply "add" this to the builder, since all these definitions will be added to the node_name.
			 * What needs to be done is:
			 * 1. create the property (the datatype property in this case
			 * 2. add the domain/range value or any other characteristics we want
			 */
			
			IRI attribute = Values.iri(ex,attribute_names.get(j));
			builder.add(attribute,RDF.TYPE,"owl:DatatypeProperty");

			
			if (third_level.get(attribute_names.get(j)) != null) 
			{
				fourth_level = (HashMap<String, Object>) third_level.get(attribute_names.get(j));
				if (fourth_level.get("type") != null) 
				{
					if ((fourth_level.get("type").equals("string")) || (fourth_level.get("type").equals("integer")) || (fourth_level.get("type").equals("float")) || (fourth_level.get("type").equals("boolean"))) 
					{

						builder.add(attribute,RDFS.RANGE,fourth_level.get("type"));
						
					}
					else
					{
						builder.add(attribute,toscaType,fourth_level.get("type"));

					}
				}
				if (fourth_level.get("description") != null) 
				{
					builder.add(attribute,tosca_description,Values.literal(fourth_level.get("description")));
				}
				
					builder.add(attribute,toscaProperty,"false");
				
			}
			
			if (third_level.get(capabilities_names.get(j)) != null) 
			{
				
				
			}
			
			/**
			 * Annotations are similar to other properties:
			 * 1. we need to define the annotation property, e.g. <our_annotation_property, type, owl:AnnotationProperty>
			 * 2. then we need to use it, e.g. <our class, our_property, value>
			 */	
		}
		Model m = builder.build();
		m.forEach(System.out::println);
		}
	}
}