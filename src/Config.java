import java.io.File;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;


public class Config {

	
	private final JSONObject json;

	public Config(JSONObject json) {
		this.json = json;
	}

	public String get(String key) throws JSONException {
		String[] subkeys = key.split("\\.");
		JSONObject parent = json;
		for (int i = 0; i < subkeys.length; i++) {
			String k = subkeys[i];
			if(i == subkeys.length - 1){
				return parent.get(k).toString();
			} else {
				parent = parent.getJSONObject(k);
			}
		}
		throw new RuntimeException(key + " does not exists");
	}

	public static Config load(File configFile) throws Exception {
		StringBuilder source = new StringBuilder();
		for (Scanner Line = new Scanner(configFile);  Line.hasNextLine();) {
			source.append(Line.nextLine());
		}
		System.out.println(source);
		JSONObject json = new JSONObject(source.toString());
		return new Config(json);
	}

	public int getInt(String key) throws Exception{
		return Integer.parseInt(get(key));
	}

	
}
