package intro;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String s1="Hello";
        String s2="Hello";
        System.out.println(s1==s2);
        s2=s2+"World";
        System.out.println(s1== s2.substring(0,5));
        String s3=new String("Hello");
//        Refers different location
//        but internally the heap refers to constant pool
        System.out.println(s3==s1);
//        internally it refers to the same
        System.out.println(s3.intern()==s1);

        char[] ch=s1.toCharArray();
        for(int i=0;i<ch.length;i++){
            System.out.print(ch[i]);
        }
        System.out.println();

        for(int i=s1.length()-1;i>=0;i--){
            System.out.print(s1.charAt(i));
        }
        System.out.println();

        System.out.println(s1.substring(0,3));
        System.out.println(s1.replaceFirst("H","Y"));
        System.out.println(s2.replaceAll("o","C"));

        System.out.println(" string ".trim());
        System.out.println("Byebyebye".startsWith("b",6));

        System.out.println("Byebyebye".endsWith("o"));

        System.out.println("Lava".substring(2,3));



    }
}