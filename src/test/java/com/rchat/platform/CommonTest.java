package com.rchat.platform;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.User;

public class CommonTest {
	@Autowired
	private JpaRepository<User, String> repository;

	@Test
	public void testUUID() throws Exception {
		for (int i = 0; i < 10; i++) {
			System.err.println(UUID.randomUUID());
		}
	}

	@Test
	public void testRepository() throws Exception {
		ExampleMatcher matcher = ExampleMatcher.matching();
		User probe = new User();
		Example<User> example = Example.of(probe, matcher);
		repository.findAll(example);
	}

	@Test
	public void testPasswordValid() throws Exception {
		User user = new User();

		user.setSalt("mIWxtbd9o5KsUedVbRhKZLnGaVGpWCN7");
		user.setPassword("3433b5d08ee65f10a0e28686b269fc25");

		assertTrue(RchatUtils.isPasswordValid("kingdom", user.getPassword(), user.getSalt()));
	}

	@Test
	public void testGenerteMD5Password() throws Exception {
		User user = new User();

		user.setPassword("kingdom");
		String salt = RandomStringUtils.randomAlphanumeric(32);
		user.setSalt(salt);

		user.setPassword(RchatUtils.encodePassword(user.getPassword(), salt));
		System.err.printf("salt = %s password = %s", salt, user.getPassword());
	}

	@Test
	public void testFormater() throws Exception {
		System.err.printf("%1$02X-%1$02X-%1$02X-%1$02X-%1$02X-%1$02X", 15);
	}
}
