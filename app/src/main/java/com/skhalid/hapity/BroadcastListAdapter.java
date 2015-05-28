package com.skhalid.hapity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.skhalid.hapity.fragments.BroadcastFragment;
import com.skhalid.hapity.fragments.LoginFragment;
import com.skhalid.hapity.fragments.ProfileFragment;


public class BroadcastListAdapter extends BaseAdapter implements OnClickListener {

	  private FragmentActivity activity;
      private ArrayList<BroadcastModal> data;
      private static LayoutInflater inflater=null;
      public Resources res;
      BroadcastModal tempValues=null;
      int i=0;
      public BroadcastListAdapter(FragmentActivity a, ArrayList<BroadcastModal> d, Resources r) {
    	  this.activity = a;
    	  this.data = d;
    	  this.res = r;
    	  inflater = ( LayoutInflater )activity.
                  getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}
	@Override
	public int getCount() {
		 if(data.size()<=0)
             return 0;
         return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	 public static class ViewHolder{
         
         public TextView name;
         public TextView like;
         public TextView dislike;
         public TextView comment;
         public TextView share;
         public TextView type;
         public ImageView broadcastImage;
        // public ImageView dreamImage;
         
  
     }
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View vi = convertView;
         ViewHolder holder;
        if(data.size() >0){
         if(convertView==null) {
             vi = inflater.inflate(R.layout.broadcast_list_item, null);
             }




             holder = new ViewHolder();
             holder.name = (TextView) vi.findViewById(R.id.nameText);
             holder.name.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     ProfileFragment profileFragment = new ProfileFragment();
                     FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                     transaction.replace(R.id.dash_container, profileFragment);
                     transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                     transaction.addToBackStack("Profile89++Fragment");
//                     activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                     transaction.commit();

                 }
             });

             holder.like = (TextView) vi.findViewById(R.id.likeText);
            holder.like.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DashboardActivity.showCustomProgress(activity, "", false);
                    String url = "http://testing.egenienext.com/project/hapity/webservice/like_broadcast?broadcast_id="+tempValues.id+"&user_id=" + DashboardActivity.hapityPref.getInt("userid",0);
                    GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                            Request.Method.GET,
                            url,
                            Jsonexample.class,
                            null,
                            null,
                            createMyReqSuccessListener(),
                            createMyReqErrorListener());


                    VolleySingleton.getInstance(activity).addToRequestQueue(myReq);
                }
            });
             holder.dislike=(TextView)vi.findViewById(R.id.dislikeText);
            holder.dislike.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DashboardActivity.showCustomProgress(activity, "", false);
                    String url = "http://testing.egenienext.com/project/hapity/webservice/dislike_broadcast?broadcast_id="+tempValues.id+"&user_id=" + DashboardActivity.hapityPref.getInt("userid",0);
                    GsonRequest<Jsonexample> myReq = new GsonRequest<Jsonexample>(
                            Request.Method.GET,
                            url,
                            Jsonexample.class,
                            null,
                            null,
                            createMyReqSuccessListener(),
                            createMyReqErrorListener());


                    VolleySingleton.getInstance(activity).addToRequestQueue(myReq);
                }
            });
//             holder.userImage=(ImageView)vi.findViewById(R.id.userPhoto);
             holder.comment = (TextView) vi.findViewById(R.id.commentText);
             holder.share = (TextView) vi.findViewById(R.id.shareText);
            holder.broadcastImage = (ImageView) vi.findViewById(R.id.broadcastItem);
             tempValues=null;
             tempValues = (BroadcastModal) data.get( position );
          
              holder.name.setText(tempValues.getUserName() );
              holder.like.setText( tempValues.getLike() );
              holder.dislike.setText(tempValues.getDislike() );
              holder.comment.setText(tempValues.getComment());
              holder.share.setText(tempValues.getShare());

            final int pos = position;
            holder.broadcastImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    performAction(pos);
                }
            });


//               holder.userImage.setImageResource(
//                       res.getIdentifier(
//                       "com.example.dreamtweats:drawable/"+tempValues.getUserPhoto()
//                       ,null,null));
             vi.setTag( holder );
         }

         return vi;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

    public  void performAction(int mPosition2) {

        BroadcastFragment broadcastFragment = new BroadcastFragment();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dash_container, broadcastFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack("BraodcastFragment");
//            activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.commit();

    }
    private Response.Listener<Jsonexample> createMyReqSuccessListener() {
        return new Response.Listener<Jsonexample>() {
            @Override
            public void onResponse(Jsonexample response) {
                try {
                    DashboardActivity.dismissCustomProgress();
                    Toast.makeText(activity, response.status, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int statuscode = error.networkResponse.statusCode;
                DashboardActivity.dismissCustomProgress();
                Toast.makeText(activity, "Some Problem With Web Service", Toast.LENGTH_LONG).show();


            }
        };
    }

}
