package dawnchase.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("dawnchase.backend.mapper") // 确保扫描到 Mapper 接口
public class BackendApplication {

	public static void main(String[] args)
	{
		SpringApplication.run(BackendApplication.class, args);
	}
}
