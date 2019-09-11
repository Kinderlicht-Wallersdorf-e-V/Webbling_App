package com.kinderlicht.ui;

import android.content.Context;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kinderlicht.json.Member;
import com.kinderlicht.json.Parser;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BirthdayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BirthdayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BirthdayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BirthdayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BirthdayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BirthdayFragment newInstance(String param1, String param2) {
        BirthdayFragment fragment = new BirthdayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_birthday, container, false);
        init(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    ListView birthday;
    private void init(View view){

        birthday = (ListView) view.findViewById(R.id.lv_birthday);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://kinderlichtwdorf.webling.eu/api/1/member?format=full&apikey=eaab12f49595f7d8ca8a938cf0d082ec";



        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = response.toString();

                System.out.println(output);
                ArrayList<Member> list = Parser.createMembers(output);

                System.out.println(list.size());
                Toast.makeText(getActivity().getApplicationContext(), list.get(0).getName() , Toast.LENGTH_LONG).show();
                String[] names = new String[list.size()];
                String[] birthdays = new String[list.size()];
                int[] ageOnNextBirthday = new int[list.size()];


                for (int i = 0; i < list.size(); i++){
                    names[i] = list.get(i).getName();
                    birthdays[i] = list.get(i).getBirthdayString();
                    ageOnNextBirthday[i] = list.get(i).getAgeOnNextBirthday();
                }
                finilize_View(names,birthdays, ageOnNextBirthday);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Hasn't worked", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);





    }

    private void finilize_View(String[] names, String[] birthdays, int[] ages){
        BirthdayArrayAdapter adapter = new BirthdayArrayAdapter(getActivity().getApplicationContext(), names, birthdays, ages);

        birthday.setAdapter(adapter);

        birthday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), birthday.getAdapter().getItem(position).toString(), Toast.LENGTH_LONG);
            }
        });
    }
}
