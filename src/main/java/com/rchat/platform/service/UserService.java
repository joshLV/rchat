package com.rchat.platform.service;

import java.util.Optional;

import com.rchat.platform.domain.User;

/**
 * 
 * 用户业务
 *
 * @author dzhang
 * @since 2017-03-10 23:21:53
 */
public interface UserService extends GenericService<User, String> {

	/**
	 * 根据用户明查找用户信息
	 * 
	 * @param username
	 *            登录用户名
	 * @return
	 */
	Optional<User> findByUsername(String username);

	/**
	 * 退出系统，更新用户在线状态信息，和退出时间
	 * 
	 * @param user
	 *            要退出的用户
	 * @return
	 */
	User logout(User user);

	/**
	 * 登录系统，更新用户在线状态信息和登录时间
	 * 
	 * @param user
	 *            要登录的用户
	 * @return
	 */
	User login(User user);

	boolean updatePassword(String id, String password);

}
