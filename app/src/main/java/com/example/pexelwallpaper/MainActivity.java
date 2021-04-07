package com.example.pexelwallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WallpaperAdapter wallpaperAdapter;
    List<WallpaperModel> wallpaperModelList;
    int pageNumber = 1;
    DrawerLayout drawerLayout;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    String url = "https://api.pexels.com/v1/curated/?page=" + pageNumber + "&per_page=80";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_navigation);

        // getSupportActionBar().hide();

        drawerLayout = findViewById( R.id.drawer_layout );

        recyclerView = findViewById( R.id.recyclerView );
        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter( this, wallpaperModelList );

        recyclerView.setAdapter( wallpaperAdapter );
        final GridLayoutManager gridLayoutManager = new GridLayoutManager( this, 1 );
        recyclerView.setLayoutManager( gridLayoutManager );


        recyclerView.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }


            }


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    fetchWallpaper();
                }


            }
        } );

        fetchWallpaper();


    }



    ////////////////////////////////////////////////////

    public void ClickMenu(View view){

        openDrawer(drawerLayout);

    }

    static void openDrawer(DrawerLayout drawerLayout) {

        drawerLayout.openDrawer( GravityCompat.START );

    }

    public void ClickLogo(View view){

        closeDrawer(drawerLayout);

    }

    public static void closeDrawer(DrawerLayout drawerLayout) {

        if (drawerLayout.isDrawerOpen( GravityCompat.START )){
            drawerLayout.closeDrawer( GravityCompat.START );

        }

    }

    public void ClickHome(View view){

        recreate();
    }
    public void ClickDashboard(View view){

        redirectActivity(this,Dashboard.class);
    }

    public void ClickAboutUs(View view){

        redirectActivity(this,AboutUs.class);
    }

    public void ClickLogout(View view){

        logout(this);
    }

    public static void logout(final Activity activity) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( activity );
        builder.setTitle( "Logout" );
        builder.setMessage( "Are you sure you want to logout ?" );
        builder.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
                System.exit( 0 );
            }
        } );

        builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        } );
        builder.show();



    }


    public static void redirectActivity(Activity activity, Class aClass) {

        Intent intent = new Intent(activity,aClass);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        activity.startActivity( intent );

    }


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer( drawerLayout );
    }

    //////////////////////////////////////////////////////////////////////

/*

    public void ClickMenu(View view){

        openDrawer(drawerLayout);

    }

    static void openDrawer(DrawerLayout drawerLayout) {

        drawerLayout.openDrawer( GravityCompat.START );

    }

    public void ClickLogo(View view){

        closeDrawer(drawerLayout);

    }

    public static void closeDrawer(DrawerLayout drawerLayout) {

        if (drawerLayout.isDrawerOpen( GravityCompat.START )){
            drawerLayout.closeDrawer( GravityCompat.START );

        }

    }

    public void ClickHome(View view){
        redirectActivity(this,HelplineActivity.class);

    }
    public void ClickAware(View view){

        redirectActivity(this,CovidawareActivity.class);
    }



    public static void redirectActivity(Activity activity, Class aClass) {

        Intent intent = new Intent(activity,aClass);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        activity.startActivity( intent );

    }


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer( drawerLayout );
    }



*/






    //////////////////////////////////////////////////////////
    public void fetchWallpaper(){

        StringRequest request = new StringRequest( Request.Method.GET,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject( response );

                            JSONArray jsonArray = jsonObject.getJSONArray( "photos" );

                            int length = jsonArray.length();
                            for(int i=0;i<length;i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt( "id" );
                                JSONObject objectImages = object.getJSONObject( "src" );

                                String originalUrl = objectImages.getString( "original" );
                                String mediumUrl = objectImages.getString( "medium" );
                                WallpaperModel wallpaperModel = new WallpaperModel( id,originalUrl,mediumUrl );
                                wallpaperModelList.add(wallpaperModel);
                            }

                            wallpaperAdapter.notifyDataSetChanged();
                            pageNumber++;


                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("Authorization","563492ad6f917000010000011069c623c7ad4919b7f934771d8adbe9");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.nav_search) {


            AlertDialog.Builder alert = new AlertDialog.Builder( this );
            final EditText editText = new EditText( this );
            editText.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );

            alert.setMessage( "Enter Category e.g. Nature" );
            alert.setTitle( "Search Images" );
            alert.setView( editText );
            alert.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    String query = editText.getText().toString().toLowerCase();

                 //   url = "https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query="+query;
                    wallpaperModelList.clear();
                    fetchWallpaper();

                }
            } );

            alert.setNegativeButton( "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            } );

            alert.show();



        }


        return super.onOptionsItemSelected( item );





    }


    public void jiii(View view) {
    }
}
