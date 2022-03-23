package com.example.tipsplit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RBClicked: ";
    private int percent;
    double mult = 0.0;
    double r_total = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void rbClicked(View v){
        RadioGroup rg;

        TextView tax_tv = (TextView) findViewById(R.id.TipAmount);
        TextView total_tv = (TextView) findViewById(R.id.TotalWithTip);



        EditText user_bill = (EditText) findViewById(R.id.BillTotal);
        String str_user_bill = user_bill.getText().toString();
        double dob_user_bill;

        rg = (RadioGroup) findViewById(R.id.radioGroup);
        // Log.d(TAG, "rbClicked: " + str_user_bill.length());
        // Log.d(TAG, "rbClicked: " + str_user_bill);
        if (str_user_bill.length() == 0){
            Log.d(TAG, "rbClicked: No Value entered for user_bill" );
            rg.clearCheck();
        }
        //id i = rg.getCheckedRadioButtonId();
        // RadioButton rb_checked = findViewById(R.id.);

        else{

            //RadioButton rb = rg.findViewById(rg.getCheckedRadioButtonId());
            Log.d(TAG, "rbClicked: " + v.getId());
            if (v.getId() == R.id.rb12){
                mult = .12;
                //Log.d(TAG, "rbClicked: IN RB12! " + Double.toString(mult));
            } else if (v.getId() == R.id.rb15){
                mult = .15;
            } else if (v.getId() == R.id.rb18){
                mult = .18;
            } else if (v.getId() == R.id.rb20){
                mult = .20;
            }
            Log.d(TAG, "rbClicked: r.ID.RB12" + R.id.rb12 );
            /*
            boolean checked = ((RadioButton) v).isChecked();

            // Check which radio button was clicked
            switch(v.getId()) {
                case R.id.rb12:
                    if (checked)
                        percent = 12;
                        break;
                case R.id.rb15:
                    if (checked)
                        percent = 15;
                        break;
                case R.id.rb18:
                    if (checked)
                        percent = 18;
                    break;
                case R.id.rb20:
                    if (checked)
                        percent = 20;
                    break;
            }*/

            Log.d(TAG, "rbClicked: " + str_user_bill );

            dob_user_bill = Double.parseDouble(str_user_bill);
            Log.d(TAG, "rbClicked: Dob_user_bill" + Double.toString(dob_user_bill) );
            //mult = mult/100;
            Log.d(TAG, "rbClicked: mult" + Double.toString(mult) );
            double tax = dob_user_bill * mult;
            Log.d(TAG, "rbClicked: tax" + Double.toString(tax) );
            double r_tax = Math.ceil(tax * 100.0) / 100.0;
            Log.d(TAG, "rbClicked: r_tax" + Double.toString(r_tax) );
            String str_tax = Double.toString(r_tax);
            Log.d(TAG, "rbClicked: str_tax" + str_tax);

            double total = dob_user_bill + r_tax;
            r_total = Math.ceil(total * 100.0)/100.0;
            String str_r_total = Double.toString(r_total);

            tax_tv.setText("$"+str_tax);
            total_tv.setText("$"+str_r_total);
        }
    }


    public void goClicked(View v){
        EditText np_et = (EditText) findViewById(R.id.NumPeople);
        String str_np = np_et.getText().toString();

        TextView per_person_tv = (TextView) findViewById(R.id.PerPerson);
        TextView overage_tv = (TextView) findViewById(R.id.Overage);

        TextView total_tv = (TextView) findViewById(R.id.TotalWithTip);

        if (total_tv.getText().toString().length() != 0) { // No Bill Total entered & Percent or either
            if (str_np.length() > 0) { // An input is placed in Num People field
                if (Integer.parseInt(str_np) != 0) {  //Num People != 0
                    int np = Integer.parseInt(str_np);
                    double per_person = r_total / np;
                    double r_per_person = Math.ceil(per_person * 100.0) / 100.0;
                    double overage = (np * r_per_person) - r_total;
                    overage = Math.ceil(overage * 100.0) / 100.0;

                    per_person_tv.setText("$" + Double.toString(r_per_person));
                    overage_tv.setText("$" + Double.toString(overage));
                } else {  // Num people == 0
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Must be more than 0!", Toast.LENGTH_LONG).show();
                    //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                    np_et.setText("");
                }
            } else {  // No num of people entered
                Context context = getApplicationContext();
                Toast.makeText(context, "Enter number of people first! ", Toast.LENGTH_SHORT).show();
            }
        } else{ //
            Context context = getApplicationContext();
            Toast.makeText(context, "Enter Bill Total & Select Tip Percent first!", Toast.LENGTH_LONG).show();

        }

    }

    public void clearClicked(View v){
        EditText user_bill = (EditText) findViewById(R.id.BillTotal);
        EditText np_et = (EditText) findViewById(R.id.NumPeople);
        TextView tax_tv = (TextView) findViewById(R.id.TipAmount);
        TextView total_tv = (TextView) findViewById(R.id.TotalWithTip);
        TextView per_person_tv = (TextView) findViewById(R.id.PerPerson);
        TextView overage_tv = (TextView) findViewById(R.id.Overage);

        user_bill.setText("");
        tax_tv.setText("");
        total_tv.setText("");
        np_et.setText("");
        per_person_tv.setText("");
        overage_tv.setText("");

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.clearCheck();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        TextView tax_tv = (TextView) findViewById(R.id.TipAmount);
        TextView total_tv = (TextView) findViewById(R.id.TotalWithTip);
        TextView per_person_tv = (TextView) findViewById(R.id.PerPerson);
        TextView overage_tv = (TextView) findViewById(R.id.Overage);
        outState.putDouble("rtotal", r_total);

        outState.putString("TipAmount", tax_tv.getText().toString());
        outState.putString("TotalWithTip", total_tv.getText().toString());
        outState.putString("PerPerson", per_person_tv.getText().toString());
        outState.putString("Overage", overage_tv.getText().toString());
        outState.putDouble("dmult", mult);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView tax_tv = (TextView) findViewById(R.id.TipAmount);
        TextView total_tv = (TextView) findViewById(R.id.TotalWithTip);
        TextView per_person_tv = (TextView) findViewById(R.id.PerPerson);
        TextView overage_tv = (TextView) findViewById(R.id.Overage);


        tax_tv.setText(savedInstanceState.getString("TipAmount"));
        total_tv.setText(savedInstanceState.getString("TotalWithTip"));
        per_person_tv.setText(savedInstanceState.getString("PerPerson"));
        overage_tv.setText(savedInstanceState.getString("Overage"));
        r_total = savedInstanceState.getDouble("rtotal");

        /*
        mult = savedInstanceState.getDouble("dmult");
        double copy = mult;
        String str_mult = "";
        if (mult != 0.0){
            copy = copy * 100;
            int c = (int)copy;
            str_mult += Integer.toString(c);
            RadioGroup rg = findViewById(R.id.radioGroup);
            rg.check(R.id.);
        }*/



    }
}