package intro;

import java.util.Scanner;

public class CountVowels {
    public static void main(String[] args) {
        System.out.println("Enter a word(vowels) :");
        int cnt=0;
        Scanner sc=new Scanner(System.in);
        String str=sc.nextLine();
        str=str.toLowerCase();
        char[] ch={'a','e','i','o','u'};
        char[] strchar=str.toCharArray();
        for(int i=0;i<ch.length;i++){
            for(int j=0;j<strchar.length;j++){
                if(ch[i]==(strchar[j])){
                    cnt++;
                }
            }
        }
        System.out.println("No. of vowels :"+cnt+" in "+str);
    }
}

//string "aeiou"
//another string1 "vowel"

//for loop
//aeiou.contains(""+string1.charAt(i)))
//cnt++