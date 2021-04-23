package tosca.examples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class ExampleComplexRestriction2 {

	public static void main(String[] args) {

		// blank nodes
		BNode r1 = Values.bnode();
		BNode r2 = Values.bnode();
		BNode r3 = Values.bnode();
		BNode r4 = Values.bnode();
		BNode r5 = Values.bnode();
		BNode r6 = Values.bnode();

		ModelBuilder builder = new ModelBuilder();

		// Credential
		builder.setNamespace("tosca", "https://intelligence.csd.auth.gr/ontologies/tosca/")
				.subject("tosca:tosca.datatypes.Credential").add(RDF.TYPE, OWL.CLASS)
				.add(RDFS.SUBCLASSOF, "tosca:tosca.datatypes.Root").add(RDFS.SUBCLASSOF, r1)

		// definition of r1 and its association with r2 (the intersection which is
		// defined later)
		.subject(r1).add(RDF.TYPE, OWL.RESTRICTION).add(OWL.ONPROPERTY, "tosca:Requirements").add(OWL.SOMEVALUESFROM, r2)

		// definition of r3 and its association with r4
		.subject(r3).add(RDF.TYPE, OWL.RESTRICTION).add(OWL.ONPROPERTY, "tosca:host").add(OWL.SOMEVALUESFROM, r4)

		// definition of r4
		.subject(r4).add(RDF.TYPE, OWL.RESTRICTION).add(OWL.ONPROPERTY, "tosca:capability")
				.add(OWL.SOMEVALUESFROM, "tosca:tosca.capabilities.Compute")

		// definition of r5
		.subject(r5).add(RDF.TYPE, OWL.RESTRICTION).add(OWL.ONPROPERTY, "tosca:node")
				.add(OWL.SOMEVALUESFROM, "tosca:tosca.nodes.Compute")

		// definition of r6
		.subject(r6).add(RDF.TYPE, OWL.RESTRICTION).add(OWL.ONPROPERTY, "tosca:relationship")
				.add(OWL.SOMEVALUESFROM, "tosca:tosca.relationships.HostedOn");

		// First we create a list with all the blank nodes that are part of the
		// intersection
		List<BNode> intersectionList = new ArrayList<BNode>();
		intersectionList.add(r3);
		intersectionList.add(r5);
		intersectionList.add(r6);

		// we need a blank node for the head of the list
		BNode head = Values.bnode();

		// We create the list that is added to the model we are creating
		RDFCollections.asRDF(intersectionList, head, builder.build());

		// We associate r2 with the head of the list
		builder.subject(r2).add(RDF.TYPE, OWL.CLASS).add(OWL.INTERSECTIONOF, head);

		Model model = builder.build();
		model.forEach(System.out::println);
		System.out.println();
		Rio.write(model, System.out, RDFFormat.TURTLE);

	}

}
