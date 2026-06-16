package daoInJDBC.ui;

import daoInJDBC.Connection.DBManager;
import daoInJDBC.dao.LoanDao;
import daoInJDBC.entity.Book;
import daoInJDBC.entity.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*
 * to create table ->use postgresql in cmd
 * */
public class LoanImpl implements LoanDao {
    public Loan mapToLoan(ResultSet rs) throws SQLException {
        return new Loan(rs.getString("loanType"),rs.getInt("loanAmount"),rs.getString("loanStatus"),rs.getDouble("interest"),rs.getInt("tenure"));
    }

    @Override
    public int save(Loan loan) throws SQLException {
        Connection con= DBManager.getConnection();
        int row=0;
        String sql="INSERT INTO loan(loanAmount,loanType,loanStatus,interest,tenure) values('Vehicle loan',50000,'Accepted',1.5,60)";
        PreparedStatement stmt=con.prepareStatement(sql);
        row=stmt.executeUpdate();
        DBManager.closeConnection(con);
        return row;
    }

    @Override
    public Loan findById(int loanId) throws SQLException {
        Loan loan=new Loan();
        Connection con= DBManager.getConnection();
        String sql="select * from loan where id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        if(rs.next()){
            loan=mapToLoan(rs);
        }
        DBManager.closeConnection(con);
        return loan;
    }

    @Override
    public Collection<Loan> findAll() throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> findByStatus(String status) throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan where loanStatus=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,status);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> findByType(String type) throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan where loanType=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,type);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> findByAmountGreaterThan(int amount) throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan where loanAmount>?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,amount);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> findByInterestLessThan(double interest) throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan where interest<?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setDouble(1,interest);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> findByTypeAndStatus(String type, String status) throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan where loanType=? and loanStatus=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,type);
        stmt.setString(2,status);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> sortByAmount() throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan order by loanAmount";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> sortByAmountDesc() throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan order by loanAmount desc";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> sortByInterest() throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan order by interest";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public Collection<Loan> sortByAmountAndInterest() throws SQLException {
        Connection con=DBManager.getConnection();
        List<Loan> loansList=new LinkedList<>();
        String sql="Select * from loan order by loanAmount,interest";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            loansList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loansList;
    }

    @Override
    public void updateInterestById(int loanId, double interest) throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="update loan set interest=? where loanId=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setDouble(1,interest);
        stmt.setInt(2,loanId);
        int row= stmt.executeUpdate();
        System.out.println("Rows updated: "+row);
        DBManager.closeConnection(con);
    }

    @Override
    public void updateLoanStatus(int loanId, String status) throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="update loan set loanStatus=? where loanId=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,status);
        stmt.setInt(2,loanId);
        int row= stmt.executeUpdate();
        System.out.println("Rows updated: "+row);
        DBManager.closeConnection(con);
    }

    @Override
    public void updateInterestByType(String type, double interest) throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="update loan set interest=? where loanType=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setDouble(1,interest);
        stmt.setString(2,type);
        int row= stmt.executeUpdate();
        System.out.println("Rows updated: "+row);
        DBManager.closeConnection(con);
    }

    @Override
    public void deleteById(int loanId) throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="delete from loan where loanId=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,loanId);
        int row=stmt.executeUpdate();
        System.out.println("Rows deleted: "+row);
        DBManager.closeConnection(con);

    }

    @Override
    public void deleteByStatus(String status) throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="delete from loan where loanStatus=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,status);
        int row=stmt.executeUpdate();
        System.out.println("Rows deleted: "+row);
        DBManager.closeConnection(con);
    }

    @Override
    public void deleteAll() throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="delete from loan";
        PreparedStatement stmt=con.prepareStatement(sql);
        int row=stmt.executeUpdate();
        System.out.println("All rows deleted: "+row);
        DBManager.closeConnection(con);
    }

    @Override
    public boolean existsById(int loanId) throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="select * from loan where loanId=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs =stmt.executeQuery();
        DBManager.closeConnection(con);
        if(rs.next()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int countLoans() throws SQLException {
        int cnt=0;
        Connection con=DBManager.getConnection();
        String sql="select * from loan";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs =stmt.executeQuery();
        DBManager.closeConnection(con);
        while(rs.next()){
            cnt++;
        }
        return cnt;
    }

    @Override
    public int countByStatus(String status) throws SQLException {
        int cnt=0;
        Connection con=DBManager.getConnection();
        String sql="select * from loan where loanStatus=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1,status);
        ResultSet rs =stmt.executeQuery();

        while(rs.next()){
            cnt++;
        }
        DBManager.closeConnection(con);
        return cnt;
    }

    @Override
    public int getMaxLoanAmount() throws SQLException {
        int maxamt=0;

        Connection con=DBManager.getConnection();
        String sql="select max(loanAmount) from loan";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs =stmt.executeQuery();
        if(rs.next()){
             maxamt=rs.getInt(1);
        }
        DBManager.closeConnection(con);

        return maxamt;
    }

    @Override
    public int getMinLoanAmount() throws SQLException {
        int minamt=0;

        Connection con=DBManager.getConnection();
        String sql="select min(loanAmount) from loan";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs =stmt.executeQuery();
        if(rs.next()){
            minamt=rs.getInt(1);
        }
        DBManager.closeConnection(con);

        return minamt;
    }

    @Override
    public double getAverageLoanAmount() throws SQLException {
        double avgamt=0;

        Connection con=DBManager.getConnection();
        String sql="select avg(loanAmount) from loan";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs =stmt.executeQuery();
        if(rs.next()){
            avgamt=rs.getInt(1);
        }
        DBManager.closeConnection(con);

        return avgamt;
    }

    @Override
    public int getTotalLoanAmount() throws SQLException {
        int totalamt=0;

        Connection con=DBManager.getConnection();
        String sql="select sum(loanAmount) from loan";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs =stmt.executeQuery();
        if(rs.next()){
            totalamt=rs.getInt(1);
        }
        DBManager.closeConnection(con);

        return totalamt;
    }

    @Override
    public void groupByStatus() throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="select loanStatus,count(*) as count from loan group by loanStatus";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            System.out.println(rs.getString(1)+" "+rs.getInt(2));
        }
        DBManager.closeConnection(con);

    }

    @Override
    public void groupByType() throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="select loanType,count(*) as count from loan group by loanType";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            System.out.println(rs.getString(1)+" "+rs.getInt(2));
        }
        DBManager.closeConnection(con);
    }

    @Override
    public void groupByTypeHavingCountGreaterThan(int count) throws SQLException {
        Connection con=DBManager.getConnection();
        String sql="select loanType,count(*) as count from loan group by loanType having count(*)>?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,count);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            System.out.println(rs.getString(1)+" "+rs.getInt(2));
        }
        DBManager.closeConnection(con);
    }

    @Override
    public Collection<Loan> topNLoans(int n) throws SQLException {
        List<Loan> loanList=new LinkedList<>();

        Connection con=DBManager.getConnection();
        String sql="select * from loan order by loanAmount desc limit ?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,n);
        ResultSet rs =stmt.executeQuery();
        while(rs.next()){
            loanList.add(mapToLoan(rs));
        }
        DBManager.closeConnection(con);
        return loanList;
    }

}
