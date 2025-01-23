package com.bot.takagi3.config;

import com.bot.takagi3.properties.MysqlProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@Slf4j
public class MysqlConfiguration
{
    private final MysqlProperties mysqlProperties;

    @Autowired
    public MysqlConfiguration(MysqlProperties mysqlProperties)
    {
        this.mysqlProperties = mysqlProperties;
    }


    @PostConstruct
    private void initDatabase()
    {
        log.info("尝试连接数据库...");
        if (currentDatabaseExists())
        {
            log.info("数据库已存在.");
            return;
        }
        log.info("数据库不存在,尝试初始化数据库...");
        createDatabase();

        try (Connection connection = DriverManager.getConnection(mysqlProperties.getUrl(), mysqlProperties.getUsername(), mysqlProperties.getPassword()))
        {
//            runSQLScript(mysqlProperties.getSchemaAdr(), true, connection);
//            runSQLScript(mysqlProperties.getDataAdr(), true, connection);
        }
        catch (Exception e)
        {
            log.error("初始化数据库失败！{}", e.getMessage());
        }
    }

    private void createDatabase()
    {
        try
        {
            // 修改连接语句，重新建立连接
            URI databaseURI = new URI(mysqlProperties.getUrl().replace("jdbc:", ""));
            // 得到连接地址中的数据库平台名（例如mysql）
            String databasePlatform = databaseURI.getScheme();
            // 得到连接地址和端口
            String hostAndPort = databaseURI.getAuthority();
            // 得到连接地址中的库名
            String databaseName = databaseURI.getPath().substring(1);
            // 组装新的连接URL，不连接至指定库
            String newURL = "jdbc:" + databasePlatform + "://" + hostAndPort + "/";
            // 重新建立连接
            Connection connection = DriverManager.getConnection(newURL, mysqlProperties.getUsername(), mysqlProperties.getPassword());
            Statement statement = connection.createStatement();
            // 执行SQL语句创建数据库
            statement.execute("create database if not exists `" + databaseName + "`");
            // 关闭会话和连接
            statement.close();
            connection.close();
            log.info("创建数据库完成！");
        }
        catch (URISyntaxException e)
        {
            log.error("数据库连接URL格式错误！:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        catch (SQLException e)
        {
            log.error("连接失败！:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void runSQLScript(String path, boolean isClasspath, Connection connection)
    {
        try (InputStream sqlFileStream = isClasspath ? new ClassPathResource(path).getInputStream() : new FileInputStream(path))
        {
            BufferedReader sqlFileStreamReader = new BufferedReader(new InputStreamReader(sqlFileStream, StandardCharsets.UTF_8));
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(sqlFileStreamReader);

            sqlFileStreamReader.close();
        }
        catch (Exception e)
        {
            log.error("读取文件或者执行脚本失败！{}", e.getMessage());
        }
    }

    private boolean currentDatabaseExists() {
        // 尝试以配置文件中的URL建立连接
        try
        {
            Connection connection = DriverManager.getConnection(mysqlProperties.getUrl(), mysqlProperties.getUsername(), mysqlProperties.getPassword());
            connection.close();
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }
}
