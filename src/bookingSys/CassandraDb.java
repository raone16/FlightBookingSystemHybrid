package bookingSys;

import java.util.List;


import org.apache.log4j.BasicConfigurator;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;

import bookingSys.FlightDetails;
import bookingSys.FlightDetails.Booking;

public class CassandraDb implements DbInterface {
	private static Session con = connectToDb();
	static PreparedStatement pStmt= con.prepare("INSERT INTO booking(flightNo, BookId, seats , meal, status, cost) VALUES (?, ?, ?, ?, ?, ?)");
	 static BoundStatement boundStatement = new BoundStatement(pStmt);
	 public static Session connectToDb() {
			BasicConfigurator.configure();
			Cluster cluster;
			Session session;
			cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
			session = cluster.connect("flight");
			return session;
		}
	 public void writeToDb(Booking b,int FlightNo)  {
    	   
    	   try {
    		con.execute(boundStatement.bind(FlightNo,b.getBookId(),b.getSeatsBooked(),b.isMealPreference(),b.getStatus(),b.getCost()));
         
    	 }
    	 catch(Exception e){
    		 System.out.println("error while inserting"+e.getMessage());
    	 }
    }
	 public void updateDb(int id) {
			
			Statement update =QueryBuilder.update("flight","booking")
					.with(QueryBuilder.set("status",false))
					.where(QueryBuilder.eq("BookId",id));
			try {
				con.execute(update);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	 public  void updateOld() {
			
			FlightDetails f=null;
			int tempId;
			
		    String query ="SELECT * from booking";
		    
		 
		    try {
		        com.datastax.driver.core.ResultSet rs=con.execute(query);
		        for (Row row: rs) {
		      
		        	tempId = row.getInt("flightNo")-101;
		        	//System.out.println("flight id"+tempId);
		        	for(int i=0;i<=tempId;i++) {
		        	try{f=BookingSystem.l.get(i);}
	    			catch(Exception e) {
	    				BookingSystem.addflight();
	    				//System.out.println(i);
	    				f=BookingSystem.l.get(i);
	    			}
		        	}
		        	List<Integer> seats=row.getList("seats", Integer.class);
	    			
	  			//System.out.println(seats+" "+rs.getDouble("cost")+" "+(rs.getInt("meal")==1 ? true:false)+" "+(seats.get(0)<6)+" "+(rs.getInt("status")==1 ? true:false));
	  			f.makeBooking(seats,row.getDouble("cost"),(row.getBool("meal")),(seats.get(0)<6),row.getBool("status"),row.getInt("BookId"));
		    	
		    }}
		    catch(Exception e) {
		    	e.printStackTrace();
		    	
		    	System.out.println(e.getMessage());
		    }
		    
			
		}
}
