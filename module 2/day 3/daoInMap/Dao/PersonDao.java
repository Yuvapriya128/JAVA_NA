package daoInMap.Dao;

import daoInMap.Entity.Person;

import java.util.Collection;

/*dao -> Map   --> todo

save
findAll
findById
deleteById
updateById
*/
public interface PersonDao {
    public void save(Person p);
    public Collection<Person> findAll();
    public Person findById(int id);
    public void deletedById(int id);
    public void updateById(int id,Person p);

}
