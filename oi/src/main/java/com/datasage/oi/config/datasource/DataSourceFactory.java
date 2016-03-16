package com.datasage.oi.config.datasource;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:datasource/datasource_config.properties")
public class DataSourceFactory {

	@Autowired
	private Environment env;

	public DataSource getDataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(env.getProperty("datasource.driver.class"));
		basicDataSource.setUrl(env.getProperty("datasource.url"));
		basicDataSource.setUsername(env.getProperty("datasource.username"));
		basicDataSource.setPassword(env.getProperty("datasource.password"));

		basicDataSource.setInitialSize(Integer.valueOf(env.getProperty("datasource.initialSize")));
		basicDataSource.setMaxTotal(Integer.valueOf(env.getProperty("datasource.maxTotal")));
		basicDataSource.setMaxIdle(Integer.valueOf(env.getProperty("datasource.maxIdle")));
		basicDataSource.setMinIdle(Integer.valueOf(env.getProperty("datasource.minIdle")));

		return basicDataSource;
	}
}
