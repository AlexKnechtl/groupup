package com.example.alexander.groupup.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.StartActivity;
import com.example.alexander.groupup.chat.GroupChat;
import com.example.alexander.groupup.group.MyGroupView;
import com.example.alexander.groupup.helpers.GeoFireHelper;
import com.example.alexander.groupup.interviews.InterviewStart;
import com.example.alexander.groupup.group.GroupView;
import com.example.alexander.groupup.models.GroupType;
import com.example.alexander.groupup.models.LanguageStringsModel;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private static final long LOCATION_REFRESH_TIME = 10000;

    private static final float LOCATION_REFRESH_DISTANCE = 10.0f;
    private static final int REQUEST_CODE_PLACE_PICKER = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 3;

    //XML
    private RecyclerView recyclerView;
    private EditText locationName;
    private TextView searchLocation;
    private SeekBar seekBar;
    private Button groupButton, groupChatButton;
    private Spinner groupType, spinnerActivity;
    private LinearLayout layoutGroupActivitySearch;

    //Constants
    public static final String ANONYMOUS = "anonymous";

    //FireBase
    private DatabaseReference GroupDatabase, DatabaseReference;

    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private GroupsAdapter groupsAdapter;

    double radius = 10;
    private boolean isLocationSelected = false;

    //Variables
    private String user_id, group_id, group_category;
    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;
    private boolean creator;
    private GeoLocation currentLocation = new GeoLocation(47.0727247, 15.4335573); //Todo change this location. with setCurrentLocation()
    private ArrayList<String> spinnerEntries;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");

        FirebaseMessaging.getInstance().subscribeToTopic("TestTopic");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        spinnerEntries = new ArrayList<>();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationTokens")
                        .child(instanceIdResult.getToken()).setValue("True");
            }
        });

        mContext = getApplicationContext();

        //Initialize FireBase
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        user_id = current_user.getUid();

        DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        DatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("my_group")) {
                    group_id = dataSnapshot.child("my_group").getValue().toString();
                    groupButton.setText(R.string.my_group);
                    groupChatButton.setVisibility(View.VISIBLE);
                    creator = true;
                    GroupDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("category").exists()) {
                                group_category = dataSnapshot.child("category").getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else
                    creator = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        setupBottomNavigationView();

        //Find Ids
        TextView dateTextView;
        dateTextView = findViewById(R.id.date_main);
        locationName = findViewById(R.id.loc_city);
        groupButton = findViewById(R.id.group_button);
        recyclerView = findViewById(R.id.main_recycler_view);
        groupChatButton = findViewById(R.id.group_chat_button);
        searchLocation = findViewById(R.id.loc_radius);
        seekBar = findViewById(R.id.slider);
        groupType = findViewById(R.id.spinner_group_type);
        spinnerActivity = findViewById(R.id.spinner_activity);
        layoutGroupActivitySearch = findViewById(R.id.ll_group_activity);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        radius = 0.25;
                        break;
                    case 1:
                        radius = 0.5;
                        break;
                    case 2:
                        radius = 1;
                        break;
                    case 3:
                        radius = 2;
                        break;
                    case 4:
                        radius = 5;
                        break;
                    case 5:
                        radius = 10;
                        break;
                    case 6:
                        radius = 20;
                        break;
                    case 7:
                        radius = 50;
                        break;
                    case 8:
                        radius = 100;
                        break;
                    case 9:
                        radius = 200;
                        break;
                    case 10:
                        radius = 500;
                        break;
                    case 11:
                        radius = 1000;
                        break;
                    case 12:
                        radius = 10000;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                geoQuery.setRadius(radius);
                if (radius < 1)
                    searchLocation.setText(String.format("%d m", Math.round(radius * 1000)));
                else
                    searchLocation.setText(String.format("%d km", Math.round(radius)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                switch (progress) {
                    case 0:
                        radius = 0.25;
                        break;
                    case 1:
                        radius = 0.5;
                        break;
                    case 2:
                        radius = 1;
                        break;
                    case 3:
                        radius = 2;
                        break;
                    case 4:
                        radius = 5;
                        break;
                    case 5:
                        radius = 10;
                        break;
                    case 6:
                        radius = 20;
                        break;
                    case 7:
                        radius = 50;
                        break;
                    case 8:
                        radius = 100;
                        break;
                    case 9:
                        radius = 200;
                        break;
                    case 10:
                        radius = 500;
                        break;
                    case 11:
                        radius = 1000;
                        break;
                    case 12:
                        radius = 10000;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
//                geoQuery.setRadius(radius);
                if (radius < 1)
                    searchLocation.setText(String.format("%d m", Math.round(radius * 1000)));
                else
                    searchLocation.setText(String.format("%d km", Math.round(radius)));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        groupType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        layoutGroupActivitySearch.setVisibility(View.GONE); //all
                        groupsAdapter.RemoveFilter();
                        break;
                    case 1:
                        layoutGroupActivitySearch.setVisibility(View.VISIBLE); //sport
                        FirebaseDatabase.getInstance().getReference().child("LanguageStrings").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                spinnerEntries.clear();
                                spinnerEntries.add(getString(R.string.all_activities));
                                for (DataSnapshot s : dataSnapshot.getChildren()) {
                                    LanguageStringsModel lsm = s.getValue(LanguageStringsModel.class);
                                    spinnerEntries.add(lsm.getLocalLanguageString());
                                }
                                spinnerActivity.setAdapter(new ArrayAdapter(HomeActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerEntries));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        groupsAdapter.Filter(new Filter() {
                            @Override
                            public boolean keep(GroupModel g) {
                                return g.category == GroupType.sport;
                            }
                        });
                        break;
                    case 2:
                        layoutGroupActivitySearch.setVisibility(View.GONE);//leisure TODO implement
                        //spinnerActivity.setAdapter(ArrayAdapter.createFromResource(HomeActivity.this, R.array.group_activities_business, R.layout.support_simple_spinner_dropdown_item));
                        groupsAdapter.Filter(new Filter() {
                            @Override
                            public boolean keep(GroupModel g) {
                                return g.category == GroupType.leisure;
                            }
                        });
                        break;
                    case 3:
                        layoutGroupActivitySearch.setVisibility(View.VISIBLE);//nightlife
                        spinnerActivity.setAdapter(ArrayAdapter.createFromResource(HomeActivity.this, R.array.group_activities_nightlife, R.layout.support_simple_spinner_dropdown_item));
                        groupsAdapter.Filter(new Filter() {
                            @Override
                            public boolean keep(GroupModel g) {
                                return g.category == GroupType.nightlife;
                            }
                        });
                        break;
                    case 4:
                        layoutGroupActivitySearch.setVisibility(View.VISIBLE);//business
                        spinnerActivity.setAdapter(ArrayAdapter.createFromResource(HomeActivity.this, R.array.group_activities_business, R.layout.support_simple_spinner_dropdown_item));
                        groupsAdapter.Filter(new Filter() {
                            @Override
                            public boolean keep(GroupModel g) {
                                return g.category == GroupType.business;
                            }
                        });
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GroupType t = null;
                String s = (String) (((TextView) view).getText());
                switch (groupType.getSelectedItemPosition()) {
                    case 1:
                        t = GroupType.sport;
                        s = LanguageStringsManager.getInstance().getLanguageStringByLocalString(s).getId();
                        break;
                    case 3:
                        t = GroupType.nightlife;
                        break;
                    case 4:
                        t = GroupType.business;
                        break;
                    default:
                        return;
                }
                final GroupType ft = t;
                final CharSequence selectedString = s;
                if (position == 0) {
                    groupsAdapter.Filter(new Filter() {
                        @Override
                        public boolean keep(GroupModel g) {
                            return g.category == ft;
                        }
                    });
                } else if (ft == GroupType.sport) {
                    groupsAdapter.Filter(new Filter() {
                        @Override
                        public boolean keep(GroupModel g) {
                            return g.category == ft && g.activity.contains(selectedString);
                        }
                    });
                } else {
                    groupsAdapter.Filter(new Filter() {
                        @Override
                        public boolean keep(GroupModel g) {
                            return g.category == ft && LanguageStringsManager.getInstance().getLanguageStringByStringId(g.activity).getLocalLanguageString().contains(selectedString);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Query f = GroupDatabase.orderByChild("d").equalTo(3);

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
                    intent.putExtra("category", group_category);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupGeoFire() {
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("GeoFire"));

        geoQuery = geoFire.queryAtLocation(currentLocation, radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                if (groupsAdapter.CheckGroupIdExists(key) == -1)
                    GroupDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            GroupModel m = dataSnapshot.getValue(GroupModel.class);

                            //Todo Filter and maybe change to geoFire with data
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
        /// Query f = GroupDatabase.orderByChild("time").equalTo(3);
        //recyclerView.setAdapter(firebaseRecyclerAdapter);
        groupsAdapter = new GroupsAdapter(this);
        recyclerView.setAdapter(groupsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLocationSelected)
            setCurrentLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (geoQuery != null) geoQuery.removeAllListeners();
        else
            Toast.makeText(this, "ERROR: GeoQuery is null. Something happend with your GPS", Toast.LENGTH_LONG).show();
        geoQuery = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GroupsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setGroupImage(String groupImage) {
            final ImageView groupBackground = mView.findViewById(R.id.background_group);
            Glide.with(mView).load(groupImage).into(new DrawableImageViewTarget(groupBackground));
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

        public void setMemberQuantity(long quantity) {
            TextView v = mView.findViewById(R.id.member_quantity_group);
            v.setText(String.format("%d", quantity));
        }

        public void setGroupDistance(Double distance) {
            TextView v = mView.findViewById(R.id.group_distance);
            v.setText(String.format("%.2f", distance));
        }
    }

    public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolder> {

        ArrayList<GeoGroup> list;
        ArrayList<GeoGroup> filteredItems;
        Context c;
        Filter filter;

        public GroupsAdapter(Context c) {

            filteredItems = this.list = new ArrayList<>();
            this.c = c;
        }

        public void Add(GeoGroup g) {
            list.add(g);
            if (filter == null) {
                notifyItemInserted(filteredItems.indexOf(g));
            } else if (filter.keep(g.group)) {
                filteredItems.add(g);
                notifyItemInserted(filteredItems.indexOf(g));
            }
        }

        public void Remove(GeoGroup g) {
            list.remove(g);
            if (filteredItems.contains(g)) {
                int pos = filteredItems.indexOf(g);
                filteredItems.remove(g);
                notifyItemRemoved(pos);
            }
        }

        public int CheckGroupIdExists(String groupId) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).groupId.equals(groupId)) {
                    return i;
                }
            }
            return -1;
        }

        public void Remove(String groupId) {
            GeoGroup g = null;
            for (int i = 0; i < list.size(); i++) {
                g = list.get(i);
                if (g.groupId.equals(groupId)) {
                    Remove(g);
                }
            }
        }

        public void Clear() {
            list.clear();
            filteredItems.clear();
            notifyDataSetChanged();
        }

        public void Filter(Filter filter) {
            this.filter = filter;
            filteredItems = new ArrayList<>();
            for (GeoGroup g : list) {
                if (filter.keep(g.group))
                    filteredItems.add(g);
            }
            notifyDataSetChanged();
        }

        public void RemoveFilter() {
            filter = null;
            filteredItems = list;
        }

        @NonNull
        @Override
        public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(c).inflate(R.layout.single_layout_group, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GeoGroup g = filteredItems.get(recyclerView.getChildLayoutPosition(view));

                    if (g.groupId.equals(group_id)) {
                        Intent intent = new Intent(HomeActivity.this, MyGroupView.class);
                        intent.putExtra("group_id", group_id);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("category", group_category);
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
            GeoGroup g = filteredItems.get(position);
            holder.setGroupDistance(GeoFireHelper.GetDistance(g.location, currentLocation));
            holder.setGroupImage(g.group.group_image);
            holder.setActivityCity(LanguageStringsManager.getInstance().getLanguageStringByStringId(g.group.activity).getLocalLanguageString(), g.group.location);
            holder.setTag1(g.group.tag1);
            holder.setTag2(g.group.tag2);
            holder.setTag3(g.group.tag3);
            holder.setMemberQuantity(g.group.getMemberCount());
        }

        @Override
        public int getItemCount() {
            return filteredItems.size();
        }
    }

    protected void setCurrentLocation(GeoLocation location) {
        currentLocation = location;
        geoQuery.setLocation(location, radius);
        groupsAdapter.notifyDataSetChanged();
        //groupsAdapter.Clear();
        //setupGeoFire();
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
        intent.putExtra("category", group_category);
        startActivity(intent);
    }

    public void searchGroupLocationClick(View view) {
        Button locationNear, chooseLocation;

        Dialog dialog = new Dialog(HomeActivity.this);

        dialog.setContentView(R.layout.popup_two_options);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        locationNear = dialog.findViewById(R.id.location_near_popup);
        chooseLocation = dialog.findViewById(R.id.choose_location_popup);

        locationNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentLocation();
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
        if (requestCode == REQUEST_CODE_PLACE_PICKER && resultCode == RESULT_OK) {
            isLocationSelected = true;
            LatLng ll = PlacePicker.getPlace(this, data).getLatLng();
            setLocationData(ll.latitude, ll.longitude);
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (resultCode == RESULT_OK) {
                setCurrentLocation();
            }
        }
    }

    private void setLocationData(double latitude, double longitude) {
        currentLocation = new GeoLocation(latitude, longitude);
        if (geoQuery == null)
            setupGeoFire();
        setCurrentLocation(new GeoLocation(latitude, longitude));

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);
            String cn = addresses.get(0).getLocality();
            if (cn.isEmpty())
                cn = addresses.get(0).getCountryName();
            locationName.setText(cn);


        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setCurrentLocation() {
        isLocationSelected = false;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "We need your location to show nearby groups...", Toast.LENGTH_LONG).show();
            } else {
            }

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);

        } else {
            try {
                int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                if (off == 0) {
                    Toast.makeText(this, "We need your location to show nearby groups...", Toast.LENGTH_LONG).show();
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);
                    return;
                }
            } catch (Settings.SettingNotFoundException e) {
                Log.e("Access Locationsettings", e.getMessage());
                e.printStackTrace();
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null && !isLocationSelected) {
                        setLocationData(location.getLatitude(), location.getLongitude());
                    }
                }
            });
        }
    }

    public interface Filter {
        boolean keep(GroupModel g);
    }
}