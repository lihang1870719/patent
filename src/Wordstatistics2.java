import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Wordstatistics2 {

	public static int sum = 0;
	
	public static void main(String[] args) throws Exception {
		
		String JDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String connectDB = "jdbc:sqlserver://localhost:1433;DatabaseName=patent";
		Class.forName(JDriver);
		String userName="sa";
        String password="sa_123";
        Connection conn=DriverManager.getConnection(connectDB,userName,password);
        System.out.println("connect to database");
        List<WordCount> result = statistics(conn);
        AddToDB(conn,result);
        conn.close();
        System.out.println("over");
	}
	
	private static void AddToDB(Connection conn, List<WordCount> result) throws Exception {
		
		String deleteSql = "delete from dbo.wordstatistics_description_2013_257";
		Statement statement = conn.createStatement();
		statement.execute(deleteSql);
		
		String insertSql = "insert into dbo.wordstatistics_description_2013_257 values(?,?)";
		PreparedStatement insertps = conn.prepareStatement(insertSql);
		for(WordCount wcnt:result){
			insertps.setString(1, wcnt.getWord());
			insertps.setInt(2, wcnt.getCount());
			insertps.executeUpdate();
		}
		System.out.println("insert over");
	}

	public static List<WordCount> statistics (Connection conn) {
		String selectSql = "select * from dbo.wordparse_description_2013_257";
		Statement statement;
		try {
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = statement.executeQuery(selectSql);
			List<WordCount> wordList = new ArrayList<WordCount>();
			int percent = 0;
			while(rs.next()){
				boolean flag = true;
				int count = rs.getInt(6);
				String temp = rs.getString(5);
				//System.out.println(temp + " " + count);				
				
				for(WordCount wCount : wordList) {
					if(wCount.getWord() == temp) {
						count += wCount.getCount();
						wCount.setCount(count);
						flag = false;
						break;
					}
				}
				if(flag) {
					WordCount newWord = new WordCount();
					newWord.setCount(count);
					newWord.setWord(temp);
					wordList.add(newWord);		
				}
				percent++;
				System.out.println("Now in percent: " + Math.floor(percent*100/(1000*721+571)) + "%");
			}			
	
			return wordList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}

