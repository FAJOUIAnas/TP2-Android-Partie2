package com.example.tp2partie2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int PICK_CONTACT_REQUEST=1;
    int Perm_CTC = 1;
    int CALL_Perm = 1;

    Button detailsContactsBtn;
    Button contactsBtn;
    Button callBtn;
    TextView textView;

    String contactId;
    String phoneNumber;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detailsContactsBtn = findViewById(R.id.detailsbtn);
        contactsBtn = findViewById(R.id.contactbtn);
        callBtn = findViewById(R.id.callbtn);
        textView = findViewById(R.id.detail);

        callBtn.setEnabled(false);
        detailsContactsBtn.setEnabled(false);

        contactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Perm_CTC);
                } else {
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            }
        });

        detailsContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSet = "Contact: " + name + ", phone number: " + phoneNumber;
                textView.setText(toSet);
                callBtn.setEnabled(true);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri phoneUri = Uri.parse("tel:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(phoneUri);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_Perm);
                } else {
                    startActivity(intent);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Perm_CTC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
        {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

            String[] segments = contactUri.toString().split("/");
            String id = segments[segments.length - 1];

            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                int cid = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                contactId = cursor.getString(cid);

                if (contactId.equals(id))
                {
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    phoneNumber = cursor.getString(column);

                    int column_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    name = cursor.getString(column_name);

                    detailsContactsBtn.setEnabled(true);
                    textView.setText(contactId);

                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}