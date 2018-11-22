package nu.yakutomi.campuscafe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class TimestampAdapter extends RecyclerView.Adapter<TimestampAdapter.MyViewHolder> {
    private ArrayList<TimestampModel> orderhTime;
    private Context context;
    TimestampAdapter(ArrayList<TimestampModel> orderhTime, Context context) {
        this.orderhTime = orderhTime;
        this.context = context;
        //Log.d("IA/L", items.get(0).getItem());
    }


    @NonNull
    @Override
    public TimestampAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderh_time_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimestampAdapter.MyViewHolder holder, int position) {
        final Button itemTitleHold = holder.timeStamp;
        RelativeLayout relLay2 = holder.relLay;
        itemTitleHold.setText(orderhTime.get(position).getTimestamp());
        itemTitleHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).setTitle("Order Info").create();
                alertDialog.setButton(Dialog.BUTTON_NEUTRAL, "DONE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                // Firebase code
                final ArrayList<OrderHistoryModel> historyItems;
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mDatabase;
                historyItems = new ArrayList<>();
                if(currentUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("Order History/" + currentUser.getUid());
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.d("FB/DB/MF", dataSnapshot.child("Items").getChildren().toString());
                            //items.clear();
                            //price.clear();
                            historyItems.clear();
                            // donot use new ProgressDialog it will create a hidden to-be dismissed dialog
                            //ProgressDialog load;
                            //load = show(getActivity(),"Loading", "Loading data...");
                            String uid = currentUser.getUid();
                            String timestamps = (String) itemTitleHold.getText();
                            StringBuilder items = new StringBuilder();
                            for (DataSnapshot snapshot : dataSnapshot.child(timestamps).child("Items").getChildren()) {
                                OrderHistoryModel i = new OrderHistoryModel();
                                if(snapshot.getValue() != null) {
                                    i.setItem(snapshot.getKey());
                                    i.setQuantity(snapshot.getValue().toString());
                                    //historyItems.add(i);
                                    //items.add(snapshot.getKey());
                                    //price.add(snapshot.getValue().toString());
                                    items.append("\n").append(i.getQuantity()).append("  ").append(i.getItem());
                                }
                            }

                            Log.d("TSA", items.toString());
                            alertDialog.setMessage(items + "\n");
                            alertDialog.show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderhTime.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        Button timeStamp;
        RelativeLayout relLay;

        MyViewHolder(View view) {
            super(view);
            this.timeStamp = view.findViewById(R.id.buttonTimestamp1);
            this.relLay = view.findViewById(R.id.cardClickRelative);
        }
    }
}
