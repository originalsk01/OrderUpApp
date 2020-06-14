package com.example.orderup.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.orderup.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragmentStaff extends Fragment {

    private HomeViewModel homeViewModel;
    ArrayList<String> listofrestaurants;
    ArrayList<String> listofcustomers;
    ArrayAdapter<String> dataAdapter;
    ArrayAdapter<String> dataCustAdapter;
    Spinner spCus;
    Spinner sp;
    String urlrestaurants="https://lamp.ms.wits.ac.za/home/s2039033/ProjectLori/getrest.php";
    String urlcust="https://lamp.ms.wits.ac.za/home/s2039033/ProjectLori/CUSTOMERS.php";
    Button OrderUp;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_staff_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        OrderUp = root.findViewById(R.id.MakeOrder);
        listofrestaurants = new ArrayList<>();
        listofcustomers=new ArrayList<>();
        sp = root.findViewById(R.id.spinner);
        spCus = root.findViewById(R.id.cust_spinner);
        populateSpinner();
        populateSpinnerCus();

        OrderUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeOrder();
            }
        });
        return root;
    }

    //Populates the Customer dropdown spinner
    public void populateSpinnerCus(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlcust,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            processJSONCustomers(response);
                        }

                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this.getActivity());
        requestQueue.add(stringRequest);
    }

    //Populates the Restaurant drop down spinner
    public void populateSpinner(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlrestaurants,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            processJSON(response);
                        }

                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this.getActivity());
        requestQueue.add(stringRequest);
    }

    //Function to process the JSON on the Restaurants
    public void processJSON(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);
        for(int i=0;i<ja.length();i++){
            JSONObject jo=ja.getJSONObject(i);
            String name=jo.getString("REST_NAME");
            listofrestaurants.add(name);
        }
        listofrestaurants.add(0, "Restaurants");

        dataAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,listofrestaurants){
            @Override
          public boolean isEnabled(int position) {
                // TODO Auto-generated method stub
                if (position == 0 ){
                    return false;
                }
                return true;
            }
            // Change color item
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                // TODO Auto-generated method stub
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0){
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);
    }

    //Function to process the JSON returned for Customers
    public void processJSONCustomers(String json) throws JSONException{
        JSONArray ja = new JSONArray(json);
        for(int i=0;i<ja.length();i++){
            JSONObject jo=ja.getJSONObject(i);
            String name=jo.getString("USER_USERNAME");
            String id=jo.getString("USER_ID");
            listofcustomers.add(id+" : "+name);
        }
        listofcustomers.add(0, "Customers");
        //Code below disables the first item from selection
        dataCustAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,listofcustomers){
            @Override
            public boolean isEnabled(int position) {
                // TODO Auto-generated method stub
                if (position == 0 ){
                    return false;
                }
                return true;
            }
            // Change color item
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                // TODO Auto-generated method stub
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0){
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };;
        dataCustAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCus.setAdapter(dataCustAdapter);
    }

    //Function to Make an order when button is clicked
    public void MakeOrder(){
        String restaurant = sp.getSelectedItem().toString();
        String customer = spCus.getSelectedItem().toString();
        Bundle info = this.getActivity().getIntent().getExtras(); //Gets the username of the person making the order (need to call the activity the fragment came from
        String StaffUsername = info.getString("username");

        Log.d("Order values", restaurant + " " + customer +  " " + StaffUsername);
    }
}

