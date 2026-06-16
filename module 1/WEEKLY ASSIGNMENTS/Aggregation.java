package nbfcExamples;

public class Aggregation {

    public static void main(String[] args) {

        // Creating CreditBureau object
        CreditBureau bureau = new CreditBureau();

        // Injecting bureau object into NBFC
        NBFC nbfc = new NBFC(bureau);

        // Calling method
        nbfc.evaluateApplicant();
    }
}

class NBFC {

    private CreditBureau bureau;

    // Aggregation through constructor
    public NBFC(CreditBureau bureau) {
        this.bureau = bureau;
    }

    public void evaluateApplicant() {

        System.out.println("NBFC evaluating applicant...");
        bureau.fetchCreditScore();
    }
}

class CreditBureau {

    public void fetchCreditScore() {

        System.out.println("Fetching CIBIL Score");
    }
}