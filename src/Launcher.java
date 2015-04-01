import java.io.File;


public class Launcher {

	/**
	 * ��ڳ���
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
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
		new ParseTask(config).run();
	}

}
