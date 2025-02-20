package org.cloudfoundry;

import org.cloudfoundry.client.CloudFoundryClient;

import com.github.zafarkhaja.semver.Version;

import reactor.core.publisher.Flux;

public interface Cleaner {
//	 public Flux<Void> cleanBuildpacks(
//	            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory);
	 public Flux<Void> cleanServiceInstances(
	            CloudFoundryClient cloudFoundryClient, NameFactory nameFactory, Version serverVersion);
}