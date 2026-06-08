package intro;

import java.util.Scanner;

public class CountSpaces {
    public static void main(String[] args) {
        System.out.println("Enter a word(spaces) :");
        int cnt=0;
        Scanner sc=new Scanner(System.in);
        String str=sc.nextLine();
        str=str.trim();
        str=str.toLowerCase();
        char[] ch={' '};
        char[] strchar=str.toCharArray();
            for(int j=0;j<strchar.length;j++){
                if(ch[0]==(strchar[j])){
                    cnt++;
                }
            }
        System.out.println("No. of spaces :"+cnt+" in "+str);
    }

 }
//string s " "
//another string1 "vowel"

//for loop
//s.contains(""+string1.charAt(i)))
//cnt++
