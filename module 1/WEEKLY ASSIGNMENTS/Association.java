package nbfcExamples;

public class Association {

    public static void main(String[] args) {

        Applicant applicant =
                new Applicant("Yuvapriya");

        RelationshipManager manager =
                new RelationshipManager("Chandu");

        LoanDiscussion discussion =
                new LoanDiscussion(applicant, manager);

        discussion.showDiscussion();
    }
}

class LoanDiscussion {

    private Applicant applicant;
    private RelationshipManager manager;

    public LoanDiscussion(Applicant applicant,
                          RelationshipManager manager) {

        this.applicant = applicant;
        this.manager = manager;
    }

    public void showDiscussion() {

        System.out.println(
                applicant.getName()
                        + " discussing loan with "
                        + manager.getManagerName()
        );
    }
}

class RelationshipManager {

    private String managerName;

    public RelationshipManager(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerName() {
        return managerName;
    }
}

class Applicant {

    private String name;

    public Applicant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}