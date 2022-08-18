module com.sitepark.ies.userrepository.core {
	exports com.sitepark.ies.userrepository.core.domain.entity;
	exports com.sitepark.ies.userrepository.core.domain.exception;
	exports com.sitepark.ies.userrepository.core.port;
	exports com.sitepark.ies.userrepository.core.usecase;
	requires javax.inject;
	requires org.eclipse.jdt.annotation;
}