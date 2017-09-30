package com.example.user.eventapp.basic;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.eventapp.R;
import com.example.user.eventapp.Utilties.RecyclerItemClickListener;
import com.example.user.eventapp.Utilties.SpacesItemDecoration;
import com.example.user.eventapp.Utilties.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class vol_dut extends AppCompatActivity {
    public ProgressDialog dialog;
    public RequestQueue queue;
    public static ArrayList<duty> duties;
    public RecyclerView recyclerView;
    public adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_dut);
        getSupportActionBar().setTitle("Assigned Duties");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue= Volley.newRequestQueue(getApplicationContext());
        duties=new ArrayList<>();
        duties=new ArrayList<>();
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView=(RecyclerView)findViewById(R.id.stud);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        fetch_data();
    }

    public void fetch_data(){
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
                            JSONArray thread = res.getJSONArray("v_duties");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                int id = obj.getInt("id");
                                //int userId = obj.getInt("user_id");
                                String dname = obj.getString("dname");
                                String oname = obj.getString("oname");
                                String mobile = obj.getString("mobile");
                                String email =obj.getString("email");
                                duty s=new duty(id,dname,oname,mobile,email);
                                duties.add(s);
                            }
                            adapter=new adapter(vol_dut.this,duties);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(vol_dut.this));

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
                params.put("type","v_duties");
                SharedPreferences sh=getSharedPreferences("loggedIn info",MODE_PRIVATE);
                params.put("uid",sh.getString("uid","0"));
                return params;
            }
        };
        queue.add(stringRequest);

    }



    class duty{
        int id;
        String dname;
        String oname;
        String mobile;
        String email;
        duty(int id,String dname,String oname,String mobile,String email){
            this.id=id;
            this.dname=dname;
            this.oname=oname;
            this.mobile=mobile;
            this.email=email;
        }
    }

    class holder extends RecyclerView.ViewHolder{

        TextView oname,email,mobile,duty;
        holder (View v) {
            super(v);
            oname = (TextView) v.findViewById(R.id.ass_by);
            email= (TextView) v.findViewById(R.id.email);
            duty= (TextView) v.findViewById(R.id.duty);
            mobile= (TextView) v.findViewById(R.id.mobile);

        }
    }

    class adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<duty> s;
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

            View v = l.inflate(R.layout.single_vol_did_item, parent,false);
            holder vh = new holder(v);
            return vh;

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
            holder holder=(holder)holder1;
            holder.oname.setText(s.get(position).oname);
            holder.email.setText(s.get(position).email);
            holder.mobile.setText(s.get(position).mobile);
            holder.duty.setText(s.get(position).dname);
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
