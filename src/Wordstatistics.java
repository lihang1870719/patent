import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;


public class Wordstatistics {

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
		
		String deleteSql = "delete from dbo.wordstatistics_description_2013_380";
		Statement statement = conn.createStatement();
		statement.execute(deleteSql);
		
		String insertSql = "insert into dbo.wordstatistics_description_2013_380 values(?,?)";
		PreparedStatement insertps = conn.prepareStatement(insertSql);
		for(WordCount wcnt:result){
			insertps.setString(1, wcnt.getWord());
			insertps.setInt(2, wcnt.getCount());
			insertps.executeUpdate();
		}
		System.out.println("insert over");
	}

	public static List<WordCount> statistics (Connection conn) {
		String selectSql = "select * from dbo.wordparse_description_2013_380";
		Statement statement;
		StringBuilder allWords = new StringBuilder();
		long schedule = 0;
		try {
			statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = statement.executeQuery(selectSql);
			String[] highFqWord = new String[]{"p","id","num","claim","text","ref","clm"
					,"idref","b","description","detdesc","end","figref","sub"};		
			while(rs.next()){
				boolean flag = true;
				int count = rs.getInt(6);
				String temp = rs.getString(5);
				System.out.println(temp + " " + count);
				for(int i = 0; i<highFqWord.length; i++){	
					if(temp.equals(highFqWord[i]))
					{					
						flag = false;
						break;
					}	
				}		
				if(flag)
				{
					while(count != 0 ){
						allWords.append(temp);
						allWords.append(' ');
						count--;					
					}	
					schedule++;
					System.out.println("Now in pecent: " + Math.floor(schedule*100/(1000*731+174))+"%");
				}		
			}
			
			List<WordCount> wList = WordCountService.getWordCount(allWords.toString());
			for(WordCount cnt :wList){
				sum += cnt.getCount();
				System.out.println("word: " + cnt.getWord() + " count: " + cnt.getCount());
			}
			System.out.println("sum: " + sum);
			return wList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
