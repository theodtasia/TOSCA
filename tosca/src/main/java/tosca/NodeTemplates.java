package tosca;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;

public class NodeTemplates
{
	Parse map = new Parse();
	@SuppressWarnings("unchecked")
	void nodeTemplates() throws IOException 
	{	
		String template_name = null;
		
		String type=null;
		HashMap<String, Object> map2 = new HashMap<>();

		final int r[]= {0};
    	final int a[]= {0};
    	final int p[]= {0};
		map2 = map.getMap().get("node_templates");
		ModelBuilder builder = new ModelBuilder();
		String tosca = "https://intelligence.csd.auth.gr/ontologies/tosca/";
		builder.setNamespace("tosca", "https://intelligence.csd.auth.gr/ontologies/tosca/");
		IRI toscaTemplateProperty = Values.iri(tosca, "toscaTemplateProperty");
		builder.subject(tosca + template_name)
		.add(toscaTemplateProperty,RDF.TYPE,OWL.ANNOTATIONPROPERTY);
		for (String key : map2.keySet()) 
		{
			HashMap<String, Object> second_level = map2;
			HashMap<String, Object> third_level = map2;
			HashMap<String, Object> fourth_level = map2;
			HashMap<String, Object> fifth_level = map2;

			ArrayList<String> attribute_names = new ArrayList<>();
			ArrayList<String> properties_names = new ArrayList<>();
			ArrayList<String> requirements_names = new ArrayList<>();;
			template_name = key;
			builder.subject(tosca+template_name);
			IRI tosca_description = Values.iri(tosca,"tosca_description");
			second_level = ((HashMap<String, Object>) map2.get(template_name));
			for (String key2 : second_level.keySet()) 
			{	
					if (key2.equals("type")) 
					{
						type = (String) second_level.get("type"); 
						builder.add(RDF.TYPE, Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + type));
					}
					else if(key2.equals("description")) 
					{
						builder.add(template_name,tosca_description,Values.literal(fourth_level.get("description")));
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
					else if (key2.equals("requirements")) 
					{
						r[0]=1;	
						third_level = (HashMap<String, Object>) second_level.get("requirements");
						for (String key3 : third_level.keySet()) 
						{
							requirements_names.add(key3);
					    }
					}
			 }
		    
			
			List<BNode> vars = new ArrayList<>();
			
			//for properties
			if(p[0]!=0)
			{
				p[0]=0;
				second_level = ((HashMap<String, Object>) map2.get(template_name));
				third_level = (HashMap<String, Object>) second_level.get("properties");
				for (int j = 0; j < properties_names.size(); j++) 
				{
				    String key1 = properties_names.get(j);
					String value = third_level.get(properties_names.get(j)).toString();
					if(value.contains("{")) //if property has more levels
					{  
						int v=0;
						fourth_level = (HashMap<String, Object>) third_level.get(key1);
						for (Object key4 : fourth_level.keySet())  
						{								
							vars.add(Values.bnode()); 
							builder.add(tosca+key1, vars.get(v)).subject(vars.get(v));
							builder.add(toscaTemplateProperty, tosca+key4);
							
							fifth_level=(HashMap<String, Object>)fourth_level.get(key4);
							builder.add(toscaTemplateProperty, tosca+key4);
							for (String key5 : fifth_level.keySet()) 
							{
								builder.add(tosca+key5, fifth_level.get(key5));
							}
							v++;

						}
					 }
					 else
					 {
						builder.subject(tosca+template_name);
						builder.add(tosca+key1, value);
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
				  String key1 = attribute_names.get(j);
				  if(third_level.get(attribute_names.get(j)) != null)
				  {	
					String value = third_level.get(attribute_names.get(j)).toString();
					
					if(value.contains("{")) //if property has more levels
					{  
						int v=0;
						fourth_level = (HashMap<String, Object>) third_level.get(key1);
						for (Object key4 : fourth_level.keySet())  
						{								
							vars.add(Values.bnode());
							builder.add(tosca+key1, vars.get(v)).subject(vars.get(v));
							builder.add(toscaTemplateProperty, tosca+key4);

							fifth_level=(HashMap<String, Object>)fourth_level.get(key4);
							builder.add(toscaTemplateProperty, tosca+key4);
							for (String key5 : fifth_level.keySet()) 
							{
								builder.add(tosca+key5, fifth_level.get(key5));
							}
							v++;

						}
					 }
					 else
					 {
						builder.subject(tosca+template_name);
						builder.add(tosca+key1, value);
					 }			
				  }
				}
			}
			
			//for requirements
			if(r[0]!=0)
			{
				r[0]=0;
				BNode r60 = Values.bnode();
				BNode r61 = Values.bnode();
				third_level = (HashMap<String, Object>) second_level.get("requirements");
				IRI requirements= Values.iri(tosca,"requirements");
				
				for (int j = 0; j < requirements_names.size(); j++) 
				{
					builder.subject(tosca+template_name)
					.add(requirements, r60);
					
					Object key1 = requirements_names.get(j);
					builder.subject(r60).add(tosca+key1, r61);
					
					fifth_level=(HashMap<String, Object>) third_level.get(key1);
					for (String key5 : fifth_level.keySet()) 
					{
						builder.subject(r61);
						builder.add(tosca+key5,Values.iri("https://intelligence.csd.auth.gr/ontologies/tosca/" + fifth_level.get(key5)));
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
			File file = new File("node_templates.ttl");
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
	