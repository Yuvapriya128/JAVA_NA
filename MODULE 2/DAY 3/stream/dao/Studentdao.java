package stream.dao;

import stream.entity.Student;

import java.util.Collection;

public interface Studentdao {
    public void add(Student s);
    public void deleteAll();
    public Collection<Student> findAll();
    public void maxPerSub();
    public void avgPerSub();
    public Collection<Student> topperPerSub();
    public void topperTotal();
    public Collection<Student> aboveAvgPhy();
}
