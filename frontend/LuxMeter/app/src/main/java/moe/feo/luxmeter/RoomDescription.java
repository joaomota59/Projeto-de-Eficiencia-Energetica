package moe.feo.luxmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RoomDescription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_description);

        getSupportActionBar().setTitle("Informações da Sala");


    }
}