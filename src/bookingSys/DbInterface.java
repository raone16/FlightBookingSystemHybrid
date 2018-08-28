package bookingSys;

import bookingSys.FlightDetails.Booking;

public interface DbInterface {
	 public void writeToDb(Booking b,int FlightNo);
	 public void updateDb(int id);
	 public void updateOld();
}
