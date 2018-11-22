package nu.yakutomi.campuscafe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MenuFragment extends Fragment {
    private ArrayList<OrderHistoryModel> cart;
    private static RecyclerView.Adapter adapter;
    //private Set<String> items = new HashSet<>();
   // private Set<String> price = new HashSet<>();
    //private View v;
    private ArrayList<ItemsModel> items;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        Log.d("MF", "Inside onCreateView");
        RecyclerView recyclerView = view.findViewById(R.id.menu_items_recycler);
        recyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        items = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Items");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("FB/DB/MF", dataSnapshot.child("Items").getChildren().toString());
                //items.clear();
                //price.clear();
                items.clear();
                // donot use new ProgressDialog it will create a hidden to-be dismissed dialog
                //ProgressDialog load;
                //load = show(getActivity(),"Loading", "Loading data...");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue() != null) {
                        ItemsModel i = new ItemsModel();
                        i.setItem(snapshot.getKey());
                        i.setPrice(snapshot.getValue().toString());
                        items.add(i);
                        //items.add(snapshot.getKey());
                        //price.add(snapshot.getValue().toString());
                        Log.d("FB/Key/Item", items.get(0).getItem());
                        Log.d("FB/Value/Price", items.get(0).getPrice());
                        adapter.notifyDataSetChanged();
                    }
                }
                //load.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FB/DB/MF", databaseError.getMessage());
            }
        });

        //Log.d("MF", items.get(0).getItem()+items.get(0).getPrice());
        ItemsAdapter itemObj = new ItemsAdapter(items, getActivity());
        adapter = itemObj;
        recyclerView.setAdapter(adapter);
        cart = itemObj.getCart();
        return view;
    }
    // this is necessary to inform parent activity that we are changing menu in fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        menu.findItem(R.id.action_view_cart).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final AlertDialog alertDialog2 = new AlertDialog.Builder(requireContext()).setTitle("Cart").create();
                String spaces = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                StringBuilder itemsAndPrices = new StringBuilder("<b>Quantity" + spaces + "Item</b><br><br>");


                for(int i=0; i < cart.size(); i++) {
                    if(Integer.parseInt(cart.get(i).getQuantity()) < 10) {
                        itemsAndPrices.append("0").append(cart.get(i).getQuantity()).append(spaces).append(spaces).append("&nbsp;").append(cart.get(i).getItem()).append("<br>");
                    }
                    else {
                        itemsAndPrices.append(cart.get(i).getQuantity()).append(spaces).append(spaces).append("&nbsp;").append(cart.get(i).getItem()).append("<br>");
                    }
                }
                alertDialog2.setMessage(Html.fromHtml("<br>"+itemsAndPrices+"<br>"));
                alertDialog2.setButton(Dialog.BUTTON_POSITIVE, "PAY", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Timestamp(System.currentTimeMillis()));
                        final DatabaseReference orderHistoryChild = FirebaseDatabase.getInstance().getReference("Order History/"+currentUser.getUid());
                        orderHistoryChild.child(timeStamp);
                        orderHistoryChild.child(timeStamp).child("Items");
                        for(int i=0; i<cart.size(); i++) {
                            orderHistoryChild.child(timeStamp).child("Items")
                                    .child(cart.get(i).getItem()).setValue(cart.get(i).getQuantity());
                        }
                        if(cart.size() < 1) {
                            Toast.makeText(getContext(), "Cart empty.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Payment Done!\nCheck Order History.", Toast.LENGTH_LONG).show();
                            cart.clear();
                        }
                        alertDialog2.dismiss();
                    }
                });
                alertDialog2.setButton(Dialog.BUTTON_NEGATIVE, "CLEAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cart.clear();
                        Toast.makeText(getContext(), "Cart Cleared!", Toast.LENGTH_LONG).show();
                        alertDialog2.dismiss();
                    }
                });
                alertDialog2.show();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}
