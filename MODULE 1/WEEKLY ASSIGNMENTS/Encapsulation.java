package nbfcExamples;

 class LoanApplicant {

    private String applicantId;
    private String applicantName;
    private int creditScore;
    private double annualIncome;

    public LoanApplicant(String applicantId,
                         String applicantName,
                         int creditScore,
                         double annualIncome) {
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.creditScore = creditScore;
        this.annualIncome = annualIncome;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {

        if(creditScore >= 300 && creditScore <= 900) {
            this.creditScore = creditScore;
        }
    }

    @Override
    public String toString() {
        return applicantName + " Score: " + creditScore;
    }

     public static void main(String[] args) {
         LoanApplicant app=new LoanApplicant("12","yuva",950,50000.00);
         System.out.println(app.getCreditScore());
     }
}