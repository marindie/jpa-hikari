package com.mig.base.wony.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ConfigurationProperties(prefix = "wony.datasource")
@PropertySource({ "classpath:application.yml" })
@EnableTransactionManagement
@EnableJpaRepositories(
 entityManagerFactoryRef = "wonyEntityManagerFactory",
 transactionManagerRef = "wonyTransactionManager",
 basePackages = { "com.mig.base.wony.repo" }
)
public class WonyDatasourceConfig extends HikariConfig {
	
	 @Bean(name = "wonyDataSource")
	 public DataSource wonyDataSource() {
		 
	    Properties dsProps = new Properties();
	    dsProps.put("driverClassName", "oracle.jdbc.OracleDriver");
	    dsProps.put("username", "WONY");
	    dsProps.put("password", "1234");
	    dsProps.put("jdbcUrl", "jdbc:oracle:thin:@192.168.0.1:2020:WONY");
	    dsProps.put("connectionTimeout", 10000);
	    dsProps.put("maximumPoolSize", 30);
//	    dsProps.put("idleTimeout", env.getProperty("hikari.idleTimeout", Integer.class));
//	    dsProps.put("maxLifetime", env.getProperty("hikari.maxLifetime", Integer.class));
//	    dsProps.put("leakDetectionThreshold", env.getProperty("hikari.leakDetectionThreshold", Integer.class));
//	    dsProps.put("jdbc4ConnectionTest", env.getProperty("hikari.jdbc4ConnectionTest", Boolean.class));

	    HikariConfig config = new HikariConfig(dsProps);
	    return new LazyConnectionDataSourceProxy(new HikariDataSource(config));
//		 return new LazyConnectionDataSourceProxy(new HikariDataSource(this));
//		 return DataSourceBuilder.create().build();
	 }

	 @Bean(name = "wonyEntityManagerFactory")
	 public EntityManagerFactory 
	 wonyEntityManagerFactory() {
	        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

	        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
	        factory.setDataSource(this.wonyDataSource());
	        factory.setJpaVendorAdapter(vendorAdapter);
	        factory.setJpaPropertyMap(ImmutableMap.of(
	                "hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect",
	                "hibernate.show_sql", "false",
	                "hibernate.jdbc.batch_size", "10",
	                "hibernate.jdbc.batch_versioned_data", "true",
	                "hibernate.order_inserts", "true"
	        ));
	        factory.setJpaPropertyMap(ImmutableMap.of(
	                "hibernate.enable_lazy_load_no_trans", "true",
	                "hibernate.order_updates", "true",
	                "hibernate.generate_statistics", "true"
	        ));	        

	        factory.setPackagesToScan("com.mig.base.dpbas.pojo");
	        factory.setPersistenceUnitName("wony");
	        factory.afterPropertiesSet();

	        return factory.getObject();
	 }

    @Bean
    public PlatformTransactionManager wonyTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(wonyEntityManagerFactory());
        return tm;
    }	 
	 
}