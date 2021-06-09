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
import org.eclipse.rdf4j.rio.Rio;

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
						attribute_names.add(key3);

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
							builder.add(properties,RDFS.RANGE,ex+fourth_level.get("type"));

						}
						else
						{
							builder.add(properties,RDF.TYPE,"owl:DatatypeProperty");
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
							
							IRI entry_schema = Values.iri(ex,"entry_schema");
							if ((fifth_level.get("type").equals("string")) || (fifth_level.get("type").equals("integer")) ||(fifth_level.get("type").equals("float")) || (fifth_level.get("type").equals("boolean")))
							{
								builder.add(entry_schema,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(entry_schema,RDFS.RANGE,fourth_level.get("type"));	
							}
							else
							{
								builder.add(entry_schema,RDF.TYPE,"owl:ObjectProperty");
								builder.add(entry_schema,RDFS.RANGE,ex+fourth_level.get("type"));	
							}
							builder.add(entry_schema,RDF.TYPE,"owl:ObjectProperty");
						}
				
						//for required
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
	
									BNode r6 = Values.bnode(); 
									builder.subject("ex:"+node_name); //set subject to the class
									builder.add(RDFS.SUBCLASSOF, r6); 
									builder.subject(r6);
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

				 					  builder.subject(r6).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head);
									
								}
								if(cons.equals("min_length")||cons.equals("max_length"))
								{
									builder.add(constr,RDFS.RANGE,"string");
									BNode r11 = Values.bnode();
									builder.subject("ex:"+node_name); //set subject to the class
									builder.add(RDFS.SUBCLASSOF, r11); 
									builder.subject(r11);
									builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property
								    builder.add(OWL.ONPROPERTY,properties);//property some constraints
									builder.add(OWL.SOMEVALUESFROM,constraints);
									
									BNode r12 = Values.bnode();
									builder.subject(r12);
									builder.add(RDF.TYPE, OWL.RESTRICTION); 
								    builder.add(OWL.ONPROPERTY,constraints);
									builder.add(OWL.SOMEVALUESFROM,constr);//constraints some length
									
									BNode r14 = Values.bnode();
									builder.subject(r14);
									builder.add(RDF.TYPE, OWL.RESTRICTION); 
								    builder.add(OWL.ONPROPERTY,constr);
									builder.add(OWL.HASVALUE,val);								
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
							if ((fourth_level.get("type").equals("string")) || (fourth_level.get("type").equals("integer")) || (fourth_level.get("type").equals("float")) || (fourth_level.get("type").equals("boolean"))) 
							{
								builder.add(attribute,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(attribute,RDFS.RANGE,fourth_level.get("type"));
			
							}
							else if(((String) fourth_level.get("type")).startsWith("tosca"))
							{
								builder.add(attribute,RDF.TYPE,"owl:ObjectProperty");
								builder.add(attribute,RDFS.RANGE,ex+fourth_level.get("type"));

							}
							else
							{
								builder.add(attribute,RDF.TYPE,"owl:DatatypeProperty");
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
							IRI entry_schema = Values.iri(ex,"entry_schema");
							if ((fifth_level.get("type").equals("string")) || (fifth_level.get("type").equals("integer")) ||(fifth_level.get("type").equals("float")) || (fifth_level.get("type").equals("boolean")))
							{
								builder.add(entry_schema,RDF.TYPE,"owl:DatatypeProperty");
								builder.add(entry_schema,RDFS.RANGE,fourth_level.get("type"));	
							}
							else
							{
								builder.add(entry_schema,RDF.TYPE,"owl:ObjectProperty");
								builder.add(entry_schema,RDFS.RANGE,ex+fourth_level.get("type"));	
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
		
										BNode r6 = Values.bnode(); 
										builder.subject("ex:"+node_name); //set subject to the class
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
											builder.add(OWL.HASVALUE,temp);
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
										builder.subject("ex:"+node_name); //set subject to the class
										builder.add(RDFS.SUBCLASSOF, r11); 
										builder.subject(r11);
										builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property
									    builder.add(OWL.ONPROPERTY,attribute);//property some constraints
										builder.add(OWL.SOMEVALUESFROM,constraints);
										
										BNode r12 = Values.bnode();
										builder.subject(r12);
										builder.add(RDF.TYPE, OWL.RESTRICTION); 
									    builder.add(OWL.ONPROPERTY,constraints);//constraints some length
										builder.add(OWL.SOMEVALUESFROM,constr);
									
										BNode r14 = Values.bnode();
										builder.subject(r14);
										builder.add(RDF.TYPE, OWL.RESTRICTION); 
									    builder.add(OWL.ONPROPERTY,constr);
										builder.add(OWL.HASVALUE,val);								
									}
								}
						}
						
						//for required
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
            	
            	int valid=0;
				IRI valid_source_types=Values.iri(ex,"valid_source_types");

				if (fourth_level.get("valid_source_types") != null) 
				{
					builder.add(valid_source_types,RDF.TYPE,"owl:ObjectProperty");
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
				builder.add(OWL.SOMEVALUESFROM,r6); 
				
				if(type!=0)
				{
				  if(valid==0) // if there is only host type
				  {
					builder.subject(r6);
					builder.add(RDF.TYPE, OWL.RESTRICTION);
					builder.add(OWL.ONPROPERTY, capability);
					Object type_host=fourth_level.get("type");
					builder.add(OWL.SOMEVALUESFROM,type_host);
				  }
				  
				  else 	 //if there is valid source types 
				  {
					  //capabilities some ( host some tosca.capabilities.Node and host some 
					  //(valid_source_types some hasvalue ... or has value)(
					  
					  BNode r8 = Values.bnode(); // for host some valid source types ..
					  BNode r9 = Values.bnode(); // for valid source types some..

                	  builder.subject(r8);// for host some tosca
					  builder.add(RDF.TYPE, OWL.RESTRICTION);
				      builder.add(OWL.ONPROPERTY, capability);
					  Object type_host=fourth_level.get("type");
					  builder.add(OWL.SOMEVALUESFROM,ex+type_host);

				      BNode r20= Values.bnode();
				      
					  builder.subject(r9);
					  builder.add(RDF.TYPE, OWL.RESTRICTION);
				      builder.add(OWL.ONPROPERTY, capability);
				      builder.add(OWL.SOMEVALUESFROM,r20);
				 
				      Object valid_host=fourth_level.get("valid_source_types");
					  builder.subject(r20);
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
							builder.add(RDF.TYPE, OWL.RESTRICTION); //restriction on property protocol
						    builder.add(OWL.ONPROPERTY,valid_source_types);
							builder.add(OWL.ALLVALUESFROM,ex+temp);
				            b++;
						}
						  BNode head2 = Values.bnode(); // blank node for the head of the list

						  RDFCollections.asRDF(unionList, head2, builder.build());

	 					  builder.subject(r20).add(RDF.TYPE, OWL.CLASS).add(OWL.UNIONOF, head2);
					
	 					  List<BNode> intersectionList = new ArrayList<BNode>();
	 					  intersectionList.add(r8);
					
	 					  intersectionList.add(r9);
					  
	 					  BNode head = Values.bnode();

	 					  RDFCollections.asRDF(intersectionList, head, builder.build());

	 					  builder.subject(r6).add(RDF.TYPE, OWL.CLASS).add(OWL.INTERSECTIONOF, head);

				  }
				}
				
			}
		}
		Parse.m = builder.build();
		Rio.write(Parse.m, System.out, RDFFormat.TURTLE);

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
