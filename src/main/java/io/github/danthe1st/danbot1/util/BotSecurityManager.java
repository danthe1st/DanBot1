package io.github.danthe1st.danbot1.util;

import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BotSecurityManager extends SecurityManager {
	private Set<Thread> sensitiveThreads=new HashSet<>();
	private Set<String> allowedRuntimePermissions= Stream.of("createClassLoader","accessClassInPackage.jdk.nashorn.internal.scripts","accessClassInPackage.jdk.nashorn.internal.runtime","accessClassInPackage.jdk.nashorn.internal.runtime.linker","accessDeclaredMembers","suppressAccessChecks").collect(Collectors.toSet());
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
				
				if (!allowedRuntimePermissions.contains(perm.getName())) {
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
