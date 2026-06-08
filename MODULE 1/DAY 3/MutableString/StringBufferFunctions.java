package MutableString;

public class StringBufferFunctions {
    public static void main(String[] args) {
        StringBuffer sb=new StringBuffer("volcano");
        System.out.println(sb.append("'s lava"));

        System.out.println(sb.reverse());
        System.out.println(sb.replace(8,10," "));
        System.out.println(sb.insert(0,"The "));
        System.out.println(sb.length());
        System.out.println(sb.capacity());
        sb.ensureCapacity(500);

        System.out.println(sb.delete(0,5));

        StringBuilder emp=new StringBuilder();
        System.out.println(emp.capacity()); //sb default 16

        System.out.println(sb.toString());

        System.out.println(sb.substring(0,5));
        System.out.println(sb.subSequence(0,9));

        sb.setCharAt(0,'V');
        System.out.println(sb);

        System.out.println(sb.charAt(0));



    }
}

