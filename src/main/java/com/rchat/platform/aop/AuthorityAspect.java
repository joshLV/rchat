package com.rchat.platform.aop;

import com.rchat.platform.common.LogContext;
import com.rchat.platform.common.LogContextHolder;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.config.AuthorityProperties;
import com.rchat.platform.config.AuthorityProperties.OperationProperties;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.AuthorityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * 用户权限认证的切面
 *
 * @author dzhang
 */
@Aspect
@Component
public class AuthorityAspect implements InitializingBean {
    private static final Log log = LogFactory.getLog(AuthorityAspect.class);
    @Autowired
    private AuthorityProperties authorityProperties;
    /**
     * 系统管理员角色
     */
    private static final Role SYSTEM_MANAGER_ROLE = new Role();
    private static final String NO_RIGHT = "NONE";

    private Map<String, OperationType> method2Right = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        OperationProperties operation = authorityProperties.getOperation();
        // 将各种方法的前缀和对应的权限映射起来
        for (String prefix : operation.getCreatePrefixes()) {
            method2Right.put(prefix, OperationType.CREATE);
        }

        for (String prefix : operation.getDeletePrefixes()) {
            method2Right.put(prefix, OperationType.DELETE);
        }

        for (String prefix : operation.getUpdatePrefixes()) {
            method2Right.put(prefix, OperationType.UPDATE);
        }
        for (String prefix : operation.getRetrievePrefixes()) {
            method2Right.put(prefix, OperationType.RETRIEVE);
        }

        method2Right.put(NO_RIGHT, OperationType.NONE);
        // 系统管理员的id = 32b62a97-502f-4164-ab7d-1f3c50bad1e0
        SYSTEM_MANAGER_ROLE.setId("32b62a97-502f-4164-ab7d-1f3c50bad1e0");
    }

    @Autowired
    private AuthorityService authorityService;

    /**
     * 无须安全检查的方法
     */
    @Pointcut("@annotation(NoSecurity)")
    private void noSecurityMethod() {
    }

    /**
     * 检查操作权限； 所谓权限，就是指 【谁对什么资源可以进行什么操作】
     *
     * @param point 拦截的安全操作方法
     * @return 最终拦截的业务方法返回值
     * @throws Throwable 业务异常或者认证授权异常
     */
    // @Before("execution(public * com.rchat.platform.service..*.*(..)) &&
    // @target(SecurityService) && !noSecurityMethod()")
    // @Before("@target(SecurityService) && !noSecurityMethod()")
    @Around("execution(public * com.rchat.platform.service..*.*(..))")
    public Object checkPrivilege(ProceedingJoinPoint point) throws Throwable {
        if (!authorityProperties.isEnabled())
            return point.proceed();

        log.info(point.getSignature().toLongString());

        // 获取要操作的资源
        Privilege privilege = getPrivilege(point);
        OperationType operation = privilege.getOperation();
        logIfNecessary(point, privilege);

        switch (operation) {
            case ALL:
                return point.proceed();
            case NONE:
                throw new NoRightAccessException();
            default:
                User user = RchatUtils.currentUser();
                boolean authenticated = authorize(user, privilege);
                if (authenticated) {
                    log.info(String.format("%s %s", user.getName(), privilege));
                    return point.proceed();
                } else {
                    throw new NoRightAccessException();
                }
        }
    }

    private void logIfNecessary(ProceedingJoinPoint point, Privilege privilege) {
        OperationType operation = privilege.getOperation();
        switch (operation) {
            case NONE:
            case RETRIEVE:
            case ALL:
                return;
            default:
                break;
        }

        Optional<LogContext> o = Optional.ofNullable(LogContextHolder.getLogContext());

        o.ifPresent(ctx -> {
            com.rchat.platform.domain.Log log = ctx.getLog();
            Object[] args = point.getArgs();
            for (Object arg : args) {
                if (arg instanceof Loggable) {
                    Loggable loggable = (Loggable) arg;
                    LogDetail detail = loggable.toLogDetail();
                    detail.setOperation(privilege.getOperation().toString());
                    detail.setDescription(privilege.toString());

                    log.addDetail(detail);
                }
            }
        });
    }

    /**
     * 获取资源
     *
     * @param point 连接点
     * @return 权限
     */
    private Privilege getPrivilege(JoinPoint point) {
        Object target = point.getTarget();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        SecurityMethod securityMethod = AnnotationUtils.findAnnotation(method, SecurityMethod.class);

        ResourceType resource = ResourceType.NONE;
        OperationType operation;

        // 如果没有 @SecurityMethod 注释，则查看target是否有SecurityService注释
        if (securityMethod == null) {
            SecurityService securityService = AnnotationUtils.findAnnotation(target.getClass(), SecurityService.class);
            // 如果 SecurityService 仍然没有注释，则说明不需要安全验证
            if (securityService == null) {
                operation = OperationType.ALL;
            } else {
                resource = securityService.value();
                operation = parseOperation(signature.getName());
            }

        } else {
            if (!securityMethod.secured()) {
                operation = OperationType.ALL;
            } else {
                resource = securityMethod.resource();
                operation = securityMethod.operation();

                // 如果是未知资源，则取查找业务bean是否指定了资源
                if (resource == ResourceType.UNKNOW) {
                    SecurityService securityService = AnnotationUtils.findAnnotation(target.getClass(),
                            SecurityService.class);

                    resource = securityService.value();
                }
            }
        }

        return new Privilege(resource, operation);
    }

    private boolean authorize(User user, Privilege privilege) {

        // 如果是系统管理员，则直接可以进行操作
        boolean isSystemManager = user.getRoles().parallelStream().anyMatch(role -> role.equals(SYSTEM_MANAGER_ROLE));

        if (isSystemManager) {
            return true;
        }
        ResourceType resource = privilege.getResource();
        OperationType operation = privilege.getOperation();

        // 查找此用户对应的所有授权信息，此用户的各种角色的授权一起查看
        List<Authority> authorities = authorityService.findByUser(user);
        // 先看是不是能操作此资源
        Predicate<Authority> pAuthority = authority -> authority.getResource().getId().equals(resource.code());
        // 是否能在此资源上执行该操作
        pAuthority.and(authority -> (authority.getOperations() & operation.code()) != 0);

        // 如果资源和对应的操作都满足，说明此用户有权限操作此资源
        return authorities.parallelStream().anyMatch(pAuthority);
    }

    /**
     * 解析出业务方法对应的操作权限，根据方法名的前缀
     *
     * @param name 方法名称
     * @return 操作类型
     */
    private OperationType parseOperation(String name) {
        Set<Entry<String, OperationType>> entrySet = method2Right.entrySet();
        for (Entry<String, OperationType> entry : entrySet) {
            if (name.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return OperationType.NONE;
    }
}
