package com.skhalid.hapity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


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
             return 1;
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
        // public ImageView dreamImage;
         
  
     }
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View vi = convertView;
         ViewHolder holder;
          
         if(convertView==null){
  
             vi = inflater.inflate(R.layout.broadcast_list_item, null);
             holder = new ViewHolder();
             holder.name = (TextView) vi.findViewById(R.id.nameText);
             holder.like = (TextView) vi.findViewById(R.id.likeText);
             holder.dislike=(TextView)vi.findViewById(R.id.dislikeText);
//             holder.userImage=(ImageView)vi.findViewById(R.id.userPhoto);
             holder.comment = (TextView) vi.findViewById(R.id.commentText);
             holder.share = (TextView) vi.findViewById(R.id.shareText);


             vi.setTag( holder );
         }
         else 
             holder=(ViewHolder)vi.getTag();
          
         if(data.size()<=0)
         {
             holder.name.setText("No Data");
              
         }
         else
         {
            
             tempValues=null;
             tempValues = (BroadcastModal) data.get( position );
          
              holder.name.setText( tempValues.getUserName() );
              holder.like.setText( tempValues.getLike() );
              holder.dislike.setText( tempValues.getDislike() );
              holder.comment.setText(tempValues.getComment());
              holder.share.setText(tempValues.getShare());

//               holder.userImage.setImageResource(
//                       res.getIdentifier(
//                       "com.example.dreamtweats:drawable/"+tempValues.getUserPhoto()
//                       ,null,null));
               vi.setOnClickListener(new OnItemClickListener(position));
         }
         return vi;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
        
        OnItemClickListener(int position){
             mPosition = position;
        }
        
        @Override
        public void onClick(View arg0) {

        	performAction(mPosition);
        }

		private void performAction(int mPosition2) {
//			// TODO Auto-generated method stub
//			DreamDetailModal DDM = new DreamDetailModal(); 
//			BroadcastModal tm = BroadcastListFragment.tweatArray.get(mPosition2);
//			DDM.setTweetTime(tm.getTweatTime());
//			DDM.setUserName(tm.getUserName());
//			DDM.setUserPhoto(tm.getUserPhoto());
//			DreamDetails dreamDetails = new DreamDetails();
//			FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.dash_container, dreamDetails);
//			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
		}               
    }   

}
