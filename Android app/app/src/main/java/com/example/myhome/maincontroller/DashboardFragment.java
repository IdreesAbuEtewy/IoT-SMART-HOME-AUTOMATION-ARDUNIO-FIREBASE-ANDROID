package com.example.myhome.maincontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class DashboardFragment extends Fragment {
    private SwitchCompat switch1, switch2, switch3, switch4, switch5, switch6;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    private MediaPlayer mp;
    private Animation animation, anim;
    private Vibrator v;
    private long[] pattern;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    private DatabaseReference database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        database = FirebaseDatabase.getInstance().getReference("users/"+currentFirebaseUser.getUid());
        mp = MediaPlayer.create(getActivity(), R.raw.buttonsound);

        switch1 = myFragmentView.findViewById(R.id.switch1);
        switch2 = myFragmentView.findViewById(R.id.switch2);
        switch3 = myFragmentView.findViewById(R.id.switch3);
        switch4 = myFragmentView.findViewById(R.id.switch4);
        switch5 = myFragmentView.findViewById(R.id.switch5);
        switch6 = myFragmentView.findViewById(R.id.switch6);

        imageView1 = myFragmentView.findViewById(R.id.imageView1);
        imageView2 = myFragmentView.findViewById(R.id.imageView2);
        imageView3 = myFragmentView.findViewById(R.id.imageView3);
        imageView4 = myFragmentView.findViewById(R.id.imageView4);
        imageView5 = myFragmentView.findViewById(R.id.imageView5);
        imageView6 = myFragmentView.findViewById(R.id.imageView6);

        animation = new AlphaAnimation(1, 0);
        animation.setDuration(750);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        pattern = new long[]{0, 100, 1000};

        database.child("components").addValueEventListener(new ValueEventListener() {
//            @SuppressLint("UseCompatLoadingForDrawables")
//            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    String p1 = (String) map.get("port1");
                    String p2 = (String) map.get("port2");
                    String p3 = (String) map.get("port3");
                    String p4 = (String) map.get("port4");
                    String p5 = (String) map.get("port5");
                    String p6 = (String) map.get("port6");


                    if (p1.equals("1")) {
                        imageView1.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                        switch1.setChecked(true);
                    } else {
                        imageView1.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                        switch1.setChecked(false);
                    }

                    if (p2.equals("1")) {
                        imageView2.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                        switch2.setChecked(true);
                    } else {
                        imageView2.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                        switch2.setChecked(false);
                    }

                    if (p3.equals("1")) {
                        imageView3.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                        switch3.setChecked(true);
                    } else {
                        imageView3.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                        switch3.setChecked(false);
                    }

                    if (p4.equals("1")) {
                        imageView4.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                        switch4.setChecked(true);
                    } else {
                        imageView4.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                        switch4.setChecked(false);
                    }

                    if (p5.equals("1")) {
                        imageView5.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                        switch5.setChecked(true);
                    } else {
                        imageView5.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                        switch5.setChecked(false);
                    }

                    if (p6.equals("1")) {
                        imageView6.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                        switch6.setChecked(true);
                    } else {
                        imageView6.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                        switch6.setChecked(false);
                    }

                    switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (switch1.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port1").setValue("1");
                                imageView1.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port1").setValue("0");
                                imageView1.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));

                            }
                        }
                    });

                    switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (switch2.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port2").setValue("1");
                                imageView2.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port2").setValue("0");
                                imageView2.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                            }
                        }
                    });

                    switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch3.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port3").setValue("1");
                                imageView3.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port3").setValue("0");
                                imageView3.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                            }
                        }
                    });

                    switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch4.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port4").setValue("1");
                                imageView4.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port4").setValue("0");
                                imageView4.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                            }
                        }
                    });

                    switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch5.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port5").setValue("1");
                                imageView5.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port5").setValue("0");
                                imageView5.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                            }
                        }
                    });

                    switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch6.isChecked()) {
                                mp.start();
                                dataSnapshot.getRef().child("port6").setValue("1");
                                imageView6.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_on));
                            } else {
                                mp.start();
                                dataSnapshot.getRef().child("port6").setValue("0");
                                imageView6.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.light_off));
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        return myFragmentView;
    }
}
