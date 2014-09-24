package com.fiftyradios.odiyan;

public interface MoreActionListener {
	
	public void onAsk();
	public void showLoading(String text, boolean bShow);
	public void onSettings();
	public void pickImage();
	public String getImagePath();
	public void onLogout();
}
