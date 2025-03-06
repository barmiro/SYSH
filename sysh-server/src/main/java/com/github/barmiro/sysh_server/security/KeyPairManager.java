package com.github.barmiro.sysh_server.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.stereotype.Component;

@Component
public class KeyPairManager {
    private final KeyPair keyPair;
    private static final String PRIVATE_KEY_PATH = "/keys/private.der";
    private static final String PUBLIC_KEY_PATH = "/keys/public.der";
    
    public KeyPairManager() {
    	if (keysExist()) {
    		this.keyPair = loadExistingKeys();
    	} else {
    		this.keyPair = generateRsaKey();
    		saveKeyToFile(PRIVATE_KEY_PATH, keyPair.getPrivate().getEncoded());
    		saveKeyToFile(PUBLIC_KEY_PATH, keyPair.getPublic().getEncoded());    		
    	}
    }
    
    private boolean keysExist() {
    	return new File(PRIVATE_KEY_PATH).exists() && new File(PUBLIC_KEY_PATH).exists();
    }
    
    private KeyPair loadExistingKeys() {
    	byte[] privateKeyBytes;
    	byte[] publicKeyBytes;
    	KeyFactory keyFactory;
    	
    	try {
    		privateKeyBytes = Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH));
    		publicKeyBytes = Files.readAllBytes(Paths.get(PUBLIC_KEY_PATH));
    		keyFactory = KeyFactory.getInstance("RSA");
    	} catch (IOException  | NoSuchAlgorithmException e) {
    		throw new RuntimeException("Failed to load RSA keys", e);
    	}
    	
    	PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
    	X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
    	
    	PrivateKey privateKey;
    	PublicKey publicKey;
    	
    	try {
    		privateKey = keyFactory.generatePrivate(privateKeySpec);
    		publicKey = keyFactory.generatePublic(publicKeySpec);    		
    	} catch (InvalidKeySpecException e) {
    		throw new RuntimeException("Invalid key spec", e);
    	}
    	
    	return new KeyPair(publicKey, privateKey);
    }
    
    private KeyPair generateRsaKey() {
    	try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("RSA key generation failed", e);
		}
    }
    
    private void saveKeyToFile(String path, byte[] key) {
    	try{
    		File file = new File(path);
    		file.getParentFile().mkdirs();
    		try (FileOutputStream fos = new FileOutputStream(file)) {
    			fos.write(key);
    		}
    	} catch (IOException e) {
    		throw new RuntimeException("Failed to write key file", e);
    	}
    }
//    i'm not sure how i feel about this casting
    public RSAPrivateKey getPrivateKey() {
    	return (RSAPrivateKey) keyPair.getPrivate();
    }

    public RSAPublicKey getPublicKey() {
    	return (RSAPublicKey) keyPair.getPublic();
    }
}
