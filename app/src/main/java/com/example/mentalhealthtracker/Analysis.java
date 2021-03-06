package com.example.mentalhealthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Analysis extends AppCompatActivity {

    String questions_depression[] = {
            "Little interest or pleasure in doing things",
            "Feeling down, depressed or hopeless",
            "Trouble falling/staying asleep, sleeping too much",
            "Feeling tired or having little energy",
            "Poor appetite or overeating",
            "Feeling bad about yourself or that you are a failure or have let yourself or your family down",
            "Trouble concentrating on things, such as reading the newspaper or watching television.",
            "Moving or speaking so slowly that other people could have noticed. Or the opposite; being so fidgety or restless that you have been moving around a lot more than usual.",
            "Thoughts that you would be better off dead or of hurting yourself in some way."
    };

    String questions_anger[] = {
            "I get angry with little or no provocation.",
            "I have a really bad temper.",
            "It’s hard for me to let go of thoughts that make me angry.",
            "When I become angry, I have urges to beat someone up.",
            "When I become angry, I have urges to break or tear things.",
            "I get impatient when people don’t understand me.",
            "I lose my temper at least once a week.",
            "I embarrass family, friends, or coworkers with my anger outbursts.",
            "I get impatient when people in front of me drive exactly the speed limit.",
            "When my neighbors are inconsiderate, it makes me angry.",
            "I find myself frequently annoyed with certain friends or family.",
            "I get angry when people do things that they are not supposed to, like smoking in a no smoking section or having more items than marked in the supermarket express checkout lane.",
            "There are certain people who always rub me the wrong way.",
            "I feel uptight/tense.",
            "I yell and/or curse.",
            "I get so angry I feel like I am going to explode with rage.",
            "I get easily frustrated when machines/equipment do not work properly.",
            "I remember people and situations that make me angry for a long time.",
            "I can’t tolerate incompetence. It makes me angry..",
            "I think people try to take advantage of me."
    };

    String questions_anxiety[] = {
            "Numbness or tingling",
            "Feeling hot",
            "Wobbliness in legs",
            "Unable to relax",
            "Fear of worst happening",
            "Dizzy or lightheaded",
            "Heart pounding / racing",
            "Unsteady",
            "Terrified or afraid",
            "Nervous",
            "Feeling of choking",
            "Hands trembling",
            "Shaky / unsteady",
            "Fear of losing control",
            "Difficulty in breathing",
            "Fear of dying",
            "Scared",
            "Indigestion",
            "Faint / lightheaded",
            "Face flushed",
            "Hot / cold sweats"
    };

    String questions_sleep[] = {
            "Little interest or pleasure in doing things",
            "Feeling down, depressed or hopeless",
            "Trouble falling/staying asleep, sleeping too much",
            "Feeling tired or having little energy",
            "Poor appetite or overeating",
            "Feeling bad about yourself or that you are a failure or have let yourself or your family down",
            "Trouble concentrating on things, such as reading the newspaper or watching television.",
            "Moving or speaking so slowly that other people could have noticed. Or the opposite; being so fidgety or restless that you have been moving around a lot more than usual.",
            "Thoughts that you would be better off dead or of hurting yourself in some way."
    };

    int flag = 0;
    float result = 0;
    String resultDB;
    int arLn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        Intent i = getIntent();
        String testFor = i.getStringExtra("AnalysisFor");

        RadioButton r1 = findViewById(R.id.opt1);
        RadioButton r2 = findViewById(R.id.opt2);
        RadioButton r3 = findViewById(R.id.opt3);
        RadioButton r4 = findViewById(R.id.opt4);
        TextView ques = findViewById(R.id.ques);
        Button nxtQ = findViewById(R.id.nxtQ);
        RelativeLayout resultDisplay = findViewById(R.id.resultDisplay);
        TextView resD = findViewById(R.id.resD);

        if (testFor.equals("DDepression")) {
            ques.setText(questions_depression[flag]);
            arLn= questions_depression.length;
            flag++;
        }
        else if (testFor.equals("Anger")) {
            ques.setText(questions_anger[flag]);
            arLn= questions_anger.length;
            flag++;
        }
        else if (testFor.equals("Anxiety")) {
            ques.setText(questions_anxiety[flag]);
            arLn= questions_anxiety.length;
            flag++;
        }
        else if (testFor.equals("Sleep")) {
            ques.setText(questions_sleep[flag]);
            arLn= questions_sleep.length;
            flag++;
        }

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    // The flow will come here when
                    // any of the radio buttons in the radioGroup
                    // has been clicked

                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId)
                    {

                        // Get the selected Radio Button
                        RadioButton
                                radioButton
                                = (RadioButton)group
                                .findViewById(checkedId);
                    }
                });

        nxtQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (r1.isChecked()) {
                    result = result+1;
                }
                else if (r2.isChecked()) {
                    result = result+2;
                }
                else if (r3.isChecked()) {
                    result = result+3;
                }
                else if (r4.isChecked()) {
                    result = result+4;
                }

                if (testFor.equals("DDepression")) {
                    ques.setText(questions_depression[flag]);
                    radioGroup.clearCheck();
                    flag++;
                }
                else if (testFor.equals("Anger")) {
                    ques.setText(questions_anger[flag]);
                    radioGroup.clearCheck();
                    flag++;
                }
                else if (testFor.equals("Anxiety")) {
                    ques.setText(questions_anxiety[flag]);
                    radioGroup.clearCheck();
                    flag++;
                }
                else if (testFor.equals("Anger")) {
                    ques.setText(questions_sleep[flag]);
                    radioGroup.clearCheck();
                    flag++;
                }

                if (flag==arLn) {
                    //String  currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
                    String currentDate = df.format(c);

                    Date date = null;
                    try {
                        date = df.parse(currentDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Long TimeMilli = date.getTime();
                    String timeStamp = String.valueOf(TimeMilli);

                    String s = testFor;

                    String test = s + " " + currentDate;

                    if (testFor.equals("DDepression")) {
                        result = (result/36)*100;
                    }
                    else if (testFor.equals("Anger")) {
                        result = (result/80)*100;
                    }
                    else if (testFor.equals("Anxiety")) {
                        result = (result/84)*100;
                    }
                    else if (testFor.equals("Anger")) {
                        result = (result/36)*100;
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("#");

                    resultDB = decimalFormat.format(result);

                    Map<String, Object> resultData = new HashMap<>();
                    resultData.put(s, resultDB);
                    resultData.put("Date", timeStamp);

                    final String TAG = "MyActivity";

                    Task<Void> result_data = db.collection("User").document(currentUser).collection(testFor).document(test)
                            .set(resultData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                    resultDisplay.setVisibility(View.VISIBLE);
                    resD.setText(resultDB);
                }
            }
        });

        Button quitBtn;
        quitBtn = findViewById(R.id.quitBtn);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}