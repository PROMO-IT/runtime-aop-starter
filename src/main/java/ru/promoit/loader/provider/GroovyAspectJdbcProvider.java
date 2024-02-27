package ru.promoit.loader.provider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
@ConditionalOnBean(DataSource.class)
public class GroovyAspectJdbcProvider implements GroovyAspectSourceProvider {
    private final DataSource dataSource;

    public GroovyAspectJdbcProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean match(String driver) {
        return "jdbc".equals(driver);
    }

    @Override
    public String provide(String property) throws Throwable {
        Connection connection = dataSource.getConnection();
        Statement stmt = connection.createStatement();

        try (connection; stmt; ResultSet rs = stmt.executeQuery(property)) {
            rs.next();
            return rs.getString(1);
        }
    }
}
