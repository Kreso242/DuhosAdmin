package com.example.duhosadmin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class KalendarFragment extends Fragment {
    ImageView actionBarImage;
    ImageButton idiNatrag,osvjeziButton,objaviButton;
    private View connectionFragmentView,kalendarFragmentView;
    private boolean connectionFlag=false;
    EditText editTextNaziv;
    EditText editTextDatum;
    EditText editTextVrijeme;
    EditText editTextOpis;
    EditText editTextLokacija;

    DatabaseReference databaseReference;
    private String idNumberString;
    private int idNumberInt;
    Calendar myCalendar=null;

    private List<String> listaNaslova=new ArrayList<>();
    private List<String> listaDatuma=new ArrayList<>();
    private List<String> listaVremena=new ArrayList<>();

    private List<Integer> idLista=new ArrayList<>();
    private int idPostojecegDogadjaja=0;
    private boolean vecPostojiDogadjajFlag=false;
    private int brojNaslova;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBar mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.toolbar);
        mActionBar.setBackgroundDrawable(this.getResources().getDrawable(R.color.background));
        View view=mActionBar.getCustomView();
        actionBarImage=view.findViewById(R.id.prijavaImage);
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_kalendarnaslov));
        idiNatrag=view.findViewById(R.id.idiNatrag);
        idiNatrag.setVisibility(View.VISIBLE);
        idiNatrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        checkInternetConnection();

        if(connectionFlag==true) {
            kalendarFragmentView = inflater.inflate(R.layout.fragment_kalendar, container, false);

            databaseReference= FirebaseDatabase.getInstance().getReference("Kalendar");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if (snapshot.exists()) {
                            idNumberString = snapshot.getKey();
                            idNumberInt = Integer.parseInt(idNumberString);
                            idNumberInt++;
                            if (snapshot.child("Naslov").getValue() == null) {
                            } else {
                                final String naslov = snapshot.child("Naslov").getValue().toString().toLowerCase();
                                final String datum = snapshot.child("Datum").getValue().toString().toLowerCase();
                                final String vrijeme = snapshot.child("Vrijeme").getValue().toString().toLowerCase();

                                listaNaslova.add(naslov);
                                listaDatuma.add(datum);
                                listaVremena.add(vrijeme);
                                idLista.add(idNumberInt - 1);
                            }
                        }
                    }
                    brojNaslova=listaNaslova.size();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
                }
            });
            editTextNaziv = kalendarFragmentView.findViewById(R.id.editTextNaziv);
            editTextDatum = kalendarFragmentView.findViewById(R.id.editTextDatum);
            editTextVrijeme = kalendarFragmentView.findViewById(R.id.editTextVrijeme);
            editTextOpis = kalendarFragmentView.findViewById(R.id.editTextOpis);
            editTextLokacija = kalendarFragmentView.findViewById(R.id.editTextLokacija);

            myCalendar = Calendar.getInstance();
             final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

            };

            editTextDatum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            Calendar mcurrentTime = Calendar.getInstance();
            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);
            editTextVrijeme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String min=String.valueOf(selectedMinute);
                            String h=String.valueOf(selectedHour);

                            if(selectedHour<10)
                                h="0"+h;
                            if(selectedMinute<10)
                                min="0"+min;

                            editTextVrijeme.setText(h+":"+min);

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Odaberi vrijeme");
                    mTimePicker.show();
                }
            });
            objaviButton = kalendarFragmentView.findViewById(R.id.objaviButton);
            onTextChange();

            objaviButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String naziv=editTextNaziv.getText().toString().toLowerCase();
                    String datum=editTextDatum.getText().toString().toLowerCase();
                    String vrijeme=editTextVrijeme.getText().toString().toLowerCase();

                    idPostojecegDogadjaja=0;
                    for(int i=0; i<brojNaslova;i++){
                        if(listaNaslova.get(i).equals(naziv) && listaDatuma.get(i).equals(datum) && listaVremena.get(i).equals(vrijeme)){
                            vecPostojiDogadjajFlag=true;
                            idPostojecegDogadjaja=idLista.get(i);
                        }
                    }


                    if (editTextNaziv.length() == 0 || editTextDatum.length() == 0 || editTextVrijeme.length() == 0 || editTextOpis.length() == 0 || editTextLokacija.length() == 0) {
                        Toast.makeText(getContext(), "Unesi podatke u sva ponuđena polja!", Toast.LENGTH_SHORT).show();
                    }
                    else if(vecPostojiDogadjajFlag){
                        new AlertDialog.Builder(getContext())
                                .setTitle("Upozorenje")
                                .setMessage("Unešeni događaj već postoji u bazi! Ukoliko želite izbrisati taj događaj te dodati navedeni odaberite \"Uredu\", ukoliko to ne želite odaberite \"Natrag\"!")
                                .setPositiveButton("Uredu", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        databaseReference.child(String.valueOf(idPostojecegDogadjaja)).removeValue();
                                        databaseReference.child(String.valueOf(idNumberInt)).setValue(new Dogadjaj(editTextNaziv.getText().toString(),editTextOpis.getText().toString(),editTextDatum.getText().toString(),editTextVrijeme.getText().toString(),editTextLokacija.getText().toString()));
                                        vecPostojiDogadjajFlag=false;
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                                    }
                                })
                                .setNegativeButton("Natrag",null)
                                .setIcon(R.drawable.duhos_logo)
                                .show();
                    }
                    else {
                        databaseReference.child(String.valueOf(idNumberInt)).setValue(new Dogadjaj(editTextNaziv.getText().toString(),editTextOpis.getText().toString(),editTextDatum.getText().toString(),editTextVrijeme.getText().toString(),editTextLokacija.getText().toString()));
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                    }
                }
            });
            return kalendarFragmentView;
        }
        else {
            connectionFragmentView = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);
            osvjeziButton=connectionFragmentView.findViewById(R.id.osvjeziButton);
            osvjeziButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new KalendarFragment()).addToBackStack("kalendarFragment").commit();
                }
            });
            return connectionFragmentView;
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        if(!myCalendar.getTime().before(calendar.getTime()))
            editTextDatum.setText(simpleDateFormat.format(myCalendar.getTime()));
        else
            Toast.makeText(getContext(),"Ne možeš upisati datum iz prošlosti!",Toast.LENGTH_SHORT).show();
    }
    private void onTextChange() {
        editTextNaziv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    editTextNaziv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextNaziv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }
        });
        editTextDatum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    editTextDatum.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextDatum.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }
        });
        editTextOpis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextOpis.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextOpis.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextLokacija.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextLokacija.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextLokacija.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager=(ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
        if(null!=activeNetwork){
            if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI){
                connectionFlag=true;
            }
            else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE){
                connectionFlag=true;
            }

        }
        else
        {
            connectionFlag=false;
        }
    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

}









