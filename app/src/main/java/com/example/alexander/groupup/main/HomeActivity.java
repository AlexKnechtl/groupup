package com.example.alexander.groupup.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.alexander.groupup.StartActivity;
import com.example.alexander.groupup.chat.GroupChat;
import com.example.alexander.groupup.group.MyGroupView;
import com.example.alexander.groupup.helpers.GeoFireHelper;
import com.example.alexander.groupup.interviews.InterviewStart;
import com.example.alexander.groupup.group.GroupView;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.IOException;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final long LOCATION_REFRESH_TIME = 10000;

    private static final float LOCATION_REFRESH_DISTANCE = 10.0f;
    private static final int REQUEST_CODE_PLACE_PICKER = 2;
    //XML
    private RecyclerView recyclerView;
    private TextView location, locationName;
    private TextView searchLocation;
    private Button groupButton, groupChatButton;
    private Dialog dialog;

    //Constants
    public static final String ANONYMOUS = "anonymous";

    //FireBase
    private DatabaseReference GroupDatabase;
    private DatabaseReference UserDatabase;

    private GeoFire geoFire;

    private GeoQuery geoQuery;

    private GroupsAdapter groupsAdapter;

    private ArrayList groups;

    //Variables
    private String city;
    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;
    private boolean creator;
    private String user_id, group_id;
    private GeoLocation currentLocation = new GeoLocation(47.0727247,15.4335573); //Todo change this location. with setCurrentLocation()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);
        FirebaseMessaging.getInstance().subscribeToTopic("TestTopic");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationTokens")
                        .child(instanceIdResult.getToken()).setValue("True");
            }
        });

        mContext = getApplicationContext();

        //Get Current User ID
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        user_id = current_user.getUid();

        setupBottomNavigationView();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        dialog = new Dialog(this);

        //Find Ids
        TextView dateTextView;
        dateTextView = findViewById(R.id.date_main);
        location = findViewById(R.id.location_main);
        locationName = findViewById(R.id.loc_city);
        groupButton = findViewById(R.id.group_button);
        recyclerView = findViewById(R.id.main_recycler_view);
        groupChatButton = findViewById(R.id.group_chat_button);
        searchLocation = findViewById(R.id.loc_radius);

        searchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) return;
                geoQuery.setRadius(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Get Data from User
        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                city = dataSnapshot.child("city").getValue().toString();

                if (dataSnapshot.hasChild("my_group")) {
                    group_id = dataSnapshot.child("my_group").getValue().toString();
                    groupButton.setText(R.string.my_group);
                    groupChatButton.setVisibility(View.VISIBLE);
                    creator = true;
                } else
                    creator = false;

                location.setText(city);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");


        //setupGeoFire();
        //Show Date in GroupCalendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MMM");
        String currentDate = formatter.format(new Date());
        dateTextView.setText(currentDate);

        //Recycler View
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!creator) {
                    Intent intent = new Intent(HomeActivity.this, InterviewStart.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, MyGroupView.class);
                    intent.putExtra("group_id", group_id);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupGeoFire() {
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("GeoFire"));

        geoQuery = geoFire.queryAtLocation(currentLocation, 100);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                if(groupsAdapter.CheckGroupIdExists(key) == -1)
                    GroupDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupModel m = dataSnapshot.getValue(GroupModel.class);
                        groupsAdapter.Add(new GeoGroup(m, location, dataSnapshot.getKey()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                groupsAdapter.Remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query f = GroupDatabase.orderByChild("d").equalTo(3);


        /*FirebaseRecyclerAdapter<GroupModel, GroupsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupModel, GroupsViewHolder>(
                GroupModel.class,
                R.layout.single_layout_group,
                GroupsViewHolder.class,
                GroupDatabase
        ) {
            @Override
            protected void populateViewHolder(final GroupsViewHolder groupsViewHolder, GroupModel groups, int position) {

                geoFire.getLocation(getRef(position).getKey(), new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {
                        if(location != null) {
                            Log.d("Location: ", location.toString());
                            groupsViewHolder.setGroupDistance(GeoFireHelper.GetDistance(location, currentLocation));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                groupsViewHolder.setGroupImage(groups.getGroup_image());

                groupsViewHolder.setActivityCity(LanguageStringsManager.getInstance().getLanguageStringByStringId(groups.getActivity()).getLocalLanguageString(), groups.getLocation());
                groupsViewHolder.setTag1(groups.getTag1());
                groupsViewHolder.setTag2(groups.getTag2());
                groupsViewHolder.setTag3(groups.getTag3());
                groupsViewHolder.setMemberQuantity(groups.getMember_count().toString());

                final String groupId = getRef(position).getKey();

                groupsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (groupId.equals(group_id)) {
                            Intent intent = new Intent(HomeActivity.this, MyGroupView.class);
                            intent.putExtra("group_id", group_id);
                            intent.putExtra("user_id", user_id);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomeActivity.this, GroupView.class);
                            intent.putExtra("group_id", groupId);
                            intent.putExtra("user_id", user_id);
                            startActivity(intent);
                        }
                    }
                });
            }
        };*/
        //recyclerView.setAdapter(firebaseRecyclerAdapter);
        groupsAdapter = new GroupsAdapter(this);
        recyclerView.setAdapter(groupsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupGeoFire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        geoQuery.removeAllListeners();
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GroupsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setGroupImage(String groupImage) {
            final ImageView groupBackground = mView.findViewById(R.id.background_group);
            Glide.with(mView).load(groupImage).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    groupBackground.setBackground(resource);
                }
            });
        }

        public void setActivityCity(String activity, String location) {
            TextView groupHeadline = mView.findViewById(R.id.group_headline);
            groupHeadline.setText(activity
                    + " @" + location);
        }

        public void setTag1(String tag1) {
            TextView tag1TextView = mView.findViewById(R.id.group_layout_tag_1);
            tag1TextView.setText(tag1);
        }

        public void setTag2(String tag2) {
            TextView tag2TextView = mView.findViewById(R.id.group_layout_tag_2);
            tag2TextView.setText(tag2);
        }

        public void setTag3(String tag3) {
            TextView tag3TextView = mView.findViewById(R.id.group_layout_tag_3);
            tag3TextView.setText(tag3);
        }

        public void setMemberQuantity(String quantity){
            TextView v = mView.findViewById(R.id.member_quantity_group);
            v.setText(quantity);
        }

        public void setGroupDistance(Double distance){
            TextView v = mView.findViewById(R.id.group_distance);
            v.setText(String.format("%.2f", distance));
        }
    }

    public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder>{

        ArrayList<GeoGroup> list;
        Context c;


        public GroupsAdapter(Context c){

            this.list = new ArrayList<>();
            this.c = c;
        }

        public void Add(GeoGroup g){
            list.add(g);
            notifyItemInserted(list.indexOf(g));
        }

        public void Remove(GeoGroup g){
            int pos = list.indexOf(g);
            list.remove(g);
            notifyItemRemoved(pos);
        }

        public int CheckGroupIdExists(String groupId){
            for(int i = 0; i<list.size(); i++)
            {
                if(list.get(i).groupId.equals(groupId))
                {
                    return i;
                }
            }
            return -1;
        }

        public void Remove(String groupId){
            for(int i = 0; i<list.size(); i++)
            {
                if(list.get(i).groupId.equals(groupId))
                {
                    list.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }

        public void Clear()
        {
            list.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(c).inflate(R.layout.single_layout_group, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GeoGroup g = list.get(recyclerView.getChildLayoutPosition(view));

                    if (g.groupId.equals(group_id)) {
                        Intent intent = new Intent(HomeActivity.this, MyGroupView.class);
                        intent.putExtra("group_id", group_id);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(HomeActivity.this, GroupView.class);
                        intent.putExtra("group_id", g.groupId);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    }
                }
            });
            return new GroupsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
            GeoGroup g = list.get(position);
            holder.setGroupDistance(GeoFireHelper.GetDistance(g.location, currentLocation));
            holder.setGroupImage(g.group.getGroup_image());
            holder.setActivityCity(LanguageStringsManager.getInstance().getLanguageStringByStringId(g.group.getActivity()).getLocalLanguageString(), g.group.getLocation());
            holder.setTag1(g.group.getTag1());
            holder.setTag2(g.group.getTag2());
            holder.setTag3(g.group.getTag3());
            holder.setMemberQuantity(g.group.getMember_count().toString());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    protected void setCurrentLocation(GeoLocation location){
        currentLocation = location;
        geoQuery.setCenter(location);
        //geoQuery.setRadius(geoQuery.getRadius());
        groupsAdapter.notifyDataSetChanged();
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, user_id, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    long lastPress;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPress > 5000) {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        } else {
            Intent intent = new Intent(HomeActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    //OnClicks
    public void groupChatClick(View view) {
        Intent intent = new Intent(HomeActivity.this, GroupChat.class);
        intent.putExtra("group_id", group_id);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    public void searchGroupLocationClick(View view) {

        Button locationNear, chooseLocation;

        dialog.setContentView(R.layout.popup_two_options);

        locationNear = dialog.findViewById(R.id.location_near_popup);
        chooseLocation = dialog.findViewById(R.id.choose_location_popup);


        locationNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlacePickerActivity();
            }
        });

        dialog.show();
    }

    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, REQUEST_CODE_PLACE_PICKER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PLACE_PICKER && resultCode == RESULT_OK) {
            displaySelectedPlace(data);
        }
    }

    private void displaySelectedPlace(Intent data) {
        Place placeSelected = PlacePicker.getPlace(this, data);

        Double latitude = placeSelected.getLatLng().latitude;
        Double longitude = placeSelected.getLatLng().longitude;

        setCurrentLocation(new GeoLocation(latitude, longitude));

        String latLng = "geo:<" + latitude  + ">,<" + longitude + ">?q=<" + latitude  + ">,<" + longitude + ">("
                + getResources().getString(R.string.group_is_here) + ")";

        Geocoder geocoder = new Geocoder(this);
        try
        {
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);
            String cn = addresses.get(0).getLocality();
            if(cn.isEmpty())
                cn = addresses.get(0).getCountryName();
            locationName.setText(cn);


        } catch (IOException e){
            e.printStackTrace();
        }

        //final String placeName = placeSelected.getName().toString();
    }

    public class GeoGroup {
        public GeoGroup(GroupModel group, GeoLocation location, String groupId) {
            this.group = group;
            this.location = location;
            this.groupId = groupId;
        }

        private GroupModel group;
        private GeoLocation location;
        private String groupId;
    }
}