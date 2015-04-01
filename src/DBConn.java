import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DBConn {
	
	static {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static DBConn make(Config config) throws Exception {
		return new DBConn(config);
	}

	private final Config config;
	private final Connection conn;
	
	private DBConn(Config config) throws Exception {
		this.config = config;
		String connString = String.format("jdbc:sqlserver://%s;DatabaseName=%s",
				config.get("connection.host"), 
				config.get("connection.database"));
		System.out.println(connString);
		conn = DriverManager.getConnection(connString,
				config.get("connection.user"),
				config.get("connection.password"));
	}

	public List<OriginData> getOriginDatas(List<String> idList) throws Exception {
		
		String sql = "select content from " + config.get("dataTable.patent") + " where id = ";
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		List<OriginData> result = new ArrayList<OriginData>();
		for(String id : idList) {
			String selectsql = sql + "'" + id +"'"; 
			System.out.println(selectsql);
			ResultSet rs = st.executeQuery(selectsql);			
			while(rs.next()) {
				String content = rs.getString(1);
				if(content == null) continue;
				OriginData oData = new OriginData(id,content);
				result.add(oData);
			}
		}		
		return result;
	}

	public void update(List<WordCount> wList, String[] classId, String id, String dataTable) throws Exception {
		String type = classId[0];
		String stStr = classId[1];
		if(isExist(id, dataTable)) {
			String updateSql = "update " + dataTable + " set count = ?"
					+ " where patentId = ?";
			PreparedStatement ps = conn.prepareStatement(updateSql);
			for(WordCount wcnt:wList) {
				ps.setString(2, id);
				ps.setInt(1, wcnt.getCount());
				ps.executeUpdate();
			}
		} else {
			String insertSql = "insert into " + dataTable + " (patentId ,type ,"
					+ "content ,word ,count) values (? ,? ,? ,? ,?)";
			PreparedStatement ps = conn.prepareStatement(insertSql);
			for(WordCount wcnt:wList){
				ps.setString(1, id);
				ps.setString(2, type);
				ps.setString(3, stStr);
				ps.setString(4, wcnt.getWord());
				ps.setInt(5, wcnt.getCount());
				//System.out.println(wcnt.getWord());
				ps.executeUpdate();
			}
			System.out.println("insert " + dataTable);
		}
	}

	public boolean isExist(String patentId, String dataType) throws Exception {
		String sql = "select * from " + dataType + " WHERE patentId = "
				 + "'" + patentId + "'";
		System.out.println(sql);
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = st.executeQuery(sql);
		if(rs.next()) {
			return true;
		} else {
			return false;
		}
	}

	public void close() {
		try {
			this.conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public List<String> getIdList() throws Exception {

		//String sql = "select * from " + config.get("dataTable.classification") + " where id =" +  "'" + "ipg130219-5273" + "'";
		//System.out.println(sql);
		String sql = "select TOP 1000 * from " + config.get("dataTable.classification") + " where left(classification,3) = " + "'" + config.get("getData.matcher") + "'";
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = st.executeQuery(sql);
		List<String> result = new ArrayList<String>();
		while(rs.next()) {
			String id = rs.getString(1);
			result.add(id);
		}
		System.out.println("Total: " + result.size());
		return result;
	}

	public void getAbstractPatent(List<String> idList) throws Exception {
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		String regex = "</abstract>";
		int count = 0;
		List<String> result = idList;
		Iterator<String> it = result.iterator();		
		while(it.hasNext()) {
			String temp = it.next();
			String sql = "select content from " + config.get("dataTable.patent") + " where id = " + "'" + temp + "'";
			System.out.println(temp);
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				String xml = rs.getString(1);
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(xml);
				if(matcher.find()) {
					count++;
				}
			}
		}
		System.out.println("count: " + count);
	}

}
