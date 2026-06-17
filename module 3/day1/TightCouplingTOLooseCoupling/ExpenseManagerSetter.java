package MODULE3.TightCouplingTOLooseCoupling;

public class ExpenseManagerSetter {

    private PaymentService ps;
    private Notification nf;

    public Notification getNf() {
        return nf;
    }

    public void setNf(Notification nf) {
        this.nf = nf;
    }

    public PaymentService getPs() {
        return ps;
    }

    public void setPs(PaymentService ps) {
        this.ps = ps;
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

