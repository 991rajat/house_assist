package android.example.house_assist;


import android.content.Context;
import android.content.Intent;
import android.example.house_assist.Models.CustomerUser_Data;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_main extends Fragment {

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    private static final String TAG = "TAG";
    private Context contextid;
    private ViewPager mViewPager;
    private CustomPagerAdapter mCustomPagerAdapter;
    private int[] mResources;
    private LinearLayout electrician,plumber,carpenter;
    private Bundle bundle;
    private Double userLat,userLng;
    private CustomerUser_Data user_data;
    public Fragment_main() {
        // Required empty public constructor
    }

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contextid = inflater.getContext();
        Log.d(TAG,"fmain");
        bundle = getArguments();
        userLat = bundle.getDouble("userLat");
        userLng = bundle.getDouble("userLng");
        Log.d(TAG," Lng"+userLat+" Lng"+userLng);
        return inflater.inflate(R.layout.fragment_fragment_main, container, false);
    }

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCustomPagerAdapter = new CustomPagerAdapter(contextid);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mResources= new int[]{R.drawable.offsale, R.drawable.offsale, R.drawable.offsale};
        mViewPager.setAdapter(mCustomPagerAdapter);
        electrician = view.findViewById(R.id.electrician);
        plumber = view.findViewById(R.id.plumber);
        carpenter = view.findViewById(R.id.carpenter);

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Electrician
        electrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(contextid,Activity_WhichServiceProviders.class);
                intent.putExtra("userLat",userLat);
                intent.putExtra("userLng",userLng);
                intent.putExtra("type","Electrician");
                startActivity(intent);
            }
        });
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        //Carpenter
        carpenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(contextid,Activity_WhichServiceProviders.class);
                intent.putExtra("userLat",userLat);
                intent.putExtra("userLng",userLng);
                intent.putExtra("type","Carpenter");
                startActivity(intent);
            }
        });
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
        plumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(contextid,Activity_WhichServiceProviders.class);
                intent.putExtra("userLat",userLat);
                intent.putExtra("userLng",userLng);
                intent.putExtra("type","Plumber");
                startActivity(intent);
            }
        });

    }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    //Pagers
    public class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
}
