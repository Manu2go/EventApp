package com.example.user.eventapp.basic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class stud_detail extends Activity implements View.OnClickListener {
    String name,email;
    int stud_id;
    RecyclerView dut_ass,dut_nt_ass;
    ProgressDialog dialog;
    RequestQueue queue;
    ArrayList<duty> ass_duties;
    ArrayList<duty> nt_assigned_duties;
    Button assign;
    TextView nm,em;
    public adapter adapter1,adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Student Profile");
        setContentView(R.layout.activity_stud_detail);
        Intent i=getIntent();
        nm=(TextView)findViewById(R.id.name);
        em=(TextView)findViewById(R.id.email);
        name=i.getStringExtra("name");
        email=i.getStringExtra("email");
        stud_id=i.getIntExtra("stud_id",0);
        nm.setText(name);
        em.setText(email);
        Log.i("stud_id",stud_id+"");
        dut_ass=(RecyclerView)findViewById(R.id.dut_ass);
        dut_nt_ass=(RecyclerView)findViewById(R.id.dut_nt_ass);
        queue= Volley.newRequestQueue(getApplicationContext());
        ass_duties=new ArrayList<>();
        nt_assigned_duties=new ArrayList<>();
        assign=(Button)findViewById(R.id.assign_duties);
        assign.setOnClickListener(this);
        fetch_duties();

    }

    @Override
    public void onClick(View view) {
        if(view==assign){
            assign_duties();
        }
    }

    public void showtoast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    public void assign_duties(){
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
                            showtoast("duties successfully assigned !!");
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showtoast("Please try again! duties not assigned!!");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showtoast("Please try again! duties not assigned!!");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                int k=0;
                params.put("type","ass_duty");
                params.put("vid",stud_id+"");

                params.put("oid",1+"");
                for(int i=0;i<nt_assigned_duties.size();i++){
                    if(nt_assigned_duties.get(i).status==2){
                        params.put("d"+k,nt_assigned_duties.get(i).id+"");
                        k++;
                    }
                }
                params.put("size",(k-1)+"");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void fetch_duties(){
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
                            JSONArray thread = res.getJSONArray("duties");
                            for (int ii = 0; ii < thread.length(); ii++) {
                                JSONObject obj = thread.getJSONObject(ii);
                                int id = obj.getInt("id");
                                String name=obj.getString("name");
                                duty d=new duty(id,name);
                                d.status=1;
                                ass_duties.add(d);
                            }
                            for(int i=0;i<ass_stud.duties.size();i++){
                                int c=0;
                                for(int j=0;j<ass_duties.size();j++){
                                    if(ass_stud.duties.get(i).id==ass_duties.get(j).id){
                                        c++;
                                    }
                                }
                                if(c==0){
                                    duty d=new duty(ass_stud.duties.get(i).id,ass_stud.duties.get(i).name);
                                    d.status=0;
                                    nt_assigned_duties.add(d);
                                }
                            }
                            if(nt_assigned_duties.size()==0){
                                assign.setVisibility(View.INVISIBLE);
                            }
                            adapter1=new adapter(stud_detail.this,ass_duties);
                            dut_ass.setAdapter(adapter1);
                            dut_ass.setLayoutManager(new LinearLayoutManager(stud_detail.this));
                            adapter2=new adapter(stud_detail.this,nt_assigned_duties);
                            dut_nt_ass.setAdapter(adapter2);
                            dut_nt_ass.setLayoutManager(new LinearLayoutManager(stud_detail.this));
                            dialog.dismiss();

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
                params.put("type","stud_duty");
                params.put("stud_id",stud_id+"");
                return params;
            }
        };
        queue.add(stringRequest);
    }


    class duty{
        int id,status;
        String name;
        duty(int id, String name){
            this.id=id;
            this.name=name;
        }
    }

    class holder extends RecyclerView.ViewHolder{

        RadioButton r;
        holder (View v) {
            super(v);
            r = (RadioButton) v.findViewById(R.id.duty_nt_ass_name);
        }
    }

    class adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList <duty> s;
        Context context;
        LayoutInflater l;
        ProgressDialog dialog;
        RequestQueue queue;

        adapter(Context c, ArrayList<duty> students) {
            this.context = c;
            this.s = students;
            l= LayoutInflater.from(context);
            queue=Volley.newRequestQueue(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = l.inflate(R.layout.single_duty_nt_ass_item, parent,false);
            holder vh = new holder(v);
            return vh;

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
            holder holder=(holder)holder1;
            holder.r.setText(s.get(position).name);
            if(s.get(position).status==1){
                // holder.r.setEnabled(false);
                holder.r.setChecked(true);
            }
            else {
                /*if(s.get(position).status==0){
                    holder.r.setChecked(false);
                }
                else{
                    holder.r.setChecked(true);
                }*/
                holder.r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RadioButton r=(RadioButton)view;
                        if(s.get(position).status==2){
                            r.setChecked(false);
                            s.get(position).status=0;
                        }
                        else if(s.get(position).status==0){
                            r.setChecked(true);
                            s.get(position).status=2;
                        }
                    }
                });
            }
            //holder.email.setText(s.get(position).email);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return s.size();
        }

    }
}
