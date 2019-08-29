package io.github.danthe1st.danbot1.util;

import java.io.FileDescriptor;
import java.security.AllPermission;
import java.security.Permission;
import java.security.SecurityPermission;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * {@link SecurityManager} to secure access on sensitive Threads(for the <code>eval</code> command)<br>
 * Also, {@link Runtime#exec(String)} is denied.<br>
 * @author Daniel Schmid
 */
public class BotSecurityManager extends SecurityManager {
	private Set<Thread> sensitiveThreads=new HashSet<>();
	private String[] allowedRuntimePermissions={"createClassLoader","accessClassInPackage.jdk.nashorn.internal.","accessDeclaredMembers","suppressAccessChecks","modifyThread","exitVM."};
	
	/**
	 * checks if the current Thread is a sensitive Thread
	 * @return <code>true</code> if it is a sensitive Thread
	 */
	private boolean isCurrentThreadSensitive() {
		return sensitiveThreads.contains(Thread.currentThread());
	}
	/**
	 * sets the current Thread as a sensitive Thread
	 */
	private void setSensitiveThread() {
		sensitiveThreads.add(Thread.currentThread());
	}
	/**
	 * unsets the current Thread as a sensitive Thread
	 */
	private void unsetSensitiveThread() {
		sensitiveThreads.remove(Thread.currentThread());
	}
	/**
	 * executes code sensitively
	 * @param toExec the Code(function) to execute
	 * @param args the arguments of the Function to access
	 * @return the return value of the Function
	 */
	public <T, R>R execSecure(Function<T, R> toExec,T args){
		setSensitiveThread();
		try {
			return toExec.apply(args);
		}finally {
			unsetSensitiveThread();
		}
	}
	@Override
	public void checkPermission(Permission perm) {
		//System.out.println(perm.getClass().getName()+perm.getName());
		if(perm instanceof AllPermission) {
			throw new SecurityException("not allowed to do everything");
		}
		if (isCurrentThreadSensitive()) {
			if(perm instanceof SecurityPermission) {
				throw new SecurityException("missing permission: "+perm.getName());
			}
			if(perm instanceof RuntimePermission) {
				String name=perm.getName();
				boolean allow=false;
				for (String allowed : allowedRuntimePermissions) {
					if (name.startsWith(allowed)) {
						allow=true;
						break;
					}
				}
				if (!allow) {
					throw new SecurityException("missing RuntimePermission: "+perm.getName());
				}
			}
		}
	}
	@Override
	public void checkPermission(Permission perm, Object context) {
		checkPermission(perm);
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
	@Override
	public void checkWrite(FileDescriptor desc) {
		if (isCurrentThreadSensitive()) {
			throw new SecurityException("File access restricted");
		}
	}
	
}
