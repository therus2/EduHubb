package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eduhub.R;
import com.example.eduhub.database.DBHelper;
import com.google.android.material.button.MaterialButton;

public class TeacherItemDetailFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_SUBTITLE = "subtitle";
    private static final String ARG_ASSIGNMENT_ID = "assignmentId";

    public static TeacherItemDetailFragment newInstance(String title, String subtitle) {
        TeacherItemDetailFragment fragment = new TeacherItemDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SUBTITLE, subtitle);
        fragment.setArguments(args);
        return fragment;
    }

    public static TeacherItemDetailFragment newInstance(String assignmentId) {
        TeacherItemDetailFragment fragment = new TeacherItemDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ASSIGNMENT_ID, assignmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_item_detail, container, false);

        ImageView ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_ASSIGNMENT_ID)) {
            String assignmentId = args.getString(ARG_ASSIGNMENT_ID);
            if (assignmentId != null && !assignmentId.isEmpty()) {
                showAssignmentEditor(view, assignmentId);
                return view;
            }
        }

        showGenericDetail(view);
        return view;
    }

    private void showGenericDetail(View view) {
        view.findViewById(R.id.card_generic).setVisibility(View.VISIBLE);
        view.findViewById(R.id.card_assignment_info).setVisibility(View.GONE);

        TextView tvItemTitle = view.findViewById(R.id.tv_item_title);
        TextView tvItemSubtitle = view.findViewById(R.id.tv_item_subtitle);

        if (getArguments() != null) {
            tvItemTitle.setText(getArguments().getString(ARG_TITLE));
            tvItemSubtitle.setText(getArguments().getString(ARG_SUBTITLE));
        }
    }

    private void showAssignmentEditor(View view, String assignmentId) {
        view.findViewById(R.id.card_generic).setVisibility(View.GONE);
        view.findViewById(R.id.card_assignment_info).setVisibility(View.VISIBLE);

        TextView tvTitle = view.findViewById(R.id.tv_assignment_title);
        TextView tvGroup = view.findViewById(R.id.tv_assignment_group);
        TextView tvSubject = view.findViewById(R.id.tv_assignment_subject);
        TextView tvDue = view.findViewById(R.id.tv_assignment_due);
        EditText etDescription = view.findViewById(R.id.et_assignment_description);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_assignment);

        DBHelper dbHelper = DBHelper.getInstance(requireContext());
        try (android.database.Cursor c = dbHelper.findAssignmentByIdRaw(assignmentId)) {
            if (c.moveToFirst()) {
                String title = c.getString(c.getColumnIndexOrThrow("title"));
                String description = c.getString(c.getColumnIndexOrThrow("description"));
                String groupCode = c.getString(c.getColumnIndexOrThrow("group_code"));
                String subjectName = c.getString(c.getColumnIndexOrThrow("subject_name"));
                String dueDate = c.getString(c.getColumnIndexOrThrow("due_date"));

                tvTitle.setText(title);
                tvGroup.setText("Группа: " + groupCode);
                tvSubject.setText("Предмет: " + subjectName);
                tvDue.setText("Сдать до: " + (dueDate != null ? dueDate : "—"));
                if (description != null && !description.isEmpty()) {
                    etDescription.setText(description);
                }
            }
        }

        btnSave.setOnClickListener(v -> {
            String newDescription = etDescription.getText().toString().trim();
            if (newDescription.isEmpty()) return;
            dbHelper.updateAssignmentDescription(assignmentId, newDescription);
            if (getView() != null) {
                Navigation.findNavController(getView()).navigateUp();
            }
        });
    }
}
