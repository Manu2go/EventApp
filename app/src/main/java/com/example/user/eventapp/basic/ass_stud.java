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

public class ass_stud extends AppCompatActivity {
    public ProgressDialog dialog;
    public RequestQueue queue;
    public ArrayList<student> students;
    public static ArrayList<duty> duties;
    public RecyclerView recyclerView;
    public adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ass_stud);
        getSupportActionBar().setTitle("Assign Duties");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue= Volley.newRequestQueue(getApplicationContext());
        students=new ArrayList<>();
        duties=new ArrayList<>();
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView=(RecyclerView)findViewById(R.id.stud);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ass_stud.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(ass_stud.this, stud_detail.class);
                        i.putExtra("stud_id", students.get(position).id);
                        i.putExtra("name", students.get(position).name);
                        i.putExtra("email", students.get(position).email);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ass_stud.this.startActivity(i);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(ass_stud.this, stud_detail.class);
                        i.putExtra("stud_id", students.get(position).id);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ass_stud.this.startActivity(i);
                    }
                }));
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
                            JSONArray thread = res.getJSONArray("volunteers");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                int id = obj.getInt("id");
                                //int userId = obj.getInt("user_id");
                                String name = obj.getString("name");
                                String email =obj.getString("email");
                                ;student s=new student(id,name,email);
                                students.add(s);
                            }
                            thread = res.getJSONArray("duties");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                int id = obj.getInt("id");
                                //int userId = obj.getInt("user_id");
                                String name = obj.getString("name");
                                duty s=new duty(id,name);
                                duties.add(s);
                            }
                            adapter=new adapter(ass_stud.this,students);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ass_stud.this));

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
                params.put("type","volunteers_duties");
                SharedPreferences sh=getSharedPreferences("loggedIn info",MODE_PRIVATE);
                params.put("c_id",sh.getString("cid","0"));
                return params;
            }
        };
        queue.add(stringRequest);

    }

    class student{
        int id;
        String name,email;
        student(int id,String name,String email){
            this.id=id;
            this.name=name;
            this.email=email;
        }
    }

    class duty{
        int id;
        String name;
        duty(int id,String name){
            this.id=id;
            this.name=name;
        }
    }

    class holder extends RecyclerView.ViewHolder{

        TextView name,email;
        holder (View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            email= (TextView) v.findViewById(R.id.email);
        }
    }

    class adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<student> s;
        Context context;
        LayoutInflater l;
        ProgressDialog dialog;
        RequestQueue queue;

        adapter(Context c, ArrayList<student> students) {
            this.context = c;
            this.s = students;
            l= LayoutInflater.from(context);
            queue=Volley.newRequestQueue(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = l.inflate(R.layout.single_stud_item, parent,false);
            holder vh = new holder(v);
            return vh;

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
            holder holder=(holder)holder1;
            holder.name.setText(s.get(position).name);
            holder.email.setText(s.get(position).email);
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
