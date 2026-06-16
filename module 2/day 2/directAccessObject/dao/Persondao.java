package DirectAccessObject.dao;
import DirectAccessObject.entity.Person;

public interface Persondao {

    public void save(Person person);
    public Person findByFname(String fname);
    public void deleteByFname(String fname);
    public void update(Person person);
    public void deleteAll();
    public Iterable<Person> findAll();
    public Iterable<Person> findByLname(String lname);
    public Iterable<Person> sortByFnameAsc();
    public Iterable<Person> sortByAgeDesc();
}