package com.example.anytimetutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SearchTutor extends AppCompatActivity {
    String category;
    String subject;
    String topic;
    TextView t_categorydb, t_subjectdb, t_topicdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tutor);
        Intent in= getIntent();
        category= in.getStringExtra("Category");
        subject= in.getStringExtra("Subject");
        topic= in.getStringExtra("Topic");
        t_categorydb= (TextView)findViewById(R.id.t_categorydb);
        t_subjectdb= (TextView)findViewById(R.id.t_subjectdb);
        t_topicdb= (TextView)findViewById(R.id.t_topicdb);
        t_categorydb.setText(category);
        t_subjectdb.setText(subject);
        t_topicdb.setText(topic);

    }
}
