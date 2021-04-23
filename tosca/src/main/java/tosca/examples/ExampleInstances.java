package tosca.examples;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class ExampleInstances {
	
	
	/*
	
	snow-vm:  
		type: sodalite.nodes.OpenStack.VM
		properties:  
			key_name:  "ssh-key-name"
			image: "image-name"
			name:  "snow-vm_am_1"
			network: "openstack-network-name"  
			security_groups: "security-groups"
			flavor: "flavor-name"
			username: "centos"
		requirements:  
			protected_by: 
				node: snow-security-rules
	*/
	
	public static void main(String[] args) {
		
		BNode r1 = Values.bnode();
		BNode r2 = Values.bnode();
		BNode r3 = Values.bnode();

		
		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace("tosca", "https://intelligence.csd.auth.gr/ontologies/tosca/").setNamespace("ex", "http://examples/")
		.subject("ex:snow-vm").add(RDF.TYPE, "ex:sodalite.nodes.OpenStack.VM")
		
		
		.add("ex:key-name", "ssh-key-name").add("ex:image", "image-name") // more properties
		
		.subject("ex:snow-vm").add("tosca:requirements", r2)
		.subject(r2).add("tosca:protected_by", r3)
		.subject(r3).add("tosca:node", "ex:snow-security-rules");
		
		
		Model model = builder.build();
		Rio.write(model, System.out, RDFFormat.TURTLE);
		
		
		
	}
	
	

}
