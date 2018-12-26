package com.rchat.platform.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentView;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.DepartmentView;
import com.rchat.platform.domain.Role;
import com.rchat.platform.domain.User;
import com.rchat.platform.exception.AuthenticationNotFoundException;
import com.rchat.platform.exception.PrincipalIvalideException;
import com.rchat.platform.web.security.SecurityUser;

/**
 * 平台工具类，全部都是静态方法
 * 
 * @author dzhang
 *
 */
public final class RchatUtils {
	/**
	 * 已经登录的用户session列表
	 */
	private static final Map<String, HttpSession> LOGON_SESSION_MAP = new ConcurrentHashMap<>();
	public static final String SECURITY_USER = "security-user";
	private static final Md5PasswordEncoder md5 = new Md5PasswordEncoder();
	private static final ThreadLocal<HttpSession> CURRENT_SESSION = new ThreadLocal<>();
	/**
	 * 平台管理员角色
	 */
	private static final Role RCHAT_ROLE = new Role();
	/**
	 * 普通代理商角色
	 */
	private static final Role NORMAL_AGENT_ROLE = new Role();
	/**
	 * 终端代理商角色
	 */
	private static final Role TERMINAL_AGENT_ROLE = new Role();
	/**
	 * 集团管理员
	 */
	private static final Role GROUP_ROLE = new Role();
	/**
	 * 公司管理员
	 */
	private static final Role COMPANY_ROLE = new Role();
	/**
	 * 部门管理员
	 */
	private static final Role DEPARTMENT_ROLE = new Role();
	static {
		RCHAT_ROLE.setId("32b62a97-502f-4164-ab7d-1f3c50bad1e0");
		NORMAL_AGENT_ROLE.setId("96cadace-4b09-4a7d-8fe1-a080f94ebbc6");
		TERMINAL_AGENT_ROLE.setId("d3a9f6ab-fd16-4916-bb69-cbc4e202a8ae");
		GROUP_ROLE.setId("a07bfa07-54c4-482d-9028-74cc9064edbc");
		COMPANY_ROLE.setId("e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0");
		DEPARTMENT_ROLE.setId("219e8e4d-f32b-4434-b045-734eb9e9f80a");
	}

	public static User currentUser() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		// 获取用户信息
		Optional<Authentication> oa = Optional.ofNullable(ctx.getAuthentication());

		// 如果认证实体不存在，抛出异常
		Authentication authentication = oa.orElseThrow(AuthenticationNotFoundException::new);
		if (!(authentication.getPrincipal() instanceof SecurityUser)) {
			throw new PrincipalIvalideException();
		}

		SecurityUser principal = SecurityUser.class.cast(authentication.getPrincipal());
		return principal.getUser();
	}

	/**
	 * 当前用户是否是代理商管理员
	 * 
	 * @return
	 */
	public static boolean isRchatAdmin() {
		User user = currentUser();

		return user.getRoles().parallelStream().anyMatch(RCHAT_ROLE::equals);
	}

	/**
	 * 当前用户是否是代理商管理员
	 * 
	 * @return
	 */
	public static boolean isAgentAdmin() {
		User user = currentUser();

		return user.getRoles().parallelStream()
				.anyMatch(r -> NORMAL_AGENT_ROLE.equals(r) || TERMINAL_AGENT_ROLE.equals(r));
	}

	/**
	 * 当前用户是否是集团管理员
	 * 
	 * @return
	 */
	public static boolean isGroupAdmin() {
		User user = currentUser();

		return user.getRoles().parallelStream().anyMatch(GROUP_ROLE::equals);
	}

	/**
	 * 当前用户是否是公司管理员或者部门管理员
	 * 
	 * @return
	 */
	public static boolean isDepartmentAdmin() {
		User user = currentUser();

		Predicate<Role> p = r -> r.equals(COMPANY_ROLE);
		p = p.or(r -> r.equals(DEPARTMENT_ROLE));

		return user.getRoles().parallelStream().anyMatch(p);
	}

	/**
	 * 加密密码，在创建新用户之前，调用一次，如果是从数据库查出来的，就別调用
	 */
	public static String encodePassword(String rawPassword, String salt) {
		// 如果编码过，或者存在Id，就不编码了，有id说明是数据库存在的用户，不是新用户
		return md5.encodePassword(rawPassword, salt);
	}

	public static boolean isPasswordValid(String rawPass, String codedPassword, String salt) {
		return md5.isPasswordValid(codedPassword, rawPass, salt);
	}

	/**
	 * 记录所有已经登录的用户的session，用于强制退出已经登录的用户
	 * 
	 * @param user
	 *            登录的用户
	 * @param session
	 *            关联的session
	 * @return 之前登录的用户关联的session
	 */
	public static Optional<HttpSession> login(User user, HttpSession session) {
		session.setAttribute(RchatUtils.SECURITY_USER, user);
		return Optional.ofNullable(LOGON_SESSION_MAP.put(user.getId(), session));
	}

	public static void currentSession(HttpSession session) {
		CURRENT_SESSION.set(session);
	}

	public static Optional<HttpSession> currentSession() {
		return Optional.ofNullable(CURRENT_SESSION.get());
	}

	public static Optional<HttpSession> logout(User user) {
		CURRENT_SESSION.remove();
		return Optional.ofNullable(LOGON_SESSION_MAP.remove(user.getId()));
	}

	/**
	 * 将spring-jpa {@link Sort} --> {@link Order}s
	 * 
	 * @param sort
	 * @param builder
	 * @param root
	 * @return
	 */
	public static <X> List<Order> sort2Orders(Sort sort, CriteriaBuilder builder, Root<X> root) {
		List<Order> orders = new ArrayList<>();

		Optional.ofNullable(sort).ifPresent(s -> s.forEach(o -> {
			if (o.isAscending())
				orders.add(builder.asc(root.get(o.getProperty())));
			else
				orders.add(builder.desc(root.get(o.getProperty())));
		}));

		return orders;
	}

	public static Agent view2Entity(AgentView view) {
		if (view == null) {
			return null;
		}

		Agent agent = new Agent();
		BeanUtils.copyProperties(view, agent);

		return agent;
	}

	public static Department view2Entity(DepartmentView view) {
		if (view == null) {
			return null;
		}

		Department department = new Department();
		BeanUtils.copyProperties(view, department);

		return department;
	}
}
