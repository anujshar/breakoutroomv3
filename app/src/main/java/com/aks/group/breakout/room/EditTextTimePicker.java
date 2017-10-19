package com.aks.group.breakout.room;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by anujs on 10/15/2017.
 */

public class EditTextTimePicker
        implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    private TimePickerDialog mTimePickerDialog;
    private EditText mTimeEditText;
    private Context mContext;
    private Date mTime;
    public static final String DATE_SERVER_PATTERN = "kk:mm";

    public EditTextTimePicker(Context context, EditText view) {
        mTimeEditText = view;
        mTimeEditText.setOnClickListener(this);
        mTimeEditText.setFocusable(false);

        mContext = context;
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        mTime = cal.getTime();
        mTimeEditText.setText(hourOfDay + ":" + minute);
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        mTimePickerDialog = new TimePickerDialog(mContext, this, calendar.get(Calendar.HOUR_OF_DAY),
                 calendar.get(Calendar.MINUTE), true);
        mTimePickerDialog.show();
    }

    public TimePickerDialog getTimePickerDialog() {
        return mTimePickerDialog;
    }

    public String getTime()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_SERVER_PATTERN);
        return formatter.format(mTime);
    }

}
