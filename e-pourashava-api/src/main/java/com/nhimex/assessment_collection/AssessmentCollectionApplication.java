package com.nhimex.assessment_collection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AssessmentCollectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssessmentCollectionApplication.class, args);
	}

}
