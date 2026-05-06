package com.moodbites.restfulapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;

@SpringBootTest
@MockBean(GoogleCredentials.class)
@MockBean(FirebaseApp.class)
class MoodBitesApplicationTests {
	@Test
	void contextLoads() {
	}
}