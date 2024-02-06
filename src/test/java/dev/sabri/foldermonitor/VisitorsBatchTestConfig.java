package dev.sabri.foldermonitor;

import lombok.val;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = {"dev.sabri.foldermonitor.repositories"})
@EntityScan(basePackages = {"dev.sabri.foldermonitor.domain"})
@ComponentScan(basePackages = {"dev.sabri.foldermonitor.mapper"})
public class VisitorsBatchTestConfig {


    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("/org/springframework/batch/core/schema-h2.sql")
                .addScript("init.sql")
                .build();
    }

    @Bean
    @Qualifier("transactionManager")
    public JpaTransactionManager transactionManager() {
        val jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return jpaTransactionManager;
    }

    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        val localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource());
        localContainerEntityManagerFactoryBean.setPackagesToScan("dev.sabri.foldermonitor.domain");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("Visitors");
        val hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        return localContainerEntityManagerFactoryBean;


    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        val jobrepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobrepositoryFactoryBean.setDataSource(dataSource());
        jobrepositoryFactoryBean.setTransactionManager(transactionManager());
        jobrepositoryFactoryBean.afterPropertiesSet();
        return jobrepositoryFactoryBean.getObject();

    }

}
