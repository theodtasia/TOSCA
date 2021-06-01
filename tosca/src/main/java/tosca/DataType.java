package tosca;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.repository.RepositoryConnection;


//class for handling data types
public class DataType 
{
	Parse map = new Parse();
	public String var = "data";
	@SuppressWarnings("unchecked")
	void dataTypes() throws IOException 
	{
		String data_name = null;
		Object derived_from = null;
		HashMap<String, Object> map2 = new HashMap<>();
		ArrayList<String> properties_names = new ArrayList<>(); //save the properties name for using the later
		HashMap<String, Object> second_level = map2;
		HashMap<String, Object> third_level = map2;
		HashMap<String, Object> fourth_level = map2;
		HashMap<String, Object> fifth_level = map2;
		String ex = "https://intelligence.csd.auth.gr/ontologies/tosca/";
		map2 = map.getMap().get("data_types");
    	
    	final int p[]= {0};
		for (String key : map2.keySet()) 
		{
			data_name = key;
			IRI tosca_description = null;
			IRI toscaDefault = null;
			IRI toscaType = null;
			IRI toscaProperty= null;
			ModelBuilder builder = new ModelBuilder();
			builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/").subject("ex:" + data_name)
			.add(RDF.TYPE, OWL.CLASS);
			
			tosca_description = Values.iri(ex,"tosca_description");
			toscaDefault=Values.iri(ex,"toscaDefault");
			toscaProperty = Values.iri(ex,"toscaProperty");
			toscaType = Values.iri(ex, "toscaType");	
			builder.subject(ex + data_name)
			
			.add(tosca_description,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(tosca_description,RDFS.RANGE,"string")
			.add(toscaDefault,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(toscaDefault,RDFS.RANGE,"string")
			.add(toscaProperty,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(toscaProperty,RDFS.RANGE,"boolean");
			
			
			// second level of hashmap
			// key names = [derived_from,properties,entry_schema,constraints]
			
			second_level = ((HashMap<String, Object>) map2.get(data_name));
			for (String key2 : second_level.keySet()) 
			{	
				
				// for derived from
				if (key2.equals("derived_from")) 
				{
					derived_from = second_level.get("derived_from"); 
					builder.add(RDFS.SUBCLASSOF, "ex:"+ derived_from);
				} 
				
				//for properties
				else if (key2.equals("properties"))
				{
					third_level = (HashMap<String, Object>) second_level.get("properties");	
					p[0]=1;
					for (String key3 : third_level.keySet()) 
					{
						properties_names.add(key3);
				    }				
				}
				
				//for datatype description
				else if(key2.equals("description")) 
				{
					builder.add(data_name,tosca_description,Values.literal(fourth_level.get("description")));
				}
				
				//for entry schema
				else if (key2.equals("entry_schema")) 
				{
					IRI entry_schema = Values.iri(ex,"entry_schema");
					if ((fifth_level.get("type").equals("string")) || (fifth_level.get("type").equals("integer")) ||(fifth_level.get("type").equals("float")) || (fifth_level.get("type").equals("boolean")))
					{
						builder.add(entry_schema,RDF.TYPE,"owl:DatatypeProperty");
						builder.add(entry_schema,RDFS.RANGE,fourth_level.get("type"));	
					}
					else
					{
						builder.add(entry_schema,RDF.TYPE,"owl:ObjectProperty");
						builder.add(entry_schema,RDFS.RANGE,fourth_level.get("type"));	
					}
				
				}
				
				//for constraints
				if (key2.equals("constraints")) 
				{
					IRI constraints = Values.iri(ex,"constraints");
					builder.add(constraints,RDF.TYPE,"owl:ObjectProperty");
					fifth_level=(HashMap<String, Object>) fourth_level.get("constraints");
					for (Entry<String, Object> entry: fifth_level.entrySet()) 
					{
						String cons = entry.getKey();
						IRI constr = Values.iri(ex,cons);
						Object val = entry.getValue();
						builder.subject(constraints);
						if(cons.equals("valid_values"))
						{
							builder.add(constr,RDF.LIST);
							builder.add(constr, val);
						}
						if(cons.equals("min_length"))
						{
							builder.add(constr,RDFS.RANGE,"string");
							builder.add(constr, val);
							
						}
						if(cons.equals("max_length"))
						{
							builder.add(constr,RDFS.RANGE,"string");
							builder.add(constr, val);
							
						}
					}
					
				}
		
				
		    }
			
			
			//for properties
			if(p[0]!=0)
			{
				p[0]=0;
				third_level = (HashMap<String, Object>) second_level.get("properties");
				for (int j = 0; j < properties_names.size(); j++) 
				{
					IRI properties = Values.iri(ex,properties_names.get(j));
					builder.add(properties,RDF.TYPE,"owl:DatatypeProperty");
					if (third_level.get(properties_names.get(j)) != null) 
					{
						fourth_level = (HashMap<String, Object>) third_level.get(properties_names.get(j));						
						
						// check if the propertys type is a datatypeProperty or objectProperty toscaType
						if ((fourth_level.get("type").equals("string")) || (fourth_level.get("type").equals("integer")) || (fourth_level.get("type").equals("float")) || (fourth_level.get("type").equals("boolean"))) 
						{
							builder.add(properties,RDF.TYPE,"owl:DatatypeProperty");
							builder.add(properties,RDFS.RANGE,fourth_level.get("type"));
							
						
						}
						else if(((String) fourth_level.get("type")).startsWith("tosca"))
						{
							builder.add(properties,RDF.TYPE,"owl:ObjectProperty");
							builder.add(properties,RDFS.RANGE,fourth_level.get("type"));

						}
						else 
						{
							builder.add(properties,RDF.TYPE,"owl:DatatypeProperty");
							builder.add(properties,toscaType,fourth_level.get("type"));

						}
						
						//for property description
						if (fourth_level.get("description") != null) 
						{
							builder.add(properties,tosca_description,Values.literal(fourth_level.get("description")));
						}
						
						//for property default
						if (fourth_level.get("default") != null) 
						{
							builder.add(properties,toscaDefault,Values.literal(fourth_level.get("default")));

						}
						
						//for entry_schema
						if (fourth_level.get("entry_schema") != null) 
						{
							IRI entry_schema = Values.iri(ex,"entry_schema");
							if ((fifth_level.get("type").equals("string")) || (fifth_level.get("type").equals("integer")) ||(fifth_level.get("type").equals("float")) || (fifth_level.get("type").equals("boolean")))
							{
								builder.add(entry_schema,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(entry_schema,RDFS.RANGE,fourth_level.get("type"));	
							}
							else
							{
								builder.add(entry_schema,RDF.TYPE,"owl:ObjectProperty");
								builder.add(entry_schema,RDFS.RANGE,fourth_level.get("type"));	
							}
						}
						
						//for basic constraints
						if (fourth_level.get("constraints") != null) 
						{
							IRI constraints = Values.iri(ex,"constraints");
							builder.add(constraints,RDF.TYPE,"owl:ObjectProperty");
							fifth_level=(HashMap<String, Object>) fourth_level.get("constraints");
							for (Entry<String, Object> entry: fifth_level.entrySet()) 
							{
								String cons = entry.getKey();
								IRI constr = Values.iri(ex,cons);
								Object val = entry.getValue();
								builder.subject(constraints);
								if(cons.equals("valid_values"))
								{
									builder.add(constr,RDF.LIST);
									builder.add(constr, val);
									BNode r15 = Values.bnode();
									builder.subject("ex:"+ properties_names.get(j));
									builder.add(RDFS.SUBCLASSOF, r15);
									builder.subject(r15);
									builder.add(RDF.TYPE, OWL.RESTRICTION);
									builder.add(OWL.ONPROPERTY, properties_names.get(j));
									builder.add(OWL.SOMEVALUESFROM, constr);
								}
								if(cons.equals("min_length"))
								{
									builder.add(constr,RDFS.RANGE,"string");
									builder.add(constr, val);
									
								}
								if(cons.equals("max_length"))
								{
									builder.add(constr,RDFS.RANGE,"string");
									builder.add(constr, val);
									
								}
							}
							
						}
				
						//for required
						if (fourth_level.get("required") != null) 
						{											
							if(fourth_level.get("required").equals(true))
							{
								BNode r1 = Values.bnode();
								builder.subject("ex:"+data_name);
								builder.add(RDFS.SUBCLASSOF, r1);
								builder.subject(r1);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 1);

							}
							else if(fourth_level.get("required").equals(false))
							{
								BNode r2 = Values.bnode();
								builder.subject("ex:"+data_name);
								builder.add(RDFS.SUBCLASSOF, r2);
								builder.subject(r2);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 0);
							}
						}
		     			
						builder.add(properties,toscaProperty,"true");
					}
					
				}	
			}
		
			
		Parse.m = builder.build();
		Rio.write(Parse.m, System.out, RDFFormat.TURTLE);
		WriteFiles.Create();
		String url=Parse.repo;

		HTTPRepository repository = new HTTPRepository(url);
        String baseURI = url;
        File file = new File("data_type.ttl");
        try {
           RepositoryConnection con = repository.getConnection();
           try 
           {
              con.add(file, baseURI, RDFFormat.TURTLE);
           }
           finally {
              con.close();
           }
        }
        catch (RDF4JException e) 
        {
           // handle exception
        }
		}
	}
	
}
