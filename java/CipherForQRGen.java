import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.IntStream;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherForQRGen {

	public static String Encrypt(String barcodeNum, String serverID, byte[] presharedKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException
	{
		Security.addProvider(new BouncyCastleProvider());
		// Assume that we have a preshared key (bytes array, 32-byte)
		SecretKeySpec key = new SecretKeySpec(Arrays.copyOfRange(presharedKey, 0, 32), "AES");
		long unixTime = System.currentTimeMillis() / 1000L;
		SecureRandom sr = SecureRandom.getInstanceStrong();
		byte[] randomIV = new byte[16];
		sr.nextBytes(randomIV);
		byte[] time = 
		{
			(byte) (unixTime >> 24),
			(byte) (unixTime >> 16),
			(byte) (unixTime >> 8),
			(byte) unixTime
		};
		IvParameterSpec iv = new IvParameterSpec(randomIV);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		byte[] barcodeBytes = barcodeNum.getBytes(StandardCharsets.UTF_8);
		byte[] serverIDBytes = serverID.getBytes(StandardCharsets.UTF_8);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(time); baos.write(serverIDBytes); baos.write(barcodeBytes);
		byte[] total = baos.toByteArray();
		cipher.init(Cipher.ENCRYPT_MODE,key, iv);
		byte[] outCipher = cipher.doFinal(total);	
		System.out.println("time");
		System.out.println(Arrays.toString(time));
		System.out.println("serverid");
		System.out.println(Arrays.toString(serverIDBytes));
		System.out.println("barcodes");
		System.out.println(Arrays.toString(barcodeBytes));
		System.out.println("total");
		System.out.println(Arrays.toString(total));
		System.out.print("Plain Len");
		System.out.println(total.length);
		System.out.print("Cipher Len");
		System.out.println(outCipher.length);
		MessageDigest hash = MessageDigest.getInstance("SHA-256", "BC");
		hash.update(time);
		hash.update(randomIV);
		hash.update(serverIDBytes);
		byte[] outHash = hash.digest();
		ByteArrayOutputStream bw = new ByteArrayOutputStream();
		bw.write(time); bw.write(randomIV); bw.write(outHash); bw.write(outCipher); // T || IV || Hash || Cipher;
		byte[] fin = bw.toByteArray();
		Base64.Encoder b64encoder = Base64.getEncoder();
		String ret = "swc:"+b64encoder.encodeToString(fin);
		return ret;
	}
}

