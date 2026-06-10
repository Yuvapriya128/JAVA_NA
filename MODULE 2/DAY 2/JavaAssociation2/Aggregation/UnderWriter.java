package JavaAssociation2.Aggregation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UnderWriter {
    private String name;
    private double approvalLimit;

    private List<LoanApplications> app;



    public UnderWriter(String name, double approvalLimit) {
        this.name = name;
        this.approvalLimit = approvalLimit;
        this.app=new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getApprovalLimit() {
        return approvalLimit;
    }

    public void setApprovalLimit(double approvalLimit) {
        this.approvalLimit = approvalLimit;
    }
    public void addApplications(LoanApplications applications){
        if(applications.getAmount()<getApprovalLimit())
        app.add(applications);
        else{
            System.out.println("Approval Limit exceeded");
        }
    }
    public void removeApplications(int customerid){
        LoanApplications temp=null;
        for(LoanApplications l:app){
            if(l.getCustomerId()==customerid){
                temp=l;
            }
        }
        app.remove(temp);
    }
    public List<LoanApplications> showApplications(){
        return app;
    }
}
