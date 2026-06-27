package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eduhub.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class GradePickerBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_DATE = "date";
    private static final String ARG_STUDENT_INDEX = "studentIndex";
    private static final String ARG_DATE_INDEX = "dateIndex";
    private static final String ARG_HAS_GRADE = "hasGrade";

    private GradePickerListener listener;
    private int studentIndex;
    private int dateIndex;
    private String selectedType = "CURRENT";

    public interface GradePickerListener {
        void onGradeSelected(int studentIndex, int dateIndex, String value, String gradeType);
        void onGradeDeleted(int studentIndex, int dateIndex);
    }

    public static GradePickerBottomSheet newInstance(String name, String date,
                                                      int studentIndex, int dateIndex,
                                                      boolean hasGrade) {
        GradePickerBottomSheet sheet = new GradePickerBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_DATE, date);
        args.putInt(ARG_STUDENT_INDEX, studentIndex);
        args.putInt(ARG_DATE_INDEX, dateIndex);
        args.putBoolean(ARG_HAS_GRADE, hasGrade);
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(GradePickerListener listener) {
        this.listener = listener;
    }

    @Override
    public int getTheme() {
        return R.style.Theme_EduHub_BottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_grade_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = requireArguments();
        String name = args.getString(ARG_NAME);
        String date = args.getString(ARG_DATE);
        studentIndex = args.getInt(ARG_STUDENT_INDEX);
        dateIndex = args.getInt(ARG_DATE_INDEX);
        boolean hasGrade = args.getBoolean(ARG_HAS_GRADE);

        TextView tvTitle = view.findViewById(R.id.tv_picker_title);
        tvTitle.setText(name + " \u2014 " + date);

        TextView btnTypeCurrent = view.findViewById(R.id.btn_type_current);
        TextView btnTypeControl = view.findViewById(R.id.btn_type_control);
        TextView btnTypeHomework = view.findViewById(R.id.btn_type_homework);
        TextView[] typeButtons = {btnTypeCurrent, btnTypeControl, btnTypeHomework};
        String[] typeValues = {"CURRENT", "CONTROL", "HOMEWORK"};
        View.OnClickListener typeClick = v -> {
            for (int i = 0; i < typeButtons.length; i++) {
                boolean active = typeButtons[i] == v;
                if (active) selectedType = typeValues[i];
                typeButtons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        requireContext().getColor(active ? R.color.marks_purple_main : R.color.marks_toggle_bg)));
                typeButtons[i].setTextColor(requireContext().getColor(
                        active ? R.color.white : R.color.marks_purple_main));
            }
        };
        btnTypeCurrent.setOnClickListener(typeClick);
        btnTypeControl.setOnClickListener(typeClick);
        btnTypeHomework.setOnClickListener(typeClick);

        View.OnClickListener gradeClick = v -> {
            String value = "";
            if (v.getId() == R.id.btn_grade_2) value = "2";
            else if (v.getId() == R.id.btn_grade_3) value = "3";
            else if (v.getId() == R.id.btn_grade_4) value = "4";
            else if (v.getId() == R.id.btn_grade_5) value = "5";
            else if (v.getId() == R.id.btn_grade_n) value = "\u041D";
            else if (v.getId() == R.id.btn_grade_b) value = "\u0411";
            else if (v.getId() == R.id.btn_grade_delete) {
                if (listener != null) listener.onGradeDeleted(studentIndex, dateIndex);
                dismiss();
                return;
            }

            if (!value.isEmpty() && listener != null) {
                listener.onGradeSelected(studentIndex, dateIndex, value, selectedType);
            }
            dismiss();
        };

        view.findViewById(R.id.btn_grade_2).setOnClickListener(gradeClick);
        view.findViewById(R.id.btn_grade_3).setOnClickListener(gradeClick);
        view.findViewById(R.id.btn_grade_4).setOnClickListener(gradeClick);
        view.findViewById(R.id.btn_grade_5).setOnClickListener(gradeClick);
        view.findViewById(R.id.btn_grade_n).setOnClickListener(gradeClick);
        view.findViewById(R.id.btn_grade_b).setOnClickListener(gradeClick);
        view.findViewById(R.id.btn_grade_delete).setOnClickListener(gradeClick);
    }
}
