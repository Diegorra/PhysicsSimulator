package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;

public class MassEqualState implements StateComparator {

	public MassEqualState() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equal(JSONObject s1, JSONObject s2) {
		boolean equal = false;
		
		JSONArray ja1 = s1.getJSONArray("bodies");
		JSONArray ja2 = s2.getJSONArray("bodies");
		
		for(int i = 0; i < ja1.length(); i++) {
			equal = ((ja1.getJSONObject(i).get("id").equals(ja2.getJSONObject(i).get("id"))) && (ja1.getJSONObject(i).getDouble("m") == ja2.getJSONObject(i).getDouble("m")));
		}
		return ((s1.getDouble("time") == s2.getDouble("time")) && equal);
	}

}
