package com.example.user.eventapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.eventapp.R;

public class RegListFragment extends Fragment {
    private int TAG_CID;
    private String uid;
    private String name;
    private String mobile;
    private String email;
    private EditText namefield;
    private EditText mobilefield;
    private EditText emailfield;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reg_list, container, false);
        namefield=(EditText)view.findViewById(R.id.name);
        mobilefield=(EditText)view.findViewById(R.id.mobile);
        emailfield=(EditText)view.findViewById(R.id.email);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            TAG_CID = bundle.getInt("Conf_id");
        }
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UID_Details", Context.MODE_PRIVATE);
        name=sharedPreferences.getString("Name","no name defined");
        mobile=sharedPreferences.getString("Mobile_No","0000000000");
        email=sharedPreferences.getString("Email","xxxxxx@YYYYYY");
        namefield.setText(name);
        mobilefield.setText(mobile);
        emailfield.setText(email);
        namefield.setEnabled(false);
        mobilefield.setEnabled(false);
        emailfield.setEnabled(false);
        uid=this.getActivity().getSharedPreferences("loggedIn info", Context.MODE_PRIVATE).getString("uid","");
        return view;

    }


}
