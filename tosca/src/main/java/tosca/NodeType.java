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

//class for handling node types

public class NodeType 
{

	Parse map = new Parse();

	@SuppressWarnings("unchecked")
	void nodeTypes() throws IOException 
	{

		String node_name = null;
		Object derived_from = null;
		HashMap<String, Object> map2 = new HashMap<>();
		String ex = "https://intelligence.csd.auth.gr/ontologies/tosca/";
		map2 = map.getMap().get("node_types");
    	final int c[]= {0};
    	final int a[]= {0};
    	final int p[]= {0};
		ModelBuilder builder = new ModelBuilder();

		for (String key : map2.keySet()) 
		{
			ArrayList<String> attribute_names = new ArrayList<>();
			ArrayList<String> properties_names = new ArrayList<>();
			ArrayList<String> capabilities_names = new ArrayList<>();
			HashMap<String, Object> second_level = map2;
			HashMap<String, Object> third_level = map2;
			HashMap<String, Object> fourth_level = map2;
			HashMap<String, Object> fifth_level = map2;
			node_name = key;
			IRI tosca_description = null;
			IRI toscaDefault = null;
			IRI toscaType = null;
			IRI requirements = null;
			IRI toscaProperty= null;
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
			
			// second level of hashmap
			// key names = [derived_from,properties,attributes,entry_schema,capabilities]
			second_level = ((HashMap<String, Object>) map2.get(node_name));
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
				else if (key2.equals("attributes")) 
				{
					third_level = (HashMap<String, Object>) second_level.get("attributes");
					a[0]=1;
					for (String key3 : third_level.keySet()) 
					{
						attribute_names.add(key3);

				    }
				} 
				else if (key2.equals("capabilities")) 
				{
					
					third_level = (HashMap<String, Object>) second_level.get("capabilities");
					c[0]=1;	
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
						// check if the type is "normal" datatype or toscaType
						if ((fourth_level.get("type").equals("string")) || (fourth_level.get("type").equals("integer")) || (fourth_level.get("type").equals("float")) || (fourth_level.get("type").equals("boolean"))) 
						{
							builder.add(properties,RDF.TYPE,"owl:DatatypeProperty");
							builder.add(properties,RDFS.RANGE,fourth_level.get("type"));
						
						}
						else if(((String) fourth_level.get("type")).startsWith("tosca"))
						{
							builder.add(properties,RDF.TYPE,"owl:ObjectProperty");
							BNode r25 = Values.bnode();
							builder.subject("ex:"+node_name);
							builder.add(RDFS.SUBCLASSOF, r25);
							builder.subject(r25);
							builder.add(RDF.TYPE, OWL.RESTRICTION);
							builder.add(OWL.ONPROPERTY, properties);
							builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + fourth_level.get("type")));
						}
						else
						{
							builder.add(properties,RDF.TYPE,"owlObjectProperty");
							builder.add(properties,toscaType,fourth_level.get("type"));

						}
						
						//for default
						if (fourth_level.get("default") != null) 
						{
							builder.add(properties,toscaDefault,Values.literal(fourth_level.get("default")));

						}
						
						//for description
						if (fourth_level.get("description") != null) 
						{
							builder.add(properties,tosca_description,Values.literal(fourth_level.get("description")));
						}
						
						//for entry schema
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
								IRI property_entry_schema_objectproperty= Values.iri(ex,"property_entry_schema_objectproperty");
								builder.add(property_entry_schema_objectproperty,RDF.TYPE,"owl:ObjectProperty");
								BNode r18 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r18);
								builder.subject(r18);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								BNode r14 = Values.bnode();
								builder.add(OWL.SOMEVALUESFROM,r14);	
								builder.subject(r14);
								builder.add(RDF.TYPE, OWL.RESTRICTION); 
							    builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + fifth_level.get("type")) );	
								builder.add(OWL.ONPROPERTY,property_entry_schema_objectproperty);
							}
						}
				
						//for required
						if (fourth_level.get("required") != null) 
						{											
							if(fourth_level.get("required").equals(true))
							{
								BNode r21 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r21);
								builder.subject(r21);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 1);

							}
							else if(fourth_level.get("required").equals(false))
							{
								BNode r22 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r22);
								builder.subject(r22);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, properties);
								builder.add(OWL.MINCARDINALITY, 0);
							}
						}
						
						//for constraints
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
	
									BNode r26 = Values.bnode(); 
									builder.subject("ex:"+node_name); //set subject to the class
									builder.add(RDFS.SUBCLASSOF, r26); 
									builder.subject(r26);
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

				 					  builder.subject(r26).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head);
									
								}
								if(cons.equals("min_length")||cons.equals("max_length"))
								{
									builder.add(constr,RDFS.RANGE,"string");
									BNode r21 = Values.bnode();
									builder.subject("ex:"+node_name); //set subject to the class
									builder.add(RDFS.SUBCLASSOF, r21); 
									builder.subject(r21);
									
									builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property
								    builder.add(OWL.ONPROPERTY,properties);
									
									BNode r23 = Values.bnode();
									builder.add(RDFS.SUBCLASSOF, r23); 
									
									builder.subject(r23);
									builder.add(RDF.TYPE, OWL.RESTRICTION); 
								    builder.add(OWL.ONPROPERTY,constraints);
								    
								    BNode r24 = Values.bnode();
									builder.add(OWL.SOMEVALUESFROM,r24);
									
									builder.subject(r24);
									builder.add(RDF.TYPE, OWL.RESTRICTION); 
								    builder.add(OWL.HASVALUE,val);
									builder.add(OWL.ONPROPERTY,constr);								
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
						
						//checking type
						if (fourth_level.get("type") != null) 
						{
							if (((String) fourth_level.get("type")).equals("string") || ((String) fourth_level.get("type")).equals("integer") || ((String) fourth_level.get("type")).equals("float") || ((String) fourth_level.get("type")).equals("boolean"))
							{
								builder.add(attribute,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(attribute,RDFS.RANGE,(String) fourth_level.get("type"));
			
							}
							else if(((String) fourth_level.get("type")).startsWith("tosca"))	
							{
								builder.add(attribute,RDF.TYPE,"owl:ObjectProperty");
								BNode r35 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r35);
								builder.subject(r35);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + (String) fourth_level.get("type")));
							}
							else
							{
								builder.add(attribute,RDF.TYPE,"owl:ObjectProperty");
								builder.add(attribute,toscaType,fourth_level.get("type"));
							}
						}
						
						//for description
						if (fourth_level.get("description") != null) 
						{
							builder.add(attribute,tosca_description,Values.literal(fourth_level.get("description")));
						}
						
						//for entry_schema
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
								BNode r38 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r38);
								builder.subject(r38);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								BNode r34 = Values.bnode();
								builder.add(OWL.SOMEVALUESFROM,r34);	
								builder.subject(r34);
								builder.add(RDF.TYPE, OWL.RESTRICTION); 
							    builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + fifth_level.get("type")));	
								builder.add(OWL.ONPROPERTY,property_entry_schema_objectproperty);	
							}
						}
						
						//for constraints
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
															
									if(cons.equals("valid_values"))
									{
										@SuppressWarnings("rawtypes")
										ArrayList listval = (ArrayList) val;
	                                		
										ArrayList<BNode> bnodes = new ArrayList<BNode>(listval.size()); //arraylist for the bnodes that we will need 
										List<BNode> unionList = new ArrayList<BNode>(); //list for all the blank nodes that are part of the union
		
										BNode r46 = Values.bnode(); 
										builder.subject("ex:"+node_name); //set subject to the class
										builder.add(RDFS.SUBCLASSOF, r46); 
										builder.subject(r46);
										int b=0;//counter
										
										for (Object temp : listval) //for each element in valid values list
										{
	                                        bnodes.add(Values.bnode()); //add a bnode to the bnode list
											unionList.add(bnodes.get(b)); //add the bnode to the union list
											builder.subject(bnodes.get(b)); 
											builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property protocol
										    builder.add(OWL.ONPROPERTY,attribute);
											builder.add(OWL.HASVALUE,temp);
								            b++;
										}
										  BNode head = Values.bnode(); // blank node for the head of the list

										  RDFCollections.asRDF(unionList, head, builder.build());

					 					  builder.subject(r46).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head);
										
									}
									if(cons.equals("min_length")||cons.equals("max_length"))
									{
										builder.add(constr,RDFS.RANGE,"string");
										BNode r41 = Values.bnode();
										builder.subject("ex:"+node_name); //set subject to the class
										builder.add(RDFS.SUBCLASSOF, r41); 
										builder.subject(r41);
										
										builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property
									    builder.add(OWL.ONPROPERTY,attribute);
										
										BNode r43 = Values.bnode();
										builder.add(RDFS.SUBCLASSOF, r43); 
										
										builder.subject(r43);
										builder.add(RDF.TYPE, OWL.RESTRICTION); 
									    builder.add(OWL.ONPROPERTY,constraints);
									    
									    BNode r44 = Values.bnode();
										builder.add(OWL.SOMEVALUESFROM,r44);
										
										builder.subject(r44);
										builder.add(RDF.TYPE, OWL.RESTRICTION); 
									    builder.add(OWL.HASVALUE,val);
										builder.add(OWL.ONPROPERTY,constr);									
									}
								}
						}
						
						//for required
						if (fourth_level.get("required") != null) 
						{											
							if(fourth_level.get("required").equals(true))
							{
								
								BNode r33 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r33);
								builder.subject(r33);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.MINCARDINALITY, 1);
							}
							else if(fourth_level.get("required").equals(false))
							{
								BNode r34 = Values.bnode();
								builder.subject("ex:"+node_name);
								builder.add(RDFS.SUBCLASSOF, r34);
								builder.subject(r34);
								builder.add(RDF.TYPE, OWL.RESTRICTION);
								builder.add(OWL.ONPROPERTY, attribute);
								builder.add(OWL.MINCARDINALITY, 0);

							}
						}
						
						builder.add(attribute,toscaProperty,"false");

				
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
				fourth_level = (HashMap<String, Object>) third_level.get(capabilities_names.get(j));	
                int type=0;
            	if (fourth_level.get("type") != null) 
				{
            		type=1;

				}
            	//for description
				if (fourth_level.get("description") != null) 
				{
					builder.add(capability,tosca_description,Values.literal(fourth_level.get("description")));
				}
				
            	
            	int valid=0;
				IRI valid_source_types=Values.iri(ex,"valid_source_types");

				if (fourth_level.get("valid_source_types") != null) 
				{
					builder.add(valid_source_types,RDF.TYPE,"owl:ObjectProperty");
					valid=1;
				}
		     	
				//start
				BNode r35 = Values.bnode();
				BNode r36 = Values.bnode();
				builder.subject("ex:"+node_name);
				builder.add(RDFS.SUBCLASSOF, r35);
				builder.subject(r35);
				builder.add(RDF.TYPE, OWL.RESTRICTION);
				builder.add(OWL.ONPROPERTY, capabilities);
				builder.add(OWL.SOMEVALUESFROM,r36); 
				
				if(type!=0)
				{
				  if(valid==0) // if there is only host type
				  {
					builder.subject(r36);
					builder.add(RDF.TYPE, OWL.RESTRICTION);
					builder.add(OWL.ONPROPERTY, capability);
					Object type_host=fourth_level.get("type");
					builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + type_host));
				  }
				  
				  else 	 //if there is valid source types 
				  {
					  //capabilities some ( host some tosca.capabilities.Node and host some 
					  //(valid_source_types some only ... or only)
					  
					  BNode r38 = Values.bnode(); // for host some type ..
					  BNode r39 = Values.bnode(); // for host some valid source types 
				      BNode r40= Values.bnode(); // for valid source types 

                	  builder.subject(r38);// for host some type
					  builder.add(RDF.TYPE, OWL.RESTRICTION);
				      builder.add(OWL.ONPROPERTY, capability);
					  Object type_host=fourth_level.get("type");
					  builder.add(OWL.SOMEVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + type_host));

					  builder.subject(r39); // for host some valid source types 
					  builder.add(RDF.TYPE, OWL.RESTRICTION);
				      builder.add(OWL.ONPROPERTY, capability);
				      builder.add(OWL.SOMEVALUESFROM,r40);
				 
				      Object valid_host=fourth_level.get("valid_source_types");
					  builder.subject(r40);
					  @SuppressWarnings("rawtypes")
					  ArrayList listval = (ArrayList) valid_host;
					  ArrayList<BNode> bnodes = new ArrayList<BNode>(listval.size()); //arraylist for the bnodes that we will need 
					  List<BNode> unionList = new ArrayList<BNode>(); //list for all the blank nodes that are part of the union
					  int b=0;//counter
						
						for (Object temp : listval) //for each element in valid values list
						{
                          bnodes.add(Values.bnode()); //add a bnode to the bnode list
							unionList.add(bnodes.get(b)); //add the bnode to the union list
							builder.subject(bnodes.get(b)); 
							builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property
						    builder.add(OWL.ONPROPERTY,valid_source_types);
						    builder.add(OWL.ALLVALUESFROM,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + temp));
				            b++;
						}
						  BNode head2 = Values.bnode(); // blank node for the head of the list

						  RDFCollections.asRDF(unionList, head2, builder.build());

	 					  builder.subject(r40).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head2);
					
	 					  List<BNode> intersectionList = new ArrayList<BNode>();//list for all the blank nodes that are part of the intersection
	 					  intersectionList.add(r38);
					
	 					  intersectionList.add(r39);
					  
	 					  BNode head = Values.bnode();

	 					  RDFCollections.asRDF(intersectionList, head, builder.build());

	 					  builder.subject(r36).add(RDF.TYPE, OWL.CLASS).add(OWL.INTERSECTIONOF, head);

				  }
				}
				
			}
		}
		}
		Parse.m = builder.build();		
		WriteFiles.Create();
		String url=Parse.repo;
		if(url!=null)
		{
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

