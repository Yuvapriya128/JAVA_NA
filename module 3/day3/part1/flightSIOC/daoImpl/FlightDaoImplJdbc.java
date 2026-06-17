package flightSIOC.daoImpl;

import flightSIOC.dao.FlightDao;
import flightSIOC.entity.Flight;
import flightSIOC.connection.DBManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlightDaoImplJdbc implements FlightDao {
    private final DBManager dbManager;

    public FlightDaoImplJdbc(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    private Flight mapToFlight(ResultSet rs) throws SQLException {
        return new Flight(
                rs.getString(1),                  // flightno
                rs.getString(2),                  // source
                rs.getString(3),                  // destination
                rs.getDouble(4),                  // costPerSeat
                rs.getInt(5),                     // noOfSeat
                rs.getDate(6).toLocalDate(),      // dOfDeparture
                rs.getDate(7).toLocalDate(),      // dOfArrival
                rs.getTime(8).toLocalTime(),      // tOfDeparture
                rs.getTime(9).toLocalTime()       // tOfArrival
        );
    }


    @Override
    public void save(Flight flight) {
        try(Connection con=dbManager.getConnection()){
            String sql="Insert into flight(flightno,source,destination,costPerSeat,noOfSeat,dOfDeparture,dOfArrival,tOfDeparture,tOfArrival) values(?,?,?,?,?,?,?,?,?)";

            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1,flight.getFlightno());
            stmt.setString(2,flight.getSource());
            stmt.setString(3,flight.getDestination());
            stmt.setDouble(4,flight.getCostPerSeat());
            stmt.setInt(5,flight.getNoOfSeat());
            stmt.setDate(6,Date.valueOf(flight.getdOfDeparture()));
            stmt.setDate(7,Date.valueOf(flight.getdOfArrival()));
            stmt.setTime(8, Time.valueOf(flight.gettOfDeparture()));
            stmt.setTime(9,Time.valueOf(flight.gettOfArrival()));
            int row=stmt.executeUpdate();
            System.out.println("Rows inserted: "+row);
        } catch (SQLException e) {
            System.out.println("DB connectivity issues:"+e.getMessage());
        }

    }

    @Override
    public Flight findByNo(String flightNo) {
        try(Connection con=dbManager.getConnection()){
            String sql="select * from flight where flightNo=?";
            PreparedStatement smt=con.prepareStatement(sql);
            smt.setString(1,flightNo);
            ResultSet rs=smt.executeQuery();
            if(rs.next()){
                return mapToFlight(rs);
            }

        } catch (SQLException e) {
            System.out.println("DB connectivity issues:"+e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<Flight> findAll() {
        Collection<Flight> flightCollection=new ArrayList<>();
        try(Connection con=dbManager.getConnection()){
            String sql="select * from flight ";
            PreparedStatement smt=con.prepareStatement(sql);

            ResultSet rs=smt.executeQuery();
            while(rs.next()){
                flightCollection.add( mapToFlight(rs) );
            }
        } catch (SQLException e) {
            System.out.println("DB connectivity issues:"+e.getMessage());
        }
        return flightCollection;
    }

    @Override
    public void deleteByNo(String flightNo) {
        try(Connection con=dbManager.getConnection()){
            String sql="delete from flight where flightNo=?";
            PreparedStatement smt=con.prepareStatement(sql);
            smt.setString(1,flightNo);
            int row=smt.executeUpdate();
            System.out.println("Rows deleted:"+row);
        } catch (SQLException e) {
            System.out.println("DB connectivity issues:"+e.getMessage());
        }

    }

    @Override
    public Collection<Flight> findBySrcDestAndDepDate(String src, String dest, LocalDate departureDate, LocalTime departureTime) {
        Collection<Flight> flightCollection=new ArrayList<>();
        try(Connection con=dbManager.getConnection()){
            String sql="select * from flight where source=? and destination=? and dOfDeparture=? and tOfDeparture=?  ";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1,src);
            stmt.setString(2,dest);
            stmt.setDate(3,Date.valueOf(departureDate));
            stmt.setTime(4,Time.valueOf(departureTime));
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                flightCollection.add( mapToFlight(rs) );
            }
        } catch (SQLException e) {
            System.out.println("DB connectivity issues:"+e.getMessage());
        }
        return flightCollection;
    }

    @Override
    public void updateByNo(String flightNo, Flight flight) {
        try(Connection con=dbManager.getConnection()){
            String sql="update flight set source=?,destination=?,costPerSeat=?,noOfSeat=?,dOfDeparture=?,dOfArrival=?,tOfDeparture=?,tOfArrival=? where flightNo=?)";

            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(9,flight.getFlightno());
            stmt.setString(1,flight.getSource());
            stmt.setString(2,flight.getDestination());
            stmt.setDouble(3,flight.getCostPerSeat());
            stmt.setInt(4,flight.getNoOfSeat());
            stmt.setDate(5,Date.valueOf(flight.getdOfDeparture()));
            stmt.setDate(6,Date.valueOf(flight.getdOfArrival()));
            stmt.setTime(7, Time.valueOf(flight.gettOfDeparture()));
            stmt.setTime(8,Time.valueOf(flight.gettOfArrival()));
            int row=stmt.executeUpdate();
            System.out.println("Rows updated: "+row);
        } catch (SQLException e) {
            System.out.println("DB connectivity issues:"+e.getMessage());
        }

    }
}
