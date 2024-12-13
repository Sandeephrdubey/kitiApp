package com.hi.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallCreateRoot{

	@SerializedName("callId")
	private CallId callId;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public CallId getCallId(){
		return callId;
	}

	public String getMessage(){
		return message;
	}

	public boolean isStatus(){
		return status;
	}

	public static class CallId{

		@SerializedName("gift")
		private List<Object> gift;

		@SerializedName("userDelete")
		private boolean userDelete;

		@SerializedName("callConnect")
		private boolean callConnect;

		@SerializedName("callEndTime")
		private Object callEndTime;

		@SerializedName("callStartTime")
		private Object callStartTime;

		@SerializedName("type")
		private String type;

		@SerializedName("host_id")
		private HostId hostId;

		@SerializedName("createdAt")
		private String createdAt;

		@SerializedName("hostDelete")
		private boolean hostDelete;

		@SerializedName("user_id")
		private UserId userId;

		@SerializedName("__v")
		private int v;

		@SerializedName("time")
		private String time;

		@SerializedName("_id")
		private String id;

		@SerializedName("coin")
		private int coin;

		@SerializedName("updatedAt")
		private String updatedAt;

		public List<Object> getGift(){
			return gift;
		}

		public boolean isUserDelete(){
			return userDelete;
		}

		public boolean isCallConnect(){
			return callConnect;
		}

		public Object getCallEndTime(){
			return callEndTime;
		}

		public Object getCallStartTime(){
			return callStartTime;
		}

		public String getType(){
			return type;
		}

		public HostId getHostId(){
			return hostId;
		}

		public String getCreatedAt(){
			return createdAt;
		}

		public boolean isHostDelete(){
			return hostDelete;
		}

		public UserId getUserId(){
			return userId;
		}

		public int getV(){
			return v;
		}

		public String getTime(){
			return time;
		}

		public String getId(){
			return id;
		}

		public int getCoin(){
			return coin;
		}

		public String getUpdatedAt(){
			return updatedAt;
		}

		public static class HostId{

			@SerializedName("image")
			private String image;

			@SerializedName("name")
			private String name;

			@SerializedName("bio")
			private String bio;

			@SerializedName("_id")
			private String id;

			@SerializedName("username")
			private String username;

			public String getImage(){
				return image;
			}

			public String getName(){
				return name;
			}

			public String getBio(){
				return bio;
			}

			public String getId(){
				return id;
			}

			public String getUsername(){
				return username;
			}
		}

		public static class UserId{

			@SerializedName("image")
			private String image;

			@SerializedName("name")
			private String name;

			@SerializedName("bio")
			private Object bio;

			@SerializedName("_id")
			private String id;

			@SerializedName("username")
			private String username;

			public String getImage(){
				return image;
			}

			public String getName(){
				return name;
			}

			public Object getBio(){
				return bio;
			}

			public String getId(){
				return id;
			}

			public String getUsername(){
				return username;
			}
		}
	}
}