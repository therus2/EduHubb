package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eduhub.R;
import com.example.eduhub.network.session.UserSessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TeacherMainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_main);

        String teacherId = UserSessionManager.getInstance(this).getTeacherId();
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = getIntent().getStringExtra("teacherId");
        }
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = getIntent().getStringExtra("userId");
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.teacherProfileFragment, false)
                .build();

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.teacherScheduleFragment ||
                itemId == R.id.teacherJournalFragment ||
                itemId == R.id.teacherTasksFragment ||
                itemId == R.id.teacherProfileFragment) {
                navController.navigate(itemId, null, navOptions);
                return true;
            }
            return false;
        });

        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {
                    MenuItem menuItem = bottomNav.getMenu().findItem(destination.getId());
                    if (menuItem != null) {
                        menuItem.setChecked(true);
                    }
                });

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBottomNavVisibility);

        if (savedInstanceState != null) {
            updateBottomNavVisibility();
        }
    }

    private void updateBottomNavVisibility() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) {
            return;
        }
        boolean overlay = getSupportFragmentManager().getBackStackEntryCount() > 0;
        bottomNav.setVisibility(overlay ? View.GONE : View.VISIBLE);
    }
}
