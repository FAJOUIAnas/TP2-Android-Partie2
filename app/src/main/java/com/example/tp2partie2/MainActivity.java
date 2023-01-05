package com.example.tp2partie2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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

    private static final int PICK_CONTACT_REQUEST = 1;

    TextView textView;
    private static final int Perm_CTC = 1;
    Button buttonCall;
    String name;
    String phoneNumber;
    private static final int PERMISSION_CALL_PHONE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button pickContactButton = findViewById(R.id.button_contact);
        textView = findViewById(R.id.text_view);
        //buttonCall.findViewById(R.id.buttonCall);
        //buttonCall.setEnabled(false);


        pickContactButton.setOnClickListener(view -> {
            Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts/people"));
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
        });

        Button detailContactButton = findViewById(R.id.detailsContact);
        detailContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
            }
        });




    }


    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                textView.setText("Opération annulée");
            }
            if (resultCode == RESULT_OK) {
                String contactUri = data.getDataString();
                // Récupération du nom et du numéro de téléphone du contact à partir de la base de données de contacts

                Uri uriContact = data.getData();
                // Interroger la base de données de contacts
                Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
                if (cursor.moveToFirst()) {
                    // Obtenir le nom du contact
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    // Obtenir l'ID du contact
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    // Interroger la base de données de numéros de téléphone
                    Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);

                    if (cursorPhone.moveToFirst()) {
                        // Obtenir le numéro de téléphone
                        phoneNumber = cursorPhone.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    cursorPhone.close();
                }
                cursor.close();

                // Mise à jour du texte de la TextView avec le nom et le numéro de téléphone du contact
                //textView.setText(contactUri);
                textView.setText(contactUri);





            }
        }
    }
    public void appelNumber() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }
    public void onClick(View view) {
        // Vérifier si la permission a été accordée
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Si la permission n'a pas été accordée, demander à l'utilisateur de l'accorder
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_PHONE_REQUEST_CODE);
        } else {
            // Si la permission a été accordée, appeler le numéro de téléphone
            appelNumber();
        }
    }




    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check the permission type using the requestCode
        if (requestCode == Perm_CTC) {
            //the array is empty if not granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "GRANTED CALL", Toast.LENGTH_SHORT).show();
            }
        }
    }
}