package stream.connectorui;

import stream.dao.Loandao;
import stream.entity.Loan;


import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class LoanImpl implements Loandao {
    Set<Loan> loanSet=new TreeSet<>();


    @Override
    public void save(Loan l) {
        loanSet.add(l);
    }

    @Override
    public void remove(int loanid) {
        Iterator loanitr=loanSet.iterator();
        while(loanitr.hasNext()){
            Loan temploan=(Loan)loanitr.next();
            if(temploan.getLoanid()==loanid){
                loanitr.remove();
            }
        }
    }

    @Override
    public Iterable<Loan> findAll() {
        return loanSet;
    }

    @Override
    public Iterable<Loan> findByStatus(String status) {
        Set<Loan> loansByStatus;
        loansByStatus = loanSet.stream().filter((loan)->loan.getLoanStatus().equalsIgnoreCase(status)).collect(Collectors.toSet());

        return loansByStatus;
    }

    @Override
    public Iterable<Loan> findByType(String type) {

        return loanSet.stream().filter((loan)->loan.getLoanType().equalsIgnoreCase(type)).toList();


    }
//     class Myoperator implements UnaryOperator<Loan>{
//        @Override
//         public Loan apply(Loan l){
//            l.setInterest(l.getInterest()+2);
//            return l;
//        }
//     }
//     Myoperator op1=new Myoperator();
//     List<Loan> newLoans =loanSet.stream().map(op1).toList();






    @Override
    public void updateInterest(int rate){
        List<Loan> newLoans =loanSet.stream().map( ( l)->{
            l.setInterest(l.getInterest()+rate);
            return l;
        }).toList();
        loanSet=new TreeSet<>(newLoans);

    }

    @Override
    public Iterable<Loan> updateLoanInterest(String type,int rate){
        List<Loan> newloanin=loanSet.stream()
                                    .filter((loan)->loan.getLoanType().equalsIgnoreCase(type))
                                    .map((l)->{
                                        l.setInterest(l.getInterest()+rate);
                                    return l;
                                    })

                                    .toList();
        return  newloanin;

    }
    @Override
    public Iterable<Loan> sortLoanByAmount(){
        return loanSet.stream().sorted((l1, l2)->l1.getLoanAmount()- l2.getLoanAmount()).toList();

    }
    @Override
    public Iterable<Loan> sortLoanByAmtInt(){
        return loanSet.stream().sorted(Comparator
                .comparing(Loan::getLoanAmount)
                .thenComparing(Loan::getInterest)).toList();

    }
    @Override
    public void details(){
        System.out.println("Max Min Average Sum Reduce ForEach");
        Loan maxloan=loanSet.stream()
                .max((l1,l2)-> Integer.compare(l1.getLoanAmount(),l2.getLoanAmount()))
                .orElse(null);
        System.out.println(maxloan);


//        orElse(null) removes the optional in min, max
//        System.out.println(loanSet.stream().max((l1,l2)-> l1.getLoanAmount()-l2.getLoanAmount()));

        System.out.println(loanSet.stream().min(Comparator.comparingInt(Loan::getLoanAmount)).orElse(null));

//        use numeric default in orElse
        System.out.println(loanSet.stream().mapToInt(Loan::getLoanAmount).average().orElse(0));

        int total = loanSet.stream().mapToInt(Loan::getLoanAmount).reduce(0,(a,current)->a+current);

        System.out.println("Sum of loans(reduce): "+total);

        int totalsum = loanSet.stream().mapToInt(Loan::getLoanAmount).sum();

        System.out.println("Sum of loans: "+totalsum);

        long count=loanSet.stream().mapToInt(Loan::getLoanid).count();

        System.out.println("No. of loans: "+count);

        System.out.println("------------------------");

        loanSet.stream().forEach((loan)-> System.out.println(loan));

        loanSet.stream().forEach(System.out::println);

        System.out.println("------------------------");

        System.out.println("Collectors");
        System.out.println(
                String.valueOf(loanSet.stream().collect(Collectors.toCollection(ArrayList::new)))

        );
        System.out.println(
                loanSet.stream().collect(Collectors.toSet())
        );
        System.out.println(
                loanSet.stream().collect(Collectors.averagingInt(Loan::getLoanAmount))
        );
        System.out.println(
                loanSet.stream().collect(Collectors.groupingBy(Loan::getLoanType,Collectors.summingInt(Loan::getLoanAmount)))
        );

        System.out.println(
                loanSet.stream().collect(Collectors.groupingBy(Loan::getLoanType,Collectors.counting()))
        );






    }
}
