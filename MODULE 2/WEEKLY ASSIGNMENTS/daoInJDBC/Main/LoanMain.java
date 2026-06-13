/*Preferred way
 * Interface Reference = Implementation Object
 * */
package daoInJDBC.Main;

import daoInJDBC.dao.LoanDao;
import daoInJDBC.entity.Loan;
import daoInJDBC.ui.LoanImpl;

import java.sql.SQLException;
import java.util.Scanner;

public class LoanMain {

    private static Scanner sc = new Scanner(System.in);

    private static LoanDao loanDao = new LoanImpl();

    public static void main(String[] args) throws SQLException {

        do {

            System.out.println("\n===== LOAN MENU =====");

            System.out.println("1. Add Loan");
            System.out.println("2. Find By Id");
            System.out.println("3. Find All Loans");
            System.out.println("4. Find By Status");
            System.out.println("5. Find By Type");
            System.out.println("6. Find By Amount Greater Than");
            System.out.println("7. Find By Interest Less Than");
            System.out.println("8. Find By Type And Status");

            System.out.println("9. Sort By Amount");
            System.out.println("10. Sort By Amount Desc");
            System.out.println("11. Sort By Interest");
            System.out.println("12. Sort By Amount And Interest");

            System.out.println("13. Update Interest By Id");
            System.out.println("14. Update Loan Status");
            System.out.println("15. Update Interest By Type");

            System.out.println("16. Delete By Id");
            System.out.println("17. Delete By Status");
            System.out.println("18. Delete All");

            System.out.println("19. Exists By Id");

            System.out.println("20. Count Loans");
            System.out.println("21. Count By Status");

            System.out.println("22. Max Loan Amount");
            System.out.println("23. Min Loan Amount");
            System.out.println("24. Average Loan Amount");
            System.out.println("25. Total Loan Amount");

            System.out.println("26. Group By Status");
            System.out.println("27. Group By Type");
            System.out.println("28. Group By Type Having Count");

            System.out.println("29. Top N Loans");

            System.out.println("0. Exit");

            System.out.print("Enter option: ");

            int option = sc.nextInt();
            sc.nextLine();

            switch(option) {

                case 1 -> addLoan();

                case 2 -> findById();

                case 3 -> loanDao.findAll().forEach(System.out::println);

                case 4 -> findByStatus();

                case 5 -> findByType();

                case 6 -> findByAmount();

                case 7 -> findByInterest();

                case 8 -> findByTypeAndStatus();

                case 9 -> loanDao.sortByAmount().forEach(System.out::println);

                case 10 -> loanDao.sortByAmountDesc().forEach(System.out::println);

                case 11 -> loanDao.sortByInterest().forEach(System.out::println);

                case 12 -> loanDao.sortByAmountAndInterest().forEach(System.out::println);

                case 13 -> updateInterestById();

                case 14 -> updateLoanStatus();

                case 15 -> updateInterestByType();

                case 16 -> deleteById();

                case 17 -> deleteByStatus();

                case 18 -> loanDao.deleteAll();

                case 19 -> existsById();

                case 20 -> System.out.println(loanDao.countLoans());

                case 21 -> countByStatus();

                case 22 -> System.out.println(loanDao.getMaxLoanAmount());

                case 23 -> System.out.println(loanDao.getMinLoanAmount());

                case 24 -> System.out.println(loanDao.getAverageLoanAmount());

                case 25 -> System.out.println(loanDao.getTotalLoanAmount());

                case 26 -> loanDao.groupByStatus();

                case 27 -> loanDao.groupByType();

                case 28 -> groupByTypeHaving();

                case 29 -> topNLoans();

                default -> {
                    return;
                }
            }

        } while(true);
    }

    static void addLoan() throws SQLException {

        System.out.print("Type: ");
        String type = sc.nextLine();

        System.out.print("Amount: ");
        int amount = sc.nextInt();

        sc.nextLine();

        System.out.print("Status: ");
        String status = sc.nextLine();

        System.out.print("Interest: ");
        double interest = sc.nextDouble();

        System.out.print("Tenure: ");
        int tenure = sc.nextInt();

        Loan loan =
                new Loan(type, amount, status, interest, tenure);

        System.out.println(
                "Rows inserted: " +
                        loanDao.save(loan)
        );
    }

    static void findById() throws SQLException {

        System.out.print("Loan Id: ");

        int id = sc.nextInt();

        System.out.println(
                loanDao.findById(id)
        );
    }

    static void findByStatus() throws SQLException {

        System.out.print("Status: ");

        String status = sc.nextLine();

        loanDao.findByStatus(status)
                .forEach(System.out::println);
    }

    static void findByType() throws SQLException {

        System.out.print("Type: ");

        String type = sc.nextLine();

        loanDao.findByType(type)
                .forEach(System.out::println);
    }

    static void findByAmount() throws SQLException {

        System.out.print("Amount: ");

        int amount = sc.nextInt();

        loanDao.findByAmountGreaterThan(amount)
                .forEach(System.out::println);
    }

    static void findByInterest() throws SQLException {

        System.out.print("Interest: ");

        double interest = sc.nextDouble();

        loanDao.findByInterestLessThan(interest)
                .forEach(System.out::println);
    }

    static void findByTypeAndStatus() throws SQLException {

        System.out.print("Type: ");

        String type = sc.nextLine();

        System.out.print("Status: ");

        String status = sc.nextLine();

        loanDao.findByTypeAndStatus(type,status)
                .forEach(System.out::println);
    }

    static void updateInterestById() throws SQLException {

        System.out.print("Id: ");

        int id = sc.nextInt();

        System.out.print("Interest: ");

        double interest = sc.nextDouble();

        loanDao.updateInterestById(id,interest);
    }

    static void updateLoanStatus() throws SQLException {

        System.out.print("Id: ");

        int id = sc.nextInt();

        sc.nextLine();

        System.out.print("Status: ");

        String status = sc.nextLine();

        loanDao.updateLoanStatus(id,status);
    }

    static void updateInterestByType() throws SQLException {

        System.out.print("Type: ");

        String type = sc.nextLine();

        System.out.print("Interest: ");

        double interest = sc.nextDouble();

        loanDao.updateInterestByType(type,interest);
    }

    static void deleteById() throws SQLException {

        int id = sc.nextInt();

        loanDao.deleteById(id);
    }

    static void deleteByStatus() throws SQLException {

        String status = sc.nextLine();

        loanDao.deleteByStatus(status);
    }

    static void existsById() throws SQLException {

        int id = sc.nextInt();

        System.out.println(
                loanDao.existsById(id)
        );
    }

    static void countByStatus() throws SQLException {

        String status = sc.nextLine();

        System.out.println(
                loanDao.countByStatus(status)
        );
    }

    static void groupByTypeHaving() throws SQLException {

        int count = sc.nextInt();

        loanDao.groupByTypeHavingCountGreaterThan(count);
    }

    static void topNLoans() throws SQLException {

        int n = sc.nextInt();

        loanDao.topNLoans(n)
                .forEach(System.out::println);
    }
}