package org.example.loananalytics.Runner;

import org.example.loananalytics.service.LendingAnalytics;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class Runner implements CommandLineRunner {
    @Override
    public void run(String... args) {

        System.out.println("Runner Started");

        List<String> records = List.of(
                "A101|Rahul Sharma|HDFC|Personal Loan|500000|780",
                "A102|Priya Verma|ICICI|Home Loan|4500000|820",
                "A103|Amit Singh|Axis Bank|Car Loan|900000|760",
                "A104|Rahul Rahul Sharma|HDFC|Personal Loan|300000|700",
                "A105|HDFC Kumar|SBI|Personal Loan|700000|650",
                "A106|RAM KUMAR|ICICI|Home Loan|1000000|800",
                "A107|KUMAR RAM|ICICI|Home Loan|1000000|800",
                "A108|Amit Sara|Axis Bank|Home Loan|900000|760"
        );

        LendingAnalytics analytics = new LendingAnalytics();

        analytics.loadApplications(records);

        System.out.println("\nTop Credit Profiles");
        System.out.println(analytics.topCreditProfiles(3));

        System.out.println("\nAverage Loan Amount By Type");
        System.out.println(analytics.averageLoanAmountByType());

        System.out.println("\nHighest Loan Application");
        System.out.println(analytics.highestLoanApplication());

        System.out.println("\nLenders With Multiple Loan Types");
        System.out.println(analytics.lendersWithMultipleLoanTypes());

        System.out.println("\nGroup Applications By Lender");
        System.out.println(analytics.groupApplicationsByLender());

        System.out.println("\nSuspicious Applications");
        System.out.println(analytics.suspiciousApplications());
    }
}