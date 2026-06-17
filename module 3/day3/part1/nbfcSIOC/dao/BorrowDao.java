package nbfcSIOC.dao;

import nbfcSIOC.entity.Borrow;

import java.util.Collection;

public interface BorrowDao {
    void save(Borrow borrow);
    Borrow findById(int id);
    Collection<Borrow> findAll();
    void updateById(int id, Borrow borrow);
    void deleteById(int id);
}
