package Overloading;

public class WeakerAccess {
    public void display(){
        System.out.println("Public display parent");
    }
    protected void ddisplay(){
        System.out.println("protected display parent");
    }
    private void dddisplay(){
        System.out.println("private display parent");
    }
    void ddddisplay(){
        System.out.println("Default display parent");
    }
}
class access extends WeakerAccess{
    @Override
    public void display(){
        System.out.println("Public display child");
    }
    @Override
    protected void ddisplay(){
        System.out.println("protected display child");
    }
    public void dddisplay(){
        System.out.println("private display child");
    }
    @Override
    void ddddisplay(){
        System.out.println("Default display child");
    }
    public static void main(String[] args) {
        WeakerAccess wa=new access();
        wa.display();
        wa.ddisplay();

        wa.ddddisplay();

        access ac=new access();
        ac.dddisplay();

        WeakerAccess awa=new WeakerAccess();
        awa.display();
        awa.ddisplay();
//        awa.dddisplay();
        awa.ddddisplay();





    }
}
