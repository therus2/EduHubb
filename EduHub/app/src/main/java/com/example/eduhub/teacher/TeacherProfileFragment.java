package com.example.eduhub.teacher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eduhub.R;
import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.teacher.models.NextClassItem;
import com.example.eduhub.teacher.models.TaskToCheckItem;
import com.example.eduhub.teacher.repository.TeacherProfileRepository;
import com.example.eduhub.ui.SyncRefreshFragment;

import androidx.navigation.Navigation;
import com.example.eduhub.utils.AvatarManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class TeacherProfileFragment extends SyncRefreshFragment {

    private String teacherId;
    private DBHelper dbHelper;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        Bundle args = getArguments();
        teacherId = args != null ? args.getString("teacherId") : null;
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }
        dbHelper = DBHelper.getInstance(requireContext());

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        View profileScroll = view.findViewById(R.id.profile_scroll);
        if (swipe != null && profileScroll != null) {
            bindRefreshScroll(swipe, profileScroll);
        }

        reloadContent();
    }

    @Override
    protected void reloadAfterSync() {
        reloadContent();
    }

    private void reloadContent() {
        if (rootView == null) return;

        View view = rootView;
        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        TextView tvCurrentDate = view.findViewById(R.id.tv_current_date);
        ImageView ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        TextView tvAvatarInitials = view.findViewById(R.id.tv_avatar_initials);

        String teacherName = "Учитель";
        String lastName = "";
        String avatarPath = null;

        String userId = UserSessionManager.getInstance(requireContext()).getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = teacherId;
        }

        if (userId != null && !userId.isEmpty()) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT first_name, last_name, avatar_url FROM users WHERE id = ?", new String[]{userId});
            if (c.moveToFirst()) {
                teacherName = c.getString(c.getColumnIndexOrThrow("first_name"));
                lastName = c.getString(c.getColumnIndexOrThrow("last_name"));
                String avatarUrl = c.getString(c.getColumnIndexOrThrow("avatar_url"));
                if (avatarUrl != null && !avatarUrl.isEmpty() && !"default_avatar".equals(avatarUrl)) {
                    avatarPath = avatarUrl;
                }
            }
            c.close();

            ivProfileIcon.setVisibility(View.GONE);
            tvAvatarInitials.setVisibility(View.VISIBLE);
            boolean hasPhoto = avatarPath != null
                    && (AvatarManager.hasAvatar(requireContext(), userId)
                    || new java.io.File(avatarPath).exists());
            if (hasPhoto) {
                tvAvatarInitials.setVisibility(View.GONE);
                ivProfileIcon.setVisibility(View.VISIBLE);
                AvatarManager.loadAvatarInto(requireContext(), ivProfileIcon, avatarPath);
            } else {
                AvatarManager.clearAvatarView(requireContext(), ivProfileIcon);
            }
        }

        String initials = "У";
        if (lastName != null && !lastName.isEmpty() && teacherName != null && !teacherName.isEmpty()) {
            initials = String.valueOf(lastName.charAt(0)) + teacherName.charAt(0);
        }
        tvAvatarInitials.setText(initials);

        tvGreeting.setText("Добрый день, " + teacherName);

        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("ru"));
        String formattedDate = today.format(DateTimeFormatter.ofPattern("d MMMM", new Locale("ru")));
        tvCurrentDate.setText(dayOfWeek + ", " + formattedDate);

        RecyclerView rvNextClasses = view.findViewById(R.id.rv_next_classes);
        rvNextClasses.setLayoutManager(new LinearLayoutManager(getContext()));
        TeacherProfileRepository profileRepo = new TeacherProfileRepository(requireContext(), teacherId);
        rvNextClasses.setAdapter(new NextClassesAdapter(profileRepo.getNextClasses()));

        RecyclerView rvTasksToCheck = view.findViewById(R.id.rv_tasks_to_check);
        rvTasksToCheck.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTasksToCheck.setAdapter(new TasksToCheckAdapter(profileRepo.getTasksToCheck()));
    }

    private class NextClassesAdapter extends RecyclerView.Adapter<NextClassesAdapter.ViewHolder> {
        private final List<NextClassItem> data;

        NextClassesAdapter(List<NextClassItem> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_next_class, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NextClassItem item = data.get(position);
            holder.tvClassSubject.setText(item.getClassSubject());
            holder.tvTimeRoom.setText(item.getTimeRoom());
            holder.tvStatusBadge.setText(item.getStatusBadge());

            holder.itemView.setOnClickListener(v -> {
                if (item.getLessonId() != null && !item.getLessonId().isEmpty()) {
                    Bundle args = new Bundle();
                    args.putString("lessonId", item.getLessonId());
                    args.putString("date", item.getDate());
                    args.putString("teacherId", teacherId);
                    Navigation.findNavController(v)
                            .navigate(R.id.teacherLessonDetailFragment, args);
                } else {
                    Bundle args = new Bundle();
                    args.putString("title", item.getClassSubject());
                    args.putString("subtitle", item.getTimeRoom() + " \u2022 " + item.getStatusBadge());
                    Navigation.findNavController(v)
                            .navigate(R.id.teacherItemDetailFragment, args);
                }
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvClassSubject, tvTimeRoom, tvStatusBadge;
            ViewHolder(View v) {
                super(v);
                tvClassSubject = v.findViewById(R.id.tv_class_subject);
                tvTimeRoom     = v.findViewById(R.id.tv_time_room);
                tvStatusBadge  = v.findViewById(R.id.tv_status_badge);
            }
        }
    }

    private class TasksToCheckAdapter extends RecyclerView.Adapter<TasksToCheckAdapter.ViewHolder> {
        private final List<TaskToCheckItem> data;

        TasksToCheckAdapter(List<TaskToCheckItem> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_tasks_check, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TaskToCheckItem item = data.get(position);
            holder.tvTaskTitle.setText(item.getTitle());
            holder.tvTaskSubtitle.setText(item.getSubtitle());
            if (holder.pbProgress != null) {
                holder.pbProgress.setProgress(item.getProgress());
            }

            holder.itemView.setOnClickListener(v -> {
                if (item.getId() != null && !item.getId().isEmpty() && item.getGroupId() != null) {
                    Bundle args = new Bundle();
                    args.putString("assignmentId", item.getId());
                    args.putString("groupId", item.getGroupId());
                    args.putString("teacherId", teacherId);
                    Navigation.findNavController(v)
                            .navigate(R.id.teacherAssignmentDetailFragment, args);
                } else if (item.getId() != null && !item.getId().isEmpty()) {
                    Bundle args = new Bundle();
                    args.putString("assignmentId", item.getId());
                    Navigation.findNavController(v)
                            .navigate(R.id.teacherItemDetailFragment, args);
                } else {
                    Bundle args = new Bundle();
                    args.putString("title", item.getTitle());
                    args.putString("subtitle", item.getSubtitle());
                    Navigation.findNavController(v)
                            .navigate(R.id.teacherItemDetailFragment, args);
                }
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTaskTitle, tvTaskSubtitle;
            android.widget.ProgressBar pbProgress;
            ViewHolder(View v) {
                super(v);
                tvTaskTitle    = v.findViewById(R.id.tv_task_title);
                tvTaskSubtitle = v.findViewById(R.id.tv_task_subtitle);
                pbProgress     = v.findViewById(R.id.pb_task_progress);
            }
        }
    }
}
