package tosca;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

public class ExampleRestriction {

	public static void main(String[] args) {


		// all restrictions are blank nodes
		BNode r1 = Values.bnode();

		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace("ex", "http://example.org/").subject("ex:SoftwareComponemt").add(RDF.TYPE, OWL.CLASS)
				.add(RDFS.SUBCLASSOF, "ex:Root")
				.add(RDFS.SUBCLASSOF, r1)
				.subject(r1) //we switch to restriction and start defining it
				.add(RDF.TYPE, OWL.RESTRICTION)
				.add(OWL.ONPROPERTY, "ex:componentVersion")
				.add(OWL.MINCARDINALITY, 0);
		
		
		Model model = builder.build();
		model.forEach(System.out::println);
		
	}

}
