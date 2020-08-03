package com.example.duhosadmin;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MolitvaFragment extends Fragment {
    ImageView actionBarImage;
    ImageButton idiNatrag,osvjeziButton,objaviButton;
    private View connectionFragmentView,molitvaFragmentView;
    private boolean connectionFlag=false;
    ImageView opceMolitvaIcon,marijanskaMolitvaIcon,nadahnucaIcon,poboznostiIcon;
    private boolean opceMolitvaFlag=false;
    private boolean marijanskaMolitvaFlag=false;
    private boolean nadahnucaFlag=false;
    private boolean svjedocanstvaFlag=false;
    EditText editTextNazivMolitve;
    EditText editTextTekstMolitve;
    DatabaseReference marijanskeReference,opceReference,nadahnucaReference,svjedocanstvaReference;
    private int idNumberOpceInt,idNumberMarijanskeInt,idNumberSvjedocanstvaInt,idNumberNadahnucaInt;
    private String idNumberOpceString,idNumberMarijanskeString,idNumberPoboznostiString,idNumberNadahnucaString;
    String date;

    private List<String> listaNazivaOpce=new ArrayList<>();
    private List<Integer> idListaOpce=new ArrayList<>();
    private int idPostojeceMolitveOpce=0;
    private boolean vecPostojiMolitvaOpceFlag=false;
    private int brojNazivaOpce;

    private List<String> listaNazivaMarijanske=new ArrayList<>();
    private List<Integer> idListaMarijanske=new ArrayList<>();
    private int idPostojeceMolitveMarijanske=0;
    private boolean vecPostojiMolitvaMarijanskeFlag=false;
    private int brojNazivaMarijanske;

    private List<String> listaNazivaNadahnuca=new ArrayList<>();
    private List<Integer> idListaNadahnuca=new ArrayList<>();
    private int idPostojeceMolitveNadahnuca=0;
    private boolean vecPostojiMolitvaNadahnucaFlag=false;
    private int brojNazivaNadahnuca;

    private List<String> listaNazivaSvjedocanstva=new ArrayList<>();
    private List<Integer> idListaSvjedocanstva=new ArrayList<>();
    private int idPostojeceMolitveSvjedocanstva=0;
    private boolean vecPostojiMolitvaSvjedocanstvaFlag=false;
    private int brojNazivaSvjedocanstva;

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
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_molitvanaslov));
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
            molitvaFragmentView = inflater.inflate(R.layout.fragment_molitva, container, false);
            setUpIcons();
            getReference();
            editTextNazivMolitve = molitvaFragmentView.findViewById(R.id.editTextNazivMolitve);
            editTextTekstMolitve = molitvaFragmentView.findViewById(R.id.editTextTekstMolitve);

            date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


            objaviButton = molitvaFragmentView.findViewById(R.id.objaviButton);
            onTextChange();
            objaviButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String naziv=editTextNazivMolitve.getText().toString().toLowerCase();
                    idPostojeceMolitveMarijanske=0;
                    idPostojeceMolitveNadahnuca=0;
                    idPostojeceMolitveOpce=0;
                    idPostojeceMolitveSvjedocanstva=0;

                    for(int i=0; i<brojNazivaMarijanske;i++){
                        if(listaNazivaMarijanske.get(i).equals(naziv)){
                            vecPostojiMolitvaMarijanskeFlag=true;
                            idPostojeceMolitveMarijanske=idListaMarijanske.get(i);
                        }
                    }

                    for(int i=0; i<brojNazivaNadahnuca;i++){
                        if(listaNazivaNadahnuca.get(i).equals(naziv)){
                            vecPostojiMolitvaNadahnucaFlag=true;
                            idPostojeceMolitveNadahnuca=idListaNadahnuca.get(i);
                        }
                    }

                    for(int i=0; i<brojNazivaOpce;i++){
                        if(listaNazivaOpce.get(i).equals(naziv)){
                            vecPostojiMolitvaOpceFlag=true;
                            idPostojeceMolitveOpce=idListaOpce.get(i);
                        }
                    }

                    for(int i=0; i<brojNazivaSvjedocanstva;i++){
                        if(listaNazivaSvjedocanstva.get(i).equals(naziv)){
                            vecPostojiMolitvaSvjedocanstvaFlag=true;
                            idPostojeceMolitveSvjedocanstva=idListaSvjedocanstva.get(i);
                        }
                    }


                    if (editTextNazivMolitve.length() == 0 || editTextTekstMolitve.length() == 0) {
                        Toast.makeText(getContext(), "Unesi podatke u sva ponuđena polja!", Toast.LENGTH_SHORT).show();
                    }
                    else if(!opceMolitvaFlag && !marijanskaMolitvaFlag && !nadahnucaFlag && !svjedocanstvaFlag)
                        Toast.makeText(getContext(), "Odaberi kategoriju molitve!", Toast.LENGTH_SHORT).show();
                    else {
                        if(opceMolitvaFlag) {
                            if(vecPostojiMolitvaOpceFlag){
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Upozorenje")
                                        .setMessage("Unešena molitva već postoji u bazi! Ukoliko želite izbrisati ovu molitvu te dodati navedenu odaberite \"Uredu\", ukoliko to ne želite odaberite \"Natrag\"!")
                                        .setPositiveButton("Uredu", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                opceReference.child(String.valueOf(idPostojeceMolitveOpce)).removeValue();
                                                opceReference.child(String.valueOf(idNumberOpceInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(),date.toString(),editTextTekstMolitve.getText().toString()));
                                                vecPostojiMolitvaOpceFlag=false;
                                                vecPostojiMolitvaMarijanskeFlag=false;
                                                vecPostojiMolitvaNadahnucaFlag=false;
                                                vecPostojiMolitvaSvjedocanstvaFlag=false;
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                                            }
                                        })
                                        .setNegativeButton("Natrag",null)
                                        .setIcon(R.drawable.duhos_logo)
                                        .show();
                            }
                            else{
                                opceReference.child(String.valueOf(idNumberOpceInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(),date.toString(),editTextTekstMolitve.getText().toString()));
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                            }
                        }
                        else if(marijanskaMolitvaFlag) {
                            if(vecPostojiMolitvaMarijanskeFlag){
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Upozorenje")
                                        .setMessage("Unešena molitva već postoji u bazi! Ukoliko želite izbrisati ovu molitvu te dodati navedenu odaberite \"Uredu\", ukoliko to ne želite odaberite \"Natrag\"!")
                                        .setPositiveButton("Uredu", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                marijanskeReference.child(String.valueOf(idPostojeceMolitveMarijanske)).removeValue();
                                                marijanskeReference.child(String.valueOf(idNumberMarijanskeInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(),date.toString(),editTextTekstMolitve.getText().toString()));
                                                vecPostojiMolitvaOpceFlag=false;
                                                vecPostojiMolitvaMarijanskeFlag=false;
                                                vecPostojiMolitvaNadahnucaFlag=false;
                                                vecPostojiMolitvaSvjedocanstvaFlag=false;
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                                            }
                                        })
                                        .setNegativeButton("Natrag",null)
                                        .setIcon(R.drawable.duhos_logo)
                                        .show();
                            }
                            else{
                                marijanskeReference.child(String.valueOf(idNumberMarijanskeInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(),date.toString(),editTextTekstMolitve.getText().toString()));
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                            }
                        }
                        else if(nadahnucaFlag) {
                            if(vecPostojiMolitvaNadahnucaFlag){
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Upozorenje")
                                        .setMessage("Unešena molitva već postoji u bazi! Ukoliko želite izbrisati ovu molitvu te dodati navedenu odaberite \"Uredu\", ukoliko to ne želite odaberite \"Natrag\"!")
                                        .setPositiveButton("Uredu", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                nadahnucaReference.child(String.valueOf(idPostojeceMolitveNadahnuca)).removeValue();
                                                nadahnucaReference.child(String.valueOf(idNumberNadahnucaInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(),date.toString(),editTextTekstMolitve.getText().toString()));
                                                vecPostojiMolitvaOpceFlag=false;
                                                vecPostojiMolitvaMarijanskeFlag=false;
                                                vecPostojiMolitvaNadahnucaFlag=false;
                                                vecPostojiMolitvaSvjedocanstvaFlag=false;
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                                            }
                                        })
                                        .setNegativeButton("Natrag",null)
                                        .setIcon(R.drawable.duhos_logo)
                                        .show();
                            }
                            else{
                                nadahnucaReference.child(String.valueOf(idNumberNadahnucaInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(),date.toString(),editTextTekstMolitve.getText().toString()));
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                            }
                        }
                        else if(svjedocanstvaFlag) {
                            if (vecPostojiMolitvaSvjedocanstvaFlag) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Upozorenje")
                                        .setMessage("Unešena molitva već postoji u bazi! Ukoliko želite izbrisati ovu molitvu te dodati navedenu odaberite \"Uredu\", ukoliko to ne želite odaberite \"Natrag\"!")
                                        .setPositiveButton("Uredu", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                svjedocanstvaReference.child(String.valueOf(idPostojeceMolitveNadahnuca)).removeValue();
                                                svjedocanstvaReference.child(String.valueOf(idNumberSvjedocanstvaInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(), date.toString(), editTextTekstMolitve.getText().toString()));
                                                vecPostojiMolitvaOpceFlag = false;
                                                vecPostojiMolitvaMarijanskeFlag = false;
                                                vecPostojiMolitvaNadahnucaFlag = false;
                                                vecPostojiMolitvaSvjedocanstvaFlag = false;
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                                            }
                                        })
                                        .setNegativeButton("Natrag", null)
                                        .setIcon(R.drawable.duhos_logo)
                                        .show();
                            } else {
                                svjedocanstvaReference.child(String.valueOf(idNumberSvjedocanstvaInt)).setValue(new Molitva(editTextNazivMolitve.getText().toString(), date.toString(), editTextTekstMolitve.getText().toString()));
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                            }
                        }
                    }
                }
            });
            return molitvaFragmentView;
        }
        else {
            connectionFragmentView = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);
            osvjeziButton=connectionFragmentView.findViewById(R.id.osvjeziButton);
            osvjeziButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new MolitvaFragment()).addToBackStack("molitvaFragment").commit();
                }
            });

            return connectionFragmentView;
        }
    }

    private void onTextChange() {
        editTextNazivMolitve.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextNazivMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextNazivMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextTekstMolitve.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextTekstMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextTekstMolitve.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getReference() {
        opceReference= FirebaseDatabase.getInstance().getReference("Molitve/Molitve i pobožnosti");
        opceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberOpceString= snapshot.getKey();
                        idNumberOpceInt=Integer.parseInt(idNumberOpceString);
                        idNumberOpceInt++;
                        if (snapshot.child("Naziv").getValue() == null) {
                        } else {
                            final String naziv = snapshot.child("Naziv").getValue().toString().toLowerCase();
                            listaNazivaOpce.add(naziv);
                            idListaOpce.add(idNumberOpceInt - 1);
                        }
                    }
                }
                brojNazivaOpce=listaNazivaOpce.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });

        marijanskeReference= FirebaseDatabase.getInstance().getReference("Molitve/Marijanske molitve");
        marijanskeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberMarijanskeString= snapshot.getKey();
                        idNumberMarijanskeInt=Integer.parseInt(idNumberMarijanskeString);
                        idNumberMarijanskeInt++;
                        if (snapshot.child("Naziv").getValue() == null) {
                        } else {
                            final String naziv = snapshot.child("Naziv").getValue().toString().toLowerCase();
                            listaNazivaMarijanske.add(naziv);
                            idListaMarijanske.add(idNumberMarijanskeInt - 1);
                        }
                    }
                }
                brojNazivaMarijanske=listaNazivaMarijanske.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });

        nadahnucaReference= FirebaseDatabase.getInstance().getReference("Molitve/Nadahnuća");
        nadahnucaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberNadahnucaString= snapshot.getKey();
                        idNumberNadahnucaInt=Integer.parseInt(idNumberNadahnucaString);
                        idNumberNadahnucaInt++;
                        if (snapshot.child("Naziv").getValue() == null) {
                        } else {
                            final String naziv = snapshot.child("Naziv").getValue().toString().toLowerCase();
                            listaNazivaNadahnuca.add(naziv);
                            idListaNadahnuca.add(idNumberNadahnucaInt - 1);
                        }
                    }
                }
                brojNazivaNadahnuca=listaNazivaNadahnuca.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });

        svjedocanstvaReference= FirebaseDatabase.getInstance().getReference("Molitve/Svjedočanstva");
        svjedocanstvaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.exists()) {
                        idNumberPoboznostiString= snapshot.getKey();
                        idNumberSvjedocanstvaInt=Integer.parseInt(idNumberPoboznostiString);
                        idNumberSvjedocanstvaInt++;
                        if (snapshot.child("Naziv").getValue() == null) {
                        } else {
                            final String naziv = snapshot.child("Naziv").getValue().toString().toLowerCase();
                            listaNazivaSvjedocanstva.add(naziv);
                            idListaSvjedocanstva.add(idNumberSvjedocanstvaInt - 1);
                        }
                    }
                }
                brojNazivaSvjedocanstva=listaNazivaSvjedocanstva.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
            }
        });
    }

    private void setUpIcons() {
        opceMolitvaIcon = molitvaFragmentView.findViewById(R.id.opceMolitveIcon);
        marijanskaMolitvaIcon = molitvaFragmentView.findViewById(R.id.marijanskeMolitveIcon);
        nadahnucaIcon = molitvaFragmentView.findViewById(R.id.nadahnucaIcon);
        poboznostiIcon = molitvaFragmentView.findViewById(R.id.poboznostiIcon);

        opceMolitvaIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = true;
                marijanskaMolitvaFlag = false;
                nadahnucaFlag = false;
                svjedocanstvaFlag = false;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveactive));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijano));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucano));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostino));
            }
        });
        marijanskaMolitvaIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = false;
                marijanskaMolitvaFlag = true;
                nadahnucaFlag = false;
                svjedocanstvaFlag = false;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveno));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijaactive));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucano));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostino));
            }
        });
        nadahnucaIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = false;
                marijanskaMolitvaFlag = false;
                nadahnucaFlag = true;
                svjedocanstvaFlag = false;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveno));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijano));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucaactive));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostino));
            }
        });
        poboznostiIcon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                opceMolitvaFlag = false;
                marijanskaMolitvaFlag = false;
                nadahnucaFlag = false;
                svjedocanstvaFlag = true;
                opceMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_opcemolitveno));
                marijanskaMolitvaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_blazenadjevicamarijano));
                nadahnucaIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_nadahnucano));
                poboznostiIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_poboznostiactive));
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








