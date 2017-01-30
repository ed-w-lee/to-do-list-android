package edwardlee.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{


    public DatePickerFragment() {
        // Required empty public constructor
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get current date as default
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Return new DatePickerDialog
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ((TextView) getActivity().findViewById(R.id.due_day)).setText("" + dayOfMonth);
        ((TextView) getActivity().findViewById(R.id.due_month)).setText("" + month);
        ((TextView) getActivity().findViewById(R.id.due_year)).setText("" + year);
    }
}
