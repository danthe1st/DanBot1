package io.github.danthe1st.danbot1.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileDescriptor;
import java.security.AllPermission;
import java.security.SecurityPermission;
import java.io.SerializablePermission;

import org.junit.jupiter.api.Test;

public class BotSecurityManagerTest {
	
	private BotSecurityManager secManager=new BotSecurityManager();
	
	@Test
	void testExecSecureWithAllowedPermission() {
		secManager.execSecure((n)->{
			secManager.checkPermission(new RuntimePermission("createClassLoader"));
			return null;
		},null);
	}
	@Test
	void testExecSecureWithNotAllowedPermission() {
		assertThrows(SecurityException.class,()->{
			secManager.execSecure((n)->{
				secManager.checkPermission(new RuntimePermission("somepermission"));
				return null;
			},null);
		});
	}

	@Test
	void testCheckPermissionRuntimePermissionWithoutSensitiveThread() {
		secManager.checkPermission(new RuntimePermission("somepermission"),null);
	}
	@Test
	void testCheckPermissionProblematicPermission() {
		assertThrows(SecurityException.class,()->{
			secManager.checkPermission(new AllPermission());
		});
		assertThrows(SecurityException.class,()->{
			secManager.execSecure((n)->{
				secManager.checkPermission(new SecurityPermission("somepermission"));
				return null;
			},null);
		});
	}
	@Test
	void testCheckPermissionUnknownPermissionWithSensitiveThread() {
		secManager.execSecure((n)->{
			secManager.checkPermission(new SerializablePermission("somepermission"));
			return null;
		},null);
	}

	@Test
	void testCheckExec() {
		assertThrows(SecurityException.class,()->{
			secManager.checkExec("ping 127.0.0.1");
		});
	}

	@Test
	void testCheckWriteFileName() {
		secManager.checkWrite("Some file");
	}
	@Test
	void testCheckWriteFD() {
		secManager.checkWrite((FileDescriptor)null);
	}
	@Test
	void testCheckSensitiveThreadWrite() {
		assertThrows(SecurityException.class,()->{
			secManager.execSecure((n)->{
				testCheckWriteFileName();
				return null;
			},null);
		});
		assertThrows(SecurityException.class,()->{
			secManager.execSecure((n)->{
				testCheckWriteFD();
				return null;
			},null);
		});
	}
}
