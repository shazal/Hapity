package com.skhalid.hapity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.skhalid.hapity.fragments.ProfileFragment;

import java.util.ArrayList;

/**
 * Created by sIrshad on 5/17/2015.
 */
public class CommentsAdapter extends ArrayAdapter<Comments> {
    private final ArrayList<Comments> comments;
    private FragmentActivity context = null;
    public CommentsAdapter( FragmentActivity context, int textViewResourceId, ArrayList<Comments> comments) {

        super(context, textViewResourceId, comments);
        this.comments = comments;
        this.context = context;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return comments.size();
    }

    @Override
    public Comments getItem(int arg0) {
        // TODO Auto-generated method stub
        return comments.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        if(arg1==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.listitem, arg2,false);
        }

        RoundedImageView roundedImageView = new RoundedImageView(context);
        ImageView imagep = (ImageView) arg1.findViewById(R.id.imageView1);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.lums);
        Bitmap resized = Bitmap.createScaledBitmap(icon, 150, 150, true);
        resized = roundedImageView.getCroppedBitmap(resized, 150);
        imagep.setImageBitmap(resized);

        imagep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.dash_container, profileFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack("Profile89++Fragment");
                context.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.commit();
            }
        });


        return arg1;
    }

}