package com.example.eduhub;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = getIntent().getStringExtra("userId");
        String studentId = resolveStudentId(userId);
        if (studentId == null || studentId.isEmpty()) {
            String sessionStudentId = com.example.eduhub.network.session.UserSessionManager
                    .getInstance(this).getStudentId();
            if (sessionStudentId != null) studentId = sessionStudentId;
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putString("studentId", studentId);
        fragmentArgs.putString("userId", userId);

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.scheduleFragment, false)
                .build();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.scheduleFragment ||
                itemId == R.id.marksFragment ||
                itemId == R.id.tasksFragment ||
                itemId == R.id.schoolFragment) {
                navController.navigate(itemId, fragmentArgs, navOptions);
                return true;
            }
            return false;
        });

        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {
                    MenuItem menuItem = binding.bottomNavigationView.getMenu().findItem(destination.getId());
                    if (menuItem != null) {
                        menuItem.setChecked(true);
                    }
                });

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBottomNavVisibility);

        if (savedInstanceState == null) {
            navController.navigate(R.id.scheduleFragment, fragmentArgs,
                    new NavOptions.Builder().setPopUpTo(R.id.scheduleFragment, true).build());
        } else {
            updateBottomNavVisibility();
        }
    }

    private void updateBottomNavVisibility() {
        if (binding == null || binding.bottomNavigationView == null) {
            return;
        }
        boolean overlay = getSupportFragmentManager().getBackStackEntryCount() > 0;
        binding.bottomNavigationView.setVisibility(overlay ? View.GONE : View.VISIBLE);
    }

    private String resolveStudentId(String userId) {
        if (userId == null) return "";
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM students WHERE user_id = ?", new String[]{userId});
        String sid = "";
        if (c.moveToFirst()) {
            sid = c.getString(0);
        }
        c.close();
        return sid;
    }
}
