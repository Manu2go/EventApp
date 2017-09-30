package com.example.user.eventapp.basic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.style.RasterizerSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.example.user.eventapp.Utilties.backGroundWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity  implements CompoundButton.OnCheckedChangeListener{

    EditText name;
    EditText password;
    RadioButton user,organiser,volunteer;
    ProgressDialog dialog;
    Button loginButton;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        queue= Volley.newRequestQueue(getApplicationContext());
        user=(RadioButton)findViewById(R.id.urad);
        organiser=(RadioButton)findViewById(R.id.orad);
        volunteer=(RadioButton)findViewById(R.id.vrad);
        user.setOnCheckedChangeListener(this);
        organiser.setOnCheckedChangeListener(this);
        volunteer.setOnCheckedChangeListener(this);
        SharedPreferences sh=getSharedPreferences("loggedIn info",MODE_PRIVATE);
        if(!sh.getString("uid","0").equals("0")){
            String t=sh.getString("category",null);
            Intent i=null;
            if(t.equals("user")){
                i=new Intent(this,UserActivity.class);
            }
            else if(t.equals("organiser")){
                i=new Intent(this,Main2Activity.class);
            }
            else{
                i=new Intent(this,Main3Activity.class);
            }
            startActivity(i);
            finish();
        }

            name = (EditText) findViewById(R.id.uname);
            password = (EditText) findViewById(R.id.password);
            loginButton = (Button) findViewById(R.id.buttonLogin);
            loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(volunteer.isChecked()&& organiser.isChecked() || organiser.isChecked()&&user.isChecked() || volunteer.isChecked()&& user.isChecked()){
                        Toast.makeText(LoginActivity.this,"Choose either Organiser or User or Volunteer! Cannot choose more than one.. ",Toast.LENGTH_LONG).show();
                    }
                    else if(volunteer.isChecked()!=true && organiser.isChecked()!=true && user.isChecked()!=true){
                        Toast.makeText(LoginActivity.this,"Choose either Organiser or User or Volunteer!!",Toast.LENGTH_LONG).show();
                    }
                    else {
                        String type;
                        if(volunteer.isChecked()){
                            type="volunteer";
                        }
                        else if(user.isChecked()){
                            type="user";
                        }
                        else{
                            type="organiser";
                        }
                        String uname = name.getText().toString();
                        String upassword = password.getText().toString();
                        login(uname, upassword, type);
                    }
                }
            });


    }

    public void login(final String uname,final String upassword,final String type){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait !!");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://eventapp.000webhostapp.com/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.i("response",response);
                        try {
                            JSONObject res = new JSONObject(response);
                            SharedPreferences share=getSharedPreferences("loggedIn info",MODE_PRIVATE);
                            SharedPreferences.Editor ed=share.edit();
                            ed.putString("category",type);
                            ed.putString("uid",res.getString("id"));
                            ed.putString("name",res.getString("name"));
                            ed.putString("mobile",res.getString("mobile_no"));
                            ed.putString("email",res.getString("email"));
                            if(type!="user"){
                                ed.putString("cid",res.getString("cid"));
                            }
                            ed.commit();
                            Intent intent=null;
                            if(type.equals("user")){
                                intent = new Intent(LoginActivity.this, UserActivity.class);
                            }
                            else if(type.equals("organiser")){
                                intent = new Intent(LoginActivity.this, Main2Activity.class);
                            }
                            else{
                                intent = new Intent(LoginActivity.this, Main3Activity.class);
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Invalide username and password",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Invalide username and password",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_name",uname);
                params.put("type",type);
                params.put("password",upassword);
                return params;
            }
        };
        queue.add(stringRequest);

    }

    public void signup(View v){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView== organiser) {
                volunteer.setChecked(false);
                user.setChecked(false);
            }
            if (buttonView == user) {
                volunteer.setChecked(false);
                organiser.setChecked(false);
            }
            if (buttonView == volunteer) {
                organiser.setChecked(false);
                user.setChecked(false);
            }
        }
    }
}
