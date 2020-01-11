package com.example.librarydatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_SUM = "summary";
    private static final String KEY_COPY = "copies";
    private static final String KEY_PRICE= "price";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dbRef = db.collection("BookCollections").document("First document");
    private CollectionReference bookCollection = db.collection("BookCollections");

    private EditText titleEditText, authorEditText, sumEditText, copiesEditText, priceEditText;
    private TextView infoTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.titleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        sumEditText = findViewById(R.id.summaryEditText);
        copiesEditText = findViewById(R.id.copiesEditText);
        priceEditText = findViewById(R.id.priceEditText);
        infoTextView = findViewById(R.id.infoTextView);

    }//end onCreate

    @Override
    protected void onStart() {
        super.onStart();

        bookCollection.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e == null){
                    String data = "";

                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Book book = documentSnapshot.toObject(Book.class);
                        book.setDocumentId(documentSnapshot.getId());
                        String title= book.getTitle();
                        String author = book.getAuthor();
                        String summary = book.getSummary();
                        int copies = book.getCopies();
                        double price = book.getPrice();
                        String bookId = book.getDocumentId();

                        data +=  "id: " + bookId + "\nTitle: " + title + "\nAuthor: " + author +
                                "\nSummary: " + summary + "\nCopies: " +copies +
                                "\nPrice: $" + price + "\n\n";

                    }

                    infoTextView.setText(data);

                }//end if

            }
        });

    }//end onStart

    public void saveInformation(View v){
        String title = titleEditText.getText().toString();
        String author = authorEditText.getText().toString();
        String summary = sumEditText.getText().toString();
        int copies = Integer.parseInt(copiesEditText.getText().toString());
        double price = Double.parseDouble(priceEditText.getText().toString());

        final Book newBook = new Book(title,author,summary,copies,price);
        //for multiple documents
        bookCollection.add(newBook)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //listBooks.add(newBook);
                        Toast.makeText(MainActivity.this, "Info saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });
    }//end saveInformation

    public void loadInformation(View v){

        bookCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String data = "";
                        for(QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots){
                            Book book = documentSnapshots.toObject(Book.class);
                            book.setDocumentId(documentSnapshots.getId());
                            String title= book.getTitle();
                            String author = book.getAuthor();
                            String summary = book.getSummary();
                            int copies = book.getCopies();
                            double price = book.getPrice();
                            String bookId = book.getDocumentId();

                            data +=  "id: " + bookId + "\nTitle: " + title + "\nAuthor: " + author +
                                    "\nSummary: " + summary + "\nCopies: " +copies +
                                    "\nPrice: $" + price + "\n\n";

                        }

                        infoTextView.setText(data);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });

    }//end loadInformation


    public void updateInfo(View v){

        try {

            int newCopy = Integer.parseInt(copiesEditText.getText().toString());
            double newPrice = Double.parseDouble(priceEditText.getText().toString());

            dbRef.update(KEY_COPY,newCopy);
            dbRef.update(KEY_PRICE,newPrice);

        }catch(Exception e){
            Toast.makeText(this, "Error!Give a new copy and price", Toast.LENGTH_SHORT).show();
            Log.d(TAG,e.toString());
        }

    }//end updateInfo

    public void deleteInformation(View v){


    }//end deleteInformation




}//end class
