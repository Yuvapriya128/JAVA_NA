package daoInMap.UI;

import daoInMap.Dao.PersonDao;
import daoInMap.Entity.Person;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersonImpl implements PersonDao {
    private Map<Integer,Person> map=new LinkedHashMap<>();

    @Override
    public void save(Person p) {
        map.put(p.getId(),p);

    }

    @Override
    public Collection<Person> findAll() {
        return map.values();
    }

    @Override
    public Person findById(int id) {
        return map.get(id);
    }

    @Override
    public void deletedById(int id) {
        map.remove(id);

    }

    @Override
    public void updateById(int id,Person p) {
        map.replace(id,p);

    }
}
