import java.util.Scanner;

public class repetitive2 {
    public static void main(String[] args) {
      System.out.println("Enter the row(all patterns):");
      Scanner sc=new Scanner(System.in);
      int n=sc.nextInt();
      
      System.out.println("PATTERNS");
      pattern1(n);
      System.out.println();

      pattern2(n);
      System.out.println();

      pattern3(n);
      System.out.println();
      
      pattern4(n);
      System.out.println();
      
      pattern5(n);
      System.out.println();
      
      // pattern6(n);
      // System.out.println();
      
      pattern7(n);
      System.out.println();
      
      pattern8(n);
      System.out.println();
      
      pattern9(n);
              
    }
    public static void pattern1(int range) {
          for(int i=0;i<=range;i++){
            for(int j=0;j<i;j++){
            System.out.print("* ");
            }
            System.out.println();
          }      
    }
    public static void pattern2(int range) {
          for(int i=range;i>0;i--){
            for(int j=0;j<i;j++){
            System.out.print("* ");
            }
            System.out.println();
          }      
    }
    public static void pattern3(int range) {
          for(int i=range;i>0;i--){
            for(int j=0;j<range-i;j++){
            System.out.print(" ");
            }
            for(int j=0;j<i;j++){
            System.out.print("*");
            }
            System.out.println();
          }      
    }
    public static void pattern4(int n) {
      for(int i=1;i<=n;i++){
            for(int j=1;j<=n;j++){
                if(i==1 || i==n || j==1 ||j==n){
                    System.out.print("*");
                }else{
                    System.out.print(" ");
                }
            }
            System.out.println();
        }        
    }
    public static void pattern5(int n) {
      for(int i=1;i<=n;i++){
        for(int j=1;j<=n-i;j++){
            System.out.print(" ");
        }
        for(int j=1;j<= 2*i-1;j++){
            System.out.print("*");
        }
        
        System.out.println();
        }
    }
    // public static void pattern6(int n) {
    //   for(int i=1;i<n;i++){
    //     for(int j=1;j<=n-i;j++){
    //       System.out.print(" ");
    //     }
    //     for(int j=1;j<=2*i-1;j++){
    //       System.out.print("* ");
    //     }
    //     System.out.println();
    //   }      
    // }
    public static void pattern7(int n) {
      for(int i=1;i<=n;i++){
        for(int j=1;j<=n-i;j++){
          System.out.print(" ");
        }
        for(int j=1;j<=i;j++){
          System.out.print("* ");
        }
        System.out.println();
      }
    
    }
    public static void pattern8(int n) {
      for(int i=1;i<=n;i++){
        for(int j=1;j<=n-i;j++){
          System.out.print(" ");
        }
        for(int j=1;j<=i;j++){
          System.out.print("*");
        }
        System.out.println();
      }
    
    }
    public static void pattern9(int n) {
      for(int i=1;i<=n;i++){
        for(int j=1;j<=n-i;j++){
          System.out.print(" ");
        }
        if(i>1){
          System.out.print("*");
          for(int k=1;k<=2*i-3;k++){
            System.out.print(" ");
          }
          System.out.print("*");
        }else{
          System.out.print("*");
        }
        System.out.println();
      }
      for(int i=n-1;i>0;i--){
        for(int j=1;j<=n-i;j++){
          System.out.print(" ");
        }
        if(i>1){
          System.out.print("*");
          for(int k=1;k<=2*i-3;k++){
            System.out.print(" ");
          }
          System.out.print("*");
        }else{
          System.out.print("*");
        }
        System.out.println();
      }

    }

}
