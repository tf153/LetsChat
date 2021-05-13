package com.example.letschat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Models.Message;
import com.example.letschat.R;
import com.example.letschat.databinding.ItemReceivedBinding;
import com.example.letschat.databinding.ItemSentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.zla.reactions.ReactionPopup;
import io.zla.reactions.ReactionsConfig;
import io.zla.reactions.ReactionsConfigBuilder;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;
    final int ITEM_SENT=1,ITEM_RECEIVED=2;

    String senderRoom,receiverRoom;

    public MessagesAdapter(Context context, ArrayList<Message> messages,String senderRoom,String receiverRoom){
        this.context=context;
        this.messages=messages;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT){
            View view= LayoutInflater.from(context).inflate(R.layout.item_sent,parent,false);
            return new SentViewHolder(view) ;
        }else{
            View view=LayoutInflater.from(context).inflate(R.layout.item_received,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }else{
            return ITEM_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        int[] reactions = new int[]{
                R.drawable.like,
                R.drawable.love,
                R.drawable.laugh,
                R.drawable.wow,
                R.drawable.sad,
                R.drawable.angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SentViewHolder.class) {
                SentViewHolder viewHolder = (SentViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);

                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);
            FirebaseDatabase.getInstance().getReference().child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if (message.getFeeling() >= 0) {
                //message.setFeeling(reactions[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends  RecyclerView.ViewHolder{

        ItemReceivedBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemReceivedBinding.bind(itemView);
        }
    }
}
