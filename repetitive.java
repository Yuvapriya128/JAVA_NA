import java.util.Scanner;

class repetitive{
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);

        System.out.println("Enter range(for even no.):");
        int erange=sc.nextInt();
        even_range(erange);

        System.out.println("Enter range(for odd no.):");
        int orange=sc.nextInt();
        odd_range(orange);

        System.out.println("Enter multiplication table:");
        int mul=sc.nextInt();
        multiplication_table(mul);

        System.out.println("Enter number(for factors):");
        int fnum=sc.nextInt();
        factors(fnum);

        System.out.println("Enter number(for count factors):");
        int cfnum=sc.nextInt();
        count_factors(cfnum);

        System.out.println("Enter number(for sum factors):");
        int sfnum=sc.nextInt();
        sum_factors(sfnum);

        System.out.println("Enter number(prime):");
        int pnum=sc.nextInt();
        prime(pnum);

        System.out.println("Enter number(fibonacci range):");
        int frange=sc.nextInt();
        fibonacci(frange);

        System.out.println("Enter number(for factorial):");
        int fact=sc.nextInt();
        factorial(fact);
   
    }
    public static void even_range(int range) {
        if(range<0){
            System.out.println("invalid");
        }
        for(int i=0;i<=range;i++){
            if(i%2==0){
                System.out.print(i+" ");
            }
        }
        System.out.println();
        
    }
     public static void odd_range(int range) {
        if(range<0){
            System.out.println("invalid");
        }
        for(int i=0;i<=range;i++){
            if(i%2!=0){
                System.out.print(i+" ");
            }
        }
        System.out.println();
        
    }
    public static void multiplication_table(int num){
        if(num==0 || num<0){
            System.out.println("Invalid");
        }
        for(int i=1;i<=20;i++){
            System.out.print(num+" * "+i+" = "+num*i);
        }
        System.out.println();
    }
    public static void factors(int num){
        if(num==0 ||num<0){
            System.out.println("Invalid");
        }
        for(int i=1;i<=num;i++){
            if(num%i==0)
            System.out.print(i +" ");
        }
        System.out.println();
    }

    public static void count_factors(int num){
         if(num==0 ||num<0){
            System.out.println("Invalid");
        }
        int cnt=0;
        for(int i=1;i<=num;i++){
            if(num%i==0){
                cnt++;
            }           
        }
        System.out.println("count of factors:"+cnt);
    }
     public static void sum_factors(int num){
         if(num==0 ||num<0){
            System.out.println("Invalid");
        }
        int sum=0;
        for(int i=1;i<=num;i++){
            if(num%i==0){
                sum+=i;
            }           
        }
        System.out.println("sum of factors:"+sum);
    }
     public static void prime(int num){
         if(num==0 ||num<0){
            System.out.println("Invalid");
        }else if(num==1){
            System.out.println("neither prime nor consonant");
        }else{
            for(int i=2;i<num;i++) {
                if(num%i == 0){
                    System.out.println("not prime");
                    return; //or System.exit(0);
                }else{
                    continue;
                }
            }
            System.out.println("prime");
        }
    }
    public static void fibonacci(int range){
        int n1=0;
        int n2=1;
        int sum=0;
        if(range==1){
            System.out.println(n1);
        }else if(range==2){
            System.out.println(n1+ " "+n2);
        }else{
        System.out.println("0 1 ");
        
        range=range-2;
        while(range>0){
            sum=n1+n2;
            n1=n2;
            n2=sum;
            System.out.print(sum+" ");
            range--;
        }
        System.out.println();
    }
    }

    public static void factorial(int num) {
        int res=1;
        for(int i=num;i>0;i--){
             res=res*i;
        }        
        System.out.println("Factorial "+res);
    }


}
