package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		customerRepository2.save(customer);
		//Save the customer in database
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		Customer customer=customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);
		// Delete customer without using deleteById function

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		TripBooking tripBooking=new TripBooking();
		Driver driver=null;
		List<Driver> allDriver=driverRepository2.findAll();
		for(Driver driver1:allDriver) {
			if(driver1.getCab().getAvailable()==Boolean.TRUE){
				if((driver == null) || (driver.getDriverId() > driver1.getDriverId())){
					driver=driver1;
				}}
		}
		if(driver ==null) {
			throw new Exception("No cab available!");
		}
		Customer customer =customerRepository2.findById(customerId).get();
          tripBooking.setCustomer(customer);
		  tripBooking.setDriver(driver);
		  driver.getCab().setAvailable(Boolean.FALSE);
		  tripBooking.setFromLocation(fromLocation);
		  tripBooking.setToLocation(toLocation);
		  tripBooking.setDistanceInKm(distanceInKm);
		  tripBooking.setStatus(TripStatus.CONFIRMED);

		  customer.getTripBookingList().add(tripBooking);
		  customerRepository2.save(customer);
		  driver.getTripBookingList().add(tripBooking);
		  driverRepository2.save(driver);
		  return tripBooking;


	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		tripBooking.getDriver().getCab().setAvailable(Boolean.FALSE);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
		int bill= tripBooking.getDriver().getCab().getPerKmRate()*tripBooking.getDistanceInKm();
		tripBooking.setBill(bill);
		tripBooking.getDriver().getCab().setAvailable(Boolean.TRUE);
		tripBookingRepository2.save(tripBooking);

	}
}
