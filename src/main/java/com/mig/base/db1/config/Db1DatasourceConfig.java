package com.mig.base.db1.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource({ "classpath:application.yml" })
@EnableTransactionManagement
@EnableJpaRepositories(
 entityManagerFactoryRef = "db1EntityManagerFactory",
 transactionManagerRef = "db1TransactionManager",
 basePackages = {
  "com.mig.base.db1.repo"
 }
)
public class Db1DatasourceConfig {
	 @Primary
	 @Bean(name = "db1DataSource")
	 @ConfigurationProperties(prefix = "spring.datasource")
	 public DataSource db1DataSource() {
		 HikariDataSource hikariDataSource = new HikariDataSource();

         return hikariDataSource;
//	  return DataSourceBuilder.create().build();
	 }

	 @Primary
	 @Bean(name = "db1EntityManagerFactory")
	 public LocalContainerEntityManagerFactoryBean
	 db1EntityManagerFactory(
	  EntityManagerFactoryBuilder builder,
	  @Qualifier("db1DataSource") DataSource dataSource
	 ) {
	  return builder
	   .dataSource(dataSource)
	   .packages("com.mig.base.db1.pojo")
	   .persistenceUnit("db1")
	   .build();
	 }

	 @Primary
	 @Bean(name = "db1TransactionManager")
	 public PlatformTransactionManager db1TransactionManager(
	  @Qualifier("db1EntityManagerFactory") EntityManagerFactory db1EntityManagerFactory
	 ) {
	  return new JpaTransactionManager(db1EntityManagerFactory);
	 }
}