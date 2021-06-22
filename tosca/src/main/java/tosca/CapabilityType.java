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

public class CapabilityType 
{
	Parse map = new Parse();
	@SuppressWarnings("unchecked")
	void capabilityTypes() throws IOException 
	{
		String capability_name = null;
		Object derived_from = null;
		HashMap<String, Object> map2 = new HashMap<>();
		String ex = "https://intelligence.csd.auth.gr/ontologies/tosca/";
		map2 = map.getMap().get("capability_types");
    	final int a[]= {0};
    	final int p[]= {0};
    	ModelBuilder builder = new ModelBuilder();
		for (String key : map2.keySet()) 
		{
			ArrayList<String> attribute_names = new ArrayList<>();
			ArrayList<String> properties_names = new ArrayList<>();
			HashMap<String, Object> second_level = map2;
			HashMap<String, Object> third_level = map2;
			HashMap<String, Object> fourth_level = map2;
			HashMap<String, Object> fifth_level = map2;
			capability_name = key;
			IRI tosca_description = null;
			IRI toscaDefault = null;
			IRI toscaType = null;
			IRI requirements = null;
			IRI toscaProperty= null;
			builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/").subject("ex:" + capability_name)
			.add(RDF.TYPE, OWL.CLASS);
			
			tosca_description = Values.iri(ex,"tosca_description");
			toscaDefault=Values.iri(ex,"toscaDefault");
			toscaProperty = Values.iri(ex,"toscaProperty");
			toscaType = Values.iri(ex, "toscaType");
			requirements = Values.iri(ex, "requirements");

			builder.subject(ex + capability_name)
			.add(requirements,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(tosca_description,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(tosca_description,RDFS.RANGE,"string")
			.add(toscaDefault,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(toscaDefault,RDFS.RANGE,"string")
			.add(toscaProperty,RDF.TYPE,OWL.ANNOTATIONPROPERTY)
			.add(toscaProperty,RDFS.RANGE,"boolean");
			
			second_level = ((HashMap<String, Object>) map2.get(capability_name));
			for (String key2 : second_level.keySet()) 
			{	
				
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
					//for datatype description
				else if(key2.equals("description")) 
				{
						builder.add(capability_name,tosca_description,Values.literal(fourth_level.get("description")));
				}
				else if (key2.equals("attributes")) 
				{
					third_level = (HashMap<String, Object>) second_level.get("attributes");
					a[0]=1;
					for (String key3 : third_level.keySet()) 
					{
						attribute_names.add(key3);

				    }
				}
				else if (key2.equals("valid_source_types"))
				{
					IRI valid_source_types=Values.iri(ex,"valid_source_types");
					if (fourth_level.get("valid_source_types") != null) 
					{
						  builder.add(valid_source_types,RDF.TYPE,"owl:ObjectProperty");
						  Object valid_source=fourth_level.get("valid_source_types");
						  
						  @SuppressWarnings("rawtypes")
						  ArrayList listval = (ArrayList) valid_source;
						  ArrayList<BNode> bnodes = new ArrayList<BNode>(listval.size()); //arraylist for the bnodes that we will need 
						
							List<BNode> unionList = new ArrayList<BNode>(); //list for all the blank nodes that are part of the union

							BNode r50 = Values.bnode(); 
							builder.subject("ex:"+capability_name); //set subject to the class
							builder.add(RDFS.SUBCLASSOF, r50); 
							builder.subject(r50);
							int b=0;//counter
							
							for (Object temp : listval) //for each element in valid values list
							{
                              bnodes.add(Values.bnode()); //add a bnode to the bnode list
								unionList.add(bnodes.get(b)); //add the bnode to the union list
								builder.subject(bnodes.get(b)); 
								builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property 
							    builder.add(OWL.ONPROPERTY,valid_source_types);
								builder.add(OWL.ALLVALUESFROM,temp);
					            b++;
							}
							  BNode head = Values.bnode(); // blank node for the head of the list

							  RDFCollections.asRDF(unionList, head, builder.build());

		 					  builder.subject(r50).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head);
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
					if (third_level.get(properties_names.get(j)) != null) 
					{
						fourth_level = (HashMap<String, Object>) third_level.get(properties_names.get(j));
						
						
						// check if the type is "normal" datatype or toscaType
						if ((fourth_level.get("type").equals("string")) || (fourth_level.get("type").equals("integer")) || (fourth_level.get("type").equals("float")) || (fourth_level.get("type").equals("boolean"))) 
						{
							builder.add(properties,RDF.TYPE,"owl:DatatypeProperty");
							builder.add(properties,RDFS.RANGE,fourth_level.get("type"));
						
						}
						else if(((String) fourth_level.get("type")).startsWith("tosca"))
						{
							builder.add(properties,RDF.TYPE,"owl:ObjectProperty");
							{
								builder.add(properties,RDF.TYPE,"owl:ObjectProperty");
								BNode r55 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r55);
								builder.subject(r55);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.SOMEVALUESFROM, Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" +fourth_level.get("type")));

							}

						}
						else
						{
							builder.add(properties,RDF.TYPE,"owl:ObjectProperty");
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
						if (fourth_level.get("entry_schema") != null) 
						{
							
							fifth_level=(HashMap<String, Object>) fourth_level.get("entry_schema");
							if ((fifth_level.get("type").equals("string")) || (fifth_level.get("type").equals("integer")) ||(fifth_level.get("type").equals("float")) || (fifth_level.get("type").equals("boolean")))
							{
								IRI property_entry_schema_dataproperty = Values.iri(ex,"property_entry_schema_dataproperty");
								builder.add(property_entry_schema_dataproperty,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(property_entry_schema_dataproperty,RDFS.RANGE,fifth_level.get("type"));	
							}
							else
							{
								IRI property_entry_schema_objectproperty = Values.iri(ex,"property_entry_schema_objectproperty");
								builder.add(property_entry_schema_objectproperty,RDF.TYPE,"owl:ObjectProperty");
								BNode r68 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r68);
								builder.subject(r68);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								BNode r64 = Values.bnode();
								builder.add(OWL.SOMEVALUESFROM,r64);	
								builder.subject(r64);
								builder.add(RDF.TYPE, OWL.RESTRICTION); 
							    builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + fifth_level.get("type")));	
								builder.add(OWL.ONPROPERTY,property_entry_schema_objectproperty);
							}
						}
				
						if (fourth_level.get("required") != null) 
						{											
							if(fourth_level.get("required").equals(true))
							{
								BNode r51 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r51);
								builder.subject(r51);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 1);

							}
							else if(fourth_level.get("required").equals(false))
							{
								BNode r52 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r52);
								builder.subject(r52);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 0);
							}
						}
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
							builder.add(constraints,RDF.TYPE,"owl:ObjectProperty");

							if(cons.equals("valid_values"))
							{
								@SuppressWarnings("rawtypes")
								ArrayList listval = (ArrayList) val;
								ArrayList<BNode> bnodes = new ArrayList<BNode>(listval.size()); //arraylist for the bnodes that we will need 
								List<BNode> unionList = new ArrayList<BNode>(); //list for all the blank nodes that are part of the union

								BNode r56 = Values.bnode(); 
								builder.subject("ex:"+capability_name); //set subject to the class
								builder.add(RDFS.SUBCLASSOF, r56); 
								builder.subject(r56);
								int b=0;//counter
								
								for (Object temp : listval) //for each element in valid values list
								{
                                    bnodes.add(Values.bnode()); //add a bnode to the bnode list
									unionList.add(bnodes.get(b)); //add the bnode to the union list
									builder.subject(bnodes.get(b)); 
									builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property protocol
								    builder.add(OWL.ONPROPERTY,properties);
									builder.add(OWL.HASVALUE,temp);
						            b++;
								}
								  BNode head = Values.bnode(); // blank node for the head of the list

								  RDFCollections.asRDF(unionList, head, builder.build());

			 					  builder.subject(r56).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head);
								
							}
							if(cons.equals("min_length")||cons.equals("max_length"))
							{
								builder.add(constr,RDFS.RANGE,"string");
								BNode r51 = Values.bnode();
								builder.subject("ex:"+capability_name); //set subject to the class
								builder.add(RDFS.SUBCLASSOF, r51); 
								builder.subject(r51);
								
								builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property
							    builder.add(OWL.ONPROPERTY,properties);
								
								BNode r53 = Values.bnode();
								builder.add(RDFS.SUBCLASSOF, r53); 
								
								builder.subject(r53);
								builder.add(RDF.TYPE, OWL.RESTRICTION); 
							    builder.add(OWL.ONPROPERTY,constraints);
							    
							    BNode r54 = Values.bnode();
								builder.add(OWL.SOMEVALUESFROM,r54);
								
								builder.subject(r54);
								builder.add(RDF.TYPE, OWL.RESTRICTION); 
							    builder.add(OWL.HASVALUE,val);
								builder.add(OWL.ONPROPERTY,constr);								
							}
							
						
						}

		     			
						builder.add(properties,toscaProperty,"true");
					}
					
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
								BNode r55 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r55);
								builder.subject(r55);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.SOMEVALUESFROM, Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/"+fourth_level.get("type")));

							}
							else
							{
								builder.add(attribute,RDF.TYPE,"owl:ObjectProperty");
								builder.add(attribute,toscaType,fourth_level.get("type"));

							}
						}
						if (fourth_level.get("description") != null) 
						{
							builder.add(attribute,tosca_description,Values.literal(fourth_level.get("description")));
						}
						
						if (fourth_level.get("entry_schema") != null) 
						{
							fifth_level=(HashMap<String, Object>) fourth_level.get("entry_schema");
							if ((fifth_level.get("type").equals("string")) || (fifth_level.get("type").equals("integer")) ||(fifth_level.get("type").equals("float")) || (fifth_level.get("type").equals("boolean")))
							{
								IRI property_entry_schema_dataproperty = Values.iri(ex,"property_entry_schema_dataproperty");
								builder.add(property_entry_schema_dataproperty,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(property_entry_schema_dataproperty,RDFS.RANGE,fifth_level.get("type"));	
							}
							else
							{
								IRI property_entry_schema_objectproperty = Values.iri(ex,"property_entry_schema_objectproperty");
								builder.add(property_entry_schema_objectproperty ,RDF.TYPE,"owl:ObjectProperty");
								BNode r58 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r58);
								builder.subject(r58);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								BNode r54 = Values.bnode();
								builder.add(OWL.SOMEVALUESFROM,r54);	
								builder.subject(r54);
								builder.add(RDF.TYPE, OWL.RESTRICTION); 
							    builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + fourth_level.get("type")));	
								builder.add(OWL.ONPROPERTY,property_entry_schema_objectproperty);
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
								@SuppressWarnings("rawtypes")
								ArrayList listval = (ArrayList) val;
                                								
								if(cons.equals("valid_values"))
								{
									ArrayList<BNode> bnodes = new ArrayList<BNode>(listval.size()); //arraylist for the bnodes that we will need 
									List<BNode> unionList = new ArrayList<BNode>(); //list for all the blank nodes that are part of the union
	
									BNode r6 = Values.bnode(); 
									builder.subject("ex:"+capability_name); //set subject to the class
									builder.add(RDFS.SUBCLASSOF, r6); 
									builder.subject(r6);
									int b=0;//counter
									
									for (Object temp : listval) //for each element in valid values list
									{
                                        bnodes.add(Values.bnode()); //add a bnode to the bnode list
										unionList.add(bnodes.get(b)); //add the bnode to the union list
										builder.subject(bnodes.get(b)); 
										builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property protocol
									    builder.add(OWL.ONPROPERTY,attribute);
										builder.add(OWL.ALLVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/"+temp));
							            b++;
									}
									  BNode head = Values.bnode(); // blank node for the head of the list

									  RDFCollections.asRDF(unionList, head, builder.build());

				 					  builder.subject(r6).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head);
									
								}
								if(cons.equals("min_length")||cons.equals("max_length"))
								{
									builder.add(constr,RDFS.RANGE,"string");
									BNode r11 = Values.bnode();
									builder.subject("ex:"+capability_name); //set subject to the class
									builder.add(RDFS.SUBCLASSOF, r11); 
									builder.subject(r11);
									
									builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property
								    builder.add(OWL.ONPROPERTY,attribute);
									
									BNode r13 = Values.bnode();
									builder.add(RDFS.SUBCLASSOF, r13); 
									
									builder.subject(r13);
									builder.add(RDF.TYPE, OWL.RESTRICTION); 
								    builder.add(OWL.ONPROPERTY,constraints);
								    
								    BNode r14 = Values.bnode();
									builder.add(OWL.SOMEVALUESFROM,r14);
									
									builder.subject(r14);
									builder.add(RDF.TYPE, OWL.RESTRICTION); 
								    builder.add(OWL.HASVALUE,val);
									builder.add(OWL.ONPROPERTY,constr);							
								}
							}
						}
						builder.add(attribute,toscaProperty,"false");
						
						if (fourth_level.get("required") != null) 
						{											
							if(fourth_level.get("required").equals(true))
							{
								
								BNode r3 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r3);
								builder.subject(r3);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.MINCARDINALITY, 1);
							}
							else if(fourth_level.get("required").equals(false))
							{
								BNode r4 = Values.bnode();
								builder.subject("ex:"+capability_name);
								builder.add(RDFS.SUBCLASSOF, r4);
								builder.subject(r4);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.MINCARDINALITY, 0);

							}
						}
						
				}
		
			
			}
		   }
		   }
			Parse.m = builder.build();
			WriteFiles.Create();
			File file = new File("data_type.ttl");
			
			String url=Parse.repo;
			if(url!=null)
			{
				HTTPRepository repository = new HTTPRepository(url);
				String baseURI = url;
	       
	        try {
	           RepositoryConnection con = repository.getConnection();
	           try 
	           {
	              con.add(file, baseURI, RDFFormat.TURTLE);
	           }
	           finally 
	           {
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
	
