package de.fzi.cep.sepa.appstore.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BundleInfo {

	private @SerializedName("_id") String id;
    private @SerializedName("_rev") String rev;
    
	private long bundleId;
	
	private String symbolicName;
	private String location;
	private String appName;
	private String appDescription;
	private String appContextPath;
	private String appStoreUrl;
	private String appVersion;
	private String targetPodUrl;
	private boolean installed;
	private boolean needsUpdate;
	private long installationTimestamp;
	
	private List<String> appComponentUris;
	
	public BundleInfo() {
		this.appComponentUris = new ArrayList<>();
	}
	
	public BundleInfo(long bundleId, String symbolicName, String location, String version, String appName, String appDescription, String appContextPath) {
		super();
		this.symbolicName = symbolicName;
		this.location = location;
		this.bundleId = bundleId;
		this.appVersion = version;
		this.appName = appName;
		this.appDescription = appDescription;
		this.appContextPath = appContextPath;
	}
	
	public String getSymbolicName() {
		return symbolicName;
	}
	
	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public long getBundleId() {
		return bundleId;
	}

	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppDescription() {
		return appDescription;
	}
	
	public String getAppContextPath() {
		return appContextPath;
	}

	public void setAppContextPath(String appContextPath) {
		this.appContextPath = appContextPath;
	}

	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}

	public String getAppStoreUrl() {
		return appStoreUrl;
	}

	public void setAppStoreUrl(String appStoreUrl) {
		this.appStoreUrl = appStoreUrl;
	}

	public String getTargetPodUrl() {
		return targetPodUrl;
	}

	public void setTargetPodUrl(String targetPodUrl) {
		this.targetPodUrl = targetPodUrl;
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	public long getInstallationTimestamp() {
		return installationTimestamp;
	}

	public void setInstallationTimestamp(long installationTimestamp) {
		this.installationTimestamp = installationTimestamp;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public boolean isNeedsUpdate() {
		return needsUpdate;
	}

	public void setNeedsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}

	public List<String> getAppComponentUris() {
		return appComponentUris;
	}

	public void setAppComponentUris(List<String> appComponentUris) {
		this.appComponentUris = appComponentUris;
	}
	
	
						
}
