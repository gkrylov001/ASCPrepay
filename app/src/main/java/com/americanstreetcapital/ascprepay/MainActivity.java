package com.americanstreetcapital.ascprepay;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static double amount = 1000000;
    public static double rate = 4.50;
    public static double term = 120;
    public static double amort = 120;
    public static double paymentsMade = 0;
    public static double treasury = 2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            String title = "";
            switch (i) {
                case 0:
                    title = "Loan Info";
                    break;
                case 1:
                    title = "Results";
                    break;
                case 2:
                    title = "Amort Table";
                    break;
            }
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(title)
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new FormSectionFragment();
                case 1:
                    return new ResultsSectionFragment();
                case 2:
                    return new AmortSectionFragment();

                default:
                    return  new ResultsSectionFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

    /*
    **FORM FRAGMENT
     */
    public static class FormSectionFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_form, container, false);

            new GetTreasury().execute();

            EditText edAmount = (EditText) rootView.findViewById(R.id.edamount);
            edAmount.setHint("1,000,000");
            edAmount.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.toString().equals(".")) {
                        amount = Double.parseDouble(s.toString());
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });

            EditText edRate = (EditText) rootView.findViewById(R.id.rate);
            edRate.setHint("4.5");
            edRate.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.toString().equals(".")) {
                        rate = Double.parseDouble(s.toString());
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });

            EditText edTerm = (EditText) rootView.findViewById(R.id.term);
            edTerm.setHint("120");
            edTerm.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.toString().equals(".")) {
                        term = Double.parseDouble(s.toString());
                        new GetTreasury().execute();
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });

            EditText edAmort = (EditText) rootView.findViewById(R.id.amort);
            edAmort.setHint("120");
            edAmort.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.toString().equals(".")) {
                        amort = Double.parseDouble(s.toString());
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });

            EditText edPayments = (EditText) rootView.findViewById(R.id.paymentsMade);
            edPayments.setHint("0");
            edPayments.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.toString().equals(".")) {
                        paymentsMade = Double.parseDouble(s.toString());
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
            return rootView;
        }

        private class GetTreasury extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    treasury = Payment.decideRate((int) term);
                } catch (Exception e) {
                    treasury = 2;
                    return null;
                }
                return null;
            }
        }
    }

    /*
    **AMORT FRAGMENT
     */
    public static class AmortSectionFragment extends Fragment implements Runnable {
        Handler handler = new Handler();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_amortization, container, false);

            //handler.post(run);
            double[][] amortA = amort(amount, rate, amort);
            String[][] formatAmort = format(amortA);
            //fillTable(formatAmort[0].length,formatAmort,(TableLayout) rootView.findViewById(R.id.tableLayout1));
            fillLayout(formatAmort, (LinearLayout)rootView.findViewById(R.id.llContainer));

            return rootView;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                //handler.post(run);
                /*double[][] amortA = amort(amount, rate, amort);
                String[][] formatAmort = format(amortA);
                fillTable(formatAmort[0].length, formatAmort, (TableLayout) getActivity().findViewById(R.id.tableLayout1));*/
            }
            else {  }
        }

        private void fillLayout(String[][] matrix, LinearLayout l) {
            l.removeAllViews();
            for (int i = 0; i< matrix.length; i++) {
                LinearLayout row = new LinearLayout(getActivity());
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);

                for (int j = 0; j < matrix[0].length; j++) {
                    float weight;

                    TextView tv = new TextView(getActivity());
                    tv.setText(matrix[i][j]);
                    switch (j) {
                        case 0:
                            weight = .15f;
                            break;
                        case 3:
                            weight = .29f;
                            break;
                        default:
                            weight = .28f;
                            break;
                    }
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, weight);
                    tv.setLayoutParams(param);
                    //tv.setPadding(10,10,10,10);
                    row.addView(tv);
                }
                l.addView(row);
            }
        }

        private void fillTable(final int n, final String[][] matrix, TableLayout table) {
            table.removeAllViews();
            for (int i = 0; i < matrix.length; i++) {
                TableRow row = new TableRow(getActivity());
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                for (int j = 0; j < n; j++) {
                    TextView tv = new TextView(getActivity());
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                    tv.setText(matrix[i][j]);
                    tv.setPadding(10,10,10,10);
                    row.addView(tv);
                }
                table.addView(row);
            }
        }

        public static double[][] amort(double amountStart, double rate, double periods) {
            double payment = Payment.payment(amountStart,rate/100,periods);
            double[][] amortA = new double[(int) periods][4];
            double totalInterest = 0;
            for (int i=0; i<amortA.length; i++) {
                double interestPayment = interestPayment(amountStart, rate/100);
                double principalPayment = payment - interestPayment;
                double balance = amountStart - principalPayment;
                totalInterest += interestPayment;
                amortA[i][0] = i+1;
                amortA[i][1] = interestPayment;
                amortA[i][2] = principalPayment;
                amortA[i][3] = balance;
                //amort[i][3] = 2;
                //amort[i][4] = interestPayment;
                //amort[i][5] = totalInterest;
                //amort[i][6] = balance;
                amountStart = balance;
            }
            amortA[amortA.length-1][amortA[0].length-1] = 0;
            return amortA;
        }

        public static String[][] format(double[][] amort) {
            String[][] formatAmort = new String[amort.length][amort[0].length];
            //formatAmort[0][0] = "Period";
            //formatAmort[0][1] = "Beginning Balance";
            //formatAmort[0][2] = "Payment";
            //formatAmort[0][3] = "Principal";
            //formatAmort[0][4] = "Interest";
            //formatAmort[0][5] = "Cum. Interest";
            //formatAmort[0][6] = "Ending Balance";
            for (int i=0; i<amort.length; i++) {
                for (int j=0; j<amort[0].length; j++) {
                    double current = amort[i][j];
                    NumberFormat formatter = NumberFormat.getNumberInstance();
                    formatter.setMinimumFractionDigits(2);
                    formatter.setMaximumFractionDigits(2);
                    String output = formatter.format(current);
                    formatAmort[i][0] = Integer.toString(i+1);
                    formatAmort[i][j] = output;
                }
            }
            return formatAmort;
        }

        public static double interestPayment(double balance, double rate) {
            double mRate = rate/12;
            double payment = balance * mRate;
            return payment;
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                double[][] amortA = amort(amount, rate, amort);
                String[][] formatAmort = format(amortA);
                //fillTable(formatAmort[0].length, formatAmort, (TableLayout) getActivity().findViewById(R.id.tableLayout1));
            }
        };

        public void run() {}

    }

    /*
    **RESULTS FRAGMENT
     */
    public static class ResultsSectionFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_results, container, false);

            double monthlyPmt = Payment.payment(amount,rate/100,amort);
            double penalty = Payment.getPenalty(amount,rate/100,term,amort,paymentsMade,treasury/100);
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            String output = formatter.format(Math.abs(penalty));
            String out2 = formatter.format(Math.abs(penalty/amount));
            String out3 = formatter.format(Math.abs(monthlyPmt));
            TextView tv = (TextView) rootView.findViewById(R.id.resultsTV);
            tv.setTextSize(20);
            String results = "Loan Amount: $"+formatter.format(amount)+"\n\nMonthly Payment: $"+out3+"\n\nPrepayment Penalty: $"+output+"\n("+out2+"% of loan amount)";
            tv.setText(results);
            //new DisplayTask().execute();

            return rootView;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                double monthlyPmt = Payment.payment(amount,rate/100,amort);
                double penalty = Payment.getPenalty(amount,rate/100,term,amort,paymentsMade,treasury/100);
                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                String output = formatter.format(Math.abs(penalty));
                String out2 = formatter.format(Math.abs(penalty/amount*100));
                String out3 = formatter.format(Math.abs(monthlyPmt));
                TextView tv = (TextView) getActivity().findViewById(R.id.resultsTV);
                tv.setTextSize(20);
                String results = "Loan Amount: $"+formatter.format(amount)+"\n\nMonthly Payment: $"+out3+"\n\nPrepayment Penalty: $"+output+"\n("+out2+"% of loan amount)";
                tv.setText(results);
                //new DisplayTask().execute();
            }
            else {  }
        }
    }
}