package MODULE3.TightCouplingTOLooseCoupling;

public class ExpenseManager {
//    PaymentService : interface
//    Debitcard : implements paymentService and override

//    tight coupling : as it creates its dependency dobject
//    private PaymentService ps=new DebitCard();

//    To do loose coupling : create constructor / setter
    private PaymentService ps;
    private Notification nf;

    public ExpenseManager(PaymentService ps,Notification nf) {
        this.ps = ps;
        this.nf=nf;
    }

    public void  payElectricityBill(double amt){
        System.out.println("Paying  Electricity bill "+amt);
        ps.pay(amt);
        nf.sendMessage("Paid bill");

    }
    public void  payWaterBill(double amt){
        System.out.println("Paying  Water bill "+amt);
        ps.pay(amt);
        nf.sendMessage("Paid bill");
    }
    public void  payGasBill(double amt){
        System.out.println("Paying Gas bill "+amt);
        ps.pay(amt);
        nf.sendMessage("Paid bill");
    }
}
