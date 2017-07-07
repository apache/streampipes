package de.fzi.cep.sepa.manager.appstore;

import java.util.List;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class InstalledAppsInfoProvider {

	public void updateAppStoreInfoWithInstalledApps(List<BundleInfo> appStoreBundles) {
		List<BundleInfo> localApps = StorageManager.INSTANCE.getAppStorageApi().getInstalledBundles();
		
		// Find already installed apps
		appStoreBundles
			.stream()
			.filter(a -> localApps.stream()
					.anyMatch(l -> a.getSymbolicName().equals(l.getSymbolicName()))).forEach(ia -> ia.setInstalled(true));
		
		// Find apps that require an update
		appStoreBundles
			.stream()
			.filter(a -> localApps.stream()
			.anyMatch(l -> a.getSymbolicName().equals(l.getSymbolicName()) && !a.getAppVersion().equals(l.getAppVersion())))
			.forEach(ua -> ua.setNeedsUpdate(true));
		
		// Find apps that are installed but no longer in the app store
		localApps
			.stream()
			.filter(l -> appStoreBundles.stream().noneMatch(a -> l.getSymbolicName().equals(a.getSymbolicName())))
			.forEach(la -> appStoreBundles.add(la));
	}
}
