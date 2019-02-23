//package com.neelk.fbla2018;
//
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.design.internal.BottomNavigationItemView;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.MenuItem;
//
//public class NavigationBarManager extends AppCompatActivity {
//
//    private Fragment fragment;
//    private BottomNavigationView mBottomNavigationView;
//    private FragmentTransaction initialTransaction;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_navigation_bar_manager);
//
//        initialTransaction = getSupportFragmentManager().beginTransaction();
//        initialTransaction.replace(R.id.navigation_manager_layout, Home.newInstance());
//        initialTransaction.addToBackStack(null);
//        initialTransaction.commit();
//
//        mBottomNavigationView = findViewById(R.id.navigation);
//        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                selectFragment(item);
//                return true;
//            }
//        });
//
//
//    }
//
//    public void selectFragment(MenuItem item) {
//        fragment = null;
//        FragmentTransaction ft;
//        switch (item.getItemId()) {
//
//            case R.id.menu_home:
//                fragment = Home.newInstance();
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.navigation_manager_layout, fragment);
//                ft.addToBackStack(null);
//                ft.commit();
//                break;
//
//
//            case R.id.menu_quiz:
//                fragment = Quiz.newInstance();
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.navigation_manager_layout, fragment);
//                ft.addToBackStack(null);
//                ft.commit();
//                break;
//
//
//            case R.id.menu_settings:
//                fragment = Settings.newInstance();
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.navigation_manager_layout, fragment);
//                ft.addToBackStack(null);
//                ft.commit();
//                break;
//
//        }
//    }
//
//
//
//}
