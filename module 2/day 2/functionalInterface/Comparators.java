package FunctionalInterface;

import java.util.function.*;

public class Comparators {
    public static void main(String[] args) {

        Function<String,Integer> d=(String s)->s.length();
        System.out.println(d.apply("Mountains"));

        BiFunction<Integer,Integer,Integer> b=(t,u)->t+u;
        System.out.println(b.apply(17,293));

        Predicate<Integer> isEven =n -> n%2==0;
        System.out.println(isEven.test(8));

        BiPredicate<String,String> isSame=(s1,s2) -> s1.equalsIgnoreCase(s2);
        System.out.println(isSame.test("JAVA","java"));

        Consumer<String> name=s ->{
            System.out.println("Welcome "+s);
        };
        name.accept("Yuva");

        BiConsumer<String,Integer> details=(s1,age) -> {
            System.out.println("Details:" +s1+" "+age);
        };
        details.accept("Gokul",19);

        Supplier<String> country=()->{
            return "India";
        };
        System.out.println(country.get());

        UnaryOperator<Integer> square=n-> n*n;
        System.out.println(square.apply(8));

        BinaryOperator<Integer> mul=(n1,n2) ->n1*n2;
        System.out.println(mul.apply(8,9));

        
    }
}
