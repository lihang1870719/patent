import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 之前做实验用的代码
 * @author Patent
 *
 */
public class WordParse {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String JDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String connectDB = "jdbc:sqlserver://localhost:1433;DatabaseName=patent_2005";
		Class.forName(JDriver);
		String userName="sa";
        String password="sa_123";
        Connection conn=DriverManager.getConnection(connectDB,userName,password);
        System.out.println("链接成功");
        String sql = "select top(20) * from dbo.patent_2005";
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
        	String id = rs.getString(1);
        	String tempString = rs.getString(2);
        	if(tempString == null)continue;
        	//System.out.println(tempString);
        	//segmentation(tempString);
        	String[] classId = segmentation(tempString);
        	update(conn,classId,id);
        }
        conn.close();
        System.out.println("over");
	}
	
	/**
	 *  统计每个单词在claim出现的次数
	 * @param sentenceString
	 * @return
	 */
	private static List<WordCount> doParse(String sentenceString) {
		// TODO Auto-generated method stub
		System.out.println("in doParse");
		List<WordCount> wList = WordCountService.getWordCount(sentenceString);
		String[] HighFqWord = new String[]{"a","an","the","for","in","am","is","are","and","as","of","at","with","to","do"};
		Iterator<WordCount> it = wList.iterator();
		while(it.hasNext()){
			//System.out.println(it.next().getCount());
			String tempWord = it.next().getWord();
			for(int i = 0; i<HighFqWord.length; i++){
				
				if(tempWord.equals(HighFqWord[i]))
				{
					it.remove();
					break;
				}	
				//System.out.println("word: "+it.next().getWord()+" count: "+it.next().getCount());
			}		
		}
		for(WordCount count : wList){
			System.out.println("word: "+count.getWord()+" count: "+count.getCount());
		}
		
		return wList;
	}

	/**
	 * 找出abstract和claim进行提取
	 * @param xmlString
	 * @return
	 */
	public static String[] segmentation(String xmlString){
		String[] wordConnection = new String[2];
		System.out.println("in segmentation");
		String regex = "<claim-text>(.+?)</claim-text>";
		String xml = xmlString;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(xml);
		if(matcher.find()){
			wordConnection[0] = "claim";
			wordConnection[1] = matcher.group(1);
			System.out.println(wordConnection[0] + " " + wordConnection[1]);
		}
		
		return wordConnection;		
	}
	
	/**
	 * 将词频统计的结果存入数据库中
	 * @param conn 
	 * @param classId
	 * @param id 
	 * @throws Exception
	 */
	public static void update(Connection conn, String[] classId, String id) throws Exception{
		String Type = classId[0];
    	String sentenceString = classId[1];
		if(isExist(id)){
			List<WordCount> wList = doParse(sentenceString);
        	String updateSql = "update dbo.wordparse set count = ? where patentId = ?";
        	PreparedStatement updateps = conn.prepareStatement(updateSql);
        	for(WordCount wcnt:wList){
        		updateps.setString(2, id);
            	updateps.setInt(1, wcnt.getCount());
            	updateps.executeUpdate();
        	}
		}
		else{
			List<WordCount> wList = doParse(sentenceString);
        	String insertSql = "insert into dbo.wordparse (patentId,type,content,word,count)values (?,?,?,?,?)";
        	PreparedStatement insertps = conn.prepareStatement(insertSql);
        	for(WordCount wcnt:wList){
        		insertps.setString(1,id);
            	insertps.setString(2,Type);
            	insertps.setString(3, sentenceString);
            	insertps.setString(4, wcnt.getWord());
            	insertps.setInt(5, wcnt.getCount());
            	insertps.executeUpdate();
        	}
		}
	}
	
	/**
	 * 判断数据库中是否已经存在这条记录
	 * @param classNum
	 * @return
	 * @throws Exception
	 */
	public static boolean isExist(String patentId)throws Exception{
	    	
	    String JDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	    String	connectDB="jdbc:sqlserver://localhost:1433;DatabaseName=patent_2005";
	    Class.forName(JDriver);
	    String userName="sa";
	    String password="sa_123";
        Connection conn=DriverManager.getConnection(connectDB,userName,password);
        System.out.println("isExit链接成功");
        String isExistSql="select * from dbo.wordParse where patentId="+"'"+patentId+"'";
        Statement  queryStatement=conn.createStatement();
        ResultSet rsResultSet = queryStatement.executeQuery(isExistSql);
        
        if( rsResultSet.next()){
        	conn.close();
        	System.out.println("isExit关闭链接");
        	return true;
        	}
        else{
        	conn.close();
        	return false;
        }
        
    }
}
