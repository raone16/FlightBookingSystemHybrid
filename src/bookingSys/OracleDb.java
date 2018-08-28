package bookingSys;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bookingSys.FlightDetails;
import bookingSys.FlightDetails.Booking;

public class OracleDb implements DbInterface{
	private static Connection con = connectToDb();
	public static Connection connectToDb() {
		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection connection = null;

		try {

			connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:" + "XE","SYSTEM","rec123");
			return connection;

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}
	   public void writeToDb(Booking b,int FlightNo) {
		   String query = "Select Bid from booking where Bid="+b.getBookId() ;
		   
      	 try {
      		Statement smt =con.createStatement();
 		   ResultSet rs = smt.executeQuery(query);
 		   if(!rs.next()) {
 		   PreparedStatement pStmt;
       	   query = "INSERT INTO booking (fNo, Bid, seats , meal, status, cost) VALUES (?, ?, ?, ?, ?, ?)";
       	   pStmt = con.prepareStatement(query);
      	   pStmt.setInt (1, FlightNo);
           pStmt.setInt (2,b.getBookId());
           pStmt.setString(3,b.getSeatsBooked().toString());
           
           pStmt.setInt (4,(b.isMealPreference()? 1:0));
           pStmt.setInt (5,(b.getStatus()? 1:0));
           pStmt.setFloat(6,(float) b.getCost());
           pStmt.executeQuery();
           
      	 }
      	 }
      	 catch(Exception e){
      		 System.out.println("error while inserting"+e.getMessage());
      	 }
      }
	   
	   public void updateDb(int id) {
			String query ="UPDATE booking SET status=0 where Bid="+id;
			try {
				PreparedStatement pStmt = con.prepareStatement(query);
				pStmt.executeQuery();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	   public  void updateOld() {
			Statement stmt = null;
			FlightDetails f=null;
			int tempId;
			
		    String query ="SELECT * from booking";
		 
		    try {
		    	stmt = con.createStatement();
		        ResultSet rs = stmt.executeQuery(query);
		        while (rs.next()) {
		      
		        	tempId = rs.getInt("fNo")-101;
		        	for(int i=0;i<=tempId;i++) {
		        	try{f=BookingSystem.l.get(i);}
	    			catch(Exception e) {
	    				BookingSystem.addflight();
	    				f=BookingSystem.l.get(i);
	    			}}
	    				
		        	String s1= rs.getString("seats");
	  			  s1 = s1.replace("[","");
	  			 s1 = s1.replace("]","");
	  			 s1 = s1.replaceAll(" ","");
	  			 List<String> myList = new ArrayList<String>(Arrays.asList(s1.split(",")));
	  			List<Integer> seats = new ArrayList<Integer>(Arrays.asList());
	  			for(String s : myList) {
	  				seats.add(Integer.parseInt(s));
	  			}
	  			
	  			f.makeBooking(seats,rs.getDouble("cost"),(rs.getInt("meal")==1 ? true:false),(seats.get(0)<6),(rs.getInt("status")==1 ? true:false),rs.getInt("bid"));
		    	
		    }}
		    catch(Exception e) {
		    	e.printStackTrace();
		    	
		    	System.out.println(e.getMessage());
		    }
		    
			
		}

}
