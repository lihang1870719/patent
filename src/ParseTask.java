import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParseTask {

	private static int indent = 0;
	private final Config config;

	public ParseTask(Config config) {
		this.config = config;
	}

	public void run() throws Exception {
		DBConn conn = DBConn.make(config);
		println("database connection success");
/*		List<OriginData> contentList = conn.getOriginDatas();		
		for(OriginData oData : contentList){
			String content = oData.getContent();
			String id = oData.getId();
			String[] classId = segmentation(content);
			String str = classId[1];
			List<WordCount> wList = doCount(str);
			conn.update(wList, classId, id);
		}		*/
		List<String> idList = conn.getIdList();
		int length = idList.size();
		int count = 0;
		List<OriginData> contentList = conn.getOriginDatas(idList);
		String abstractBefore = "<abstract id=\"abstract\">";
		String abstractAfter = "</abstract>";
		String descriptionBefore = "<description id=\"description\">";
		String descriptionAfter = "</description>";
		String claimBefore = "<claims id=\"claims\">";
		String claimAfter = "</claims>";
		
		for(OriginData oData : contentList) {
			String content = oData.getContent();
			String id = oData.getId();					
			
/*			String[] classIdAb = segmentation(content, abstractBefore, abstractAfter, config.get("type.abstract"));
			String strAb = classIdAb[1];
			List<WordCount> wListAb = doCount(strAb);
			conn.update(wListAb, classIdAb, id, config.get("dataTable.abstract"));
			
			String[] classIdClm = segmentation(content, claimBefore, claimAfter, config.get("type.claim"));
			String strClm = classIdClm[1];
			List<WordCount> wListClm = doCount(strClm);
			System.out.println(id);
			conn.update(wListClm, classIdClm, id, config.get("dataTable.claim"));*/
			
			String[] classIdDsp = segmentation(content, descriptionBefore, descriptionAfter, config.get("type.description"));
			String strDsp = classIdDsp[1];
			List<WordCount> wListDsp = doCount(strDsp);
			conn.update(wListDsp, classIdDsp, id, config.get("dataTable.description"));
			count++;
			System.out.println("Now in percent: " + Math.floor(count*100/length) + "%");
		}
		println("Over");
		conn.close();
	}

	private static List<WordCount> doCount(String str) {
		List<WordCount> wList = WordCountService.getWordCount(str);
		String[] HighFqWord = new String[]{"p","id","num"};
		Iterator<WordCount> it = wList.iterator();
		while(it.hasNext()){
			String tempWord = it.next().getWord();
			for(int i = 0; i<HighFqWord.length; i++){
				
				if(tempWord.equals(HighFqWord[i]))
				{
					it.remove();
					break;
				}	
			}		
		}
/*		for(WordCount count : wList){
			System.out.println("word: "+count.getWord()+" count: "+count.getCount());
		}*/
		
		return wList;
	}

	public String[] segmentation(String content, String s, String e, String type) throws Exception {
		String[] wordConnection = new String[2];
		//String regex = "<claim-text>(.*?)</claim-text>";
		String regex = "(?<=(" + s + "))[.\\s\\S]*?(?=(" + e + "))";
		//String regex = "(?<=<claim-text>)[^<]+(?=</claim-text>)";
		System.out.println(regex);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			wordConnection[0] = type;
			wordConnection[1] = matcher.group();
			System.out.println(wordConnection[1]);
		}
		return wordConnection;
	}

	private static void println(Object value) {
		for (int i = 0; i < indent ; i++) {
			System.out.println(" ");
		}
		System.out.println(value);
	}

}
