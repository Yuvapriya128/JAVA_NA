package com.example.demo.service;

import org.junit.jupiter.api.*;

//To create order while exection
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalculatorServiceTest {
    private  static CalculatorService cal;

    @BeforeAll

    static void setup(){
        System.out.println("Before All");
        cal=new CalculatorService();

    }

//For this, there is no need to make cal as static
//creates object everytime
  @BeforeEach
    void setCal(){
        System.out.println("Before Each");
//        cal=new CalculatorService();
    }

    @Test
//    @Order(1)

    void add(){
        System.out.println("ADD");
        int actualres=cal.add(3,4);
        int expectedres=6;
        assert (actualres==expectedres);
    }
    @Test

    void sub(){
        System.out.println("SUB");
        int actualres=cal.sub(3,4);
        int expectedres=-1;
        assert (actualres==expectedres);
    }
    @Test
    void mul(){
        System.out.println("MUL");
        int actualres=cal.mul(3,4);
        int expectedres=12;
        assert (actualres==expectedres);
    }
    @Test
    void div(){
        System.out.println("DIV");
        int actualres=cal.div(8,4);
        int expectedres=2;
        assert (actualres==expectedres);
    }

    @AfterAll
    static void  end(){
        System.out.println("After all");
    }

    @AfterEach
     void  endeach(){
        System.out.println("After each");
    }




}
