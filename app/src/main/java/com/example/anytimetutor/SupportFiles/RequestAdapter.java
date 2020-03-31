package com.example.anytimetutor.SupportFiles;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anytimetutor.R;
import com.example.anytimetutor.RequestDetailsActivity;
import com.example.anytimetutor.TimerActivity;

import java.util.HashMap;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{
    //this context we will use to inflate the layout
    private Context mCtx;
    List<String> topic, tutee_name, sess_date, sess_time, sess_status, req_id, subject, accept_stat, date_time;

    //we are storing all the products in a list
    private HashMap<String ,List<String>> request_det = new HashMap<>();

    String activity;

    //getting the context and product list with constructor
    public RequestAdapter(Context mCtx, HashMap<String, List<String>> request_det, String activity) {
        this.mCtx = mCtx;
        this.request_det = request_det;
        this.activity = activity;

        this.topic = request_det.get("topic");
        this.tutee_name = request_det.get("tutee_name");
        this.sess_date = request_det.get("sess_date");
        this.sess_time = request_det.get("sess_time");
        this.sess_status = request_det.get("sess_status");
        this.subject = request_det.get("subject");
        this.req_id = request_det.get("req_id");
        this.accept_stat = request_det.get("accept_stat");
        this.date_time = request_det.get("date_time");
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view;
        if(activity.equals("home")) {
            view = inflater.inflate(R.layout.request_card_homepage, null);
        }
        else {
            view = inflater.inflate(R.layout.request_card, null);
        }
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, final int position) {

        /* Trying to move holder to each condition type so that in else condition for session history/past sessions,
        dont have to add unnecessary items in adapter
        * *///binding the data with the viewholder views

        if(activity.equals("tutor profile")) {
            holder.topic.setText(topic.get(position));
            holder.tutee_name.setText(tutee_name.get(position));
            holder.sess_date.setText(sess_date.get(position));
            holder.sess_time.setText(sess_time.get(position));
            holder.sess_status.setText(sess_status.get(position));
            holder.id.setText(req_id.get(position));
            holder.subject.setText(subject.get(position));

            if (accept_stat.get(position).equals("accepted")) {
                holder.request.setCardBackgroundColor(Color.parseColor("#7EDA82"));
            } else if (accept_stat.get(position).equals("rejected")) {
                holder.request.setCardBackgroundColor(Color.parseColor("#DD8EA8"));
            }
            holder.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mCtx, RequestDetailsActivity.class);
                    in.putExtra("id",req_id.get(position));
                    in.putExtra("subject",subject.get(position));
                    in.putExtra("accept_stat",accept_stat.get(position));
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(in);
                }
            });
        }
        else if(activity.equals("home"))
        {holder.topic.setText(topic.get(position));
            holder.tutee_name.setText(tutee_name.get(position));
            holder.sess_date.setText(sess_date.get(position));
            holder.sess_time.setText(sess_time.get(position));
            holder.sess_status.setText(sess_status.get(position));
            holder.id.setText(req_id.get(position));
            holder.subject.setText(subject.get(position));

            holder.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mCtx, TimerActivity.class);
                    in.putExtra("id",req_id.get(position));
                    in.putExtra("subject",subject.get(position));
                    in.putExtra("date_time",date_time.get(position));
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(in);
                }
            });
        }
        else if (activity.equals("past sessions"))
        {
            holder.topic.setText(topic.get(position));
            holder.sess_date.setText(sess_date.get(position));
            holder.sess_time.setText(sess_time.get(position));
            holder.id.setText(req_id.get(position));
            holder.subject.setText(subject.get(position));

            holder.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(mCtx);
                    View view;
                        view = inflater.inflate(R.layout.request_card, null);

                }
            });


        }

    }


    @Override
    public int getItemCount() {
        return topic.size();
    }
    public void refreshView(int position) {
        notifyItemChanged(position);
    }


    class RequestViewHolder extends RecyclerView.ViewHolder {

        CardView request;
        TextView topic, tutee_name, sess_date, sess_time, sess_status, id , subject;

        public RequestViewHolder(View itemView) {
            super(itemView);

            request = (CardView) itemView.findViewById(R.id.request);
            topic = (TextView) itemView.findViewById(R.id.topic);
            tutee_name = (TextView) itemView.findViewById(R.id.tutee_name);
            sess_date = (TextView) itemView.findViewById(R.id.sess_date);
            sess_time = (TextView) itemView.findViewById(R.id.sess_time);
            sess_status = (TextView) itemView.findViewById(R.id.sess_status);
            id = (TextView) itemView.findViewById(R.id.id);
            subject= (TextView) itemView.findViewById(R.id.subject);
        }
    }
}
