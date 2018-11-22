package nu.yakutomi.campuscafe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistoryFragment extends Fragment {
    private static RecyclerView.Adapter adapter;
    //private Set<String> items = new HashSet<>();
    // private Set<String> price = new HashSet<>();
    //private View v;
    private ArrayList<TimestampModel> orderhTime;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // this is necessary to inform parent activity that we are changing menu in fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_view_cart).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.order_history_fragment, container, false);
       // Log.d("MF", "Inside onCreateView");
        RecyclerView recyclerView = view.findViewById(R.id.order_history_recycler);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        orderhTime = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Order History/"+currentUser.getUid());
        Log.d("FB/DB/OHF", FirebaseDatabase.getInstance().getReference("Order History/"+currentUser.getUid()).toString());


        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.d("FB/DB/MF", dataSnapshot.child("Items").getChildren().toString());
                //items.clear();
                //price.clear();
                orderhTime.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TimestampModel i = new TimestampModel();
                    i.setTimestamp(snapshot.getKey());
                    orderhTime.add(i);
                    //items.add(snapshot.getKey());
                    //price.add(snapshot.getValue().toString());
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FB/DB/OHF", databaseError.getMessage()+"\n"+databaseError.getDetails()+"\n"+databaseError.getCode());
            }
        });

        //Log.d("MF", items.get(0).getItem()+items.get(0).getPrice());
        adapter = new TimestampAdapter(orderhTime, getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }
}
