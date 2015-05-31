package com.skhalid.hapity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.skhalid.hapity.fragments.BroadcastFragment;
import com.skhalid.hapity.fragments.LoginFragment;
import com.skhalid.hapity.fragments.ProfileFragment;

import org.w3c.dom.Text;


public class BroadcastListAdapter extends BaseAdapter implements OnClickListener {

	  private FragmentActivity activity;
      private ArrayList<BroadcastModal> data;
      private static LayoutInflater inflater=null;
      public Resources res;
      BroadcastModal tempValues=null;
    View likeview;
    int likepos = -1;
    ViewHolder holder;
    String liketype;
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
		return this.data.get(position);
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
         public LinearLayout braodcastItemBottomLayout;
         
  
     }
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 View vi = convertView;

        if(data.size() >0) {

             vi = inflater.inflate(R.layout.broadcast_list_item, null);

             holder = new ViewHolder();
             holder.braodcastItemBottomLayout = (LinearLayout) vi.findViewById(R.id.broadcastItem_LL);
             holder.name = (TextView) vi.findViewById(R.id.nameText);
            holder.comment = (TextView) vi.findViewById(R.id.commentText);
             holder.name.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {

                 }
             });



             holder.like = (TextView) vi.findViewById(R.id.likeText);
            holder.like.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    liketype = "like";
                    likeview = v;
                    likepos = position;
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
                    liketype = "dislike";
                    likeview = v;
                    likepos = position;
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

            holder.comment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("bID", data.get(position).id );
                    BroadcastFragment broadcastFragment = new BroadcastFragment();
                    broadcastFragment.setArguments(bundle);
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.dash_container, broadcastFragment);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.addToBackStack("BroadcastFragment");
//            activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commit();
                }
            });

            if(data.get(position).like.equalsIgnoreCase("l")){
                holder.like.setTextColor(activity.getResources().getColor(R.color.Yellow));
            }

            if(data.get(position).dislike.equalsIgnoreCase("l")){
                holder.dislike.setTextColor(activity.getResources().getColor(R.color.Yellow));
            }
//             holder.userImage=(ImageView)vi.findViewById(R.id.userPhoto);
             holder.comment = (TextView) vi.findViewById(R.id.commentText);
             holder.share = (TextView) vi.findViewById(R.id.shareText);
            holder.broadcastImage = (ImageView) vi.findViewById(R.id.broadcastItem);
             tempValues=null;
             tempValues = (BroadcastModal) data.get( position );
          
              holder.name.setText(tempValues.getTitle() + " (By "+ tempValues.getUserName() +")" );
              holder.like.setText( " like" );
              holder.dislike.setText(" dislike" );
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
        Bundle bundle = new Bundle();
        bundle.putString("bID", data.get(mPosition2).id );
        bundle.putString("sURL", data.get(mPosition2).stream_url);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dash_container, profileFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack("ProfileFragment");
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

                    if(likeview != null){
                        TextView vi = (TextView)likeview;
                        vi.setTextColor(activity.getResources().getColor(R.color.Yellow));

                        if(liketype.equalsIgnoreCase("like")){
                            data.get(likepos).like = "l";
                    } else {
                            data.get(likepos).dislike = "l";
                    }
                        likeview = null;
                        likepos = -1;

                    }

//                    if(liketype.equalsIgnoreCase("like")){
//                        holder.like.setTextColor(activity.getResources().getColor(R.color.Yellow));
//                    } else {
//                        holder.dislike.setTextColor(activity.getResources().getColor(R.color.Yellow));
//                    }

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
