package com.example.user.eventapp.basic;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.eventapp.R;
import com.example.user.eventapp.Utilties.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ins_study extends AppCompatActivity implements View.OnClickListener{

    EditText name,mobile,email;
    Button submit;
    ProgressDialog dialog;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_study);
        getSupportActionBar().setTitle("Add Volunteer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue= Volley.newRequestQueue(getApplicationContext());
        name=(EditText)findViewById(R.id.name1);
        email=(EditText)findViewById(R.id.email1);
        mobile=(EditText)findViewById(R.id.mobile1);
        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    public boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        return check;
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onClick(View view) {
        if(view==submit){
            if(name.getText().toString().equals("") || email.getText().toString().equals("") || mobile.getText().toString().equals("")){
                Toast.makeText(this,"None of the fields should be left empty..",Toast.LENGTH_LONG ).show();
            }
            else{
                boolean status=isValidMail(email.getText().toString().trim());
                if(status==false){
                    Toast.makeText(this,"Invalid email id..",Toast.LENGTH_LONG ).show();
                }
                else{
                    status=isValidMobile(mobile.getText().toString());
                    if(status==false){
                        Toast.makeText(this,"Invalid mobile-no ..",Toast.LENGTH_LONG ).show();
                    }
                    else{
                        submit();
                    }
                }
            }
        }
    }

    public void submit(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.baseurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.i("response",response);
                        try {
                            JSONObject res = new JSONObject(response);
                            String obj = res.getString("status");
                            if(obj.equals("true")){
                                Toast.makeText(ins_study.this,"Volunteer added successfully",Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else{
                                Toast.makeText(ins_study.this,"Try again!! entered mail-id is already registered ",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type","add_volunteer");
                SharedPreferences sh=getSharedPreferences("loggedIn info",MODE_PRIVATE);
                params.put("cid",sh.getString("cid","0"));
                params.put("name",name.getText().toString());
                params.put("email",email.getText().toString());
                params.put("mobile",mobile.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);

    }

}
