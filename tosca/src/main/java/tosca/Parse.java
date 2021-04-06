package tosca;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;

public class Parse {
	
	private static HashMap<String, HashMap<String, Object>> map;
	
	
	
	public static void main(String[] args) throws IOException

	  {
		Parse p = new Parse();
		//p.loadyaml();
		 InputStream inputStream = new FileInputStream("example.yml");
		    Yaml yaml = new Yaml();
		    HashMap<String, HashMap<String,Object>> data = new HashMap<>();
		    data = yaml.load(inputStream);
		    //System.out.print(data)
	        p.setMap(data);
         //System.out.print(p.getMap());
         ArrayList<String> list = new ArrayList<String>();
 		for ( String key : data.keySet() ) {
 		    list.add(key);
 		}
 		
 		for(String x : list){

 			if(x.equals("node_types"))
 			{
 			  Nodes node_t = new Nodes();
 			  node_t.nodeTypes();
 		    }
 			if(x.equals("data_types"))
 			{
 			  Data data_t = new Data();
 			  data_t.dataTypes();
 		    }
 			
 		}
	  }
	
	public void setMap(HashMap<String, HashMap<String, Object>> map) {
		this.map = map;
	}
	
	public HashMap<String, HashMap<String, Object>> getMap() {
		return map;
	}
	
}
 
