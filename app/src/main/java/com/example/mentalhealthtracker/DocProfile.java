package com.example.mentalhealthtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DocProfile extends AppCompatActivity implements PaymentResultListener {

    TextView docName, temp;
    ImageButton chat, video;
    String validity, uName, docId, doc_Name, uNumber;
    LinearLayout contact;
    Button bkApp;
    String proceed;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile);

        ////////////////////////Checking validity
        Intent i = getIntent();
        docId = i.getStringExtra("ID");
        doc_Name = i.getStringExtra("Name");
        validity = i.getStringExtra("Validity");

        docId = docId.replaceAll("\\s", " ");

        docName = findViewById(R.id.DocName);
        docName.setText(docId);

        temp = findViewById(R.id.temp);
        temp.setText(doc_Name);


        //validity = "true";



        ////////////////////////Setting Name
        docName = findViewById(R.id.DocName);

        db.collection("DoctorUser").document(docId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (task.isSuccessful()){
                            doc_Name = documentSnapshot.getString("Name");
                            docName.setText(documentSnapshot.getString("Name"));
                        }
                    }
                });



        db.collection("User").document(currentUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            uName = documentSnapshot.getString("Name");
                            uNumber = documentSnapshot.getString("Number");
                        }
                    }
                });



        ////////////////////Setting Visibility based on validity
        contact = findViewById(R.id.contact);
        bkApp = findViewById(R.id.bkApp);

        if (validity.equals("false"))
        {
            contact.setVisibility(View.GONE);
            bkApp.setVisibility(View.VISIBLE);
        }
        else if (validity.equals("true"))
        {
            contact.setVisibility(View.VISIBLE);
            bkApp.setVisibility(View.GONE);
        }

        //////////////////////////Payment
        bkApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_m8Mx6M6wvVB1qu");
                //checkout.setImage(R.drawable.nev_cart);
                JSONObject object = new JSONObject();
                try {
                    object.put("name", uName);
                    //object.put("description", "Test Payment");
                    object.put("theme.color", "#FF8C00");
                    object.put("currency", "INR");
                    object.put("amount", 1000);
                    object.put("prefill.contact", uNumber);
                    checkout.open(DocProfile.this, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ////////////////////Contacting the Therapist
        chat = findViewById(R.id.chatBtn);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocProfile.this, Chat.class);
                intent.putExtra("SenderId", currentUser);
                intent.putExtra("ReceiverId", docId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        //payment=true;
        String  currentDateTimeString = DateFormat.getDateTimeInstance()
                .format(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 0); // Adding 5 days
        String output = sdf.format(c.getTime());

        Map<String, Object> patientData = new HashMap<>();
        patientData.put("Name(User)", "Peter");
        patientData.put("Name(Doctor)", "Dr. Tony");
        patientData.put("Email", "peter@spiderman.com");
        patientData.put("Time", currentDateTimeString);
        patientData.put("Validity", output);
        patientData.put("PatientId", currentUser);

        Task<Void> appointmentList = db.collection("DoctorUser").document(docId).collection("AppointmentList").document(currentUser)
                .set(patientData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });

        Task<Void> appointment = db.collection("User").document(currentUser).collection("Appointment").document(docId)
                .set(patientData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });

        contact.setVisibility(View.VISIBLE);
        bkApp.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast toast = Toast.makeText(getApplicationContext(), "We are unable to process your request. Try Again", Toast.LENGTH_SHORT);
        toast.show();
    }
}