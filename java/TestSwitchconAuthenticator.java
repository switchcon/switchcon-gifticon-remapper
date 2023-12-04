public class TestSwitchconAuthenticator {
    private static byte[] keyBytes = {
        (byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,
        (byte)0x02,(byte)0x02,(byte)0x02,(byte)0x02,
        (byte)0x03,(byte)0x03,(byte)0x03,(byte)0x03,
        (byte)0x04,(byte)0x04,(byte)0x04,(byte)0x04,
        (byte)0x05,(byte)0x05,(byte)0x05,(byte)0x05,
        (byte)0x06,(byte)0x06,(byte)0x06,(byte)0x06,
        (byte)0x07,(byte)0x07,(byte)0x07,(byte)0x07,
        (byte)0x08,(byte)0x08,(byte)0x08,(byte)0x08,
    };
    public static void main(String[] args){
        System.out.println("Verfying Starts--------");
        try {
            System.out.println("bnum 123456789 sid swtchcon");
            String s1 = SwitchconHMACAuthenticator.getAuthStringQRUse("123456789", "swtchcon", keyBytes);
            System.out.println(s1);
            String s2 = SwitchconHMACAuthenticator.getAuthStringRegister("123456789", "swtchcon", keyBytes);
            System.out.println(s2);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
    }
}
