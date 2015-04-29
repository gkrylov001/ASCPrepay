package com.americanstreetcapital.ascprepay;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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

    public static double[] getRates() {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.treasury.gov/resource-center/data-chart-center/interest-rates/Pages/TextView.aspx?data=yield").get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Elements tables = doc.getElementsByClass("t-chart");
        Element rows = tables.get(0);
        Elements rowTD = rows.getElementsByTag("td");
        String[] rowTDS = new String[rowTD.size()];
        for (int i=0; i<rowTDS.length; i++) {
            Element td = rowTD.get(i);
            rowTDS[i] = td.html();
        }
        double[] rates = new double[11];
        int j=0;
        for (int i=11; i>0; i--) {
            rates[j] = Double.parseDouble(rowTDS[rowTDS.length-i]);
            j++;
        }
        return rates;
    }

    public static double decideRate(int term) {
        int years = Math.round(term/12);
        double[] yields = getRates();
        if (yields == null) {
            return .02;
        }
        double oneMo = yields[0];
        double threeMo = yields[1];
        double sixMo = yields[2];
        double oneYr = yields[3];
        double twoYr = yields[4];
        double threeYr = yields[5];
        double fiveYr = yields[6];
        double sevenYr = yields[7];
        double tenYr = yields[8];
        double twentyYr = yields[9];
        double thirtyYr = yields[10];
        if (term<12) {
            if (term<3)
                return (oneMo+threeMo)/2;
            else if (term>=3 && term <8)
                return (threeMo+sixMo)/2;
            else if (term>=8 && term<12)
                return (sixMo+oneYr)/2;
            else
                return oneYr;
        } else if (years < 2) {
            return (oneYr+twoYr)/2;
        } else if (years < 3) {
            return (twoYr+threeYr)/2;
        } else if (years < 4) {
            return (threeYr+fiveYr)/2;
        } else if (years < 6) {
            return (fiveYr+sevenYr)/2;
        } else if (years < 9) {
            return (sevenYr+tenYr)/2;
        } else if (years < 16) {
            return (tenYr+twentyYr)/2;
        } else if (years < 25) {
            return (twentyYr+thirtyYr)/2;
        } else {
            return thirtyYr;
        }
    }

}