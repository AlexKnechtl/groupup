package com.example.alexander.groupup.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.group.MyGroupView;
import com.example.alexander.groupup.models.RequestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewholder> {

    private List<RequestModel> requestList;
    private Context context;
    private boolean myGroup;

    public RequestAdapter(List<RequestModel> requestList, Context context, boolean myGroup) {
        this.requestList = requestList;
        this.context = context;
        this.myGroup = myGroup;
    }

    @Override
    public RequestViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_layout_request, parent, false);
        return new RequestViewholder(v);
    }

    public class RequestViewholder extends RecyclerView.ViewHolder {
        private TextView name;
        private Button requestButton;
        private CircleImageView thumbImage;

        public RequestViewholder(View view) {
            super(view);
            requestButton = view.findViewById(R.id.request_button);
            name = view.findViewById(R.id.request_user_name);
            thumbImage = view.findViewById(R.id.request_user_picture);
        }
    }

    @Override
    public void onBindViewHolder(final RequestViewholder viewHolder, final int position) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String user_id = mAuth.getCurrentUser().getUid();

        final RequestModel c = requestList.get(position);
        final String receiver_user_id = c.getFrom();
        String type = c.getType();

        final DatabaseReference DataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(c.getFrom());

        if (type.equals("friend_request")) {
            DataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    Picasso.with(context).load(thumb_image)
                            .placeholder(R.drawable.default_user_black).into(viewHolder.thumbImage);
                    viewHolder.name.setText(name + " hat dir eine Freundschaftsanfrage geschickt!");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            viewHolder.requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatabaseReference UserProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(receiver_user_id);
                    final DatabaseReference MyAccountDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                    UserProfileDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            final String userCity = dataSnapshot.child("city").getValue().toString();
                            final String userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                            String friendCount = dataSnapshot.child("friends_count").getValue().toString();
                            final Long friendsCountUser = Long.parseLong(friendCount);

                            MyAccountDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String myProfileName = dataSnapshot.child("name").getValue().toString();
                                    final String myProfileThumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                                    final String myProfileCity = dataSnapshot.child("city").getValue().toString();

                                    String counter = dataSnapshot.child("friends_count").getValue().toString();
                                    final Long friendsCountMyAccount = Long.parseLong(counter);

                                    Calendar cal = Calendar.getInstance();
                                    int year = cal.get(Calendar.YEAR);
                                    int month = cal.get(Calendar.MONTH);
                                    int day = cal.get(Calendar.DAY_OF_MONTH);

                                    final String friendDate = day + "." + month + "." + year;

                                    String friendsMyAccount = Objects.toString(friendsCountMyAccount + 1);
                                    final String friendsUser = Objects.toString(friendsCountUser + 1);

                                    MyAccountDatabase.child("friends_count").setValue(friendsMyAccount);
                                    MyAccountDatabase.child("friends").child(receiver_user_id).child("date").setValue(friendDate);
                                    MyAccountDatabase.child("friends").child(receiver_user_id).child("name").setValue(userName);
                                    MyAccountDatabase.child("friends").child(receiver_user_id).child("city").setValue(userCity);
                                    MyAccountDatabase.child("friends").child(receiver_user_id).child("thumb_image").setValue(userThumbImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            UserProfileDatabase.child("friends_count").setValue(friendsUser);
                                            UserProfileDatabase.child("friends").child(user_id).child("date").setValue(friendDate);
                                            UserProfileDatabase.child("friends").child(user_id).child("name").setValue(myProfileName);
                                            UserProfileDatabase.child("friends").child(user_id).child("city").setValue(myProfileCity);
                                            UserProfileDatabase.child("friends").child(user_id).child("thumb_image").setValue(myProfileThumbImage)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            MyAccountDatabase.child("requests").child("received").child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    UserProfileDatabase.child("requests").child("sent").child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(context, (R.string.you_are_now_friends), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

        } else if (type.equals("group_invite")) {
            DataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();

                    Picasso.with(context).load(thumb_image)
                            .placeholder(R.drawable.default_user_black).into(viewHolder.thumbImage);
                    viewHolder.name.setText(name + " hat dich in seine Gruppe eingeladen.");

                    viewHolder.requestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final DatabaseReference UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                            final DatabaseReference GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(c.getFrom());

                            UserDatabase.child(user_id).child("my_group").setValue(c.getFrom());
                            GroupDatabase.child("members").child(user_id).child("rank").setValue("member");
                            GroupDatabase.child("member_count").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    GroupDatabase.child("member_count").setValue(Integer.parseInt(dataSnapshot.getValue().toString()) + 1);

                                    DatabaseReference GroupChatDatabase = FirebaseDatabase.getInstance().getReference()
                                            .child("GroupChat").child(c.getFrom());

                                    DatabaseReference user_message_push = GroupChatDatabase.push();
                                    String pushId = user_message_push.getKey();

                                    Map messageMap = new HashMap();
                                    messageMap.put("message", c.getName() + " joined the Group.");
                                    messageMap.put("from", user_id);
                                    messageMap.put("type", "information");

                                    GroupChatDatabase.child(pushId).updateChildren(messageMap)
                                            .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            DatabaseReference notificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(user_id);
                                            notificationDatabase.child(c.getFrom()).removeValue();

                                            Intent intent = new Intent(context, MyGroupView.class);
                                            intent.putExtra("group_id", c.getFrom());
                                            intent.putExtra("user_id", user_id);
                                            context.startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}