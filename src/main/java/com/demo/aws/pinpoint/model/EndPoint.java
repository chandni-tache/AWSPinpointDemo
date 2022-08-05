package com.demo.aws.pinpoint.model;

public class EndPoint {
	
	private String make;
	private String model;
	private String modelVersion;
	private String platform;
	private String platformVersion;
	private String channelType; //APNS or FCM
	private String token;
	private String userId;
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getPlatformVersion() {
		return platformVersion;
	}
	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getModelVersion() {
		return modelVersion;
	}
	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
