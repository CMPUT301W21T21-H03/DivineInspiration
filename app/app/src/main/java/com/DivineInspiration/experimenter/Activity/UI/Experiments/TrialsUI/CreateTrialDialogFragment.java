package com.DivineInspiration.experimenter.Activity.UI.Experiments.TrialsUI;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.DivineInspiration.experimenter.Controller.TrialManager;
import com.DivineInspiration.experimenter.Model.Experiment;
import com.DivineInspiration.experimenter.Model.Trial.BinomialTrial;
import com.DivineInspiration.experimenter.Model.Trial.CountTrial;
import com.DivineInspiration.experimenter.Model.Trial.MeasurementTrial;
import com.DivineInspiration.experimenter.Model.Trial.NonNegativeTrial;
import com.DivineInspiration.experimenter.Model.Trial.Trial;
import com.DivineInspiration.experimenter.R;

import org.osmdroid.util.GeoPoint;

import static android.content.Context.LOCATION_SERVICE;

/**
 * This class deals with the UI of the trial data being recorded
 */
public class CreateTrialDialogFragment extends DialogFragment {

    private final OnTrialCreatedListener callback;
    String trialTypeCheck;              // The type of the trial we are dealing with
    CheckBox geoTrial;
    EditText measurementTextBox;        // View for the measurement trial
    TextView countNNTrial;              // View for the non-negative trial
    Button negativeCountNNButton;       // View for the non-negative trial
    Button positiveCountNNButton;       // View for the non-negative trial
    TextView failNumTrial;              // View for the binomial trial
    TextView trueNumTrial;              // View for the binomial trial
    Button passButton;                  // View for the binomial trial
    Button failButton;                  // View for the binomial trial
    Button decrementFailNumButton;      // View for the binomial trial
    Button decrementPassNumButton;      // View for the binomial trial
    int failNum = 0;                    // Count no. of fails for the binomial trial
    int passNum = 0;                    // Count no. of fails for the binomial trial
    int count = 0;                      // Count for both non-negative and count trials
    double myLat = 0;
    double myLong = 0;
    GeoPoint trialLocation = null;

    /**
     * When trial data is retrieved, it is passed along as a parameter by the interface method.
     */
    public interface OnTrialCreatedListener {
        void onTrialAdded(Trial trial);
    }

    /**
     * Constructor.
     */
    public CreateTrialDialogFragment(OnTrialCreatedListener callback) {
        super();
        this.callback = callback;
    }

    /**
     * Runs when the dialog is first created.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.create_trial_dialog_fragment, null);

        Bundle args = getArguments();
        Experiment exp = (Experiment) args.getSerializable("experiment");       // Get the experiment the trial is for
        init(view);
        trialTypeCheck = exp.getTrialType();        // Get the trial type
        visibility(trialTypeCheck);
        gettingLocation();

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.dialogColor)
                .setView(view)
                .setMessage("Create Trial")
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the appropriate method (when "OK" button of dialog is clicked) depending on type of the trial
                switch (trialTypeCheck) {
                    case "Binomial trial":
                        binomialTrialDialog(args, exp);
                        break;
                    case "Count trial":
                        countTrialDialog(args, exp);
                        break;
                    case "Non-Negative trial":
                        nonNegativeTrialDialog(args, exp);
                        break;
                    case "Measurement trial":
                        String measure = measurementTextBox.getText().toString();
                        measurementTrialDialog(args, exp, measure);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * This method deals with the information retrieval and Trial object creation for when the type of the trial is binomial.
     * @param: args:Bundle
     * @param: exp:Experiment (the experiment this trial is being performed for).
     */
    public void binomialTrialDialog(Bundle args, Experiment exp) {
        trialLocation = new GeoPoint(myLong,myLat);
        // We create a separate Trial object for each 'Pass'
        for (int i = 0; i < passNum; i++) {
            BinomialTrial binomialTrial = new BinomialTrial(
                    args.getString("experimenterID"),
                    args.getString("experimenterName"),
                    exp.getExperimentID(),
                    true,
                    trialLocation
            );
            TrialManager.getInstance().addTrial(binomialTrial, trials -> {
            });
            callback.onTrialAdded(binomialTrial);
        }
        // We create a separate Trial object for each 'Fail'
        for (int i = 0; i < failNum; i++) {
            BinomialTrial binomialTrial = new BinomialTrial(
                    args.getString("experimenterID"),
                    args.getString("experimenterName"),
                    exp.getExperimentID(),
                    false,
                    trialLocation
            );
            TrialManager.getInstance().addTrial(binomialTrial, trials -> {
            });
            callback.onTrialAdded(binomialTrial);
        }
    }

    /**
     * This method deals with the information retrieval and Trial object creation for when the type of the trial is count.
     * @param: args:Bundle
     * @param: exp:Experiment (the experiment this trial is being performed for).
     */
    public void countTrialDialog(Bundle args, Experiment exp) {
        trialLocation = new GeoPoint(myLat, myLong);
        CountTrial countTrial = new CountTrial(
                args.getString("experimenterID"),
                args.getString("experimenterName"),
                exp.getExperimentID(),
                count,
                trialLocation
        );

        TrialManager.getInstance().addTrial(countTrial, trials -> {
        });
        callback.onTrialAdded(countTrial);
    }

    /**
     * This method deals with the information retrieval and Trial object creation for when the type of the trial is non-negative.
     * @param: args:Bundle
     * @param: exp:Experiment (the experiment this trial is being performed for).
     */
    public void nonNegativeTrialDialog(Bundle args, Experiment exp) {
        trialLocation = new GeoPoint(myLat, myLong);
        NonNegativeTrial nonNegativeTrial = new NonNegativeTrial(
                args.getString("experimenterID"),
                args.getString("experimenterName"),
                exp.getExperimentID(),
                count,
                trialLocation
        );
        TrialManager.getInstance().addTrial(nonNegativeTrial, trials -> {
        });
        callback.onTrialAdded(nonNegativeTrial);
    }

    /**
     * This method deals with the information retrieval and Trial object creation for when the type of the trial is measurement.
     * @param: args:Bundle
     * @param: exp:Experiment (the experiment this trial is being performed for).
     */
    public void measurementTrialDialog(Bundle args, Experiment exp, String measure) {
        trialLocation = new GeoPoint(myLat, myLong);
        double measureValue = Double.valueOf(measure);
        MeasurementTrial measurementTrial = new MeasurementTrial(
                args.getString("experimenterID"),
                args.getString("experimenterName"),
                exp.getExperimentID(),
                measureValue,
                trialLocation
        );
        TrialManager.getInstance().addTrial(measurementTrial, trials -> {
        });
        callback.onTrialAdded(measurementTrial);
    }

    /**
     * Initialize the View instance variables.
     */
    public void init(View view) {
        measurementTextBox = view.findViewById(R.id.editMeasurementValue);
        countNNTrial = view.findViewById(R.id.value_trial);
        failButton = view.findViewById(R.id.binomial_fail_button);
        passButton = view.findViewById(R.id.binomial_pass_button);
        negativeCountNNButton = view.findViewById(R.id.decrease_trial_value);
        positiveCountNNButton = view.findViewById(R.id.increase_trial_value);
        failNumTrial = view.findViewById(R.id.binomial_fail_textView);
        trueNumTrial = view.findViewById(R.id.binomial_pass_textView);
        decrementFailNumButton = view.findViewById(R.id.binomial_fail_decrement);
        decrementPassNumButton = view.findViewById(R.id.binomial_pass_decrement);
    }

    public void gettingLocation() {
        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                myLong = location.getLongitude();
                myLat = location.getLatitude();

            }
        };

        LocationManager mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },301);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    /**
     *  This method deals with giving visibility to a certain Views depending on the trial.
     * @param: trialTypeCheck:String
     */
    public void visibility(String trialTypeCheck){
        measurementTextBox.setVisibility(View.GONE);
        countNNTrial.setVisibility(View.GONE);
        failButton.setVisibility(View.GONE);
        passButton.setVisibility(View.GONE);
        negativeCountNNButton.setVisibility(View.GONE);
        positiveCountNNButton.setVisibility(View.GONE);
        failNumTrial.setVisibility(View.GONE);
        trueNumTrial.setVisibility(View.GONE);
        decrementFailNumButton.setVisibility(View.GONE);
        decrementPassNumButton.setVisibility(View.GONE);
        switch (trialTypeCheck){
            case "Binomial trial":
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
                failNumTrial.setVisibility(View.VISIBLE);
                trueNumTrial.setVisibility(View.VISIBLE);
                decrementFailNumButton.setVisibility(View.VISIBLE);
                decrementPassNumButton.setVisibility(View.VISIBLE);
                BinomialTrialButtonController();

                break;
            case "Count trial":
                negativeCountNNButton.setVisibility(View.VISIBLE);
                positiveCountNNButton.setVisibility(View.VISIBLE);
                countNNTrial.setVisibility(View.VISIBLE);
                CountTrialButtonController();
                break;
            case "Non-Negative trial":
                negativeCountNNButton.setVisibility(View.VISIBLE);
                positiveCountNNButton.setVisibility(View.VISIBLE);
                countNNTrial.setVisibility(View.VISIBLE);
                NNTrialButtonController();
                break;
            case "Measurement trial":
                measurementTextBox.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     *  This method deals with the buttons control when the trial type is binomial.
     */
    public void BinomialTrialButtonController() {
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passNum = passNum + 1;
                trueNumTrial.setText(String.valueOf(passNum));
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failNum = failNum + 1;
                Log.d("wtf", String.valueOf(failNum));
                failNumTrial.setText( String.valueOf(failNum));
            }
        });
        decrementFailNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failNum = failNum - 1;
                failNumTrial.setText( String.valueOf(failNum));
            }
        });

        decrementPassNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passNum = passNum - 1;
                trueNumTrial.setText(String.valueOf(passNum));
            }
        });
    }

    /**
     *  This method deals with the buttons control when the trial type is count.
     */
    public void CountTrialButtonController(){
        positiveCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count + 1;
                countNNTrial.setText(String.valueOf(count));
            }
        });
        negativeCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count - 1;
                countNNTrial.setText(String.valueOf(count));
            }
        });
    }

    /**
     *  This method deals with the buttons control when the trial type is non-negative.
     */
    public void NNTrialButtonController(){
        positiveCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count + 1;
                countNNTrial.setText(String.valueOf(count));
            }
        });
        negativeCountNNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count > 0){
                    count = count - 1;
                    countNNTrial.setText(String.valueOf(count));
                }else{

                }


            }
        });
    }
}