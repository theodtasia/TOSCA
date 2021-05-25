package tosca;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;

public class NodeType 
{

	Parse map = new Parse();

	@SuppressWarnings("unchecked")
	void nodeTypes() throws IOException 
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
		HashMap<String, Object> fifth_level = map2;
		String ex = "https://intelligence.csd.auth.gr/ontologies/tosca/";
		map2 = map.getMap().get("node_types");
    	final int c[]= {0};
    	final int a[]= {0};
    	final int p[]= {0};
		for (String key : map2.keySet()) 
		{
			node_name = key;
			IRI tosca_description = null;
			IRI toscaDefault = null;
			IRI toscaType = null;
			IRI requirements = null;
			IRI toscaProperty= null;
			ModelBuilder builder = new ModelBuilder();
			builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/").subject("ex:" + node_name)
			.add(RDF.TYPE, OWL.CLASS);
			
			tosca_description = Values.iri(ex,"tosca_description");
			toscaDefault=Values.iri(ex,"toscaDefault");
			toscaProperty = Values.iri(ex,"toscaProperty");
			toscaType = Values.iri(ex, "toscaType");
			requirements = Values.iri(ex, "requirements");	
			builder.subject(ex + node_name)
			.add(requirements,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(tosca_description,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(tosca_description,RDFS.RANGE,"string")
			.add(toscaDefault,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(toscaDefault,RDFS.RANGE,"string")
			.add(toscaProperty,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(toscaProperty,RDFS.RANGE,"boolean");
			
			
			//System.out.println(key);
			second_level = ((HashMap<String, Object>) map2.get(node_name));
			for (String key2 : second_level.keySet()) 
			{	
				
				

				//System.out.println(key2);
				if (key2.equals("derived_from")) 
				{
					derived_from = second_level.get("derived_from"); 
					builder.add(RDFS.SUBCLASSOF, "ex:"+ derived_from);
				} 
				else if (key2.equals("properties"))
				{
					third_level = (HashMap<String, Object>) second_level.get("properties");	
					p[0]=1;
					for (String key3 : third_level.keySet()) 
					{
						properties_names.add(key3);
				    }
				
				}
				else if (key2.equals("attributes")) 
				{
					third_level = (HashMap<String, Object>) second_level.get("attributes");
					a[0]=1;
					for (String key3 : third_level.keySet()) 
					{
						// fourth_level= (HashMap<String, Object>) third_level.get(key3);
						attribute_names.add(key3);

						// System.out.println("F" + fourth_level);

				    }
				} 
				else if (key2.equals("capabilities")) 
				{
					c[0]=1;	
					third_level = (HashMap<String, Object>) second_level.get("capabilities");
					for (String key3 : third_level.keySet()) 
					{
						capabilities_names.add(key3);
				    }
				}
				else if(key2.equals("description")) 
				{
					builder.add(node_name,tosca_description,Values.literal(fourth_level.get("description")));
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
					if (third_level.get(properties_names.get(j)) != null) 
					{
						fourth_level = (HashMap<String, Object>) third_level.get(properties_names.get(j));
						//System.out.println(fourth_level);
						
						// check if the type is "normal" datatype or toscaType
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
						if (fourth_level.get("default") != null) 
						{
							builder.add(properties,toscaDefault,Values.literal(fourth_level.get("default")));

						}
						if (fourth_level.get("description") != null) 
						{
							builder.add(properties,tosca_description,Values.literal(fourth_level.get("description")));
						}
						if (fourth_level.get("entry_schema") != null) 
						{
							
							IRI entry_schema = Values.iri(ex,"entry_schema");
							builder.add(entry_schema,RDF.TYPE,"owl:ObjectProperty");
						}
				
						if (fourth_level.get("required") != null) 
						{											
							if(fourth_level.get("required").equals(true))
							{
								BNode r1 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r1);
								builder.subject(r1);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 1);

							}
							else if(fourth_level.get("required").equals(false))
							{
								BNode r2 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r2);
								builder.subject(r2);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 0);
							}
						}
						if (fourth_level.get("constraints") != null) 
						{
							IRI constraints = Values.iri(ex,"constraints");
							builder.add(constraints,RDF.TYPE,"owl:ObjectProperty");
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

		     			
						builder.add(properties,toscaProperty,"true");
					}
					
				}	
			}
		
			//for attributes
			if(a[0]!=0)
			{
				a[0]=0;
				third_level = (HashMap<String, Object>) second_level.get("attributes");
				for (int j = 0; j < attribute_names.size(); j++) 
				{
					IRI attribute = Values.iri(ex,attribute_names.get(j));
					builder.add(attribute,RDF.TYPE,"owl:DatatypeProperty");
					if (third_level.get(attribute_names.get(j)) != null) 
					{
						fourth_level = (HashMap<String, Object>) third_level.get(attribute_names.get(j));
						if (fourth_level.get("type") != null) 
						{
							if ((fourth_level.get("type").equals("string")) || (fourth_level.get("type").equals("integer")) || (fourth_level.get("type").equals("float")) || (fourth_level.get("type").equals("boolean"))) 
							{
								builder.add(attribute,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(attribute,RDFS.RANGE,fourth_level.get("type"));
			
							}
							else if(((String) fourth_level.get("type")).startsWith("tosca"))
							{
								builder.add(attribute,RDF.TYPE,"owl:ObjectProperty");
								builder.add(attribute,RDFS.RANGE,fourth_level.get("type"));

							}
							else
							{
								builder.add(attribute,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(attribute,toscaType,fourth_level.get("type"));


							}
						}
						if (fourth_level.get("description") != null) 
						{
							builder.add(attribute,tosca_description,Values.literal(fourth_level.get("description")));
						}
						
						if (fourth_level.get("entry_schema") != null) 
						{
							IRI entry_schema = Values.iri(ex,"entry_schema");
							builder.add(entry_schema,RDF.TYPE,"owl:ObjectProperty");
						}
						if (fourth_level.get("constraints") != null) 
						{
							
								IRI constraints = Values.iri(ex,"constraints");
								builder.add(constraints,RDF.TYPE,"owl:ObjectProperty");
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
						builder.add(attribute,toscaProperty,"false");
						if (fourth_level.get("required") != null) 
						{											
							if(fourth_level.get("required").equals(true))
							{
								
								BNode r3 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r3);
								builder.subject(r3);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.MINCARDINALITY, 1);
							}
							else if(fourth_level.get("required").equals(false))
							{
								BNode r4 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r4);
								builder.subject(r4);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.MINCARDINALITY, 0);

							}
						}
						if (fourth_level.get("constraints") != null) 
						{
							IRI constraints = Values.iri(ex,"constraints");
							builder.add(constraints,RDF.TYPE,"owl:ObjectProperty");
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
		
			
			}
		}
		
		//for capabilities
		if(c[0]!=0)
		{
			c[0]=0;
			third_level = (HashMap<String, Object>) second_level.get("capabilities");
			IRI capabilities= Values.iri(ex,"capabilities");
			
			for (int j = 0; j < capabilities_names.size(); j++) 
			{
				IRI capability = Values.iri(ex,capabilities_names.get(j));
				builder.add(capability,RDF.TYPE,"owl:ObjectProperty");
				//System.out.println(fourth_level.get(third_level.get(capabilities_names.get(j))));
				fourth_level = (HashMap<String, Object>) third_level.get(capabilities_names.get(j));
				
	
                int type=0;
            	if (fourth_level.get("type") != null) 
				{
            		type=1;

				}
            	
            	int valid=0;
				IRI valid_source_types=Values.iri(ex,"valid_source_types");

				if (fourth_level.get("valid_source_types") != null) 
				{
					builder.add(valid_source_types,RDF.LIST,"owl:ObjectProperty");
					valid=1;

				}
		     	
				//start
				BNode r5 = Values.bnode();
				BNode r6 = Values.bnode();
				builder.subject("ex:"+node_name);
				builder.add(RDFS.SUBCLASSOF, r5);
				builder.subject(r5);
				builder.add(RDF.TYPE, OWL.RESTRICTION);
				builder.add(OWL.ONPROPERTY, capabilities);
				builder.add(capabilities,OWL.SOMEVALUESFROM,r6); 
	
				
				
				if(type!=0)
				{
				  if(valid==0) // if there is only host type
				  {
					builder.subject(r6);
					builder.add(RDF.TYPE, OWL.RESTRICTION);
					builder.add(OWL.ONPROPERTY, capability);
					Object type_host=fourth_level.get("type");
					builder.add(capability,OWL.SOMEVALUESFROM,type_host);
				  }
				  
				  else 	 //if there is valid source types 
				  {
					  //capabilities some ( host some tosca.capabilities.Node and host some 
					  //(valid_source_types some [sodalite.nodes.DockerizedComponent])(
					  
					  
					  BNode r7 = Values.bnode(); // for host some tosca
					  BNode r8 = Values.bnode(); // for host some valid source types ..
					  BNode r9 = Values.bnode(); // for valid source types some..

					  
                	  builder.subject(r7);
					  builder.add(RDF.TYPE, OWL.RESTRICTION);
				      builder.add(OWL.ONPROPERTY, capability);
					  Object type_host=fourth_level.get("type");
					  builder.add(capability,OWL.SOMEVALUESFROM,type_host);
					  
					  builder.subject(r8);
					  builder.add(RDF.TYPE, OWL.RESTRICTION);
				      builder.add(OWL.ONPROPERTY, capability);
				      builder.add(capability,OWL.SOMEVALUESFROM,r9);
				      
				      builder.subject(r9)
					         .add(RDF.TYPE, OWL.RESTRICTION)
				             .add(OWL.ONPROPERTY, valid_source_types);
					  Object valid_host=fourth_level.get("valid_source_types");
					  builder.add(valid_source_types,OWL.SOMEVALUESFROM,valid_host);
					  
					  List<BNode> intersectionList = new ArrayList<BNode>();
					  intersectionList.add(r7);
					  intersectionList.add(r8);
					  
					  BNode head = Values.bnode();

					  RDFCollections.asRDF(intersectionList, head, builder.build());

 					 builder.subject(r6).add(RDF.TYPE, OWL.CLASS).add(OWL.INTERSECTIONOF, head);

				  }
				}
				
			}
		}
		Parse.m = builder.build();
		WriteFiles.Create();
		String url=Parse.repo;
		HTTPRepository repository = new HTTPRepository(url);
        String baseURI = url;
		File file = new File("node_type.ttl");
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
