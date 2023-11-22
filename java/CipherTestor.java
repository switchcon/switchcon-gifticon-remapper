
public class CipherTestor {
    public static void main(String[] args)
    {
        byte[] key = {
            (byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,
            (byte)0x02,(byte)0x02,(byte)0x02,(byte)0x02,
            (byte)0x03,(byte)0x03,(byte)0x03,(byte)0x03,
            (byte)0x04,(byte)0x04,(byte)0x04,(byte)0x04,
            (byte)0x05,(byte)0x05,(byte)0x05,(byte)0x05,
            (byte)0x06,(byte)0x06,(byte)0x06,(byte)0x06,
            (byte)0x07,(byte)0x07,(byte)0x07,(byte)0x07,
            (byte)0x08,(byte)0x08,(byte)0x08,(byte)0x08,
        };
        System.out.println("Test: -----------------------------");
        System.out.println("key: 0x01010101_02020202_03030303_04040404_05050505_06060606_07070707_08080808");
        try{
        System.out.println("CipherForQRGen, 12345678, switchcon");
        String s1 = CipherForQRGen.Encrypt("12345678", "switchcon", key);
        System.out.println(s1);
        System.out.println("CipherForRegister, 12345678, switchcon");
        String s2 = CipherForRegister.Encrypt("12345678","switchcon", key);
        System.out.println(s2);
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
        }
        System.out.println("End: ------------------------------");
    }
}
