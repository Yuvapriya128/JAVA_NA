package org.example.personspringdemo.dao;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.personspringdemo.entity.Person;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class PersonDaoImpl implements PersonDao{
    List<Person> personList;

    @PostConstruct
    public void init(){
        personList=new LinkedList<>();
        personList.add(new Person(1,"yuva","priya",21));
        personList.add(new Person(2,"siva","s",51));
    }
    @PreDestroy
    public void clear(){
        personList.clear();
    }


    @Override
    public Person save(Person p) {
          personList.add(p);
        return p;
    }

    @Override
    public Person updateById(int id, Person p) {
        Person temp=null;
        for(Person per:personList){
            if(id==per.getId()){
                per.setFname(p.getFname());
                per.setLname(p.getLname());
                per.setAge(p.getAge());
                temp=per;
            }
        }
        return temp;

    }

    @Override
    public void deleteById(int id) {
        Person temp=null;
        for(Person per:personList){
            if(id==per.getId()){
               temp=per;
            }
        }
        personList.remove(temp);

    }

    @Override
    public List<Person> findAll() {
        return personList;
    }

    @Override
    public Person findById(int id) {

        for(Person per:personList){
            if(id==per.getId()){
                return per;
            }
        }
        return null;
    }
}
