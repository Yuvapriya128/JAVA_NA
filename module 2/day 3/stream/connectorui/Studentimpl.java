package stream.connectorui;

import stream.dao.Studentdao;
import stream.entity.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Studentimpl implements Studentdao {
    List<Student> studentList=new ArrayList<>();
    @Override
    public void add(Student s) {
        studentList.add(s);

    }

    @Override
    public void deleteAll() {
        studentList.clear();

    }

    @Override
    public Collection<Student> findAll() {
        return studentList;
    }

    @Override
    public void maxPerSub() {
        double maxphy=studentList.stream().mapToDouble(Student::getPhy).max().orElse(0.0);
        double maxchem=studentList.stream().mapToDouble(Student::getChem).max().orElse(0);
        double maxmath=studentList.stream().mapToDouble(Student::getMath).max().orElse(0);
        double maxhist=studentList.stream().mapToDouble(Student::getHist).max().orElse(0);
        double maxgeo=studentList.stream().mapToDouble(Student::getGeo).max().orElse(0);

    }

    @Override
    public void avgPerSub() {
        double avgphy=studentList.stream().mapToDouble(Student::getPhy).average().orElse(0.0);
        double avgchem=studentList.stream().mapToDouble(Student::getChem).average().orElse(0);
        double avgmath=studentList.stream().mapToDouble(Student::getMath).average().orElse(0);
        double avghist=studentList.stream().mapToDouble(Student::getHist).average().orElse(0);
        double avggeo=studentList.stream().mapToDouble(Student::getGeo).average().orElse(0);

    }

    @Override
    public Collection<Student> topperPerSub() {
        List<Student> toppers=new ArrayList<>();
        toppers.add(
                studentList.stream().max(Comparator.comparingDouble(Student::getPhy)).orElse(null)
        );
        toppers.add(
                studentList.stream().max(Comparator.comparingDouble(Student::getChem)).orElse(null)
        );
        toppers.add(
                studentList.stream().max(Comparator.comparingDouble(Student::getMath)).orElse(null)
        );
        toppers.add(studentList.stream().max(Comparator.comparingDouble(Student::getHist)).orElse(null));
        toppers.add(
                studentList.stream().max(Comparator.comparingDouble(Student::getGeo)).orElse(null)
        );
        return toppers;

    }

    @Override
    public void topperTotal() {
        Student totaltop=studentList.stream().max((s1,s2)->
                Double.compare(
                        s1.getPhy()+s1.getChem()+s1.getMath()+s1.getHist()+s1.getGeo(),
                        s2.getPhy()+s2.getChem()+s2.getMath()+s2.getHist()+s2.getGeo()
                )
        ).orElse(null);
        System.out.println(totaltop);

    }

    @Override
    public Collection<Student> aboveAvgPhy() {
        double avgphy2=studentList.stream().mapToDouble(Student::getPhy).average().orElse(0);

        long cnt=studentList.stream().filter((s)->s.getPhy() > avgphy2).collect(Collectors.counting());

        System.out.println("count : "+cnt);
        return studentList.stream().filter((s)->s.getPhy() > avgphy2).toList();


    }

}
