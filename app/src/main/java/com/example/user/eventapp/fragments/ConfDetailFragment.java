package com.example.user.eventapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.eventapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfDetailFragment extends Fragment {
    private String TAG_TOPIC;
    private String TAG_ABOUT;
    private String TAG_CONFCHAIR;
    private int TAG_CID;
    private int TAG_DAYS;
    private Date TAG_STARTDATE;
    private TextView title;
    private TextView chair;
    private TextView about;
    private TextView startdate;
    private TextView days;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_conf_detail, container, false);
        title = (TextView)view.findViewById(R.id.Name);
        chair = (TextView)view.findViewById(R.id.Mobile);
        about = (TextView)view.findViewById(R.id.Email);
        startdate = (TextView)view.findViewById(R.id.Spec);
        days = (TextView)view.findViewById(R.id.Category);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            TAG_CID = bundle.getInt("Conf_id");
            TAG_DAYS = bundle.getInt("Conf_days");
            TAG_STARTDATE = new Date(bundle.getLong("Conf_date"));
            TAG_TOPIC = bundle.getString("Conf_name");
            TAG_CONFCHAIR = bundle.getString("Conf_chair");
            TAG_ABOUT = bundle.getString("Conf_desc");
        }

//        String num=Integer.toString(TAG_CID);
//        Toast.makeText(getActivity(),num,Toast.LENGTH_SHORT).show();

        title.setText(TAG_TOPIC);
        chair.setText(TAG_CONFCHAIR);
        about.setText(TAG_ABOUT);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(TAG_STARTDATE.getTime());
        startdate.setText(formattedDate);
        days.setText(Integer.toString(TAG_DAYS));
        return view;

    }


}