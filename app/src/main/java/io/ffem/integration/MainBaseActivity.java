package io.ffem.integration;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class contains just the setup for the demo app only
 * This code is not required for integration.
 * Please see MainActivity for the integration code.
 */
public class MainBaseActivity extends BaseActivity {

    TextView textResult;
    CheckBox dummyResultCheckBox;
    String selectedTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAppTheme();

        setContentView(R.layout.activity_main);

        initialize();
    }

    void clearResultDisplay() {
        textResult.setText("");
    }

    private void initialize() {
        textResult = findViewById(R.id.result);
        dummyResultCheckBox = findViewById(R.id.dummyResultCheckBox);
        dummyResultCheckBox.setOnCheckedChangeListener((compoundButton, b) -> clearResultDisplay());
        Spinner spinner = findViewById(R.id.spinner);

        ArrayList<String> tests = new ArrayList<>();
        tests.add(Constants.AVAILABLE_IRON);
        tests.add(Constants.CALCIUM_MAGNESIUM);
        tests.add(Constants.FLUORIDE);
        tests.add(Constants.INVALID_TEST);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tests);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearResultDisplay();
                selectedTest = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public interface DialogDismissListener {
        void dialogDismissed(DialogInterface dialog);
    }
}
