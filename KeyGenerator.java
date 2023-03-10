import java.security.*;
import java.util.*;
import java.security.spec.*;
import java.math.*;
import javax.crypto.Cipher;

public class KeyGenerator {
    public static enum EncodeType { 
        RSA
    }
    private PrivateKey privateKey;
    private HashMap<String, PublicKey> publicKeyListDecoder = new HashMap<>();
    private PublicKey publicKey;
    private EncodeType encodeType;
    
    public KeyGenerator() {
        this(KeyGenerator.EncodeType.RSA);
    }
    
    public KeyGenerator(EncodeType encodeType) {
        this.encodeType = encodeType;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(encodeType.toString());
            generator.initialize(2048);
            KeyPair key = generator.generateKeyPair();
            this.privateKey = key.getPrivate();
            this.publicKey = key.getPublic();
        } catch (Exception e) {
            System.out.println(e);
        }
    } 
    
    public KeyGenerator(EncodeType encodeType, String modPublic, String exponentForPublicKey, String modPrivate, String exponentForPrivateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encodeType.toString());
            this.publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(new BigInteger(modPublic), new BigInteger(exponentForPublicKey)));
            this.privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(new BigInteger(modPrivate), new BigInteger(exponentForPrivateKey)));
            this.encodeType = encodeType;
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //Wrappers around the encrypt/decrypt msgs
    
    public String decryptMessageUsingPrivateKey(String msg) {
        System.out.println("Deprecated!");
        return decryptMessage(msg, this.privateKey, this.encodeType);
    }
    public String encryptMessageUsingPublicKey(String msg) {
        System.out.println("Deprecated!");
        return encryptMessage(msg, this.publicKey, this.encodeType);
    }
    
    public String decryptMessageUsingPublicKey(String msg) {
        return decryptMessage(msg, this.publicKey, this.encodeType);
    }
    
    public String encryptMessageUsingPrivateKey(String msg) {
        return encryptMessage(msg, this.privateKey, this.encodeType);
    }
    
    public static String encryptMessageWithKey(String msg, Key key, EncodeType encodeType) {
        return encryptMessage(msg, key, encodeType);
    }
    
    public static String decryptMessageWithKey(String msg, Key key, EncodeType encodeType) {
        return decryptMessage(msg, key, encodeType);
    }


    public String encryptMessageFromList(String name, String message, EncodeType encodeType) {
        if (publicKeyListDecoder.containsKey(name)) {
            PublicKey key = publicKeyListDecoder.get(name);
            return encryptMessage(message, key, encodeType);
        }
        return null;
    }
    
    /**
     * Adds a contact [public key] to your list
     * @param name of the contact, mod [as a string], exponent [as a string]
     */
    public void addPublicKeyToList(String name, String mod, String exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encodeType.toString());
            PublicKey key = keyFactory.generatePublic(new RSAPublicKeySpec(new BigInteger(mod), new BigInteger(exponent)));
            publicKeyListDecoder.put(name, key);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    /**
     * Adds a contact [public key] to your list
     * @param name of the contact, machine that is being defined
     */
    public void addPublicKeyToList(String name, KeyGenerator secondMachine) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encodeType.toString());
            PublicKey key = secondMachine.getPublicKey();
            publicKeyListDecoder.put(name, key);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //imports a private key... kind of useless tbh
    public void importPrivateKey(String mod, String exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encodeType.toString());
            this.privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(new BigInteger(mod), new BigInteger(exponent)));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    //imports a public key, also useless tbh
    public void importPublicKey(String mod, String exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(encodeType.toString());
            this.publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(new BigInteger(mod), new BigInteger(exponent)));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //why are you here
    
    private static String encryptMessage(String plainText, Key key, EncodeType encodeType) {
        try {
            Cipher cipher = Cipher.getInstance(encodeType.toString());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    
    
    private static String decryptMessage(String encryptedText, Key key, EncodeType encodeType) {
        try {
            Cipher cipher = Cipher.getInstance(encodeType.toString());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    
    // Getters for the keys
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    } 
    public PublicKey getPublicKey() {
        return this.publicKey;
    } 
}