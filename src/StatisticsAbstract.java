import java.io.File;
import java.util.List;

/**
 * 
 * @author Patent
 * Testing for abstract
 */
public class StatisticsAbstract {

	private final Config config;

	public StatisticsAbstract(Config config) {
		this.config = config;
	}

	public static void main(String args[]) throws Exception{
		String configPath = "config.json";
		if(args.length > 0) {
			configPath = args[0];
		}
		File configFile = new File(configPath);
		if(!configFile.exists()){
			File jarFile = new File(System.getProperty("java.class.path"));
			configFile = new File(jarFile.getParent(),configPath);
		}
		Config config = Config.load(configFile);
		new StatisticsAbstract(config).run();
	}

	private void run() throws Exception {
		DBConn conn = DBConn.make(config);
		System.out.println("database connection success");		
		List<String> idList = conn.getIdList();
		//conn.getAbstractPatent(idList);
		System.out.println("Over");
		conn.close();
	}
	
}
