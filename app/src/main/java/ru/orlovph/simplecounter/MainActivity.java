package ru.orlovph.simplecounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    final int REP_DELAY = 50;
    TextView numberAdded;
    TextView numberCounted;
    Button plus;
    Button minus;
    Button reset;

    final String keyNumberTotal = "total";

    public int numberTotal = 0;
    public int numberAddedInteger = 0;

    // onLongClickListener для кнопок увеличени и уменьшения:
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private Handler repeatUpdateHandler = new Handler();

    class RptUpdater implements Runnable {
        public void run() {
            if (mAutoIncrement) {
                increase();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            } else if (mAutoDecrement) {
                decrease();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            }
        }
    }

    SharedPreferences sharedPreferences;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("ru.orlovph.simplecounter",
                Context.MODE_PRIVATE);

        reset = findViewById(R.id.buttonRESET);

        plus = findViewById(R.id.buttonPlus);
        minus = findViewById(R.id.buttonMinus);

        numberAdded = findViewById(R.id.numberAdded);
        numberCounted = findViewById(R.id.numberCounted);

        try {
            numberTotal = sharedPreferences.getInt(keyNumberTotal, 0);
            numberCounted.setText(String.valueOf(numberTotal));
            Log.i("Number from Shared pref", String.valueOf(numberTotal));
        } catch (Exception e) {
            e.printStackTrace();
        }


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increase();
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrease();
            }
        });

        plus.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoIncrement = true;
                        repeatUpdateHandler.post(new RptUpdater());
                        return true;
                    }
                }
        );

        plus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement) {
                    mAutoIncrement = false;
                }
                return false;
            }
        });

        minus.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoDecrement = true;
                        repeatUpdateHandler.post(new RptUpdater());
                        return true;
                    }
                }
        );

        minus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement) {
                    mAutoDecrement = false;
                }
                return false;
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                reset();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                if (numberTotal != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
                    builder.setMessage("Are you sure?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener)
                            .show();
                }
            }
        });


    }

    public void increase() {
        display(++numberAddedInteger);
    }

    public void decrease() {
        if (numberAddedInteger != 0) {
            display(--numberAddedInteger);
        }
    }

    public void addNumber(View view) {
        if (numberAddedInteger != 0) {
            numberTotal += numberAddedInteger;
            sharedPreferences.edit().putInt(keyNumberTotal, numberTotal).apply();
            displayTotal();
            numberAddedInteger = 0;
            display(numberAddedInteger);
        }
    }


    public void reset() {
        if (numberTotal != 0) {
            numberTotal = 0;
            sharedPreferences.edit().putInt(keyNumberTotal, numberTotal).apply();
            displayTotal();


        }
    }


    private void display(int number) {
        numberAdded.setText(String.valueOf(number));
    }

    private void displayTotal() {
        int number = sharedPreferences.getInt(keyNumberTotal, 0);
        numberCounted.setText(String.valueOf(number));
    }
}
