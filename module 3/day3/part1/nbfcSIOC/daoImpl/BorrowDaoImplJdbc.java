package nbfcSIOC.daoImpl;

import bookSIOC.entity.Book;
import nbfcSIOC.connection.DBManager;
import nbfcSIOC.dao.BorrowDao;
import nbfcSIOC.entity.Borrow;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class BorrowDaoImplJdbc implements BorrowDao {

    private final DBManager dbManager;

    public BorrowDaoImplJdbc(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void save(Borrow borrow) {
        try (Connection con = dbManager.getConnection()) {
            String sql="Insert into borrow(lenderName,lenderType,amount,interest,status,tenureMonths,borrowedDate,maturityDate) values(?,?,?,?,?,?,?,?)";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1, borrow.getLenderName());
            stmt.setString(2,borrow.getLenderType());
            stmt.setDouble(3,borrow.getAmount());
            stmt.setDouble(4,borrow.getInterest());
            stmt.setString(5,borrow.getStatus());
            stmt.setInt(6,borrow.getTenureMonths());
            stmt.setDate(7, Date.valueOf(borrow.getBorrowedDate()));
            stmt.setDate(8, Date.valueOf(borrow.getMaturityDate()));
            int row=stmt.executeUpdate();
            System.out.println("Rows updated: "+row);

        } catch (Exception e) {
            System.out.println("DB issues"+e.getMessage());
        }


    }

    public Borrow mapToBorrow(ResultSet rs) throws SQLException {
        return new Borrow(rs.getInt(1),rs.getString(2),rs.getString(3),
                rs.getDouble(4),rs.getDouble(5),rs.getString(6),
                rs.getInt(7),rs.getDate(8).toLocalDate(),rs.getDate(9).toLocalDate());
    }

    @Override
    public Borrow findById(int id) {

        try(Connection con = dbManager.getConnection()){
            String sql="select * from borrow where id=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setInt(1,id);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return mapToBorrow(rs);
            }

        }catch (Exception e) {
            System.out.println("DB issues"+e.getMessage());
        }

        return null;
    }

    @Override
    public Collection<Borrow> findAll() {
        Collection<Borrow> borrows=new LinkedHashSet<>();
        try(Connection con = dbManager.getConnection()){
            String sql="select * from borrow";
            PreparedStatement stmt=con.prepareStatement(sql);

            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                borrows.add( mapToBorrow(rs) );
            }

        }catch (Exception e) {
            System.out.println("DB issues"+e.getMessage());
        }

        return borrows;
    }

    @Override
    public void updateById(int id, Borrow borrow) {

        try (Connection con = dbManager.getConnection()) {
            String sql="update borrow set lenderName=?,lenderType=?,amount=?,interest=?,status=?,tenureMonths=?,borrowedDate=?,maturityDate=? where id=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setString(1, borrow.getLenderName());
            stmt.setString(2,borrow.getLenderType());
            stmt.setDouble(3,borrow.getAmount());
            stmt.setDouble(4,borrow.getInterest());
            stmt.setString(5,borrow.getStatus());
            stmt.setInt(6,borrow.getTenureMonths());
            stmt.setDate(7, Date.valueOf(borrow.getBorrowedDate()));
            stmt.setDate(8, Date.valueOf(borrow.getMaturityDate()));
            stmt.setInt(9,id);
            int row=stmt.executeUpdate();
            System.out.println("Rows updated: "+row);

        } catch (Exception e) {
            System.out.println("DB issues"+e.getMessage());
        }
    }

    @Override
    public void deleteById(int id) {
        try(Connection con=dbManager.getConnection()){
            String sql="delete from borrow where id=?";
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setInt(1,id);
            int row=stmt.executeUpdate();
            System.out.println("Rows deleted: "+row);


        } catch (Exception e) {
        System.out.println("DB issues"+e.getMessage());
    }

    }
}
