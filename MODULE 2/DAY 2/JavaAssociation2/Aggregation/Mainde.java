package JavaAssociation2.Aggregation;

public class Mainde {
    public static void main(String[] args) {

        /*

        *This is aggregation as i am creating Employees in Department
        as i continue to add employees, remove employees
        also with no employees i am creating Department

        *
        * */
        Department itdep=new Department("IT","chennai");
        itdep.addEmployees(new Employee("siva","nantham"));
        itdep.addEmployees(new Employee("madhu","sri"));
        itdep.showEmployees();
        itdep.removeEmployee("siva");
        itdep.showEmployees();

        Department salesdep=new Department("Sales","pune");
        salesdep.addEmployees(new Employee("mari","kumar"));
        salesdep.addEmployees(new Employee("selva","mani"));
        salesdep.showEmployees();
        salesdep.removeEmployee("selva");
        salesdep.showEmployees();



    }
}
