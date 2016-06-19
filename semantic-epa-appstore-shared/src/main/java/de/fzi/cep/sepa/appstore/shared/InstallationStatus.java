package de.fzi.cep.sepa.appstore.shared;

public class InstallationStatus {

	private BundleInfo bundleInfo;
	
	private boolean success;
	private boolean inProgress;
	
	private String errorMessage;
	
	private long currentProgress;

	public InstallationStatus() {
		
	}
	
	public InstallationStatus(BundleInfo bundleInfo, boolean success,
			boolean inProgress, String errorMessage, long currentProgress) {
		super();
		this.bundleInfo = bundleInfo;
		this.success = success;
		this.inProgress = inProgress;
		this.errorMessage = errorMessage;
		this.currentProgress = currentProgress;
	}
	
	public InstallationStatus(BundleInfo bundleInfo, boolean success) {
		this.bundleInfo = bundleInfo;
		this.success = success;
	}
	
	public InstallationStatus(BundleInfo bundleInfo, boolean success, String errorMessage) {
		this(bundleInfo, success);
		this.errorMessage = errorMessage;
	}

	public BundleInfo getBundleInfo() {
		return bundleInfo;
	}

	public void setBundleInfo(BundleInfo bundleInfo) {
		this.bundleInfo = bundleInfo;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getCurrentProgress() {
		return currentProgress;
	}

	public void setCurrentProgress(long currentProgress) {
		this.currentProgress = currentProgress;
	}
	
	
}
