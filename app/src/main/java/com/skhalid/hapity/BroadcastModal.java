package com.skhalid.hapity;

import android.widget.ImageView;

public class BroadcastModal {
	
	private String userName = "";
	public String like = "";
    public String dislike = "";
	private String comment = " Coment";
	private String share = " Share";
	private String status = " Offline";
	private String type = "";
	public String id;
	private String title;
	public String stream_url;
	public String broadcast_image;
	public String numberofLikes = "0";
    public String onlinestatus;
    public String user_id;


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getDislike() {
		return dislike;
	}

	public void setDislike(String dislike) {
		this.dislike = dislike;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
