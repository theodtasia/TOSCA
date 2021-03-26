package tosca;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

public class Nodes {

	Parse map = new Parse();

	void nodeTypes() {
		String node_name = null;
		Object derived_from = null;
		final int i[] = { 0 };
		HashMap<String, Object> map2 = new HashMap<>();
		ArrayList<String> attribute_names = new ArrayList<>();
		map2 = map.getMap().get("node_types");
		HashMap<String, Object> second_level = map2;
		HashMap<String, Object> third_level = map2;
		HashMap<String, Object> fourth_level = map2;

		for (String key : map2.keySet()) {
			node_name = key;
			second_level = (HashMap<String, Object>) map2.get(node_name);
			for (String key2 : second_level.keySet()) {

				System.out.println("d " + key2);
				if (key2.equals("derived_from")) {
					derived_from = second_level.get("derived_from"); // this is not map2 but second_level
					System.out.println(derived_from);
				} else if (key2.equals("properties")) {
					System.out.println("d");

				} else if (key2.equals("attributes")) {
					third_level = (HashMap<String, Object>) second_level.get("attributes");
					for (String key3 : third_level.keySet()) {
						// fourth_level= (HashMap<String, Object>) third_level.get(key3);
						attribute_names.add(key3);
						// System.out.println("F" + fourth_level);

					}
					System.out.println("Third" + third_level);
				} else if (key2.equals("capabilities")) {

				}

			}
		}

		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace("ex", "https://intelligence.csd.auth.gr/ontologies/tosca/").subject("ex:" + node_name)
				.add(RDF.TYPE, "owl:Class")
				.add(RDFS.SUBCLASSOF, "https://intelligence.csd.auth.gr/ontologies/tosca/" + derived_from);
		
		/**
		 * Above, the same "subject" is used for all "add" definitions. So, the two "add" have as subject the ex:node_name:
		 * 
		 *  <ex:node_name type owl:Class>
		 *  <ex:node_name rdfs:subClassOf <drivedFrom>
		 *  
		 */
		
		
		for (int j = 0; j < attribute_names.size(); j++) {
			// System.out.println(third_level.get(third_level.get(attribute_names.get(j))));
			/**
			 * Properties are defined as objects as well. So for example, the property public_address should be defined as:
			 * <public_address, type, owl:DatatypeProperty>. So, we can not simply "add" this to the builder, since all these definitions will be added to the node_name.
			 * What needs to be done is:
			 * 1. create the property (the datatype property in this case
			 * 2. add the domain/range value or any other characteristics we want
			 */

			builder.add(OWL.DATATYPEPROPERTY,
					"https://intelligence.csd.auth.gr/ontologies/tosca/" + attribute_names.get(j));
			if (third_level.get(attribute_names.get(j)) != null) {
				fourth_level = (HashMap<String, Object>) third_level.get(attribute_names.get(j));
				if (fourth_level.get("type") != null) {
					builder.add(RDFS.RANGE, RDFS.RESOURCE, fourth_level.get("type"));
				}
			}
			
			/**
			 * Annotations are similar to other properties:
			 * 1. we need to define the annotation property, e.g. <our_annotation_property, type, owl:AnnotationProperty>
			 * 2. then we need to use it, e.g. <our class, our_property, value>
			 */
			
		}
		Model m = builder.build();
		m.forEach(System.out::println);

	}
}