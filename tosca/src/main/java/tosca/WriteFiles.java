package tosca;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;  

public class WriteFiles 
{
	
	public static void Create() throws IOException 
	{
		
		String caller_class="";
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        boolean flag = false;
		for (int i=1; i<stElements.length; i++) 
        {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(WriteFiles.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0 && flag==false) 
            {   

      	        caller_class = ste.getClassName();
      	        flag = true;
            }
        }
		File file = null;
        String f = Parse.folder;	

        if(f==null)
        {
		try
		{
		  	if(caller_class.equals("tosca.DataType"))
			{
				file = new File("data_type.ttl");
			}
			else if(caller_class.equals("tosca.NodeType"))
			{
				file = new File("node_type.ttl");
			}
			else if(caller_class.equals("tosca.CapabilityType"))
			{
				file = new File("capability_type.ttl");
			}
			else if(caller_class.equals("tosca.NodeTemplates"))
			{
				file = new File("node_templates.ttl");
			}
			if (file.createNewFile()) 
			{
						System.out.println("File created: " + file.getName() + " at " + System.getProperty("user.dir"));
			}	 
			else
			{		

						System.out.println("File " + file.getName() + " updated at " + System.getProperty("user.dir"));
			}
		  
		} 
		catch (IOException e) 
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		FileOutputStream out = new FileOutputStream(file.getName());	
		RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);	
		System.getProperty("user.dir");
		try 
		{
		  writer.startRDF();
		  for (Statement st: Parse.m)
		  {
		    writer.handleStatement(st);
		  }
		  writer.endRDF();
		}
		catch (RDFHandlerException e) 
		{
		 // oh no, do something!
		}
		finally 
		{
		  out.close();
		
		}		
		
	  }
	
	else
	{
		{
			try
			{
			  	if(caller_class.equals("tosca.DataType"))
				{
					file = new File(f+"\\data_type.ttl");
					System.out.println(file);
				}
				else if(caller_class.equals("tosca.NodeType"))
				{
					file = new File(f+"\\node_type.ttl");
				}
				else if(caller_class.equals("tosca.CapabilityType"))
				{
					file = new File(f+"\\capability_type.ttl");
				}
				else if(caller_class.equals("tosca.NodeTemplates"))
				{
					file = new File(f+"\\node_templates.ttl");
				}
				if (file.createNewFile()) 
				{
							System.out.println("File created: " + file.getName() + " at " +f);
				}	 
				else
				{		

							System.out.println("File " + file.getName() + " updated at " +f);
				}
			  
			} 
			catch (IOException e) 
			{
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
			FileOutputStream out2 = new FileOutputStream(f+"\\"+file.getName());	
			RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out2);	
			try 
			{
			  writer.startRDF();
			  for (Statement st: Parse.m)
			  {
			    writer.handleStatement(st);
			  }
			  writer.endRDF();
			}
			catch (RDFHandlerException e) 
			{
				 out2.close(); // oh no, do something!
			}
			finally 
			{
			  out2.close();
			
			}		
			
		  }
	}

	}
}
