package com.innoxyz.ui.customviews;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.innoxyz.R;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.data.runtime.interfaces.IDataObserver;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.ui.activities.MainActivity;
import com.innoxyz.ui.fragments.SelectPeople;
import com.innoxyz.ui.utils.DataTag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 上午9:21
 * To change this template use File | Settings | File Templates.
 */

@SuppressWarnings("unchecked")
public class datePickerDialogFragment extends DialogFragment {

    EditText dateEditText;

    public void setEditText(EditText mEditText) {
        dateEditText = mEditText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar calendar= Calendar.getInstance();

        return new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dateEditText.setText(String.format("%d-%02d-%02d", year, monthOfYear+1, dayOfMonth));

            }
        },

        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH));

    }
}

