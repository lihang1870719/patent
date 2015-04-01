import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.Buffer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExportXML {

	
	private final Config config;

	public ExportXML(Config config) {
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
		new ExportXML(config).run();
	}

	private void run() throws Exception {
		DBConn conn = DBConn.make(config);
		System.out.println("database connection success");		
		List<String> idList = conn.getIdList();
		String content = "";
		List<OriginData> contentList = conn.getOriginDatas(idList);
		int count = 0;
		for(OriginData oData : contentList){
			count++;
			String id = oData.getId();
			File file = new File("D:/" + id + ".xml");
			if(!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			content = oData.getContent();
			bufferWriter.write(content);
			bufferWriter.close();
			System.out.println("Now in percent: " + Math.floor(count/10) + "%");					
		}
		/*String content = "";
		List<OriginData> contentList = conn.getOriginDatas(idList);
		String descriptionBefore = "<description id=\"description\">";
		String descriptionAfter = "</description>";
		int count = 0;
		File file = new File("D:/a.txt");
		if(!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		for(OriginData oData : contentList){
			content = oData.getContent();
			String classIdDsp = segmentation(content, descriptionBefore, descriptionAfter);
			String strDsp = classIdDsp;			
			System.out.println(strDsp);
			bufferWriter.write(strDsp);
			count++;
			System.out.println("Now in percent: " + Math.floor(count/10) + "%");					
		}

		bufferWriter.close();*/		
		System.out.println("Over");
		conn.close();
	}
	public String segmentation(String content, String s, String e) throws Exception {
		String word = "";
		String regex = "(?<=(" + s + "))[.\\s\\S]*?(?=(" + e + "))";
		System.out.println(regex);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			word = matcher.group();
		}
		return word;
	}
	
}
