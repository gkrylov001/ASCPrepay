package com.americanstreetcapital.ascprepay;

public class Payment {

    public static double payment(double amount, double interest, double amort) {
        double mRate = interest/12;
        double monthlyPmt = (amount*(mRate)) / (1-Math.pow(1+mRate, -amort));
        return monthlyPmt;
    }

    public static double futureValue(double amount, double interest, double numPay, double payment) {
        double mRate = interest/12;
        double fv = amount*Math.pow(1+mRate, numPay)-payment*(Math.pow(1+mRate, numPay)-1)/mRate;
        return fv;
    }

    public static double presentValue(double interest, double numPay, double payment, double fv) {
        double mRate = interest/12;
        double pv = payment*((1-Math.pow(1 + mRate, -numPay))/mRate)+fv/Math.pow(1+mRate,numPay);
        return pv;
    }

    public static double getPenalty(double amount, double rate, double term, double amort, double paymentsMade, double treasury) {
        double payment = payment(amount, rate, amort);
        double prepayBal = futureValue(amount, rate, paymentsMade, payment);
        double matBal = futureValue(amount, rate, term, payment);
        double pvRemainPay = presentValue(treasury, term - paymentsMade, payment, matBal);
        double penalty = prepayBal - pvRemainPay;
        //double percent = penalty / amount;

        /*NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        String output = formatter.format(Math.abs(penalty));*/
        return penalty;
    }

}