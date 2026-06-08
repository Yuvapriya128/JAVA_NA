package InterfaceClass;

public class interface1 {
    public static void main(String[] args) {
        Shape shapeCircle=new Circle(1.5);
        System.out.println(shapeCircle.calculateArea());

        Shape shapeRectangle=new Rectangle(1,5.3);
        System.out.println(shapeRectangle.calculateArea());

        Shape shapeTriangle= new Triangle(2,3.4);
        System.out.println(shapeTriangle.calculateArea());

        System.out.println(Shape.color);
        System.out.println(Rectangle.color);

        Shape.colorshape();
    }
}
interface Shape{
    double calculateArea();//this is public abstract
    String color="Purple"; //variable is static , can be inherited
    static void colorshape(){
        System.out.println("Color is red");
    }
}
class Triangle implements Shape{
    private double base;
    private double length;

    public Triangle(double base, double length) {
        this.base = base;
        this.length = length;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
    public double calculateArea(){
        return 0.5*base*length;
    }

}
class Rectangle implements Shape{
    private double length;
    private double breadth;

    public Rectangle(double length, double breadth) {
        this.length = length;
        this.breadth = breadth;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getBreadth() {
        return breadth;
    }

    public void setBreadth(double breadth) {
        this.breadth = breadth;
    }
    public double calculateArea(){
        return this.length*this.breadth;
    }
}
class Circle implements Shape{
    private double radius;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Circle(double radius) {
        this.radius = radius;
    }
    public double calculateArea(){
        return Math.PI*this.radius*this.radius;
    }
}
