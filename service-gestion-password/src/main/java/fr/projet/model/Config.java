package fr.projet.model;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class Config {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/stolen_passwords");
        dataSource.setUsername("postgres");
        dataSource.setPassword("root");
        return dataSource;
    }
   
    @Bean
    public JdbcTemplate clickHouseJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}


 