package com.neelk.fbla2018;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class AddQuestion extends AppCompatActivity {

    private Spinner categorySpinner;
    private Spinner correctAnswer;
    private EditText option1EditText;
    private EditText option2EditText;
    private EditText option3EditText;
    private EditText option4EditText;
    private EditText questionEditText;
    private Button submitButton;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        categorySpinner = findViewById(R.id.categorySpinner);
        correctAnswer = findViewById(R.id.correctAnswerSpinner);
        option1EditText = findViewById(R.id.option1EditText);
        option2EditText = findViewById(R.id.option2EditText);
        option3EditText = findViewById(R.id.option3EditText);
        option4EditText = findViewById(R.id.option4EditText);
        questionEditText = findViewById(R.id.questionEditText);
        submitButton = findViewById(R.id.addQuestionSubmitButton);
        submitButton.setOnClickListener(onSubmit);

        ArrayAdapter<CharSequence> categoryArrayAdapter = ArrayAdapter.createFromResource(AddQuestion.this, R.array.categorySpinnerArray, android.R.layout.simple_spinner_item);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryArrayAdapter);


        ArrayAdapter<CharSequence> correctAnswerAdapter = ArrayAdapter.createFromResource(AddQuestion.this, R.array.questionsOptionsArray, android.R.layout.simple_spinner_item);
        correctAnswerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        correctAnswer.setAdapter(correctAnswerAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (TextUtils.isEmpty(questionEditText.getText()) || TextUtils.isEmpty(option1EditText.getText()) || TextUtils.isEmpty(option2EditText.getText()) || TextUtils.isEmpty(option3EditText.getText()) || TextUtils.isEmpty(option4EditText.getText())) {
                Toasty.error(AddQuestion.this, "Please fill in all fields!", Toast.LENGTH_SHORT, true).show();
                return;
            }

            String category = categorySpinner.getSelectedItem().toString();
            String correctAnswerString = correctAnswer.getSelectedItem().toString();

            HashMap <String, Object> questionInfo = new HashMap<>();
            questionInfo.put("a", option1EditText.getText().toString().trim());
            questionInfo.put("b", option2EditText.getText().toString().trim());
            questionInfo.put("c", option3EditText.getText().toString().trim());
            questionInfo.put("d", option4EditText.getText().toString().trim());
            questionInfo.put("Answer", correctAnswerString);
            questionInfo.put("Question", questionEditText.getText().toString().trim());


            databaseReference.child("Questions").child(category).child("User Question " + UserInfo.getEmail() + (int) (Math.random() * 1000)).setValue(questionInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(AddQuestion.this, "Added Question!", Toast.LENGTH_SHORT, true).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(AddQuestion.this, "Error adding Question!", Toast.LENGTH_SHORT, true).show();

                        }
                    });

        }
    };
}
