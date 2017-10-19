package com.aks.group.breakout.room;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by anujs on 10/15/2017.
 */

public class EditTextDatePicker
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static final String DATE_SERVER_PATTERN = "yyyy-MM-dd";
    private DatePickerDialog mDatePickerDialog;
    private EditText mDateEditText;
    private Context mContext;
    private long mMinDate;
    private long mMaxDate;
    Date mDate;

    public EditTextDatePicker(Context context, EditText view) {
        this(context, view, 0, 0);
    }

    public EditTextDatePicker(Context context, EditText view, long minDate, long maxDate) {
        mDateEditText = view;
        mDateEditText.setOnClickListener(this);
        mDateEditText.setFocusable(false);

        mContext = context;
        mMinDate = minDate;
        mMaxDate = maxDate;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mDate = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_SERVER_PATTERN);
        mDateEditText.setText(formatter.format(mDate));
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        mDatePickerDialog = new DatePickerDialog(mContext, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (mMinDate != 0) {
            mDatePickerDialog.getDatePicker().setMinDate(mMinDate);
        }
        if (mMaxDate != 0) {
            mDatePickerDialog.getDatePicker().setMaxDate(mMaxDate);
        }
        mDatePickerDialog.show();
    }

    public DatePickerDialog getDatePickerDialog() {
        return mDatePickerDialog;
    }

    public String getDate ()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_SERVER_PATTERN);

        return formatter.format(mDate);
    }

    public void setMinDate(long minDate) {
        mMinDate = minDate;
    }

    public void setMaxDate(long maxDate) {
        mMaxDate = maxDate;
    }
}
