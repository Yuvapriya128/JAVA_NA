package DirectAccessObject.ui;
import DirectAccessObject.dao.Persondao;
import DirectAccessObject.entity.Person;

import java.util.*;

public class Personimpl implements Persondao {

    List<Person> persons = new ArrayList<>();

    @Override
    public void save(Person person) {

        persons.add(person);
    }

    @Override
    public Person findByFname(String fname) {

        for(Person p : persons) {

            if(p.getFname().equalsIgnoreCase(fname)) {

                return p;
            }
        }

        return null;
    }

    @Override
    public void deleteByFname(String fname) {

        Iterator<Person> personItr = persons.iterator();

        while(personItr.hasNext()) {

            Person ptemp = personItr.next();

            if(ptemp.getFname().equalsIgnoreCase(fname)) {

                personItr.remove();
            }
        }
    }

    @Override
    public void update(Person person) {

        for(Person p : persons) {

            if(p.getFname().equalsIgnoreCase(person.getFname())) {

                p.setLname(person.getLname());
                p.setAge(person.getAge());
            }
        }
    }

    @Override
    public void deleteAll() {

        persons.clear();
    }

    @Override
    public Iterable<Person> findAll() {

        return persons;
    }

    @Override
    public Iterable<Person> findByLname(String lname) {

        List<Person> lnamePersons = new ArrayList<>();

        for(Person p : persons) {

            if(p.getLname().equalsIgnoreCase(lname)) {

                lnamePersons.add(p);
            }
        }

        return lnamePersons;
    }

    @Override
    public Iterable<Person> sortByFnameAsc() {

        Collections.sort(persons, new Comparator<Person>() {

            @Override
            public int compare(Person p1, Person p2) {

                return p1.getFname()
                        .compareToIgnoreCase(p2.getFname());
            }
        });

        return persons;
    }

    @Override
    public Iterable<Person> sortByAgeDesc() {

        Collections.sort(persons, new Comparator<Person>() {

            @Override
            public int compare(Person p1, Person p2) {

                return p2.getAge() - p1.getAge();
            }
        });

        return persons;
    }
}