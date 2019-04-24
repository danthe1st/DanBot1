package io.github.danthe1st.danbot1.util;

import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class BotSecurityManager extends SecurityManager {
	private Set<Thread> sensitiveThreads=new HashSet<>();
	private String[] allowedRuntimePermissions={"createClassLoader","accessClassInPackage.jdk.nashorn.internal.","accessDeclaredMembers","suppressAccessChecks"};
	
	private boolean isCurrentThreadSensitive() {
		return sensitiveThreads.contains(Thread.currentThread());
	}
	private void setSensitiveThread() {
		sensitiveThreads.add(Thread.currentThread());
	}
	private void unsetSensitiveThread() {
		sensitiveThreads.remove(Thread.currentThread());
	}
	public <T, R>R execSecure(Function<T, R> toExec,T args) throws Throwable {
		setSensitiveThread();
		try {
			return toExec.apply(args);
		}finally {
			unsetSensitiveThread();
		}
	}
	
	@Override
	public void checkPermission(Permission perm) {
		if (isCurrentThreadSensitive()) {
			if (perm instanceof RuntimePermission) {
				String name=perm.getName();
				boolean allow=false;
				for (String allowed : allowedRuntimePermissions) {
					if (name.startsWith(allowed)) {
						allow=true;
						break;
					}
				}
				if (!allow) {
					throw new SecurityException("missing permission: "+perm.getName());
				}
			}else if (perm instanceof ReflectPermission&&perm.getName().equals("suppressAccessChecks")) {
			}
		}
	}
	@Override
	public void checkPermission(Permission perm, Object context) {
		if (isCurrentThreadSensitive()) {
			super.checkPermission(perm,context);
		}
	}
	@Override
	public void checkExec(String cmd) {
		throw new SecurityException("System code execution denied");
	}
	@Override
	public void checkWrite(String filepath) {
		if (isCurrentThreadSensitive()) {
			throw new SecurityException("File access restricted");
		}
	}
}
