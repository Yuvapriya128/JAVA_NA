package org.example.loananalytics.service;

import org.example.loananalytics.LoanAnalyticsApplication;
import org.example.loananalytics.model.LoanApplication;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LendingAnalytics {
    Map<String, LoanApplication> applications=new HashMap<>();

    public void loadApplications(List<String> records) {
        for (String record : records) {
            if (record == null || record.trim().isEmpty()) continue;

            String[] parts = record.split("\\|");
            if (parts.length != 6) continue;

            try {
                String applicationId = parts[0].trim();
                String customerName = parts[1].trim();
                String lenderName = parts[2].trim();
                String loanType = parts[3].trim();
                double loanAmount = Double.parseDouble(parts[4].trim());
                int creditScore = Integer.parseInt(parts[5].trim());

                if (applicationId.isEmpty()
                        || customerName.isEmpty()
                        || lenderName.isEmpty()
                        || loanType.isEmpty()
                        || loanAmount <= 0
                        || creditScore < 300
                        || creditScore > 900)
                    continue;

                LoanApplication current = new LoanApplication(
                        applicationId,
                        customerName,
                        lenderName,
                        loanType,
                        loanAmount,
                        creditScore
                );

                applications.merge(applicationId, current, (oldApp, newApp) -> {
                    if (newApp.getCreditScore() > oldApp.getCreditScore()) return newApp;
                    if (newApp.getCreditScore() < oldApp.getCreditScore()) return oldApp;
                    if (newApp.getLoanAmount() < oldApp.getLoanAmount()) return newApp;
                    if (newApp.getLoanAmount() > oldApp.getLoanAmount()) return oldApp;
                    if (newApp.getCustomerName().compareTo(oldApp.getCustomerName()) < 0) return newApp;
                    return oldApp;
                });

            } catch (Exception e) {
            }
        }
    }

    public List<LoanApplication> topCreditProfiles(int n) {
        return applications.values()
                .stream()
                .sorted(
                        Comparator.comparingInt(LoanApplication::getCreditScore)
                                .reversed()
                                .thenComparing(LoanApplication::getLoanAmount)
                                .thenComparing(LoanApplication::getCustomerName)
                )
                .limit(n)
                .toList();
    }

    public Map<String, Double> averageLoanAmountByType() {
        return applications.values()
                .stream()
                .collect(Collectors.groupingBy(
                        LoanApplication::getLoanType,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(LoanApplication::getLoanAmount),
                                avg -> Math.round(avg * 100.0) / 100.0
                        )
                ));
    }

    public Optional<LoanApplication> highestLoanApplication() {
        return applications.values()
                .stream()
                .max(
                        Comparator.comparingDouble(LoanApplication::getLoanAmount)
                                .thenComparingInt(LoanApplication::getCreditScore)
                                .thenComparing(
                                        LoanApplication::getApplicationId,
                                        Comparator.reverseOrder()
                                )
                );
    }

    public Set<String> lendersWithMultipleLoanTypes() {
        return applications.values()
                .stream()
                .collect(Collectors.groupingBy(
                        LoanApplication::getLenderName,
                        Collectors.mapping(
                                LoanApplication::getLoanType,
                                Collectors.toSet()
                        )
                ))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Map<String, List<LoanApplication>> groupApplicationsByLender() {
        return applications.values()
                .stream()
                .sorted(Comparator.comparing(LoanApplication::getLenderName))
                .collect(Collectors.groupingBy(
                        LoanApplication::getLenderName,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(
                                                Comparator.comparingInt(LoanApplication::getCreditScore)
                                                        .reversed()
                                                        .thenComparing(LoanApplication::getLoanAmount)
                                        )
                                        .toList()
                        )
                ));
    }

    public Map<String, Map<String, Optional<LoanApplication>>> loanTypeWiseTopApplicantByLender() {
        return applications.values()
                .stream()
                .collect(Collectors.groupingBy(
                        LoanApplication::getLoanType,
                        Collectors.groupingBy(
                                LoanApplication::getLenderName,
                                Collectors.maxBy(
                                        Comparator.comparingInt(LoanApplication::getCreditScore)
                                                .thenComparingDouble(LoanApplication::getLoanAmount)
                                )
                        )
                ));
    }

    public List<String> suspiciousApplications() {

        Collection<LoanApplication> apps = applications.values();

        Map<String, Double> avgLoanByType = apps.stream()
                .collect(Collectors.groupingBy(
                        LoanApplication::getLoanType,
                        Collectors.averagingDouble(LoanApplication::getLoanAmount)
                ));

        Map<String, Double> avgCreditByType = apps.stream()
                .collect(Collectors.groupingBy(
                        LoanApplication::getLoanType,
                        Collectors.averagingInt(LoanApplication::getCreditScore)
                ));

        // Condition 1:
        // Customer name contains consecutive repeated words
        Set<String> c1 = apps.stream()
                .filter(a -> {
                    String[] words = a.getCustomerName().trim().toLowerCase().split("\\s+");
                    return IntStream.range(0, words.length - 1)
                            .anyMatch(i -> words[i].equals(words[i + 1]));
                })
                .map(LoanApplication::getCustomerName)
                .collect(Collectors.toSet());

        // Condition 2:
        // Lender name appears inside customer name
        Set<String> c2 = apps.stream()
                .filter(a ->
                        a.getCustomerName().toLowerCase()
                                .contains(a.getLenderName().toLowerCase()))
                .map(LoanApplication::getCustomerName)
                .collect(Collectors.toSet());

        // Condition 3:
        // Loan amount exceeds loan type average by 250%
        Set<String> c3 = apps.stream()
                .filter(a ->
                        avgLoanByType.getOrDefault(a.getLoanType(), 0.0) > 0 &&
                                a.getLoanAmount() >
                                        avgLoanByType.get(a.getLoanType()) * 2.5)
                .map(LoanApplication::getCustomerName)
                .collect(Collectors.toSet());

        // Condition 4:
        // Credit score below loan type average
        // AND loan amount above loan type average
        Set<String> c4 = apps.stream()
                .filter(a ->
                        a.getCreditScore() <
                                avgCreditByType.get(a.getLoanType()) &&
                                a.getLoanAmount() >
                                        avgLoanByType.get(a.getLoanType()))
                .map(LoanApplication::getCustomerName)
                .collect(Collectors.toSet());

        // Condition 5:
        // Customer name contains more than 3 words
        Set<String> c5 = apps.stream()
                .filter(a ->
                        a.getCustomerName().trim()
                                .split("\\s+").length > 3)
                .map(LoanApplication::getCustomerName)
                .collect(Collectors.toSet());

        // Condition 6:
        // Same customer applied with more than 3 different lenders
        Set<String> c6 = apps.stream()
                .collect(Collectors.groupingBy(
                        LoanApplication::getCustomerName,
                        Collectors.mapping(
                                LoanApplication::getLenderName,
                                Collectors.toSet()
                        )))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Condition 7:
        // Same loan type + same loan amount + same credit score
        // but different customer names
        Set<String> c7 = apps.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getLoanType() + "|" +
                                a.getLoanAmount() + "|" +
                                a.getCreditScore()
                ))
                .values()
                .stream()
                .filter(list ->
                        list.stream()
                                .map(LoanApplication::getCustomerName)
                                .distinct()
                                .count() > 1)
                .flatMap(List::stream)
                .map(LoanApplication::getCustomerName)
                .collect(Collectors.toSet());

        // Condition 8:
        // Customer name is an anagram of another customer
        // within the same lender
        Set<String> c8 = apps.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getLenderName() + "|" +
                                Arrays.stream(
                                                a.getCustomerName()
                                                        .toLowerCase()
                                                        .replaceAll("\\s+", "")
                                                        .split(""))
                                        .sorted()
                                        .collect(Collectors.joining())
                ))
                .values()
                .stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .map(LoanApplication::getCustomerName)
                .collect(Collectors.toSet());

        return Stream.of(c1, c2, c3, c4, c5, c6, c7, c8)
                .flatMap(Set::stream)
                .distinct()
                .sorted()
                .toList();
    }
}
