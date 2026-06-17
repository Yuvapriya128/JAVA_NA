package nbfcSIOC.controller;

import nbfcSIOC.dao.BorrowDao;
import nbfcSIOC.entity.Borrow;

import java.util.Scanner;

public class BorrowConsoleController {
    private Scanner sc;
    private BorrowDao borrowDao;

    public BorrowConsoleController(Scanner sc, BorrowDao borrowDao) {
        this.sc = sc;
        this.borrowDao = borrowDao;
    }

    public void welcome(){
        System.out.println("Welcome to Borrower panel");
    }
    public void showMenu(){
        System.out.println("Menu");
        System.out.println("1.add");
        System.out.println("2.Find by id");
        System.out.println("3.Find All");
        System.out.println("4.update by id");
        System.out.println("5.delete by id");

        do{
            System.out.println("Enter option:");
            int option=sc.nextInt();
            redirectChoice(option);
        }while(true);
    }
    private Borrow readBorrow() {
        Borrow borrow = new Borrow();

        System.out.println("Enter lender name");
        borrow.setLenderName(sc.next());

        System.out.println("Enter lender type");
        borrow.setLenderType(sc.next());

        System.out.println("Enter amount");
        borrow.setAmount(sc.nextDouble());

        System.out.println("Enter interest");
        borrow.setInterest(sc.nextDouble());

        System.out.println("Enter status");
        borrow.setStatus(sc.next());

        System.out.println("Enter tenure months");
        borrow.setTenureMonths(sc.nextInt());

        System.out.println("Enter borrowed date (yyyy-mm-dd)");
        borrow.setBorrowedDate(java.time.LocalDate.parse(sc.next()));

        System.out.println("Enter maturity date (yyyy-mm-dd)");
        borrow.setMaturityDate(java.time.LocalDate.parse(sc.next()));

        return borrow;
    }

    private void redirectChoice(int option){
        switch (option){
            case 1 -> borrowDao.save(readBorrow());

            case 4 -> {
                System.out.println("Enter id");
                int id = sc.nextInt();
                borrowDao.updateById(id, readBorrow());
            }
            case 2->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                System.out.println(borrowDao.findById(id));}
            case 3->{
                borrowDao.findAll().forEach(System.out::println);
            }

            case 5->{
                System.out.println("Enter id");
                int id=sc.nextInt();
                borrowDao.deleteById(id);
            }
            default -> {throw new IllegalArgumentException("Invalid choice");}
        }
    }
}
