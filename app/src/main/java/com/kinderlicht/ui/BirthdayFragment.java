package com.kinderlicht.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kinderlicht.json.Member;
import com.kinderlicht.sql.Connector;

import java.util.ArrayList;
import java.util.Calendar;


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

    private Connector connector;

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
        System.out.println("onCreate");

        super.onCreate(savedInstanceState);
        connector = new Connector(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        calendar = Calendar.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_birthday, container, false);

        connector = ((StartActivity) getActivity()).getConnector();
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
        System.out.println("onAttach");
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
        System.out.println("onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        System.out.println("onStart");
        super.onStart();

    }

    @Override
    public void onResume() {
        System.out.println("onResume");
        super.onResume();
        //fetchData();
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
    SeekBar seekBar_months;

    TextView tv_Months;

    Calendar calendar;

    private void init(View view) {
        System.out.println("init");
        birthday = (ListView) view.findViewById(R.id.lv_birthday);
        seekBar_months = (SeekBar) view.findViewById(R.id.seekBar_month);
        tv_Months = (TextView) view.findViewById(R.id.tV_month);

        seekBar_months.setMax(12);
        seekBar_months.setMin(1);
        seekBar_months.setProgress(3, true);


        String[] months = getResources().getStringArray(R.array.months);
        int a_month = calendar.get(Calendar.MONTH);

        tv_Months.setText(months[a_month] + " - " + months[(a_month + 3) % 12]);

        System.out.println("Month" + calendar.get(Calendar.MONTH));

        seekBar_months.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fetchData(progress);
                String[] months = getResources().getStringArray(R.array.months);
                int a_month = calendar.get(Calendar.MONTH);

                tv_Months.setText(months[a_month] + " - " + months[(a_month + progress) % 12]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        fetchData(3);
    }

    public void fetchData(int months) {
        System.out.println("fetchData");
        ArrayList<Member> list = connector.getBirthdayList(months);

        String[] names = new String[list.size()];
        String[] birthdays = new String[list.size()];
        int[] ageOnNextBirthday = new int[list.size()];


        for (int i = 0; i < list.size(); i++) {
            names[i] = list.get(i).getName();
            birthdays[i] = list.get(i).getBirthdayString();
            ageOnNextBirthday[i] = list.get(i).getAgeOnNextBirthday();
        }
        BirthdayArrayAdapter adapter = new BirthdayArrayAdapter(getActivity().getApplicationContext(), names, birthdays, ageOnNextBirthday);

        birthday.setAdapter(adapter);

        birthday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), birthday.getAdapter().getItem(position).toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
