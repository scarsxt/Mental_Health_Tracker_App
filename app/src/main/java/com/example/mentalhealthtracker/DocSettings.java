package com.example.mentalhealthtracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

public class DocSettings extends AppCompatActivity {
    TextView ah, ca, logout, ph, pa, name, abtus, deleteAcc;
    CardView profile;
    String n;
    ImageView profile_image, SettingsBackBtn;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_settings);

        ph = findViewById(R.id.ph);
        //ah = findViewById(R.id.appointmentHistory);
        profile = findViewById(R.id.profile);
        ca = findViewById(R.id.mailUs);
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);

        abtus = findViewById(R.id.au);
        abtus.setMovementMethod(LinkMovementMethod.getInstance());

        SettingsBackBtn = findViewById(R.id.SettingsBackBtn);
        SettingsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pa = findViewById(R.id.payAdmin);


        ph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocSettings.this, DPaymentHistory.class));
            }
        });

        db.collection("DoctorUser").document(currentUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String n = documentSnapshot.getString("Name");
                            no = documentSnapshot.getString("Number");
                            name.setText(n);
                            String url = documentSnapshot.getString("Profileimage");
                            Glide.with(DocSettings.this)
                                    .load(url)
                                    .placeholder(R.drawable.profile)
                                    .into(profile_image);
                        }
                    }
                });

        pa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_m8Mx6M6wvVB1qu");
                //checkout.setImage(R.drawable.nev_cart);
                JSONObject object = new JSONObject();
                try {
                    object.put("name", String.valueOf(name));
                    //object.put("description", "Test Payment");
                    object.put("theme.color", "#FF8C00");
                    object.put("currency", "INR");
                    object.put("amount", 500000);
                    object.put("prefill.contact", no);
                    checkout.open(DocSettings.this, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("DoctorUser").document(currentUser)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    String no = documentSnapshot.getString("Number");

                                    Intent i =  new Intent(DocSettings.this, DDoctorDetails.class);
                                    i.putExtra("EditMode", "false");
                                    i.putExtra("Number", no);
                                    i.putExtra("frm", "settings");
                                    startActivity(i);
                                }
                            }
                        });
            }
        });

        ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "example@example.com"));
                startActivity(intent);
            }
        });

        deleteAcc = findViewById(R.id.deleteAcc);
        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "example@example.com"));
                startActivity(intent);
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DocSettings.this, SplashScreen.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}